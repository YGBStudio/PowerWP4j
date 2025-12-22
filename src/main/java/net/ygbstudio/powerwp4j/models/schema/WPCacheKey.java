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

import net.ygbstudio.powerwp4j.base.extension.CacheKeyEnum;

/**
 * Enumeration of possible cache keys for the WordPress REST API cache. It is the default
 * implementation of the {@link CacheKeyEnum} interface.
 *
 * @see CacheKeyEnum
 */
public enum WPCacheKey implements CacheKeyEnum {
  ID("id"),
  SLUG("slug"),
  TITLE("title"),
  LINK("link"),
  GUID("guid"),
  DATE("date"),
  DATE_GMT("date_gmt"),
  CONTENT("content"),
  CLASS_LIST("class_list"),
  EXCERPT("excerpt");

  private final String value;

  WPCacheKey(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
