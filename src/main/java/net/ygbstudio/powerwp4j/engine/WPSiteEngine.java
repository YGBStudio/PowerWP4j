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

package net.ygbstudio.powerwp4j.engine;

import static net.ygbstudio.powerwp4j.services.HttpRequestService.makeRequestURL;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.jsonReader;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.readJsonFs;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.writeJsonFs;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.LongStream;
import net.ygbstudio.powerwp4j.base.extension.PostStatusEnum;
import net.ygbstudio.powerwp4j.base.extension.QueryParamEnum;
import net.ygbstudio.powerwp4j.builders.WPBasicPayloadBuilder;
import net.ygbstudio.powerwp4j.exceptions.CacheConstructionException;
import net.ygbstudio.powerwp4j.exceptions.CacheFileSystemException;
import net.ygbstudio.powerwp4j.exceptions.CacheMetaDataException;
import net.ygbstudio.powerwp4j.exceptions.MediaUploadError;
import net.ygbstudio.powerwp4j.models.entities.CacheMeta;
import net.ygbstudio.powerwp4j.models.entities.PostInfo;
import net.ygbstudio.powerwp4j.models.schema.WPQueryParam;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.services.HttpRequestService;
import net.ygbstudio.powerwp4j.services.RestClientService;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import net.ygbstudio.powerwp4j.utils.functional.Trigger;
import net.ygbstudio.powerwp4j.utils.functional.TypedTrigger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;

/**
 * WPSiteEngine is the main class for interacting with a WordPress site. It provides methods for
 * fetching data from the WordPress REST API and caching it locally using JSON.
 *
 * @author Yoham Gabriel B.
 */
public class WPSiteEngine {
  private static final Logger wpSiteEngineLogger = LoggerFactory.getLogger(WPSiteEngine.class);
  protected static final int DEFAULT_PER_PAGE = 10;

  private String fullyQualifiedDomainName;
  private String apiBasePath;
  private String username;
  private String applicationPassword;
  private Set<PostInfo> createdPosts = new HashSet<>();
  private CacheMeta wpCacheMeta;
  private Path cachePath;
  private Path cacheMetadataFilePath;
  private File cacheFile;
  private List<String> linkList;

  /**
   * Initializes a new instance of the WPSiteEngine class. If a local WordPress cache is found, it
   * is loaded into memory, otherwise a new cache must be created using the {@link
   * WPSiteEngine#fetchJsonCache(Path cachePath, boolean overwriteCache)} or {@link
   * WPSiteEngine#fetchJsonCache(boolean overwriteCache)} if you already provided a path in the
   * constructor.
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
  public WPSiteEngine(
      @NonNull String fullyQualifiedDomainName,
      @NonNull String username,
      @NonNull String applicationPassword,
      @Nullable Path cachePath) {
    this.fullyQualifiedDomainName = fullyQualifiedDomainName.replaceAll("^https?:+//\\b", "");
    this.username = username;
    this.applicationPassword = applicationPassword;
    this.cachePath = cachePath;
    if (cachePath != null) cacheFile = cachePath.toFile().exists() ? cachePath.toFile() : null;
    apiBasePath = String.format("https://%s/wp-json/wp/v2", this.fullyQualifiedDomainName);
    wpSiteEngineLogger.info("Initialized WPSiteEngine for site: {}", fullyQualifiedDomainName);
    wpSiteEngineLogger.info("API Base Path set to: {}", apiBasePath);
  }

  /**
   * Initializes a new instance of the WPSiteEngine class. If a local WordPress cache is not needed,
   * the cache will be ignored and the cachePath parameter will be set to null.
   *
   * <p>In case you want to create a cache in the current instance of the WPSiteEngine, proceed to
   * create the cache file using the {@link WPSiteEngine#fetchJsonCache(Path cachepath, boolean
   * overwriteCache)} method.
   *
   * @param fullyQualifiedDomainName the fully qualified domain name of the WordPress site
   * @param username the username for the WordPress site
   * @param applicationPassword the application password for the WordPress site
   */
  public WPSiteEngine(
      @NonNull String fullyQualifiedDomainName,
      @NonNull String username,
      @NonNull String applicationPassword) {
    this(fullyQualifiedDomainName, username, applicationPassword, null);
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
  @NonNull
  public Optional<HttpResponse<String>> connectWP(
      @NonNull Map<QueryParamEnum, String> queryParams, @NonNull WPRestPath pathParam) {
    String url = makeRequestURL(apiBasePath, queryParams, pathParam);
    return HttpRequestService.connectGetWP(url, username, applicationPassword, wpSiteEngineLogger);
  }

  /**
   * Updates the cache metadata fields based on the WordPress response headers {@code x-wp-total}
   * and {@code x-wp-totalpages}.
   *
   * @throws IOException if an I/O error occurs
   * @throws InterruptedException if the thread is interrupted
   */
  private void updateCacheMeta() throws IOException, InterruptedException {
    Optional<HttpResponse<String>> headersRequest =
        connectWP(
            Map.of(
                WPQueryParam.PAGE,
                String.valueOf(1),
                WPQueryParam.PER_PAGE,
                String.valueOf(DEFAULT_PER_PAGE)),
            WPRestPath.POSTS);
    if (headersRequest.isPresent()) {
      Map<String, List<String>> headers = headersRequest.get().headers().map();
      long wpTotal = Long.parseLong(headers.get("x-wp-total").getFirst());
      long wpTotalPages = Long.parseLong(headers.get("x-wp-totalpages").getFirst());
      wpCacheMeta =
          new CacheMeta(wpTotalPages, wpTotal, LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC));
    }
  }

