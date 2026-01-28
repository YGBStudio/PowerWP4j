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

package net.ygbstudio.powerwp4j.base.extension.enums;

import java.util.Comparator;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 * QueryParamEnum is an interface that promotes a unified type for query parameters and a {@code
 * value()} method to ensure consistency with the design practices of the project. It can be used as
 * a means to provide your own query parameters to the WPCacheManager safely.
 *
 * @see FriendlyEnum
 * @author Yoham Gabriel B. @YGBStudio
 */
public non-sealed interface QueryParamEnum extends FriendlyEnum {

  /**
   * This method helps you join a map of query parameters into a string that can be used as a query
   * string.
   *
   * <p>It is up to you to create a separate class that contains such "leading" parameters or use
   * the same for all as long as both implement {@link QueryParamEnum}. In either case this method
   * will help you and throw an {@link IllegalArgumentException} if you do not provide any query
   * parameters.
   *
   * @param wpRestQueriesMap
   *     <p>Map of query parameters and their values.
   * @return
   *     <p>String that can be used as a query string.
   */
  static <E extends QueryParamEnum> @NotNull String joinQueryParams(
      @NotNull Map<E, String> wpRestQueriesMap) {
    if (wpRestQueriesMap.isEmpty()) {
      throw new IllegalArgumentException("You need to include at least one query parameter.");
    }
    StringBuilder pathString = new StringBuilder("?");
    wpRestQueriesMap.keySet().stream()
        .sorted(Comparator.comparing(FriendlyEnum::value))
        .forEach(
            param -> {
              pathString.append(param.value());
              pathString.append(wpRestQueriesMap.get(param));
              pathString.append("&");
            });
    pathString.deleteCharAt(pathString.length() - 1);
    return pathString.toString();
  }
}
