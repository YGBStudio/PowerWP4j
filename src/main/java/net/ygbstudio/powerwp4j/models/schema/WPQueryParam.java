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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NullMarked;

/**
 * WPQueryParam is an enum that represents the query parameters for the WordPress REST API.
 *
 * @author Yoham Gabriel B. @ YGBStudio
 */
@NullMarked
public enum WPQueryParam {
  PER_PAGE("per_page="),
  PAGE("?page="),
  TIMESTAMP("_t=");

  private final String value;

  WPQueryParam(String value) {
    this.value = value;
  }

  public static String joinQueryParams(Map<WPQueryParam, String> wpRestQueriesMap) {
    StringBuilder pathString = new StringBuilder();
    Consumer<Map.Entry<WPQueryParam, String>> appendToPath =
        entry -> {
          if (!pathString.isEmpty()) pathString.append("&");
          pathString.append(entry.getKey());
          pathString.append(entry.getValue());
        };
    wpRestQueriesMap.entrySet().forEach(appendToPath);
    return pathString.toString();
  }

  @Override
  public String toString() {
    return value;
  }
}
