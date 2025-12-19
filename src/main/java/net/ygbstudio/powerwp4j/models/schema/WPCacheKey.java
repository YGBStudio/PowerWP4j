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
  public String toString() {
    return value;
  }
}
