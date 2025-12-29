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

package net.ygbstudio.powerwp4j.models.taxonomies;

import net.ygbstudio.powerwp4j.base.extension.enums.ClassMarkerEnum;

/**
 * Enum class for the WordPress cache taxonomy key markers.
 *
 * <p>A taxonomy marker is a prefix or identifier used in the 'class_list' field of a post (in the
 * WordPress Posts JSON file) to indicate the type of taxonomy (e.g., tag, category, post type,
 * status).
 *
 * <p>Example: {@code "tag-python", "category-tutorial", "status-published"}
 *
 * <p>These markers help extract and group related metadata from posts.
 *
 * @author Yoham Gabriel @ YGBStudio
 */
public enum TaxonomyMarker implements ClassMarkerEnum {
  TAG("tag"),
  CATEGORY("category"),
  POST("post"),
  TYPE("type"),
  STATUS("status");

  private final String value;

  TaxonomyMarker(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return this.value;
  }
}