  /**
   * Loads the cache metadata from the local classpath/filesystem or creates a new cache metadata
   * file in case it does not exist, or it is empty.
   *
   * @param cachePath the path to the cache file
   * @param overwriteMetaFile whether to overwrite the cache metadata file if it exists
   */
  private void loadCacheMetaData(@NonNull Path cachePath, boolean overwriteMetaFile) {
    cacheFile = cachePath.toFile();
    String cacheName = cachePath.toFile().getName();
    String cacheDir = cachePath.toFile().getParent();
    String cacheMetadataFileName = cacheName.replaceFirst("\\.json$", "") + "_metadata.json";
    cacheMetadataFilePath =
        Path.of(Objects.requireNonNullElse(cacheDir, ""), cacheMetadataFileName);
    File cacheMetadataFile = cacheMetadataFilePath.toFile();

    BiConsumer<File, CacheMeta> writeCacheMetaData =
        (file, cacheObj) -> {
          writeJsonFs(file, cacheObj);
          wpCacheMeta = readJsonFs(file, CacheMeta.class);
          wpSiteEngineLogger.info(
              overwriteMetaFile
                  ? "Replaced cache metadata file"
                  : "Successfully created cache metadata.");
        };

    Consumer<File> loadCacheMetaData =
        file -> {
          wpCacheMeta = readJsonFs(file, CacheMeta.class);
          wpSiteEngineLogger.info("Loading cache metadata from: {}", file.getAbsolutePath());
        };

    if (cacheMetadataFile.exists() && cacheFile.exists()) {
      if (overwriteMetaFile) {
        writeCacheMetaData.accept(cacheMetadataFile, wpCacheMeta);
        return;
      }
      loadCacheMetaData.accept(cacheMetadataFile);
    } else if (cacheMetadataFile.length() == 0 && wpCacheMeta != null) {
      writeCacheMetaData.accept(cacheMetadataFile, wpCacheMeta);
    } else if (!cacheFile.exists()) {
      if (wpCacheMeta == null && cacheMetadataFile.exists()) {
        loadCacheMetaData.accept(cacheMetadataFile);
      }
    } else if (overwriteMetaFile && wpCacheMeta != null) {
      writeCacheMetaData.accept(cacheMetadataFile, wpCacheMeta);
    } else {
      throw new CacheMetaDataException("Failed to create cache metadata");
    }
  }

  /**
   * Creates a list of links to the WordPress REST API.
   *
   * @param totalPages the total number of pages
   * @param perPage the number of posts per page
   * @return a list of links to the WordPress REST API
   */
  @Unmodifiable
  @NonNull
  private List<String> linkListCreator(long totalPages, int perPage) {
    Map<WPQueryParam, String> queryParams = new EnumMap<>(WPQueryParam.class);
    return LongStream.range(1, totalPages + 1)
        .mapToObj(
            i -> {
              if (!queryParams.isEmpty()) queryParams.clear();
              queryParams.put(WPQueryParam.PAGE, String.valueOf(i));
              if (perPage > 0) queryParams.put(WPQueryParam.PER_PAGE, String.valueOf(perPage));
              return makeRequestURL(apiBasePath, queryParams, WPRestPath.POSTS);
            })
        .toList();
  }

