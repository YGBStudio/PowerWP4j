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
