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

package net.ygbstudio.powerwp4j.base.extension;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.ygbstudio.powerwp4j.base.FriendlyEnum;
import org.jspecify.annotations.NonNull;

/**
 * QueryParamEnum is an interface that promotes a unified type for query parameters and a {@code
 * toString} method to ensure consistency with the design practices of the project. It can be used
 * as a means to provide your own query parameters to the WPCacheManager safely.
 *
 * @see FriendlyEnum
 * @author Yoham Gabriel B. @YGBStudio
 */
public interface QueryParamEnum extends FriendlyEnum {

  /**
   * This method helps you join a map of query parameters into a string that can be used as a query
   * string.
   *
   * <p>You need to have at least one "leading" (starting with '?') or main query parameter in your
   * map. This is the parameter that will be used to separate the query parameters from the rest of
   * the URL and avoid malformed requests.
   *
   * <p>It is up to you to create a separate class that contains such "leading" parameters or use
   * the same for all as long as both implement {@link QueryParamEnum}. In either case this method
   * will help you and throw an {@link IllegalArgumentException} if you do not provide a single
   * query parameter that starts with '?' with its value pair.
   *
   * @param wpRestQueriesMap
   *     <p>Map of query parameters and their values.
   * @return
   *     <p>String that can be used as a query string.
   */
  static <E extends QueryParamEnum> @NonNull String joinQueryParams(
      @NonNull Map<E, String> wpRestQueriesMap) {
    if (wpRestQueriesMap.isEmpty()) return "";
    if (wpRestQueriesMap.keySet().stream().filter(param -> param.toString().startsWith("?")).count()
        != 1) {
      throw new IllegalArgumentException(
          "You need to include at least, and only one, leading query parameter or a query string start key.");
    }
    Comparator<E> leadingParam = Comparator.comparing(param -> param.toString().startsWith("?"));
    List<E> sortedParams = wpRestQueriesMap.keySet().stream().sorted(leadingParam).toList();

    StringBuilder pathString = new StringBuilder();
    sortedParams
        .reversed()
        .forEach(
            param -> {
              if (!pathString.isEmpty() && !param.toString().startsWith("?"))
                pathString.append("&");
              pathString.append(param);
              pathString.append(wpRestQueriesMap.get(param));
            });

    return pathString.toString();
  }
}
