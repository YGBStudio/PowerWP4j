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

package net.ygbstudio.powerwp4j.models.entities;

import java.util.Map;
import net.ygbstudio.powerwp4j.base.extension.CacheKeyEnum;
import net.ygbstudio.powerwp4j.base.extension.CacheSubKeyEnum;

/**
 * WPCacheKeySnapshot is a record that represents a snapshot of a cache key and its subkeys.
 *
 * @param <V> the type of the values in the cache subkey map. It is useful to think of this
 *     parameter as the kind of transformation that you apply to the cache subkeys. For example,
 *     {@code JsonNode::asString} will require a {@code WPCacheKeySnapshot<String>}.
 * @param cacheKey the cache key represented by the {@link CacheKeyEnum} enum
 * @param cacheSubKeyMap a map containing the cache subkeys represented by {@link CacheSubKeyEnum}
 *     and their corresponding values of type {@code V}.
 */
public record WPCacheKeySnapshot<V>(
    CacheKeyEnum cacheKey, Map<CacheSubKeyEnum, V> cacheSubKeyMap) {}