  /**
   * Fetches the local cache file from the WordPress REST API.
   *
   * @param cachePath the path to the cache file
   * @param overwriteCache whether to overwrite the cache file if it exists
   * @throws IOException if an I/O error occurs
   */
  private void fetchCache(@NonNull Path cachePath, boolean overwriteCache) throws IOException {

    if (Objects.isNull(linkList) || linkList.isEmpty()) {
      try {
        updateCacheMeta();
        loadCacheMetaData(cachePath, true);
        linkList = linkListCreator(wpCacheMeta.totalPages(), DEFAULT_PER_PAGE);
      } catch (IOException ioEx) {
        wpSiteEngineLogger.warn(
            "Failed to gather WordPress post metadata. Check your connection", ioEx);
        wpSiteEngineLogger.warn("IOExeption caused by: ", ioEx.getCause());
        throw new CacheConstructionException(
            () -> "Failed to fetch site data for " + fullyQualifiedDomainName);
      } catch (InterruptedException intEx) {
        Thread currentThread = Thread.currentThread();
        wpSiteEngineLogger.debug(
            "Current Thread state: {} -> Interrupting after attempted cache creation",
            currentThread.getState());
        currentThread.interrupt();
        return;
      }
    }

    wpSiteEngineLogger.info("Processing cache links for {}", apiBasePath);
    ArrayNode wpJsonArray = fetchJsonCache(linkList, null, 0, 0, null, null);

    writeCacheFs(cachePath, wpJsonArray, overwriteCache, false);
    cacheFile = cachePath.toFile();
  }

