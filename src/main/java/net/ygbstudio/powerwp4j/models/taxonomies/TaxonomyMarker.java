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

import net.ygbstudio.powerwp4j.base.extension.ClassMarkerEnum;

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
  public String toString() {
    return this.value;
  }
}
