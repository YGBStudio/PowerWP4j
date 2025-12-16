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
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
import net.ygbstudio.powerwp4j.base.extension.QueryParamEnum;
import net.ygbstudio.powerwp4j.exceptions.InvalidApiUrlError;
import net.ygbstudio.powerwp4j.exceptions.MediaUploadError;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.utils.functional.TriggerCallable;
import org.apache.tika.Tika;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;

/**
 * HttpRequestService is a utility class for interacting with the WordPress REST API and provide
 * convenience methods for common flows in the library.
 *
 * @author Yoham Gabriel B.
 */
@NullMarked
public final class HttpRequestService {

  private static final int TASK_TERMINATION_TIMEOUT_MINS = 5;

  private static final Logger httpServiceLogger = LoggerFactory.getLogger(HttpRequestService.class);
  public static final String JSON_CONTENT_TYPE = "application/json";

  private HttpRequestService() {}

  /**
   * Encode the username and application password to be passed on to the authentication request
   * headers.
   *
   * @return a BinaryOperator that encodes a username and application password into a base64 string.
   */
  @Contract(pure = true)
  private static BinaryOperator<String> basicAuthEncode() {
    return (username, appPassword) -> {
      String authStr = username + ":" + appPassword;
      return "Basic "
          + Base64.getEncoder().encodeToString(authStr.getBytes(StandardCharsets.UTF_8));
    };
  }

  /**
   * Builds a main request for the WordPress REST API by centralising the common headers, URI,
   * exception handling, and basic authentication. The return value of this method is meant to be
   * decorated with the specific HTTP method (GET, POST, etc.) and body, if relevant.
   *
   * @param url target URL for the request
   * @param username username in your WordPress installation
   * @param applicationPassword secret that you configured for your user
   * @param classLogger logger instance that will be used for logging in this class
   * @return HttpRequest.Builder instance with common headers and URI set
   */
  private static Builder getMainRequestBuilder(
      String url, String username, String applicationPassword, @Nullable Logger classLogger) {
    try {
      return HttpRequest.newBuilder()
          .uri(new URI(url))
          .header("Accept", JSON_CONTENT_TYPE)
          .header("Authorization", basicAuthEncode().apply(username, applicationPassword));
    } catch (URISyntaxException uriSyntaxEx) {
      Supplier<String> errorMessageUri =
          () -> "Unable to process this request. URL: " + url + " seems malformed";
      if (classLogger != null) classLogger.warn(errorMessageUri.get(), uriSyntaxEx);
      throw new InvalidApiUrlError(errorMessageUri.get(), uriSyntaxEx);
    }
  }

  /**
   * Creates a request URL for the WordPress REST API.
   *
   * @param apiBasePath the base path for the WordPress REST API
   * @param queryParams the query parameters to be used in the request
   * @param pathParam the path parameter to be used in the request
   * @return the request URL
   */
  public static <E extends QueryParamEnum> String makeRequestURL(
      String apiBasePath, @Nullable Map<E, String> queryParams, WPRestPath pathParam) {
    return apiBasePath
        + pathParam
        + QueryParamEnum.joinQueryParams(Objects.requireNonNullElse(queryParams, Map.of()));
  }

