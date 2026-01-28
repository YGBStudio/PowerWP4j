/*
 * PowerWP4j - Power WP for Java
 *
 * Copyright 2025-2026 Yoham Gabriel Barboza B. (YGBStudio)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package net.ygbstudio.powerwp4j.engine;

import static net.ygbstudio.powerwp4j.services.HttpRequestService.makeRequestURL;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.jsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.LongStream;
import net.ygbstudio.powerwp4j.base.extension.enums.QueryParamEnum;
import net.ygbstudio.powerwp4j.exceptions.CacheConstructionException;
import net.ygbstudio.powerwp4j.exceptions.CacheFileSystemException;
import net.ygbstudio.powerwp4j.models.entities.WPSiteInfo;
import net.ygbstudio.powerwp4j.models.schema.WPCacheKey;
import net.ygbstudio.powerwp4j.models.schema.WPQueryParam;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.services.HttpRequestService;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import net.ygbstudio.powerwp4j.utils.functional.Trigger;
import net.ygbstudio.powerwp4j.utils.functional.TypedTrigger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

/**
 * WPCacheManager is the main class for interacting with a WordPress site. It provides methods for
 * fetching data from the WordPress REST API and caching it locally using JSON.
 *
 * @author Yoham Gabriel B.
 */
public class WPCacheManager {
  private static final Logger wpSiteEngineLogger = LoggerFactory.getLogger(WPCacheManager.class);
  private static final int DEFAULT_PER_PAGE = 10;
  private final ReentrantLock cacheLock = new ReentrantLock();

  private final WPSiteInfo siteInfo;
  private WPCacheMeta wpCacheMeta;
  private final Path cachePath;
  private File cacheFile;
  private List<String> linkList;

  /**
   * Initializes a new instance of the WPCacheManager class. If a local WordPress cache is found, it
   * is loaded into memory, otherwise a new cache must be created using the {@link
   * WPCacheManager#fetchCache(Path, boolean, boolean)} or {@link
   * WPCacheManager#fetchCacheFromInstancePath(boolean, boolean)} if you already provided a path in
   * the constructor.
   *
   * <p>A local cache is not created automatically since the client must handle any exceptions that
   * result from the cache creation process to ensure maximum control of client-specific flows,
   * exception handling, and logging styles.
   *
   * @param fullyQualifiedDomainName the fully qualified domain name of the WordPress site
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   * @param cachePath the path to the cache file
   */
  public WPCacheManager(
      @NotNull String fullyQualifiedDomainName,
      @NotNull String username,
      @NotNull String applicationPassword,
      @Nullable Path cachePath) {
    this.siteInfo =
        new WPSiteInfo(
            fullyQualifiedDomainName.replaceAll("^https?:+//\\b", ""),
            username,
            applicationPassword);
    this.cachePath = cachePath;
    if (cachePath != null) {
      cacheFile = cachePath.toFile().exists() ? cachePath.toFile() : null;
      WPCacheMeta.from(cachePath).ifPresent(cacheMeta -> wpCacheMeta = cacheMeta);
    }
    String apiBaseUrl = siteInfo.apiBaseUrl();
    wpSiteEngineLogger.info("Initialized WPCacheManager for site: {}", fullyQualifiedDomainName);
    wpSiteEngineLogger.info("API Base Path set to: {}", apiBaseUrl);
  }

  /**
   * Initializes a new instance of the WPCacheManager class. If a local WordPress cache is not
   * needed, the cache will be ignored and the cachePath parameter will be set to null.
   *
   * <p>In case you want to create a cache in the current instance of the WPCacheManager, proceed to
   * create the cache file using the {@link WPCacheManager#fetchCache(Path, boolean, boolean)}
   * method.
   *
   * @param fullyQualifiedDomainName the fully qualified domain name of the WordPress site
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   */
  public WPCacheManager(
      @NotNull String fullyQualifiedDomainName,
      @NotNull String username,
      @NotNull String applicationPassword) {
    this(fullyQualifiedDomainName, username, applicationPassword, null);
  }

  /**
   * Initializes a new instance of the WPCacheManager class using the provided {@link WPSiteInfo}
   * object and an optional cache path. If a cache path is provided, the cache will be loaded from
   * the file system. If a cache path is not provided, the cache will not be created and the
   * cachePath parameter will be set to null.
   *
   * @param siteInfo the site information object containing the fully qualified domain name,
   *     username, and application password.
   * @param cachePath an optional path to the cache file.
   */
  public WPCacheManager(@NotNull WPSiteInfo siteInfo, Path cachePath) {
    this(siteInfo.fullyQualifiedDomainName(), siteInfo.wpUser(), siteInfo.wpAppPass(), cachePath);
  }