  /**
   * Fetches the JSON cache from the WordPress REST API.
   *
   * @param listOfLinks the list of links to fetch
   * @param retryPred the predicate to retry on in case a specific condition is expected
   * @return the JSON cache as a single ArrayNode
   */
  private ArrayNode fetchJsonCache(
      @NonNull List<String> listOfLinks,
      @Nullable Predicate<ArrayNode> retryPred,
      int retryAttempts,
      int intervalTime,
      TimeUnit intervalUnit,
      Supplier<String> retryFailedMsg) {
    ObjectMapper mapper = JsonSupport.getMapper();
    BiFunction<HttpClient, String, CompletableFuture<ArrayNode>> procedureFunction =
        getFetchProcedure();
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
   * @return the fetch procedure BiFunction
   */
  @NonNull
  @Contract(pure = true)
  private BiFunction<HttpClient, String, CompletableFuture<ArrayNode>> getFetchProcedure() {
    Function<String, HttpRequest> requestFunction =
        link -> {
          wpSiteEngineLogger.debug("Processing link -> {} ", link);
          Optional<HttpRequest> request =
              ApiService.buildWpGetRequest(link, username, applicationPassword, wpSiteEngineLogger);
          return request.orElseThrow();
        };

    return (client, link) ->
        client
            .sendAsync(requestFunction.apply(link), BodyHandlers.ofString(StandardCharsets.UTF_8))
            .thenApply(HttpResponse::body)
            .thenApply(
                body -> {
                  try {
                    JsonNode node = JsonSupport.getMapper().readTree(body);
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
      @NonNull Path cachePath,
      @NonNull ArrayNode wpJsonArray,
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
   * @throws IOException if an I/O error occurs
   */
  public void fetchJsonCache(boolean overwriteCache) throws IOException {
    if (cachePath == null)
      throw new UnsupportedOperationException(
          "Unable to fetch cache without a cache path as local cache creation requires it in this class.");
    fetchCache(cachePath, overwriteCache);
  }

  /**
   * Fetches the local cache file from the WordPress REST API.
   *
   * @param cachePath the path to the cache file
   * @param overwriteCache whether to overwrite the cache file if it exists
   * @throws IOException if an I/O error occurs
   */
  public void fetchJsonCache(@NonNull Path cachePath, boolean overwriteCache) throws IOException {
    fetchCache(cachePath, overwriteCache);
  }

  /**
   * Creates or overwrites a cache file in the local classpath/filesystem that will contain the
   * local cache of a WordPress site. Overwrites must be specified by the user in any of the
   * overloaded methods in this class.
   *
   * @param cachePath the path to the cache file
   * @param overwriteCache whether to overwrite the cache file if it exists
   */
  private void createCacheFile(@NonNull Path cachePath, boolean overwriteCache) {
    TypedTrigger<Exception> exceptionLogging =
        ex ->
            wpSiteEngineLogger.debug(
                "Rethrowing {} caused by: {} as {} while trying to create a new cache file",
                ex.getClass().getSimpleName(),
                ex.getCause().getMessage(),
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
        updateCacheMeta();
      } catch (IOException ioEx) {
        exceptionLogging.activate(ioEx);
        throw new CacheConstructionException(cacheExMsg.apply(cachePathFile));
      } catch (InterruptedException intEx) {
        Thread.currentThread().interrupt();
        exceptionLogging.activate(intEx);
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
  public FileReader getCacheReader() throws IOException {
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
   * Synchronizes the cache file with the WordPress site.
   *
   * @return true if the cache was successfully synchronized, false if the cache is up-to-date.
   * @throws InterruptedException if the thread is interrupted
   */
  public boolean cacheSync() throws InterruptedException {
    Supplier<String> notFoundMsg = () -> "Cache file does not exist at + " + cachePath;
    TypedTrigger<Exception> ioExceptionLogging =
        ex ->
            wpSiteEngineLogger.debug(
                "Failed to read cache file: {} caused by: {}",
                ex.getMessage(),
                ex.getCause().getMessage());

    TypedTrigger<String> throwCacheException =
        exMsg -> {
          throw new CacheFileSystemException(() -> "Failed to read cache file: " + exMsg);
        };

    if (wpCacheMeta == null) loadCacheMetaData(cachePath, false);

    CacheMeta cacheMetaOld =
        new CacheMeta(
            wpCacheMeta.totalPages(),
            wpCacheMeta.totalPosts(),
            LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC));

    try {
      updateCacheMeta();
      loadCacheMetaData(cachePath, true);
    } catch (IOException ioEx) {
      ioExceptionLogging.activate(ioEx);
      throwCacheException.activate(ioEx.getMessage());
    }

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
      wpSiteEngineLogger.info("{} Cache is up-to-date", fullyQualifiedDomainName);
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
          long id1 = jsonNode1.get("id").asLong();
          long id2 = jsonNode2.get("id").asLong();
          return Long.compare(id1, id2);
        };

    long lastId =
        fromCache.valueStream().sorted(jsonNodeComparator).toList().getLast().get("id").asLong();

    Predicate<ArrayNode> testForLastElem =
        elem ->
            elem.valueStream().sorted(jsonNodeComparator).toList().getLast().get("id").asLong()
                > lastId;

    List<JsonNode> updatedPosts =
        fetchJsonCache(
                linkList,
                testForLastElem,
                3,
                2,
                TimeUnit.SECONDS,
                () -> "Failed to fetch new cache pages. Reached maximum retry attempts")
            .valueStream()
            .sorted(jsonNodeComparator.reversed())
            .limit(nodeDiff)
            .toList();

    fromCache.addAll(updatedPosts);

    try {
      writeCacheFs(cacheFile.toPath(), fromCache, true, true);
    } catch (IOException ioEx) {
      wpSiteEngineLogger.debug(
          "Caught {} caused by {}", ioEx.getClass().getSimpleName(), ioEx.getCause().getMessage());
      throw new CacheFileSystemException(() -> "Failed to write cache file: " + ioEx.getMessage());
    }
    return true;
  }

  public String getFullyQualifiedDomainName() {
    return fullyQualifiedDomainName;
  }

  public void setFullyQualifiedDomainName(String fullyQualifiedDomainName) {
    this.fullyQualifiedDomainName = fullyQualifiedDomainName;
  }

  public String getApiBasePath() {
    return apiBasePath;
  }

  public void setApiBasePath(String apiBasePath) {
    this.apiBasePath = apiBasePath;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getApplicationPassword() {
    return applicationPassword;
  }

  public void setApplicationPassword(String applicationPassword) {
    this.applicationPassword = applicationPassword;
  }

  public Set<Post> getCreatedPosts() {
    return createdPosts;
  }

  public void setCreatedPosts(Set<Post> createdPosts) {
    this.createdPosts = createdPosts;
  }

  public CacheMeta getWpCacheMeta() {
    return wpCacheMeta;
  }

  public void setWpCacheMeta(CacheMeta wpCacheMeta) {
    this.wpCacheMeta = wpCacheMeta;
  }

  public Path getCachePath() {
    return cachePath;
  }

  public void setCachePath(Path cachePath) {
    this.cachePath = cachePath;
  }

  public Path getCacheMetadataFilePath() {
    return cacheMetadataFilePath;
  }

  public void setCacheMetadataFilePath(Path cacheMetadataFilePath) {
    this.cacheMetadataFilePath = cacheMetadataFilePath;
  }

  public File getCacheFile() {
    return cacheFile;
  }

  public void setCacheFile(File cacheFile) {
    this.cacheFile = cacheFile;
  }

  public List<String> getLinkList() {
    return linkList;
  }

  public void setLinkList(List<String> linkList) {
    this.linkList = linkList;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    WPSiteEngine that = (WPSiteEngine) o;
    return Objects.equals(getFullyQualifiedDomainName(), that.getFullyQualifiedDomainName())
        && Objects.equals(getApiBasePath(), that.getApiBasePath())
        && Objects.equals(getUsername(), that.getUsername())
        && Objects.equals(getApplicationPassword(), that.getApplicationPassword())
        && Objects.equals(getWpCacheMeta(), that.getWpCacheMeta())
        && Objects.equals(getCachePath(), that.getCachePath())
        && Objects.equals(getCacheMetadataFilePath(), that.getCacheMetadataFilePath())
        && Objects.equals(getCacheFile(), that.getCacheFile());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        getFullyQualifiedDomainName(),
        getApiBasePath(),
        getUsername(),
        getApplicationPassword(),
        getWpCacheMeta(),
        getCachePath(),
        getCacheMetadataFilePath(),
        getCacheFile());
  }
}
