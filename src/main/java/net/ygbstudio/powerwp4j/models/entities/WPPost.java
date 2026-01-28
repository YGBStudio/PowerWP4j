/*
 * JBrave
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

package net.ygbstudio.powerwp4j.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A record representing a WordPress Post.
 *
 * @param id The unique identifier of the post.
 * @param date The date the post was published, in the site's timezone.
 * @param dateGmt The date the post was published, as GMT.
 * @param guid The globally unique identifier for the post.
 * @param modified The date the post was last modified, in the site's timezone.
 * @param modifiedGmt The date the post was last modified, as GMT.
 * @param slug An alphanumeric identifier for the post unique to its type.
 * @param status A named status for the post.
 * @param type The type of the post.
 * @param link The URL to the post.
 * @param title The title for the post.
 * @param content The content for the post.
 * @param excerpt The excerpt for the post.
 * @param author The ID for the author of the post.
 * @param featuredMedia The ID of the featured media for the post.
 * @param commentStatus Whether or not comments are open on the post.
 * @param pingStatus Whether or not the post can be pinged.
 * @param sticky Whether or not the post should be treated as sticky.
 * @param template The theme file to use to display the post.
 * @param format The format of the post.
 * @param meta Meta fields.
 * @param categories The terms assigned to the post in the category taxonomy.
 * @param tags The terms assigned to the post in the post_tag taxonomy.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WPPost(
    @JsonProperty("id") long id,
    @JsonProperty("date") @Nullable LocalDateTime date,
    @JsonProperty("date_gmt") @Nullable LocalDateTime dateGmt,
    @JsonProperty("guid") @Nullable WPRendered guid,
    @JsonProperty("modified") @Nullable LocalDateTime modified,
    @JsonProperty("modified_gmt") @Nullable LocalDateTime modifiedGmt,
    @JsonProperty("slug") @NotNull String slug,
    @JsonProperty("status") @NotNull String status,
    @JsonProperty("type") @NotNull String type,
    @JsonProperty("link") @NotNull String link,
    @JsonProperty("title") @NotNull WPRendered title,
    @JsonProperty("content") @NotNull WPRendered content,
    @JsonProperty("excerpt") @Nullable WPRendered excerpt,
    @JsonProperty("author") long author,
    @JsonProperty("featured_media") long featuredMedia,
    @JsonProperty("comment_status") @Nullable String commentStatus,
    @JsonProperty("ping_status") @Nullable String pingStatus,
    @JsonProperty("sticky") boolean sticky,
    @JsonProperty("template") @Nullable String template,
    @JsonProperty("format") @Nullable String format,
    @JsonProperty("meta") @Nullable Map<String, Object> meta,
    @JsonProperty("categories") @Nullable List<? extends Number> categories,
    @JsonProperty("tags") @Nullable List<? extends Number> tags) {}
