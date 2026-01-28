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

package net.ygbstudio.powerwp4j.builders;

import java.util.List;
import java.util.Set;
import net.ygbstudio.powerwp4j.base.extension.builders.AbstractPayloadNodeBuilder;
import net.ygbstudio.powerwp4j.base.extension.enums.CommentStatusEnum;
import net.ygbstudio.powerwp4j.base.extension.enums.PostFormatEnum;
import net.ygbstudio.powerwp4j.base.extension.enums.PostStatusEnum;
import net.ygbstudio.powerwp4j.base.extension.enums.PostTypeEnum;
import net.ygbstudio.powerwp4j.models.entities.WPClassMapping;
import net.ygbstudio.powerwp4j.models.schema.WPCacheKey;
import net.ygbstudio.powerwp4j.models.taxonomies.TaxonomyValues;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This class is a specialized builder for constructing JSON payloads in the format used by the
 * WordPress REST API. It provides a fluent interface for creating JSON payloads by adding
 * properties to the internal {@code ObjectNode}.
 *
 * <p>This class is parameterized with the type of the concrete subclass. This allows subclasses to
 * define their own type while still being able to use the common functionality provided by this
 * class.
 *
 * <p>This class is an extension of {@link AbstractPayloadNodeBuilder} and provides additional
 * methods for setting properties specific to the WordPress REST API.
 */
public class WPPayloadNodeBuilder extends AbstractPayloadNodeBuilder<WPPayloadNodeBuilder> {

  private WPPayloadNodeBuilder() {}

  /**
   * Returns a new instance of the payload node builder.
   *
   * @return a new instance of the payload node builder
   */
  @Contract(" -> new")
  public static @NotNull WPPayloadNodeBuilder builder() {
    // .clear() Initialises the internal ObjectNode that will be used for
    // construction
    return new WPPayloadNodeBuilder().clear();
  }

  /**
   * Sets the author.
   *
   * @param authorInt the author to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder author(int authorInt) {
    return add(WPCacheKey.AUTHOR, authorInt);
  }

  /**
   * Sets the post status.
   *
   * @param status the post status to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder status(PostStatusEnum status) {
    return add(WPCacheKey.STATUS, status);
  }

  /**
   * Sets the post type.
   *
   * @param type the post type to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder type(PostTypeEnum type) {
    return add(WPCacheKey.TYPE, type);
  }

  /**
   * Sets the post format.
   *
   * @param format the post format to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder format(PostFormatEnum format) {
    return add(WPCacheKey.FORMAT, format);
  }

  /**
   * Sets the comment status.
   *
   * @param status the comment status to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder commentStatus(CommentStatusEnum status) {
    return add(WPCacheKey.COMMENT_STATUS, status);
  }

  /**
   * Sets the categories.
   *
   * @param categories the categories to set
   * @param <T> the numeric type of the categories
   * @return the payload node builder
   */
  public <T extends Number> WPPayloadNodeBuilder categories(@NotNull List<T> categories) {
    return add(TaxonomyValues.CATEGORIES, categories);
  }

  /**
   * Sets the tags.
   *
   * @param tags the tags to set
   * @param <T> the numeric type of the tags
   * @return the payload node builder
   */
  public <T extends Number> WPPayloadNodeBuilder tags(@NotNull List<T> tags) {
    return add(TaxonomyValues.TAGS, tags);
  }

  /**
   * Sets the categories using a set of {@link WPClassMapping} objects.
   *
   * @param categoriesMappingSet the set of {@link WPClassMapping} objects representing the
   *     categories
   * @param <T> the type of the key in each class mapping object
   * @param <U> the numeric type of the category values in the {@link WPClassMapping} objects
   * @return the payload node builder
   */
  public <T, U extends Number> WPPayloadNodeBuilder categories(
      @NotNull Set<WPClassMapping<T, U>> categoriesMappingSet) {
    return add(TaxonomyValues.CATEGORIES, WPClassMapping.toNumberSet(categoriesMappingSet));
  }

  /**
   * Sets the tags using a set of {@link WPClassMapping} objects.
   *
   * @param tagsMappingSet the set of {@link WPClassMapping} objects representing the tags
   * @param <T> the type of the key in each class mapping object
   * @param <U> the numeric type of the tag values in the {@link WPClassMapping} objects
   * @return the payload node builder
   */
  public <T, U extends Number> WPPayloadNodeBuilder tags(
      @NotNull Set<WPClassMapping<T, U>> tagsMappingSet) {
    return add(TaxonomyValues.TAGS, WPClassMapping.toNumberSet(tagsMappingSet));
  }

  /**
   * Sets the slug.
   *
   * @param slug the slug to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder slug(String slug) {
    return add(WPCacheKey.SLUG, slug);
  }

  /**
   * Sets the description.
   *
   * @param description the description to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder description(String description) {
    return add(WPCacheKey.DESCRIPTION, description);
  }

  /**
   * Sets the title.
   *
   * @param title the title to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder title(String title) {
    return add(WPCacheKey.TITLE, title);
  }

  /**
   * Sets the content.
   *
   * @param content the content to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder content(String content) {
    return add(WPCacheKey.CONTENT, content);
  }

  /**
   * Sets the excerpt.
   *
   * @param excerpt the excerpt to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder excerpt(String excerpt) {
    return add(WPCacheKey.EXCERPT, excerpt);
  }

  /**
   * Sets the sticky flag.
   *
   * @param sticky the sticky flag to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder sticky(boolean sticky) {
    return add(WPCacheKey.STICKY, sticky);
  }

  /**
   * Sets the featured media.
   *
   * @param featuredMediaId the featured media to set
   * @return the payload node builder
   */
  public WPPayloadNodeBuilder featuredMedia(int featuredMediaId) {
    return add(WPCacheKey.FEATURED_MEDIA, featuredMediaId);
  }
}
