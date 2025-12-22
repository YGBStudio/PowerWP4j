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

import java.time.LocalDate;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

/**
 * CacheMeta is a record that represents the metadata of the cache.
 *
 * @author Yoham Gabriel B. @ YGBStudio
 */
public record CacheMeta(long totalPages, long totalPosts, @Nullable LocalDate lastUpdated) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    CacheMeta cacheMeta = (CacheMeta) o;
    return totalPages() == cacheMeta.totalPages()
        && totalPosts() == cacheMeta.totalPosts()
        && Objects.equals(lastUpdated(), cacheMeta.lastUpdated());
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalPages(), totalPosts(), lastUpdated());
  }
}
