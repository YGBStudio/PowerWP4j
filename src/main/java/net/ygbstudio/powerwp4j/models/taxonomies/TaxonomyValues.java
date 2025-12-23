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

import net.ygbstudio.powerwp4j.base.extension.ClassValueKeyEnum;

/**
 * Enum class for the WordPress cache taxonomy value representation.
 *
 * <p>A taxonomy value is a key to the WordPress post data that holds a list of numeric IDs for a
 * given taxonomy in the posts data structure (typically a JSON file), such as tags or categories.
 *
 * <p>Example: {@code "tags": [12, 34, 56]} {@code "categories": [2, 5]}
 *
 * @author Yoham Gabriel @ YGBStudio
 */
public enum TaxonomyValues implements ClassValueKeyEnum {
  CATEGORIES("categories"),
  TAGS("tags");

  private final String value;

  TaxonomyValues(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return this.value;
  }
}
