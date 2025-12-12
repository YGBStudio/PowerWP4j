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

package net.ygbstudio.powerwp4j.extension;

/**
 * RestPathEnum is an interface that promotes unified type for rest paths and a {@code toString}
 * method to ensure consistency with the design practices of the project. It can be used as a means
 * to provide your own rest paths to the WPSiteEngine safely.
 *
 * @author Yoham Gabriel B. @YGBStudio
 */
public interface RestPathEnum {

  /**
   * Returns the string representation of the rest path. This is a common override of the {@link
   * Object#toString()} method.
   *
   * <p>The design recommendation of this project is to return the {@code Enum} value as the string
   * representation of the rest path to make sure the methods and helper utilities work as expected
   * in this project.
   *
   * @return the string value of the rest path as provided in its constructor
   */
  String toString();
}