  /**
   * Initializes a new instance of the WPCacheManager class using the provided {@link WPSiteInfo}
   * object. If a local WordPress cache is not needed, the cache will be ignored and the cachePath
   * parameter will be set to null.
   *
   * <p>In case you want to create a cache in the current instance of the WPCacheManager, proceed to
   * create the cache file using the {@link WPCacheManager#fetchCache(Path, boolean, boolean)}
   * method.
   *
   * @param siteInfo the site information object containing the fully qualified domain name,
   *     username, and application password.
   */
  public WPCacheManager(@NotNull WPSiteInfo siteInfo) {
    this(siteInfo.fullyQualifiedDomainName(), siteInfo.wpUser(), siteInfo.wpAppPass(), null);
  }

  /**
   * Connects to the WordPress REST API and returns the response. This a convenience method for this
   * class, however, other methods that can carry out more functionality are available in the {@link
   * net.ygbstudio.powerwp4j.services.RestClientService} class.
   *
   * @param queryParams the query parameters to be used in the request
   * @param pathParam the path parameter to be used in the request
   * @return an Optional containing the response from the WordPress REST API
   */
  @NotNull
  public Optional<HttpResponse<String>> connectWP(
      @NotNull Map<QueryParamEnum, String> queryParams, @NotNull WPRestPath pathParam) {
    String url = makeRequestURL(siteInfo.apiBaseUrl(), queryParams, pathParam);
    return HttpRequestService.connectGetWP(
        url, siteInfo.wpUser(), siteInfo.wpAppPass(), wpSiteEngineLogger);
  }

  /**
   * Creates a list of links to the WordPress REST API.
   *
   * @param totalPages the total number of pages
   * @param perPage the number of posts per page
   * @return a list of links to the WordPress REST API
   */
  @Unmodifiable
  @NotNull
  private List<String> linkListCreator(long totalPages, int perPage) {
    Map<WPQueryParam, String> queryParams = new EnumMap<>(WPQueryParam.class);
    return LongStream.range(1, totalPages + 1)
        .mapToObj(
            i -> {
              if (!queryParams.isEmpty()) queryParams.clear();
              queryParams.put(WPQueryParam.PAGE, String.valueOf(i));
              if (perPage > 0) queryParams.put(WPQueryParam.PER_PAGE, String.valueOf(perPage));
              return makeRequestURL(siteInfo.apiBaseUrl(), queryParams, WPRestPath.POSTS);
            })
        .toList();
  }

  /**
   * Fetches the local cache file from the WordPress REST API.
   *
   * @param cachePath the path to the cache file
   * @param overwriteCache whether to overwrite the cache file if it exists
   * @param ignoreSSLHandshakeException whether to ignore SSL Handshake Exception
   * @throws IOException if an I/O error occurs
   */
  private void fetchCacheInternal(
      @NotNull Path cachePath, boolean overwriteCache, boolean ignoreSSLHandshakeException)
      throws IOException {

    if (Objects.isNull(linkList) || linkList.isEmpty()) {
      Runnable throwCacheException =
          () -> {
            throw new CacheConstructionException(
                () ->
                    "Failed to gather WordPress post metadata for "
                        + siteInfo.fullyQualifiedDomainName()
                        + " Check your connection and try again");
          };
      WPCacheMeta.updateCacheMeta(siteInfo, cachePath, ignoreSSLHandshakeException)
          .ifPresentOrElse(
              cacheMeta -> {
                wpCacheMeta = cacheMeta;
                linkList = linkListCreator(wpCacheMeta.totalPages(), DEFAULT_PER_PAGE);
              },
              throwCacheException);
    }

    String apiBaseUrl = siteInfo.apiBaseUrl();
    wpSiteEngineLogger.info("Processing cache links for {}", apiBaseUrl);
    ArrayNode wpJsonArray =
        fetchCacheFromInstancePath(linkList, null, 0, 0, null, null, ignoreSSLHandshakeException);

    cacheLock.lock();
    try {
      writeCacheFs(cachePath, wpJsonArray, overwriteCache, false);
    } finally {
      cacheLock.unlock();
    }
    cacheFile = cachePath.toFile();
  }

