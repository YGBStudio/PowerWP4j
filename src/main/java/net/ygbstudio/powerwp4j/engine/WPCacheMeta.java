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

import static net.ygbstudio.powerwp4j.utils.JsonSupport.readJsonFs;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.writeJsonFs;

import java.io.File;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.ygbstudio.powerwp4j.exceptions.CacheMetaDataException;
import net.ygbstudio.powerwp4j.models.entities.WPSiteInfo;
import net.ygbstudio.powerwp4j.models.schema.WPQueryParam;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.services.HttpRequestService;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WPCacheMeta is a record that represents the metadata of the cache.
 *
 * @author Yoham Gabriel B. @ YGBStudio
 */
@NullMarked
public record WPCacheMeta(long totalPages, long totalPosts, @Nullable LocalDate lastUpdated) {

  private static final Logger wpCacheMetaLogger = LoggerFactory.getLogger(WPCacheMeta.class);

  /**
   * Returns the metadata file path for a given cache file path.
   *
   * @param cachePath the path to the cache file
   * @return the metadata file path
   */
  private static Path getMetaPath(Path cachePath) {
    String cacheName = cachePath.toFile().getName();
    String cacheDir = cachePath.toFile().getParent();
    String cacheMetadataFileName = cacheName.replaceFirst("\\.json$", "") + "_metadata.json";
    return Path.of(Objects.requireNonNullElse(cacheDir, ""), cacheMetadataFileName);
  }

  /**
   * Reads the metadata file from the given cache file path and returns an optional WPCacheMeta
   * object.
   *
   * @param cachePath the path to the cache file
   * @return an optional WPCacheMeta object
   */
  public static Optional<WPCacheMeta> from(Path cachePath) {
    Path cacheMetadataFilePath = getMetaPath(cachePath);
    Function<File, WPCacheMeta> loadCacheMetaData =
        file -> {
          wpCacheMetaLogger.info("Loading cache metadata from: {}", file.getAbsolutePath());
          return readJsonFs(file, WPCacheMeta.class);
        };

    if (Files.exists(cacheMetadataFilePath) && Files.exists(cachePath)) {
      return Optional.of(loadCacheMetaData.apply(cacheMetadataFilePath.toFile()));
    }
    return Optional.empty();
  }

  /**
   * Writes the WPCacheMeta object to the metadata file and returns a boolean indicating if the
   * metadata file was updated.
   *
   * @param metaPath the path to the metadata file
   * @param cacheMetaObj the WPCacheMeta object to write
   * @param overwriteMetadata a boolean indicating if the metadata file should be overwritten
   * @return a boolean indicating if the metadata file was updated
   */
  public static boolean writeCacheMetadata(
      Path metaPath, WPCacheMeta cacheMetaObj, boolean overwriteMetadata) {
    ReentrantLock metaLock = new ReentrantLock();

    // Used for lambda capture and thread safety
    AtomicBoolean updated = new AtomicBoolean(false);

    BiConsumer<Path, WPCacheMeta> writeJsonFsConsumer =
        (path, meta) -> {
          metaLock.lock();
          try {
            writeJsonFs(path.toFile(), meta);
            updated.set(true);
          } finally {
            metaLock.unlock();
          }
        };

    if (Files.exists(metaPath) && overwriteMetadata) {
      writeJsonFsConsumer.accept(metaPath, cacheMetaObj);
    } else if (!Files.exists(metaPath)) {
      writeJsonFsConsumer.accept(metaPath, cacheMetaObj);
    }

    if (updated.get())
      wpCacheMetaLogger.info(
          "{}",
          overwriteMetadata && updated.get()
              ? "Replaced cache metadata file"
              : "Successfully created cache metadata.");

    return updated.get();
  }

  /**
   * Updates the cache metadata by making a request to the WordPress REST API and returns an
   * optional WPCacheMeta object.
   *
   * @param siteInfo the WPSiteInfo object containing the necessary information to make the request
   * @param cachePath the path to the cache file
   * @param ignoreSSLHandshakeException a boolean indicating if SSL Handshake Exception should be
   *     ignored
   * @return an optional WPCacheMeta object
   */
  public static Optional<WPCacheMeta> updateCacheMeta(
      WPSiteInfo siteInfo, Path cachePath, boolean ignoreSSLHandshakeException) {
    int defaultPerPage = 10;
    String requestUrl =
        HttpRequestService.makeRequestURL(
            siteInfo.apiBaseUrl(),
            Map.of(
                WPQueryParam.PAGE,
                String.valueOf(1),
                WPQueryParam.PER_PAGE,
                String.valueOf(defaultPerPage)),
            WPRestPath.POSTS);
    HttpRequest request =
        HttpRequestService.buildWpGetRequest(
            requestUrl, siteInfo.wpUser(), siteInfo.wpAppPass(), wpCacheMetaLogger);
    Optional<HttpResponse<String>> response =
        HttpRequestService.clientSend(request, wpCacheMetaLogger, ignoreSSLHandshakeException);
    if (response.isEmpty()) return Optional.empty();
    Map<String, List<String>> headers = response.get().headers().map();
    long wpTotal;
    long wpTotalPages;
    try {
      wpTotal = Long.parseLong(headers.get("x-wp-total").getFirst());
      wpTotalPages = Long.parseLong(headers.get("x-wp-totalpages").getFirst());
    } catch (NullPointerException e) {
      throw new CacheMetaDataException(
          "Unable to update the cache metadata - incomplete, non-exposed or blocked response headers");
    }
    WPCacheMeta newWPCacheMeta =
        new WPCacheMeta(wpTotalPages, wpTotal, LocalDate.ofInstant(Instant.now(), ZoneOffset.UTC));
    boolean isUpdated =
        WPCacheMeta.writeCacheMetadata(getMetaPath(cachePath), newWPCacheMeta, true);
    if (isUpdated) return Optional.of(newWPCacheMeta);
    return Optional.empty();
  }
}
