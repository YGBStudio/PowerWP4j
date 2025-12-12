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
 * TriggerCallable is a functional interface that represents a callable that can be triggered
 * multiple times and returns a result of type {@code R}. It acts as a placeholder of a chunk of
 * repetitive calls to, for example, a reusable method that fetches data from a remote API.
 *
 * <p>It resembles the {@code Callable<V>} from {@link java.util.concurrent} but it does not require
 * explicit exception handling while using it.
 *
 * @see Trigger
 * @see TypedTrigger
 * @param <R> the type of the result of the callable
 */
@FunctionalInterface
public interface TriggerCallable<R> {

  /**
   * Triggers the callable and returns the result {@code R}.
   *
   * @return the result of the callable
   */
  R get();
}
