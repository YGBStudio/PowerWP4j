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

package net.ygbstudio.powerwp4j.models.schema;

import java.time.YearMonth;
import net.ygbstudio.powerwp4j.base.extension.RestPathEnum;
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
