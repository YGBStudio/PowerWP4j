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

package net.ygbstudio.powerwp4j.models.entities;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Represents a mapping between a WordPress API class ID and a taxonomy value.
 *
 * @param key The key of the class (category or tag).
 * @param value The value of the taxonomy marker.
 * @param <V> The type of the taxonomy value.
 */
public record WPClassMapping<K, V>(@NotNull K key, @NotNull V value) {

  /**
   * Converts a set of {@link WPClassMapping} with numeric values to a set of the same numeric type.
   *
   * @param classMappingSet the set of {@link WPClassMapping} objects to convert
   * @param <T> the type of the key in each class mapping object
   * @param <U> the numeric type of the values in the input set and the output set
   * @return a set of the numeric type {@code U}
   */
  public static <T, U extends Number> Set<U> toNumberSet(
      @NotNull Set<WPClassMapping<T, U>> classMappingSet) {
    return classMappingSet.stream().map(WPClassMapping::value).collect(Collectors.toSet());
  }

  /**
   * Converts a list of {@link WPClassMapping} with numeric values to a set of the same numeric
   * type.
   *
   * @param classMappingList the list of {@link WPClassMapping} objects to convert
   * @param <T> the type of the key in each class mapping object
   * @param <U> the numeric type of the values in the input list and the output set
   * @return a list of the numeric type {@code U}
   */
  public static <T, U extends Number> @Unmodifiable @NotNull List<U> toNumberList(
      @NotNull List<WPClassMapping<T, U>> classMappingList) {
    return classMappingList.stream().map(WPClassMapping::value).toList();
  }
}