  /**
   * Fetches the JSON cache from the WordPress REST API.
   *
   * @param listOfLinks the list of links to fetch
   * @param retryPred the predicate to retry on in case a specific condition is expected
   * @param fetchCacheWithoutSSL whether to fetch the cache without SSL (useful in testing)
   * @return the JSON cache as a single ArrayNode
   */
  private ArrayNode fetchCacheFromInstancePath(
      @NotNull List<String> listOfLinks,
      @Nullable Predicate<ArrayNode> retryPred,
      int retryAttempts,
      int intervalTime,
      TimeUnit intervalUnit,
      Supplier<String> retryFailedMsg,
      boolean fetchCacheWithoutSSL) {
    ObjectMapper mapper = JsonSupport.getMapper();
    BiFunction<HttpClient, String, CompletableFuture<ArrayNode>> procedureFunction =
        getFetchProcedure(fetchCacheWithoutSSL);
    return HttpRequestService.linkProcessor(
        listOfLinks,
        procedureFunction,
        Objects::nonNull,
        Collector.of(mapper::createArrayNode, ArrayNode::addAll, ArrayNode::addAll),
        Objects.isNull(retryPred) ? null : retryPred,
        intervalUnit,
        intervalTime,
        retryAttempts,
        retryFailedMsg);
  }

  /**
   * Applies the fetch procedure to the link list.
   *
   * @param fetchCacheWithoutSSL whether to fetch the cache without SSL (useful in testing)
   * @return the fetch procedure BiFunction
   */
  @NotNull
  @Contract(pure = true)
  private BiFunction<HttpClient, String, CompletableFuture<ArrayNode>> getFetchProcedure(
      boolean fetchCacheWithoutSSL) {

    Function<String, HttpRequest> requestFunction =
        link -> {
          String currentLink = fetchCacheWithoutSSL ? link.replaceFirst("https", "http") : link;
          wpSiteEngineLogger.debug("Processing link -> {} ", currentLink);
          return HttpRequestService.buildWpGetRequest(
              currentLink, siteInfo.wpUser(), siteInfo.wpAppPass(), wpSiteEngineLogger);
        };

    return (client, link) ->
        client
            .sendAsync(requestFunction.apply(link), BodyHandlers.ofString(StandardCharsets.UTF_8))
            .thenApply(HttpResponse::body)
            .thenApply(
                body -> {
                  try {
                    JsonNode node = JsonSupport.getTreeNode(body);
                    if (!node.isArray()) {
                      wpSiteEngineLogger.debug(
                          "Expected JSON array but got {} for link {}", node.getNodeType(), link);
                      return null;
                    }
                    return (ArrayNode) node;
                  } catch (Exception ex) {
                    wpSiteEngineLogger.debug(
                        "Failed parsing JSON for link {}: {}", link, ex.getMessage());
                    return null;
                  }
                })
            .exceptionally(
                ex -> {
                  wpSiteEngineLogger.debug(
                      "Failed processing link {} due to {} cause: {}",
                      link,
                      ex.getClass().getSimpleName(),
                      ex.getCause() != null ? ex.getCause().getMessage() : "unknown");
                  return null;
                });
  }

  /**
   * Writes the local cache file from a JSON array to a file on the filesystem.
   *
   * @param cachePath the path to the cache file
   * @param wpJsonArray the JSON array to write
   * @param overwriteCache whether to overwrite the cache file if it exists
   * @param isUpdate whether the cache is being updated
   * @throws IOException if an I/O error occurs
   */
  private void writeCacheFs(
      @NotNull Path cachePath,
      @NotNull ArrayNode wpJsonArray,
      boolean overwriteCache,
      boolean isUpdate)
      throws IOException {
    createCacheFile(cachePath, overwriteCache);
    try (FileWriter writer = new FileWriter(cachePath.toFile(), StandardCharsets.UTF_8)) {
      JsonSupport.getMapper().writerWithDefaultPrettyPrinter().writeValue(writer, wpJsonArray);
    }

    if (isUpdate) {
      wpSiteEngineLogger.info("Cache has been updated at {}", cachePath);
      return;
    }

    wpSiteEngineLogger.info("Cache created successfully at {}", cachePath);
  }

  /**
   * Fetches the local cache file from the WordPress REST API.
   *
   * @param overwriteCache whether to overwrite the cache file if it exists
   * @param ignoreSSLHandshakeException whether to ignore SSL Handshake Exception, useful for
   *     testing purposes only or local environments.
   * @throws IOException if an I/O error occurs
   */
  public void fetchCacheFromInstancePath(
      boolean overwriteCache, boolean ignoreSSLHandshakeException) throws IOException {
    if (cachePath == null)
      throw new UnsupportedOperationException(
          "Unable to fetch cache without a cache path in this instance. Provide a cache path and try again.");
    fetchCacheInternal(cachePath, overwriteCache, ignoreSSLHandshakeException);
  }

