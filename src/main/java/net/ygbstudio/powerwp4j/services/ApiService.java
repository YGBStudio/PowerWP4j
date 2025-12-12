/*
 * PowerWP4j - Power WP for Java
 * Copyright (C) 2025 Yoham Gabriel Barboza B.
 *
 * This file is part of PowerWP4j.
 *
 * PowerWP4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PowerWP4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package net.ygbstudio.powerwp4j.services;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import net.ygbstudio.powerwp4j.exceptions.InvalidApiUrlError;
import net.ygbstudio.powerwp4j.models.schema.WPQueryParam;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.utils.functional.TriggerCallable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ApiService is a utility class for interacting with the WordPress REST API and provide convenience
 * methods for common flows in the library.
 *
 * @author Yoham Gabriel B.
 */
@NullMarked
public final class ApiService {

  private static final int TASK_TERMINATION_TIMEOUT_MINS = 5;

  private static final Logger apiServiceLogger = LoggerFactory.getLogger(ApiService.class);

  private ApiService() {}

  public static Optional<HttpRequest> buildWpGetRequest(
      String url, String username, String applicationPassword, @Nullable Logger classLogger) {
    BinaryOperator<String> encodeAuthStr =
        (user, appPass) -> {
          String authStr = user + ":" + appPass;
          return Base64.getEncoder().encodeToString(authStr.getBytes(StandardCharsets.UTF_8));
        };
    try {
      return Optional.of(
          HttpRequest.newBuilder()
              .GET()
              .header("keep_alive", "true")
              .header("Accept-Encoding", "true")
              .header("Accept", "application/json")
              .header("basic_auth", encodeAuthStr.apply(username, applicationPassword))
              .uri(new URI(url))
              .build());
    } catch (URISyntaxException uriSyntaxEx) {
      Supplier<String> errorMessageUri =
          () -> "Unable to process this request. URL: " + url + " seems malformed";
      if (classLogger != null) classLogger.warn(errorMessageUri.get(), uriSyntaxEx);
      throw new InvalidApiUrlError(errorMessageUri.get(), uriSyntaxEx);
    }
  }

  public static String makeRequestURL(
      String apiBasePath, Map<WPQueryParam, String> queryParams, WPRestPath pathParam) {
    return apiBasePath + pathParam + WPQueryParam.joinQueryParams(queryParams);
  }

  /**
   * Connects to the WordPress REST API and returns the response.
   *
   * @param url the URL to connect to
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   * @param classLogger the logger to use for logging
   * @return an Optional containing the response from the WordPress REST API
   * @throws IOException if an I/O error occurs
   * @throws InterruptedException if the thread is interrupted
   * @throws InvalidApiUrlError if the URL is invalid
   */
  public static Optional<HttpResponse<String>> connectGetWP(
      String url, String username, String applicationPassword, @Nullable Logger classLogger)
      throws IOException, InterruptedException, InvalidApiUrlError {
    try (HttpClient client = HttpClient.newHttpClient()) {
      Optional<HttpRequest> requestOptional =
          buildWpGetRequest(url, username, applicationPassword, classLogger);
      return Optional.of(
          client.send(
              requestOptional.orElseThrow(),
              HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)));
    }
  }

  /**
   * Processes a list of links using a client function and returns a result artifact.
   *
   * @param linkList the list of links to process
   * @param clientProcedure the client function to use for link processing
   * @param filterPred the predicate to use for filtering results
   * @param collector the collector to use for collecting results
   * @param <R> the type of the result
   * @return the result of the processing
   */
  public static <R> R linkProcessor(
      List<String> linkList,
      BiFunction<HttpClient, String, CompletableFuture<R>> clientProcedure,
      Predicate<? super R> filterPred,
      Collector<? super R, R, R> collector) {
    R artifact;
    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    try (HttpClient client =
        HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).executor(executor).build()) {
      artifact =
          linkList.parallelStream()
              .unordered()
              .map(link -> clientProcedure.apply(client, link))
              .map(CompletableFuture::join)
              .filter(filterPred)
              .collect(collector);
    } finally {
      executor.shutdown();
      try {
        if (!executor.awaitTermination(TASK_TERMINATION_TIMEOUT_MINS, TimeUnit.MINUTES)) {
          executor.shutdownNow();
        }
      } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
    return artifact;
  }

  public static <R> R linkProcessor(
      List<String> linkList,
      BiFunction<HttpClient, String, CompletableFuture<R>> clientProcedure,
      Predicate<? super R> filterPred,
      Collector<? super R, R, R> collector,
      @Nullable Predicate<? super R> retryPred,
      @Nullable TimeUnit intervalUnit,
      long intervalTime,
      int retryAttempts,
      @Nullable Supplier<String> retryFailedMessage) {

    TriggerCallable<R> processLinks =
        () -> linkProcessor(linkList, clientProcedure, filterPred, collector);

    int retryCount = 0;
    R resultType = processLinks.get();
    while (retryPred != null
        && !retryPred.test(resultType)
        && retryAttempts >= retryCount
        && intervalUnit != null) {
      retryCount++;
      apiServiceLogger.info(
          "Retrying the last batch of links. Attempt: {}/{}", retryCount, retryAttempts);
      resultType = processLinks.get();
      try {
        Thread.sleep(intervalUnit.toMillis(intervalTime));
      } catch (InterruptedException intEx) {
        Thread.currentThread().interrupt();
        apiServiceLogger.error("Thread interrupted while retrying", intEx);
      }
    }
    if (retryCount >= retryAttempts && retryPred != null && !retryPred.test(resultType)) {
      Supplier<String> errorMessage =
          () -> Objects.requireNonNullElse(retryFailedMessage.get(), "Retries exceeded");
      apiServiceLogger.error(errorMessage.get());
    }
    return resultType;
  }
}
