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

import static net.ygbstudio.powerwp4j.utils.JsonSupport.readJsonFs;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.ygbstudio.powerwp4j.base.FriendlyEnum;
import net.ygbstudio.powerwp4j.base.extension.CacheKeyEnum;
import net.ygbstudio.powerwp4j.base.extension.ClassMarkerEnum;
import net.ygbstudio.powerwp4j.base.extension.ClassValueEnum;
import net.ygbstudio.powerwp4j.exceptions.CacheFileSystemException;
import net.ygbstudio.powerwp4j.models.schema.WPCacheKey;
import net.ygbstudio.powerwp4j.models.taxonomies.TaxonomyMarker;
import net.ygbstudio.powerwp4j.models.taxonomies.TaxonomyValues;
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
 * @see WPSiteEngine
 * @author Yoham Gabriel B.
 */
public class WPCacheAnalyzer {

  private File cacheFile;
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
    cacheFile = cachePath.toFile();
    if (!cacheFile.exists())
      throw new CacheFileSystemException(
          () ->
              String.format(
                  "Path %s does not point to an existing file", cacheFile.getAbsolutePath()));
    this.inMemoryCache = readJsonFs(cacheFile, ArrayNode.class);
  }

  /**
   * Low-level method that returns a stream of ArrayNodes from the in-memory local cache filtered by
   * a specified enum that follows the {@link FriendlyEnum} interface.
   *
   * @param friendlyEnum the enum representing the class value to filter by
   * @return a stream of ArrayNodes containing the filtered class values
   */
  private Stream<Object> getFriendlyEnumStream(FriendlyEnum friendlyEnum) {
    return inMemoryCache
        .valueStream()
        .filter(node -> node.has(friendlyEnum.toString()))
        .map(item -> item.get(friendlyEnum.toString()));
  }

  /**
   * Returns a stream of ArrayNodes from the in-memory local cache filtered by a specified enum that
   * follows the {@link CacheKeyEnum} interface.
   *
   * @param cacheKey the enum representing the cache key to filter by
   * @return a stream of ArrayNodes containing the filtered cache keys
   */
  public Stream<JsonNode> getCacheKeyValueStream(CacheKeyEnum cacheKey) {
    return getFriendlyEnumStream(cacheKey).map(JsonNode.class::cast);
  }

  /**
   * Returns a stream of ArrayNodes from the in-memory local cache filtered by a specified enum that
   * follows the {@link CacheKeyEnum} interface.
   *
   * @param cacheKey the enum representing the cache key to filter by
   * @return a stream of ArrayNodes containing the filtered cache keys
   */
  public Stream<ArrayNode> getCacheKeyArrayStream(CacheKeyEnum cacheKey) {
    return getFriendlyEnumStream(cacheKey).map(ArrayNode.class::cast);
  }

  /**
   * Returns a stream of ArrayNodes from the in-memory local cache filtered by a specified enum that
   * follows the {@link ClassValueEnum} interface.
   *
   * @param classValues the enum representing the class value to filter by
   * @return a stream of ArrayNodes containing the filtered class values
   */
  public Stream<ArrayNode> getClassValueStream(ClassValueEnum classValues) {
    return getFriendlyEnumStream(classValues).map(ArrayNode.class::cast);
  }

  /**
   * Returns the count of unique class values from the in-memory local cache.
   *
   * @param classValues the enum representing the class value to count
   * @return the count of the class values
   */
  public long getClassValueCount(ClassValueEnum classValues) {
    return getClassValueStream(classValues)
        .flatMap(array -> array.valueStream().map(JsonNode::asLong))
        .distinct()
        .count();
  }

  /**
   * Returns a set of values extracted from the in-memory local cache filtered by a specified enum
   * that follows the {@link CacheKeyEnum} interface.
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
   * Returns a set of unique slugs from the in-memory local cache.
   *
   * @return a set of unique slugs
   */
  public Set<String> getSlugs() {
    return getCacheKeyValueSet(WPCacheKey.SLUG, JsonNode::asString);
  }

  /**
   * Returns a set of unique links from the in-memory local cache.
   *
   * @return a set of unique links
   */
  public Set<String> getLinks() {
    return getCacheKeyValueSet(WPCacheKey.LINK, JsonNode::asString);
  }

  /**
   * Returns a stream of class list elements from the in-memory local cache that match the provided
   * filter.
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
   * Returns a stream of class list elements from the in-memory local cache that match the provided
   * class marker enum.
   *
   * @param classMarker the class marker enum to filter the class list elements
   * @return a stream of class list elements that match the provided class marker enum
   */
  public Stream<String> getClassListStream(ClassMarkerEnum classMarker) {
    return getClassListStream(classItem -> classItem.contains(classMarker.toString()));
  }

  /**
   * Returns a set of unique categories from the in-memory local cache.
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
   * Returns a set of unique tags from the in-memory local cache.
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

  public File getCacheFile() {
    return cacheFile;
  }

  public void setCacheFile(File cacheFile) {
    this.cacheFile = cacheFile;
  }

  public ArrayNode getInMemoryCache() {
    return inMemoryCache;
  }

  public void setInMemoryCache(ArrayNode inMemoryCache) {
    this.inMemoryCache = inMemoryCache;
  }
}
