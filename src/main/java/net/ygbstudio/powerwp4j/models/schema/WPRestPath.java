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

import java.time.YearMonth;
import net.ygbstudio.powerwp4j.extension.RestPathEnum;
import org.jspecify.annotations.NullMarked;

/**
 * Enum representing various API endpoints and query parameters for WordPress REST API.
 *
 * <p>This enum includes common endpoints and parameters used to interact with WordPress data, such
 * as users, posts, media, and categories. Each enum constant is associated with its corresponding
 * string representation used in the API URLs.
 *
 * <p><strong>Assume the endpoint is provided with a leading slash and the implementation will add
 * any trailing slashes if needed.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@NullMarked
public enum WPRestPath implements RestPathEnum {
  USERS("/users"),
  POSTS("/posts"),
  PHOTOS("/photos"),
  CATEGORIES("/categories"),
  MEDIA("/media"),
  TAGS("/tags"),
  CONTENT_UPLOADS(
      "/wp-content/uploads/"
          + YearMonth.now().getYear()
          + "/"
          + String.format("%02d", YearMonth.now().getMonthValue()));

  private final String value;

  WPRestPath(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
