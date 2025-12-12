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
