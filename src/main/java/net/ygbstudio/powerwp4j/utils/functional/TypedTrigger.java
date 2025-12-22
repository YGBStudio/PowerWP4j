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

package net.ygbstudio.powerwp4j.utils.functional;

/**
 * Typed variant of the {@link Trigger} interface for representing an action that can be activated
 * at a later point in the program with a specific argument supplied to a function to be executed.
 *
 * <p>This interface may resemble a {@code Consumer<T>} from {@link java.util.function} but its
 * intent combines the latter with a {@link Runnable} in nature because it does not have a return
 * value, and it is designed to run (activate) a statement multiple times.
 *
 * @param <T> the type of the argument
 * @see Trigger
 * @author Yoham Gabriel B.
 */
@FunctionalInterface
public interface TypedTrigger<T> {

  /**
   * Supplies the given argument to the trigger function and activates it.
   *
   * @param arg the argument to pass to the trigger
   */
  void activate(T arg);
}