  /**
   * Sends a request to the WordPress REST API and returns the response wrapped in an Optional type.
   * This method is a wrapper around the {@link HttpClient} class and is used to send requests with
   * centralised error handling and resource management.
   *
   * @param request {@link HttpRequest} object that will be sent
   * @param classLogger Instance of a class logger for error logging
   * @return Optional containing the response from the REST API
   */
  public static Optional<HttpResponse<String>> clientSend(HttpRequest request, Logger classLogger) {
    try (HttpClient client = HttpClient.newHttpClient()) {
      return Optional.of(
          client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)));
    } catch (IOException ioEx) {
      classLogger.warn("Caught IOException while sending request", ioEx);
      classLogger.debug("Request {} resulted in IOException", request);
    } catch (InterruptedException intEx) {
      Thread.currentThread().interrupt();
      classLogger.warn(
          "Caught InterruptedException the current thread has been interrupted", intEx);
    }
    return Optional.empty();
  }

  /**
   * Builds a GET request for the WordPress REST API.
   *
   * @param url the URL to connect to
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   * @param classLogger the logger to use for logging
   * @return an Optional containing the request
   */
  public static HttpRequest buildWpGetRequest(
      String url, String username, String applicationPassword, @Nullable Logger classLogger) {
    return getMainRequestBuilder(url, username, applicationPassword, classLogger).GET().build();
  }

  /**
   * Builds a POST request for the WordPress REST API.
   *
   * @param body the JSON body of the request
   * @param url the URL to connect to
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   * @param classLogger the logger to use for logging
   * @return an HttpRequest object representing the POST request
   */
  public static HttpRequest buildWpPostRequest(
      JsonNode body,
      String url,
      String username,
      String applicationPassword,
      @Nullable Logger classLogger) {
    return getMainRequestBuilder(url, username, applicationPassword, classLogger)
        .header("Content-Type", JSON_CONTENT_TYPE)
        .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
        .build();
  }

  /**
   * Builds a POST request for the WordPress REST API to upload a media file.
   *
   * @param url the URL to connect to
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   * @param attachmentPath the path to the media file to be uploaded
   * @param classLogger the logger to use for logging
   * @return an HttpRequest object representing the POST request
   */
  public static Optional<HttpRequest> buildWpPostRequest(
      String url,
      String username,
      String applicationPassword,
      Path attachmentPath,
      @Nullable Logger classLogger) {
    String fileName = attachmentPath.getFileName().toString();
    if (!attachmentPath.toFile().exists())
      throw new MediaUploadError(
          () -> "Attachment path " + attachmentPath.toAbsolutePath() + " does not exist");
    Tika tika = new Tika();
    try {
      return Optional.of(
          getMainRequestBuilder(url, username, applicationPassword, classLogger)
              .POST(HttpRequest.BodyPublishers.ofFile(attachmentPath))
              .header("Content-Type", tika.detect(attachmentPath))
              .headers("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
              .build());
    } catch (IOException ioEx) {
      if (classLogger != null) {
        classLogger.warn("Failed to upload media to {}", url);
        classLogger.debug("Caught IOException while trying to upload ", ioEx);
      }
      return Optional.empty();
    }
  }

  /**
   * Builds a DELETE request for the WordPress REST API.
   *
   * @param url the URL to connect to
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   * @param classLogger the logger to use for logging
   * @return an HttpRequest object representing the DELETE request
   */
  public static HttpRequest buildWpDeleteRequest(
      String url, String username, String applicationPassword, @Nullable Logger classLogger) {
    return getMainRequestBuilder(url, username, applicationPassword, classLogger).DELETE().build();
  }

  /**
   * Connects to the WordPress REST API and returns the response.
   *
   * @param url the URL to connect to
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   * @param classLogger the logger to use for logging
   * @return an Optional containing the response from the WordPress REST API
   * @throws InvalidApiUrlError if the URL is invalid
   */
  public static Optional<HttpResponse<String>> connectGetWP(
      String url, String username, String applicationPassword, @Nullable Logger classLogger)
      throws InvalidApiUrlError {
    HttpRequest requestOptional =
        buildWpGetRequest(url, username, applicationPassword, classLogger);
    return clientSend(requestOptional, httpServiceLogger);
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

  /**
   * Processes a list of links using a client function and returns a result artifact.
   *
   * @param linkList the list of links to process
   * @param clientProcedure the client function to use for link processing
   * @param filterPred the predicate to use for filtering results
   * @param collector the collector to use for collecting results
   * @param retryPred the predicate to use for retrying results
   * @param intervalUnit the time unit for the retry interval
   * @param intervalTime the time for the retry interval
   * @param retryAttempts the number of retry attempts
   * @param retryFailedMessage the message to use for retry failure
   * @param <R> the type of the result
   * @return the result of the processing
   */
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
      ++retryCount;
      httpServiceLogger.info(
          "Retrying the last batch of links. Attempt: {}/{}", retryCount, retryAttempts);
      resultType = processLinks.get();
      try {
        Thread.sleep(intervalUnit.toMillis(intervalTime));
      } catch (InterruptedException intEx) {
        Thread.currentThread().interrupt();
        httpServiceLogger.error("Thread interrupted while retrying", intEx);
      }
    }
    if (retryCount >= retryAttempts && retryPred != null && !retryPred.test(resultType)) {
      Supplier<String> errorMessage =
          () -> Objects.requireNonNullElse(retryFailedMessage.get(), "Retries exceeded");
      httpServiceLogger.error(errorMessage.get());
    }
    return resultType;
  }
}
