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

package net.ygbstudio.powerwp4j.builders;

import static net.ygbstudio.powerwp4j.utils.Helpers.enumFromValue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import net.ygbstudio.powerwp4j.base.AbstractPayloadBuilder;
import net.ygbstudio.powerwp4j.base.extension.CommentStatusEnum;
import net.ygbstudio.powerwp4j.base.extension.PostStatusEnum;
import net.ygbstudio.powerwp4j.models.schema.WPCommentStatus;
import net.ygbstudio.powerwp4j.models.schema.WPStatus;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import tools.jackson.databind.JsonNode;

/**
 * Basic payload builder for WordPress REST Payloads. It can be used for posts and taxonomy
 * creation.
 *
 * <p>Inherits all methods from {@link AbstractPayloadBuilder} to provide a chainable pattern for
 * building payloads for the WordPress REST API. The payload is built using a {@link JsonNode}
 * object.
 *
 * <p>In case you need to attach a payload to a media file, use {@link WPMediaPayloadBuilder}.
 *
 * @see AbstractPayloadBuilder
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonInclude(Include.NON_NULL)
public class WPBasicPayloadBuilder extends AbstractPayloadBuilder<WPBasicPayloadBuilder> {

  WPBasicPayloadBuilder() {}

  @Override
  protected WPBasicPayloadBuilder self() {
    return this;
  }

  /**
   * Creates a new instance of {@link WPBasicPayloadBuilder}.
   *
   * @return A new instance of {@link WPBasicPayloadBuilder}.
   */
  @Contract(" -> new")
  public static @NonNull WPBasicPayloadBuilder builder() {
    return new WPBasicPayloadBuilder();
  }

  public PostStatusEnum getStatus() {
    return enumFromValue(WPStatus.class, status, true).orElse(null);
  }

  public List<Integer> getCategories() {
    return categories;
  }

  public List<Integer> getTags() {
    return tags;
  }

  public String getSlug() {
    return slug;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public Integer getAuthor() {
    return author;
  }

  public Integer getFeaturedMedia() {
    return featuredMedia;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public CommentStatusEnum getCommentStatus() {
    return enumFromValue(WPCommentStatus.class, commentStatus, true).orElse(null);
  }

  @Override
  public JsonNode build() {
    return JsonSupport.getMapper().valueToTree(this);
  }
}
