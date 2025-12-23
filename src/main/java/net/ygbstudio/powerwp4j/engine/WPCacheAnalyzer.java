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

import static net.ygbstudio.powerwp4j.utils.Helpers.zip;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.readJsonFs;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.ygbstudio.powerwp4j.base.FriendlyEnum;
import net.ygbstudio.powerwp4j.base.extension.CacheKeyEnum;
import net.ygbstudio.powerwp4j.base.extension.CacheSubKeyEnum;
import net.ygbstudio.powerwp4j.base.extension.ClassMarkerEnum;
import net.ygbstudio.powerwp4j.base.extension.ClassValueKeyEnum;
import net.ygbstudio.powerwp4j.exceptions.CacheFileSystemException;
import net.ygbstudio.powerwp4j.models.entities.WPCacheKeySnapshot;
import net.ygbstudio.powerwp4j.models.entities.WPClassGroup;
import net.ygbstudio.powerwp4j.models.entities.WPClassMapping;
import net.ygbstudio.powerwp4j.models.schema.WPCacheKey;
import net.ygbstudio.powerwp4j.models.schema.WPCacheSubKey;
import net.ygbstudio.powerwp4j.models.taxonomies.TaxonomyMarker;
import net.ygbstudio.powerwp4j.models.taxonomies.TaxonomyValues;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jspecify.annotations.NonNull;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;

/**
 * WPCacheAnalyzer is a class that provides methods for analyzing the local WordPress cache.
 *
 * <p>Note that these utilities only retrieve values from the local cache and do not make any
 * requests to the WordPress REST API, that said, you can expect that only items that are currently
 * in use be taken into account in the return values, and accuracy will depend on whether your cache
 * is up-to-date.
 *
 * @see WPCacheManager
 * @author Yoham Gabriel B.
 */
public class WPCacheAnalyzer {

  private ArrayNode inMemoryCache;

  public WPCacheAnalyzer() {}

  public WPCacheAnalyzer(@NonNull Path cachePath) {
    this.loadLocalCache(cachePath);
  }

  /**
   * Loads the local cache file from the filesystem.
   *
   * @param cachePath the path to the cache file
   * @throws CacheFileSystemException if the provided path does not point to an existing file
   */
  public void loadLocalCache(@NonNull Path cachePath) {
    File cacheFile;
    cacheFile = cachePath.toFile();
    if (!cacheFile.exists())
      throw new CacheFileSystemException(
          () ->
              String.format(
                  "Path %s does not point to an existing file", cacheFile.getAbsolutePath()));
    this.inMemoryCache = readJsonFs(cacheFile, ArrayNode.class);
  }

  /**
   * Low-level method that returns ArrayNodes from the in-memory local cache filtered by a specified
   * enum that follows the {@link FriendlyEnum} interface.
   *
   * @param friendlyEnum the enum representing the class value to filter by
   * @return a stream of ArrayNodes containing the filtered class values
   */
  @Internal
  private Stream<Object> getFriendlyEnumStream(FriendlyEnum friendlyEnum) {
    return inMemoryCache
        .valueStream()
        .filter(node -> node.has(friendlyEnum.value()))
        .map(item -> item.get(friendlyEnum.value()));
  }

  /**
   * Returns the ArrayNodes from the in-memory local cache filtered by a specified enum that follows
   * the {@link CacheKeyEnum} interface.
   *
   * @param cacheKey the enum representing the cache key to filter by
   * @return a stream of ArrayNodes containing the filtered cache keys
   */
  public Stream<JsonNode> getCacheKeyValueStream(CacheKeyEnum cacheKey) {
    return getFriendlyEnumStream(cacheKey).map(JsonNode.class::cast);
  }

  /**
   * Returns the ArrayNodes from the in-memory local cache filtered by a specified enum that follows
   * the {@link CacheKeyEnum} interface.
   *
   * @param cacheKey the enum representing the cache key to filter by
   * @return a stream of ArrayNodes containing the filtered cache keys
   */
  public Stream<ArrayNode> getCacheKeyArrayStream(CacheKeyEnum cacheKey) {
    return getFriendlyEnumStream(cacheKey).map(ArrayNode.class::cast);
  }

  /**
   * Returns the ArrayNodes from the in-memory local cache filtered by a specified enum that follows
   * the {@link ClassValueKeyEnum} interface.
   *
   * @param classValues the enum representing the class value to filter by
   * @return a stream of ArrayNodes containing the filtered class values
   */
  public Stream<ArrayNode> getClassValueStream(ClassValueKeyEnum classValues) {
    return getCacheKeyValueStream(classValues).map(ArrayNode.class::cast);
  }

  /**
   * Returns the count of unique class values from the in-memory local cache.
   *
   * @param classValues the enum representing the class value to count
   * @return the count of the class values
   */
  public long getClassValueCount(ClassValueKeyEnum classValues) {
    return getClassValueStream(classValues)
        .flatMap(array -> array.valueStream().map(JsonNode::asLong))
        .distinct()
        .count();
  }

