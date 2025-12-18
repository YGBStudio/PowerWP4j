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

package net.ygbstudio.powerwp4j.models.taxonomies;

import net.ygbstudio.powerwp4j.base.extension.ClassValueEnum;

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
public enum TaxonomyValues implements ClassValueEnum {
  CATEGORIES("categories"),
  TAGS("tags");

  private final String value;

  TaxonomyValues(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
