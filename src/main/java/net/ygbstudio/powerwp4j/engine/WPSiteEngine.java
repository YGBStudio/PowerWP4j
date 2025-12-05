package net.ygbstudio.powerwp4j.engine;

import static net.ygbstudio.powerwp4j.services.ApiService.makeRequestURL;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.JSON_INDENT;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.JSON_INDENT_FACTOR;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.readJsonFs;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.writeJsonFs;

import java.io.File;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.LongStream;
import net.ygbstudio.powerwp4j.exceptions.CacheConstructionException;
import net.ygbstudio.powerwp4j.exceptions.CacheMetaDataException;
import net.ygbstudio.powerwp4j.models.entities.CacheMeta;
import net.ygbstudio.powerwp4j.models.entities.Post;
import net.ygbstudio.powerwp4j.models.schema.WPQueryParam;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.services.ApiService;
import org.json.JSONArray;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WPSiteEngine is the main class for interacting with a WordPress site. It provides methods for
 * fetching data from the WordPress REST API and caching it locally using JSON.
 *
 * @author Yoham Gabriel @ YGBStudio
 */
public class WPSiteEngine {
  private static Logger wpSiteEngineLogger = LoggerFactory.getLogger(WPSiteEngine.class);
  private static final int DEFAULT_PER_PAGE = 10;

  private String fullyQualifiedDomainName;
  private String apiBasePath;
  private String username;
  private String applicationPassword;
  private Set<Post> createdPosts = new HashSet<>();
  private CacheMeta wpCacheMeta;
  private Path cachePath;
  private Path cacheMetadataFilePath;
  private List<String> linkList;

  public WPSiteEngine(
      @NonNull String fullyQualifiedDomainName,
      @NonNull String username,
      @NonNull String applicationPassword,
      @Nullable Path cachePath) {
    this.fullyQualifiedDomainName = fullyQualifiedDomainName.replaceAll("^https?:+//\\b", "");
    this.username = username;
    this.applicationPassword = applicationPassword;
    this.cachePath = cachePath;
    apiBasePath = String.format("https://%s/wp-json/wp/v2", this.fullyQualifiedDomainName);
    wpSiteEngineLogger.info("Initialized WPSiteEngine for site: {}", fullyQualifiedDomainName);
    wpSiteEngineLogger.info("API Base Path set to: {}", apiBasePath);
  }

  public WPSiteEngine(
      String fullyQualifiedDomainName, String username, String applicationPassword) {
    this(fullyQualifiedDomainName, username, applicationPassword, null);
  }

  public Optional<HttpResponse<String>> connectWP(
      Map<WPQueryParam, String> queryParams, WPRestPath pathParam)
      throws IOException, InterruptedException {
    String url = makeRequestURL(apiBasePath, queryParams, pathParam);
    return ApiService.connectWP(url, username, applicationPassword, wpSiteEngineLogger);
  }

