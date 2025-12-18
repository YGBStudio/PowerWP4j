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
 * Unified Enum interface for the WordPress taxonomy/class key markers. objects.
 *
 * <p>Implement this interface if you have, for instance, custom taxonomies and want to extract them
 * with the utilities provided in PowerWP4j.
 *
 * <p>A class marker is a prefix or identifier used in the 'class_list' field of a post to indicate
 * a categorical separation that is related to a value in a site (e.g., tag, category, post type,
 * status).
 *
 * <p>The value corresponding to each of these markers is represented in PowerWP4j as a {@link
 * ClassValueEnum} and can those be can be found in a different list depending on the marker as not
 * all markers are supposed to be paired with numerical values. The reason why this is the case
 * resides in that a {@link ClassMarkerEnum} is a categorical marker in nature, and such categories
 * could be aggregated or just used as a marker for other states or metadata concerning post
 * elements.
 *
 * <p>Example: {@code "tag-python", "category-tutorial", "status-published"}
 *
 * @author Yoham Gabriel B. @YGBStudio
 */
public interface ClassMarkerEnum extends FriendlyEnum {}
