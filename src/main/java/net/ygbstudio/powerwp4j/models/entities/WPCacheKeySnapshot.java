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

package net.ygbstudio.powerwp4j.models.entities;

import java.util.Map;
import net.ygbstudio.powerwp4j.base.extension.enums.CacheKeyEnum;
import net.ygbstudio.powerwp4j.base.extension.enums.CacheSubKeyEnum;
import org.jspecify.annotations.Nullable;

/**
 * WPCacheKeySnapshot is a record that represents a snapshot of a cache key and its subkeys. Use
 * this abstraction if you are getting nested values and a key exists. If the values are contained
 * in an array or immediate value (not inside another JSON object), you can use
 *
 * <p>{@link net.ygbstudio.powerwp4j.engine.WPCacheAnalyzer#getCacheKeyValueStream(CacheKeyEnum)}
 *
 * <p>Consumers must be wary that values in the subkey map can be {@code null}.
 *
 * @param <V> the type of the values in the cache subkey map. It is useful to think of this
 *     parameter as the kind of transformation that you apply to the cache subkeys. For example,
 *     {@code JsonNode::asString} will require a {@code WPCacheKeySnapshot<String>}.
 * @param cacheKey the cache key represented by the {@link CacheKeyEnum} enum
 * @param cacheSubKeyMap a map containing the cache subkeys represented by {@link CacheSubKeyEnum}
 *     and their corresponding values of type {@code V}.
 */
public record WPCacheKeySnapshot<V>(
    CacheKeyEnum cacheKey, Map<CacheSubKeyEnum, @Nullable V> cacheSubKeyMap) {}