  /**
   * Fetches the local cache file from the WordPress REST API.
   *
   * @param cachePath the path to the cache file
   * @param overwriteCache whether to overwrite the cache file if it exists
   * @param ignoreSSLHandshakeException whether to ignore SSL Handshake Exception, useful for
   *     testing purposes only or local environments.
   * @throws IOException if an I/O error occurs
   */
  public void fetchCache(
      @NotNull Path cachePath, boolean overwriteCache, boolean ignoreSSLHandshakeException)
      throws IOException {
    fetchCacheInternal(cachePath, overwriteCache, ignoreSSLHandshakeException);
  }

  /**
   * Creates or overwrites a cache file in the local classpath/filesystem that will contain the
   * local cache of a WordPress site. Overwrites must be specified by the user in any of the
   * overloaded methods in this class.
   *
   * @param cachePath the path to the cache file
   * @param overwriteCache whether to overwrite the cache file if it exists
   */
  private void createCacheFile(@NotNull Path cachePath, boolean overwriteCache) {
    TypedTrigger<Exception> exceptionLogging =
        ex ->
            wpSiteEngineLogger.debug(
                "Rethrowing {} caused by: {} as {} while trying to create a new cache file",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                CacheConstructionException.class);

    Function<File, String> cacheExMsg =
        file -> String.format("Unable to create a file at %s", file.getAbsolutePath());

    File cachePathFile = cachePath.toFile();
    if (cachePathFile.exists() && overwriteCache) {
      try {
        Files.delete(cachePath);
      } catch (IOException ioEx) {
        wpSiteEngineLogger.debug(
            "Rethrowing {} as {} while trying to overwrite a cache file",
            ioEx.getClass().getSimpleName(),
            CacheConstructionException.class);
        throw new CacheConstructionException(cacheExMsg.apply(cachePathFile));
      }
    } else if (!cachePathFile.exists()) {
      try {
        Files.createFile(cachePath);
        WPCacheMeta.updateCacheMeta(siteInfo, cachePath, false);
      } catch (IOException ioEx) {
        exceptionLogging.activate(ioEx);
        throw new CacheConstructionException(cacheExMsg.apply(cachePathFile));
      }
    }
  }

  /**
   * Returns a FileReader for the cache file after verifying its existence, in case it does not
   * exist, returns null.
   *
   * @return a FileReader for the cache file, or null if the cache file does not exist
   * @throws IOException if an I/O error occurs
   */
  @Nullable
  private FileReader getCacheReader() throws IOException {
    return cacheFile.exists() ? new FileReader(cacheFile, StandardCharsets.UTF_8) : null;
  }

  /**
   * Returns a FileWriter for the cache file after verifying its existence, in case it does not
   * exist, returns null.
   *
   * @return a FileWriter for the cache file, or null if the cache file does not exist
   * @throws IOException if an I/O error occurs
   */
  @Nullable
  private FileWriter getCacheWriter() throws IOException {
    return cacheFile.exists() ? new FileWriter(cacheFile, StandardCharsets.UTF_8) : null;
  }

  /**
   * Synchronizes the cache file with the WordPress site. It assumes incremental cache changes will
   * be taking place, and that is why this method relies heavily on sorting pipelines as the deltas
   * are meant to be limited.
   *
   * @see #cacheSync()
   * @param ignoreSSL whether to ignore SSL errors, useful for testing purposes or local
   *     environments <strong>(do not use in production)</strong>.
   * @return true if the cache was successfully synchronized, false if the cache is up-to-date.
   * @throws InterruptedException if the thread is interrupted
   */
  public boolean cacheSync(boolean ignoreSSL) throws InterruptedException {
    Supplier<String> notFoundMsg = () -> "Cache file does not exist at " + cachePath;
    TypedTrigger<Exception> ioExceptionLogging =
        ex ->
            wpSiteEngineLogger.debug(
                "Failed to read cache file: {} caused by: {}",
                ex.getMessage(),
                ex.getCause() != null ? ex.getCause().getMessage() : "no cause");

    TypedTrigger<String> throwCacheException =
        exMsg -> {
          throw new CacheFileSystemException(() -> "Failed to read cache file: " + exMsg);
        };

    Trigger updateCache = () -> WPCacheMeta.updateCacheMeta(siteInfo, cachePath, ignoreSSL);

    if (wpCacheMeta == null) updateCache.activate();

    WPCacheMeta cacheMetaOld =
        new WPCacheMeta(
            wpCacheMeta.totalPages(),
            wpCacheMeta.totalPosts(),
            LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC));