  private void populateLinkList(int perPage) throws IOException, InterruptedException {
    // Minimal request to gather cache metadata
    Optional<HttpResponse<String>> headersRequest =
        connectWP(Map.of(WPQueryParam.PER_PAGE, String.valueOf(perPage)), WPRestPath.POSTS);
    if (headersRequest.isPresent()) {
      Map<String, List<String>> headers = headersRequest.get().headers().map();
      long wpTotal = Long.parseLong(headers.get("x-wp-total").getFirst());
      long wpTotalPages = Long.parseLong(headers.get("x-wp-totalpages").getFirst());
      wpCacheMeta =
          new CacheMeta(wpTotalPages, wpTotal, LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC));
      linkList = linkListCreator(wpTotalPages, DEFAULT_PER_PAGE);
    }
  }

  @NonNull
  private List<String> linkListCreator(long totalPages, int perPage) {
    return LongStream.range(1, totalPages + 1)
        .mapToObj(
            i ->
                makeRequestURL(
                    apiBasePath,
                    Map.of(
                        WPQueryParam.PAGE,
                        String.valueOf(i),
                        WPQueryParam.PER_PAGE,
                        String.valueOf(perPage)),
                    WPRestPath.POSTS))
        .toList();
  }

  private void fetchCache(@NonNull Path cachePath, boolean overwriteCache) throws IOException {

    if (Objects.isNull(linkList) || linkList.isEmpty()) {
      try {
        populateLinkList(DEFAULT_PER_PAGE);
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

    Function<String, HttpRequest> requestFunction =
        link -> {
          wpSiteEngineLogger.info("Processing cache links for {}", apiBasePath);
          wpSiteEngineLogger.debug("Processing link -> {} ", link);
          Optional<HttpRequest> request =
              ApiService.buildWpGetRequest(link, username, applicationPassword, wpSiteEngineLogger);
          return request.orElseThrow();
        };

    ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    JSONArray wpJsonArray;
    try (HttpClient client =
        HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).executor(executor).build()) {
      wpJsonArray =
          linkList.parallelStream()
              .map(
                  link ->
                      client
                          .sendAsync(
                              requestFunction.apply(link),
                              BodyHandlers.ofString(StandardCharsets.UTF_8))
                          .thenApply(HttpResponse::body)
                          .thenApply(JSONArray::new)
                          .thenApply(JSONArray::toList)
                          .exceptionally(
                              ex -> {
                                wpSiteEngineLogger.debug(
                                    "Failed proceding of link {} due to {} cause: {}",
                                    link,
                                    ex.getClass().getSimpleName(),
                                    ex.getCause().getMessage());
                                return null;
                              }))
              .map(CompletableFuture::join)
              .filter(Objects::nonNull)
              .flatMap(List::stream)
              .collect(JSONArray::new, JSONArray::put, JSONArray::putAll);
    } finally {
      executor.shutdown();
      try {
        if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
          executor.shutdownNow();
        }
      } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }

    createCacheFile(cachePath, overwriteCache);
    loadCacheMetaData(cachePath);
    try (FileWriter writer = new FileWriter(cachePath.toFile())) {
      wpJsonArray.write(writer, JSON_INDENT_FACTOR, JSON_INDENT);
    }
  }

  public void fetchJsonCache(boolean overwriteCache) throws IOException {
    if (cachePath == null)
      throw new UnsupportedOperationException(
          "Unable to fetch cache without a cache path. Cache creation requires instantiating this class with a cache path.");
    fetchCache(cachePath, overwriteCache);
  }

  public void fetchJsonCache(@NonNull Path cachePath, boolean overwriteCache) throws IOException {
    fetchCache(cachePath, overwriteCache);
  }

  private void createCacheFile(@NonNull Path cachePath, boolean overwriteCache) {
    File cacheFile = cachePath.toFile();
    if (cacheFile.exists() && overwriteCache) {
      try {
        Files.delete(cachePath);
      } catch (IOException ioEx) {
        wpSiteEngineLogger.debug(
            "Rethrowing {} as {} while trying to overwrite a cache file",
            ioEx.getClass().getSimpleName(),
            CacheConstructionException.class);
        throw new CacheConstructionException(
            () -> String.format("Unable to create a file at %s", cacheFile.getAbsolutePath()));
      }
    } else if (!cacheFile.exists()) {
      try {
        Files.createFile(cachePath);
      } catch (IOException ioEx) {
        wpSiteEngineLogger.debug(
            "Rethrowing {} as {} while trying to create a new cache file",
            ioEx.getClass().getSimpleName(),
            CacheConstructionException.class);
        throw new CacheConstructionException(
            () -> String.format("Unable to create a file at %s", cacheFile.getAbsolutePath()));
      }
    }
  }

  private void loadCacheMetaData(@NonNull Path cachePath) {
    String cacheName = cachePath.getFileName().toString();
    String cacheDir = cachePath.getParent().toString();
    String cacheMetadataFileName = cacheName + "_metadata.json";
    cacheMetadataFilePath = Path.of(cacheDir, cacheMetadataFileName);

    if (Files.exists(cacheMetadataFilePath)) {
      wpCacheMeta = readJsonFs(cacheMetadataFilePath.toFile(), CacheMeta.class);
      wpSiteEngineLogger.info("Loading cache metadata from file: {}", cacheMetadataFilePath);
    } else if (cacheMetadataFilePath.toFile().length() == 0) {
      wpSiteEngineLogger.warn("Cache metadata file is empty.");
      if (Files.exists(cachePath)) {
        writeJsonFs(cacheMetadataFilePath.toFile(), wpCacheMeta);
        wpCacheMeta = readJsonFs(cacheMetadataFilePath.toFile(), CacheMeta.class);
        wpSiteEngineLogger.info("Successfully created cache metadata.");
      } else {
        wpSiteEngineLogger.warn(
            "Cache metadata can't be created since the associated cache file does not exist.");
        throw new CacheMetaDataException(
            () -> "Cache file does not exist at : " + cacheMetadataFilePath);
      }
    }
  }

  public static void setWpSiteEngineLogger(Logger wpSiteEngineLogger) {
    WPSiteEngine.wpSiteEngineLogger = wpSiteEngineLogger;
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

  public Path getCachePath() {
    return cachePath;
  }

  public void setCachePath(Path cachePath) {
    this.cachePath = cachePath;
  }

  public static Logger getWpSiteEngineLogger() {
    return wpSiteEngineLogger;
  }

  public CacheMeta getWpCacheMeta() {
    return wpCacheMeta;
  }

  public void setWpCacheMeta(CacheMeta wpCacheMeta) {
    this.wpCacheMeta = wpCacheMeta;
  }

  public List<String> getLinkList() {
    return linkList;
  }

  public void setLinkList(List<String> linkList) {
    this.linkList = linkList;
  }
}
