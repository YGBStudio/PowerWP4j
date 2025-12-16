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

package net.ygbstudio.powerwp4j.base;

import org.jetbrains.annotations.Contract;

/**
 * Interface for enums that provides a unified enforcement of the different constraints or methods
 * based on design decisions in this project. For instance, one of those constraints is the
 * enforcement of developer friendly {@link Object#toString()} overrides, and thus the name of this
 * interface is {@code FriendlyEnum}.
 *
 * <p>The enforcement of such constraints is a difficult task, but it is recommended to ensure
 * consistency with the design practices of the project, so that is why all {@link Enum} interfaces
 * extend this class and their JavaDocs point you here.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public interface FriendlyEnum {

  /**
   * Returns the string representation of the query parameter. This is a common override of the
   * {@link Object#toString()} method. The design recommendation of this project is to return the
   * {@code Enum} value as the string representation of the query parameter to make sure the methods
   * and helper utilities work as expected in this project.
   *
   * <p>For example, if you provide a {@link String} value to {@link Enum} {@code ENUM} like {@code
   * ENUM("value")} then the {@link Object#toString()} method will return {@code "value"} not {@code
   * "ENUM(value)"}.
   *
   * <p>This has been done to ensure that {@link Enum} constant names are for the code, not for
   * people. So, all implementations have taken this design principle into consideration.
   *
   * @return the string value of the query parameter as provided in its constructor
   */
  @Contract(pure = true)
  String toString();
}
