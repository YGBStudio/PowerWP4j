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

import net.ygbstudio.powerwp4j.base.extension.PostTypeEnum;

/**
 * Enum representing different types of WordPress posts.
 *
 * <p>This enum includes common post types such as standard posts, attachments, and photos. Each
 * enum constant is associated with its corresponding string representation used in WordPress.
 *
 * @see <a
 *     href="https://developer.wordpress.org/reference/functions/get_post_type/">get_post_type()</a>
 * @author Yoham Gabriel @ YGB Studio
 */
public enum WPPostType implements PostTypeEnum {
  POST("post"),
  ATTACHMENT("attachment"),
  ALL("all");

  private final String value;

  WPPostType(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
