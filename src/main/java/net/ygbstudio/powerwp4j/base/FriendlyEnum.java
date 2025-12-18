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
 * This interface is designed to enforce a consistent approach to Java enums in the PowerWP4j
 * project. It's called "FriendlyEnum" because it enforces a developer-friendly {@link
 * Object#toString()} override, which is a common practice in the project. By requiring all enums to
 * extend this interface, it helps ensure consistency and maintainability of the codebase.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public interface FriendlyEnum {

  /**
   * Returns the string representation of the enum constant.
   *
   * <p>This is a common override of the {@link Object#toString()} method. The goal is to return the
   * enum constant value as a string representation, not the enum constant name. This is to ensure
   * that the string representation of the enum constant matches the actual value used in the API
   * calls.
   *
   * @return the string value of the enum constant, which is the value that was passed in the
   *     constructor
   * @example
   *     <pre>
   *          public enum MyEnum implements FriendlyEnum {
   *            MY_VALUE("my_value");
   *
   *            private final String value;
   *
   *            MyEnum(String value) {
   *              this.value = value;
   *            }
   *
   *            {@code @Override}
   *            public String toString() {
   *              return value;
   *            }
   *          }
   *          </pre>
   */
  @Contract(pure = true)
  String toString();
}