    updateCache.activate();

    ArrayNode fromCache = null;
    try (FileReader cacheReader = getCacheReader()) {
      if (cacheReader == null) {
        wpSiteEngineLogger.debug(notFoundMsg.get());
        throw new FileNotFoundException(notFoundMsg.get());
      }
      fromCache = jsonReader(cacheReader, ArrayNode.class);
    } catch (IOException ieEx) {
      ioExceptionLogging.activate(ieEx);
      throwCacheException.activate(ieEx.getMessage());
    }

    long nodeDiff = wpCacheMeta.totalPosts() - cacheMetaOld.totalPosts();
    long pageDiff = wpCacheMeta.totalPages() - cacheMetaOld.totalPages();
    wpSiteEngineLogger.info("Node diff: {}", nodeDiff);
    wpSiteEngineLogger.info("Page diff: {}", pageDiff);

    if (nodeDiff == 0 && pageDiff == 0) {
      wpSiteEngineLogger.info("{} Cache is up-to-date", siteInfo.fullyQualifiedDomainName());
      return false;
    }

    linkList =
        linkListCreator(wpCacheMeta.totalPages(), DEFAULT_PER_PAGE).stream()
            // Each page has a default number of items and,
            // if the number of pages is less than the default number of items,
            // those may be contained in the last page
            .limit(nodeDiff < DEFAULT_PER_PAGE && pageDiff == 0 ? pageDiff + 1 : pageDiff)
            .toList();

    Comparator<JsonNode> jsonNodeComparator =
        (jsonNode1, jsonNode2) -> {
          long id1 = jsonNode1.get(WPCacheKey.ID.value()).asLong();
          long id2 = jsonNode2.get(WPCacheKey.ID.value()).asLong();
          return Long.compare(id1, id2);
        };

    long lastId =
        fromCache
            .valueStream()
            .sorted(jsonNodeComparator)
            .toList()
            .getLast()
            .get(WPCacheKey.ID.value())
            .asLong();

    Predicate<ArrayNode> testForLastElem =
        elem ->
            elem.valueStream()
                    .sorted(jsonNodeComparator)
                    .toList()
                    .getLast()
                    .get(WPCacheKey.ID.value())
                    .asLong()
                > lastId;

    List<JsonNode> updatedPosts =
        fetchCacheFromInstancePath(
                linkList,
                testForLastElem,
                3,
                2,
                TimeUnit.SECONDS,
                () -> "Failed to fetch new cache pages. Reached maximum retry attempts",
                ignoreSSL)
            .valueStream()
            .sorted(jsonNodeComparator.reversed())
            .limit(nodeDiff)
            .toList();

    fromCache.addAll(updatedPosts);

    cacheLock.lock();
    try {
      writeCacheFs(cacheFile.toPath(), fromCache, true, true);
    } catch (IOException ioEx) {
      wpSiteEngineLogger.debug(
          "Caught {} caused by {}", ioEx.getClass().getSimpleName(), ioEx.getMessage());
      throw new CacheFileSystemException(() -> "Failed to write cache file: " + ioEx.getMessage());
    } finally {
      cacheLock.unlock();
    }
    return true;
  }

  /**
   * Performs a cache synchronization between the local cache file and the WordPress REST API.
   *
   * @return {@code true} if the cache was updated, {@code false} otherwise
   * @throws InterruptedException if the thread is interrupted while waiting to fetch new cache
   *     pages
   */
  public boolean cacheSync() throws InterruptedException {
    return cacheSync(false);
  }

  /**
   * Returns the base URL of the WordPress REST API.
   *
   * @return the base URL of the WordPress REST API
   */
  public String apiBaseUrl() {
    return siteInfo.apiBaseUrl();
  }

  /**
   * Returns the user name for the WordPress site.
   *
   * @return the user name for the WordPress site
   */
  public String wpUser() {
    return siteInfo.wpUser();
  }

  /**
   * Returns a new WPCacheAnalyzer instance using the cache file specified at construction time.
   *
   * @return a new WPCacheAnalyzer instance
   */
  public WPCacheAnalyzer getCacheAnalyzer() {
    return new WPCacheAnalyzer(cachePath);
  }
}