  /**
   * Returns values extracted from the in-memory local cache filtered by a specified enum that
   * follows the {@link CacheKeyEnum} interface.
   *
   * @param cacheKey the enum representing the cache key to filter by
   * @param transformer the function used to transform the JsonNode to the desired type
   * @param <V> the type of the values in the returned set
   * @return a set of values extracted from the cache filtered by the specified cache key
   */
  public <V> Set<V> getCacheKeyValueSet(
      CacheKeyEnum cacheKey, @NonNull Function<? super JsonNode, ? extends V> transformer) {
    return getCacheKeyValueStream(cacheKey).map(transformer).collect(Collectors.toSet());
  }

  /**
   * Returns {@link WPCacheKeySnapshot} instances extracted from the in-memory local cache filtered
   * by a specified cache key. Each {@link WPCacheKeySnapshot} instance contains a cache key and a
   * map of cache subkeys and their corresponding values. The values are transformed by the
   * specified function.
   *
   * @param subKeyTransformer the function used to transform the JsonNode to the desired type {@code
   *     V}
   * @param cacheKey the enum representing the cache key to filter by
   * @param subKeys the cache subkeys to filter by
   * @param <V> the type of the values in the cache subkey map
   * @return a stream of {@link WPCacheKeySnapshot} instances extracted from the cache filtered by
   *     the specified cache key
   */
  public <V> Stream<WPCacheKeySnapshot<V>> getCacheSubKeySnapshotStream(
      @NonNull Function<? super JsonNode, ? extends V> subKeyTransformer,
      CacheKeyEnum cacheKey,
      CacheSubKeyEnum... subKeys) {
    Set<CacheSubKeyEnum> subKeySet = new HashSet<>(Arrays.asList(subKeys));
    return getCacheKeyValueStream(cacheKey)
        .filter(JsonNode::isObject)
        .map(
            jsonNode -> {
              Map<CacheSubKeyEnum, V> subKeyMap = new HashMap<>();
              jsonNode
                  .propertyStream()
                  .forEach(
                      entry -> {
                        for (CacheSubKeyEnum key : subKeySet) {
                          if (entry.getKey().equals(key.value())) {
                            subKeyMap.put(key, subKeyTransformer.apply(entry.getValue()));
                          }
                        }
                      });
              return new WPCacheKeySnapshot<>(cacheKey, subKeyMap);
            });
  }

  /**
   * Returns the count of unique cache keys from the in-memory local cache.
   *
   * @param cacheKey the enum representing the cache key to count
   * @return the count of the cache keys
   */
  public long getCacheKeyCount(CacheKeyEnum cacheKey) {
    return getCacheKeyValueStream(cacheKey).count();
  }

  /**
   * Returns the count of unique tags from the in-memory local cache.
   *
   * @return the count of the tags
   */
  public long getTagCount() {
    return getClassValueCount(TaxonomyValues.TAGS);
  }

  /**
   * Returns the count of unique categories from the in-memory local cache.
   *
   * @return the count of the categories
   */
  public long getCategoryCount() {
    return getClassValueCount(TaxonomyValues.CATEGORIES);
  }

  /**
   * Returns the count of unique posts from the in-memory local cache.
   *
   * @return the count of the posts
   */
  public long getPostCount() {
    return getCacheKeyCount(WPCacheKey.ID);
  }

  /**
   * Returns the count of unique slugs from the in-memory local cache.
   *
   * @return the count of the slugs
   */
  public long getSlugCount() {
    return getCacheKeyCount(WPCacheKey.SLUG);
  }

  /**
   * Returns unique slugs from the in-memory local cache.
   *
   * @return a set of unique slugs
   */
  public Set<String> getSlugs() {
    return getCacheKeyValueSet(WPCacheKey.SLUG, JsonNode::asString);
  }

  /**
   * Returns unique links from the in-memory local cache.
   *
   * @return a set of unique links
   */
  public Set<String> getLinks() {
    return getCacheKeyValueSet(WPCacheKey.LINK, JsonNode::asString);
  }

  /**
   * Returns class list elements from the in-memory local cache that match the provided filter.
   *
   * @param filterClassListElems the predicate to filter the class list elements
   * @return a stream of class list elements that match the provided filter
   */
  public Stream<String> getClassListStream(Predicate<? super String> filterClassListElems) {
    return getCacheKeyArrayStream(WPCacheKey.CLASS_LIST)
        .flatMap(array -> array.valueStream().map(JsonNode::asString))
        .filter(filterClassListElems);
  }

  /**
   * Returns class list elements from the in-memory local cache that match the provided class marker
   * enum.
   *
   * @param classMarker the class marker enum to filter the class list elements
   * @return a stream of class list elements that match the provided class marker enum
   */
  public Stream<String> getClassListStream(ClassMarkerEnum classMarker) {
    return getClassListStream(classItem -> classItem.contains(classMarker.value()));
  }

