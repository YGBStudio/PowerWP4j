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

import java.util.Arrays;
import java.util.stream.Collectors;
import net.ygbstudio.powerwp4j.base.FriendlyEnum;

/**
 * An interface for enum types that represent fields that can be used in a URL.
 *
 * <p>Classes that implement this interface must provide a string representation of the field that
 * can be appended to a URL to fetch that field.
 *
 * @see FriendlyEnum
 * @author Yoham Gabriel B. @YGBStudio
 */
public interface URLFieldsEnum {
  /**
   * Joins the given fields into a single string, separated by commas.
   *
   * @param fields The fields to join.
   * @return A string containing the joined fields.
   */
  default String joinFields(URLFieldsEnum baseField, URLFieldsEnum... fields) {
    return baseField
        + Arrays.stream(fields).map(URLFieldsEnum::toString).collect(Collectors.joining(","));
  }
}
