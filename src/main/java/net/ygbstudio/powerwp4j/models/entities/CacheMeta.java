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
