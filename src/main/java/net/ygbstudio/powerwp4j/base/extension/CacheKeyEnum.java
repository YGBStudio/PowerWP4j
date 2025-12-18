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

import net.ygbstudio.powerwp4j.base.FriendlyEnum;

/**
 * This interface provides a way to create enum constants that represent keys in the JSON local
 * cache and use them with utilities in PowerWP4j. These keys represent values that can be isolated
 * or inside arrays.
 *
 * <p>In reality, a key could be any string that you know is contained in the cache data structure,
 * and most libraries allow you to get away with providing this as a simple string. However,
 * PowerWP4j favors predictability and treats everything as part of a pattern. If, for some reason,
 * a method taking a {@code CacheKeyEnum} does not work as expected with a value from a class that
 * implements it, it only means that:
 *
 * <ol>
 *   <li>The cache file does not contain that element
 *   <li>The enum implementation may not be following the {@link FriendlyEnum} style
 *   <li>There could be an error in the pattern followed by PowerWP4j and requires attention
 * </ol>
 *
 * @author Yoham Gabriel B. @YGBStudio
 */
public interface CacheKeyEnum extends FriendlyEnum {}
