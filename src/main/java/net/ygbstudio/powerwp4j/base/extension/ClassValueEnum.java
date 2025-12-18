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
 * Unified Enum interface for the WordPress class/taxonomy value representation objects.
 *
 * <p>Implement this interface if you have, for instance, custom taxonomies with specific values and
 * want to use them with the utilities provided in PowerWP4j.
 *
 * <p>A class value marker in the WordPress post data is a key that holds a list of numeric IDs for
 * a given taxonomy/class, typically paired with a respective class marker that PowerWP4j represents
 * as a {@link ClassMarkerEnum} in a different list, such as tags or categories.
 *
 * <p>Note that not all {@link ClassMarkerEnum} are paired with numerical values that {@link
 * ClassValueEnum} is supposed to help you distinguish and aggregate.
 *
 * <p>Example: {@code "tags": [12, 34, 56]} or {@code "categories": [2, 5]}
 *
 * @author Yoham Gabriel B. @YGBStudio
 */
public interface ClassValueEnum extends FriendlyEnum {}