  /**
   * Returns unique categories from the in-memory local cache.
   *
   * <p>The categories are filtered by the {@link TaxonomyMarker#CATEGORY} enum and their prefix is
   * removed. Additionally, any non-alphanumeric characters are replaced with spaces.
   *
   * <p>If you need custom processing for your categories, you can use the {@link
   * #getClassListStream(Predicate)} method to apply a function and collect the results based on
   * your needs.
   *
   * @return a set of unique categories
   */
  public Set<String> getCategories() {
    return getClassListStream(TaxonomyMarker.CATEGORY)
        .map(
            category ->
                category.replaceFirst("^category-", "").replaceAll("[^a-zA-Z0-9]", " ").trim())
        .collect(Collectors.toSet());
  }

  /**
   * Returns unique tags from the in-memory local cache.
   *
   * <p>The tags are filtered by the {@link TaxonomyMarker#TAG} enum and their prefix is removed.
   * Additionally, any non-alphanumeric characters are replaced with spaces.
   *
   * <p>If you need custom processing for your categories, you can use the {@link
   * #getClassListStream(ClassMarkerEnum)} method to apply a function and collect the results based
   * on your needs.
   *
   * @return a set of unique tags
   */
  public Set<String> getTags() {
    return getClassListStream(TaxonomyMarker.TAG)
        .map(tag -> tag.replaceFirst("^tag-", "").replaceAll("[^a-zA-Z0-9]", " ").trim())
        .collect(Collectors.toSet());
  }

  /**
   * Returns {@link WPCacheKeySnapshot} instances extracted from the in-memory local cache filtered
   * by the cache key {@link WPCacheKey}.
   *
   * <p>Each {@link WPCacheKeySnapshot} instance contains the cache key and a map of {@link
   * WPCacheSubKey} as strings (in this method) and their corresponding values.
   *
   * @return a stream of {@link WPCacheKeySnapshot} instances extracted from the cache filtered by
   *     the specified cache key
   */
  public Set<WPCacheKeySnapshot<String>> getContents() {
    return getCacheSubKeySnapshotStream(
            JsonNode::asString, WPCacheKey.CONTENT, WPCacheSubKey.RENDERED, WPCacheSubKey.PROTECTED)
        .collect(Collectors.toSet());
  }

  /**
   * Returns {@link WPCacheKeySnapshot} instances extracted from the in-memory local cache filtered
   * by the cache key {@link WPCacheKey} enum and containing the {@code "rendered"} and {@code
   * "protected"} represented by the {@link WPCacheSubKey} enum.
   *
   * <p>Each {@link WPCacheKeySnapshot} instances extracted from the in-memory local instance
   * contains the cache key and a map of {@link WPCacheSubKey} as strings (in this method) and their
   * corresponding values.
   *
   * @return a set of {@link WPCacheKeySnapshot} instances extracted from the cache filtered by the
   *     specified cache key
   */
  public Set<WPCacheKeySnapshot<String>> getExcerpts() {
    return getCacheSubKeySnapshotStream(
            JsonNode::asString, WPCacheKey.EXCERPT, WPCacheSubKey.RENDERED, WPCacheSubKey.PROTECTED)
        .collect(Collectors.toSet());
  }

  /**
   * Returns {@link WPCacheKeySnapshot} instances extracted from the in-memory local cache filtered
   * by the cache key {@link WPCacheKey#GUID} enum and containing the {@code "rendered"} represented
   * by the {@link WPCacheSubKey} enum.
   *
   * <p>Each {@link WPCacheKeySnapshot} instance contains the cache key and a map of the present
   * {@link WPCacheSubKey} as strings (in this method) and their corresponding values.
   *
   * @return a set of {@link WPCacheKeySnapshot} instances extracted from the cache filtered by the
   *     specified cache key
   */
  public Set<WPCacheKeySnapshot<String>> getGuids() {
    return getCacheSubKeySnapshotStream(JsonNode::asString, WPCacheKey.GUID, WPCacheSubKey.RENDERED)
        .collect(Collectors.toSet());
  }

  /**
   * Returns {@link Map.Entry} instances extracted from the in-memory local cache filtered by the
   * class marker {@link TaxonomyMarker#TAG} and the class value {@link TaxonomyValues#TAGS}. Each
   * entry contains a tag name and the corresponding value assigned to the term by the WordPress
   * backend.
   *
   * <p><em>Tip: If you do not wish to transform/clean your taxonomy (class) strings, you can pass
   * the {@link UnaryOperator#identity()} to ignore the transformation step.
   *
   * @return a set of {@link Map.Entry} instances extracted from the cache filtered by the specified
   *     cache key
   */
  public Set<Map.Entry<String, Long>> mapWPClassId(
      UnaryOperator<String> transformClassText,
      ClassMarkerEnum classMarker,
      ClassValueEnum classValue) {
    List<String> classListElems = getClassListStream(classMarker).map(transformClassText).toList();
    List<Long> tagValues =
        getClassValueStream(classValue)
            .flatMap(ArrayNode::valueStream)
            .map(JsonNode::asLong)
            .toList();
    return zip(classListElems, tagValues).collect(Collectors.toSet());
  }
}
