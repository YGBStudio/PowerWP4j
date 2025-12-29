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

package net.ygbstudio.powerwp4j.models.schema;

import net.ygbstudio.powerwp4j.base.extension.enums.CacheKeyEnum;

/**
 * Enumeration of possible cache keys for the WordPress REST API cache. It is the default
 * implementation of the {@link CacheKeyEnum} interface.
 *
 * @see CacheKeyEnum
 */
public enum WPCacheKey implements CacheKeyEnum {
  ALT_TEXT("alt_text"),
  AUTHOR("author"),
  CAPTION("caption"),
  CLASS_LIST("class_list"),
  CONTENT("content"),
  COMMENT_STATUS("comment_status"),
  DATE("date"),
  DATE_GMT("date_gmt"),
  DESCRIPTION("description"),
  EXCERPT("excerpt"),
  FEATURED_MEDIA("featured_media"),
  FORMAT("format"),
  GUID("guid"),
  ID("id"),
  LINK("link"),
  SLUG("slug"),
  STATUS("status"),
  STICKY("sticky"),
  TITLE("title"),
  TYPE("type");

  private final String value;

  WPCacheKey(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
