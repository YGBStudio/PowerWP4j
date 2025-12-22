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
