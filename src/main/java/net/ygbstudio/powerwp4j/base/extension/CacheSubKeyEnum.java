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
 * CacheSubKeyEnum is an interface that extends the {@link FriendlyEnum} interface and is used to
 * represent the keys nested in the values represented by {@link CacheKeyEnum} in the local cache.
 *
 * <p>For example, if the cache has the following:
 *
 * <p>{@code {"content" : {"rendered" : "some content", "protected" : false}}}
 *
 * <p>then {@code "rendered"} and {@code "protected"} are the cache subkeys of the {@code "content"}
 * cache key.
 *
 * <p>This separation makes it possible to identify when the client wants to access a nested element
 * inside the cache, making it easier to implement advanced querying procedures and extraction
 * strategies.
 *
 * @see FriendlyEnum
 */
public interface CacheSubKeyEnum extends FriendlyEnum {}
