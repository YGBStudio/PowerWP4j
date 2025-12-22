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
 * Interface for representing a trigger function that can be activated lazily in context were code
 * reuse is desired and enforced.
 *
 * <p>Usually used as a callback for when a certain condition is met or when a piece of logic; for
 * instance a logging message, must be repeated multiple times in the same method. It resembles a
 * {@link Runnable} but its intent differs from it.
 *
 * @see TypedTrigger
 * @author Yoham Gabriel B.
 */
@FunctionalInterface
public interface Trigger {

  /**
   * Activates the trigger function without passing arguments or by using variables in the same
   * scope. If you need to pass arguments to the trigger function, use the {@link TypedTrigger}
   * interface.
   */
  void activate();
}
