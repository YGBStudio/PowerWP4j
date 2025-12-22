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

package net.ygbstudio.powerwp4j.base;

import java.util.Collections;
import java.util.List;
import net.ygbstudio.powerwp4j.base.extension.CommentStatusEnum;
import net.ygbstudio.powerwp4j.base.extension.PostFormatEnum;
import net.ygbstudio.powerwp4j.base.extension.PostStatusEnum;
import net.ygbstudio.powerwp4j.base.extension.PostTypeEnum;
import net.ygbstudio.powerwp4j.services.HttpRequestService;
import org.jspecify.annotations.NonNull;
import tools.jackson.databind.JsonNode;

/**
 * Abstract class for payload builders that can be used to simplify the process of building payloads
 * for the WordPress REST API. From this class, you can create your own payload builders that can be
 * used to interact with the methods provided by the {@link HttpRequestService} class.
 *
 * <p>Methods that return the payload builder are chainable and inherited by all subclasses, so that
 * builders can relay on the same behaviour or specialize it for their specific needs.
 *
 * <p>Note that it is possible to reuse the same builder in cases where your flow requires it. To do
 * that, call {@link AbstractPayloadBuilder#clear()} before building a new payload. The {@code
 * clear} operation is chainable so you can start your builder with it and chain the elements you
 * need after a clean start.
 *
 * @see FriendlyEnum
 * @author Yoham Gabriel @ YGB Studio
 */
public abstract class AbstractPayloadBuilder<T extends AbstractPayloadBuilder<T>> {
  protected Integer author;
  protected String status;
  protected String type;
  protected String format;
  protected String commentStatus;
  protected List<Integer> categories;
  protected List<Integer> tags;
  protected String slug;
  protected String name;
  protected String description;
  protected String title;
  protected String content;
  protected String excerpt;
  protected String password;
  protected Boolean sticky;
  protected Integer featuredMedia;

  /**
   * Returns the current instance of the payload builder. This is a protected method used for
   * chaining method calls.
   *
   * <p>The cast to type T is safe because the method is only called within the class hierarchy and
   * this class is parameterized with the type of the subclass.
   *
   * @return the current instance of the payload builder
   */
  @SuppressWarnings("unchecked")
  protected T self() {
    return (T) this;
  }

  /**
   * Sets the post status.
   *
   * @param postStatus the post status to set
   * @return the payload builder
   */
  public T status(@NonNull PostStatusEnum postStatus) {
    this.status = postStatus.toString();
    return self();
  }

  /**
   * Sets the post type.
   *
   * @param postType the post type to set
   * @return the payload builder
   */
  public T type(@NonNull PostTypeEnum postType) {
    this.type = postType.toString();
    return self();
  }

  /**
   * Sets the post format.
   *
   * @param postFormat the post format to set
   * @return the payload builder
   */
  public T format(@NonNull PostFormatEnum postFormat) {
    this.format = postFormat.toString();
    return self();
  }

  /**
   * Sets the categories.
   *
   * @param categories the categories to set
   * @return the payload builder
   */
  public T categories(@NonNull List<Integer> categories) {
    this.categories = categories;
    return self();
  }

  /**
   * Sets the tags.
   *
   * @param tags the tags to set
   * @return the payload builder
   */
  public T tags(@NonNull List<Integer> tags) {
    this.tags = tags;
    return self();
  }

  /**
   * Sets the slug.
   *
   * @param slug the slug to set
   * @return the payload builder
   */
  public T slug(@NonNull String slug) {
    this.slug = slug;
    return self();
  }

  /**
   * Sets the name.
   *
   * @param name the name to set
   * @return the payload builder
   */
  public T name(@NonNull String name) {
    this.name = name;
    return self();
  }

  /**
   * Sets the description.
   *
   * @param description the description to set
   * @return the payload builder
   */
  public T description(@NonNull String description) {
    this.description = description;
    return self();
  }

  /**
   * Sets the title.
   *
   * @param title the title to set
   * @return the payload builder
   */
  public T title(@NonNull String title) {
    this.title = title;
    return self();
  }

  /**
   * Sets the content.
   *
   * @param content the content to set
   * @return the payload builder
   */
  public T content(@NonNull String content) {
    this.content = content;
    return self();
  }

  /**
   * Sets the excerpt.
   *
   * @param excerpt the excerpt to set
   * @return the payload builder
   */
  public T excerpt(@NonNull String excerpt) {
    this.excerpt = excerpt;
    return self();
  }

  /**
   * Sets the author.
   *
   * @param author the author to set
   * @return the payload builder
   */
  public T author(@NonNull Integer author) {
    this.author = author;
    return self();
  }

  /**
   * Sets the password.
   *
   * @param password the password to set
   * @return the payload builder
   */
  public T password(@NonNull String password) {
    this.password = password;
    return self();
  }

  /**
   * Sets the comment status.
   *
   * @param status the comment status to set
   * @return the payload builder
   */
  public T commentStatus(@NonNull CommentStatusEnum status) {
    this.commentStatus = status.toString();
    return self();
  }

  /**
   * Sets the sticky status.
   *
   * @param sticky the sticky status to set
   * @return the payload builder
   */
  public T sticky(@NonNull Boolean sticky) {
    this.sticky = sticky;
    return self();
  }

  /**
   * Sets the featured media.
   *
   * @param featuredMedia the featured media to set
   * @return the payload builder
   */
  public T featuredMedia(@NonNull Integer featuredMedia) {
    this.featuredMedia = featuredMedia;
    return self();
  }

  /**
   * Resets all fields of the builder to their initial state. This method is marked as final to
   * ensure consistent behavior across all subclasses. Collections are cleared rather than set to
   * null to prevent memory leaks.
   *
   * @return the current builder instance for method chaining
   * @see #self()
   */
  public final T clear() {
    this.title = null;
    this.name = null;
    this.description = null;
    this.content = null;
    this.excerpt = null;
    this.status = null;
    this.slug = null;
    this.author = null;
    this.password = null;
    this.commentStatus = null;
    this.sticky = null;
    this.featuredMedia = null;
    this.type = null;
    if (this.categories != null) {
      try {
        this.categories.clear();
      } catch (UnsupportedOperationException e) {
        this.categories = Collections.emptyList();
      }
    }
    if (this.tags != null) {
      try {
        this.tags.clear();
      } catch (UnsupportedOperationException e) {
        this.tags = Collections.emptyList();
      }
    }
    this.format = null;
    return self();
  }

  /**
   * Builds the payload as a JsonNode.
   *
   * @return the built payload as a JsonNode
   */
  public abstract JsonNode build();
}
