/*
 * PowerWP4j - Power WP for Java
 *
 * Copyright 2025-2026 Yoham Gabriel Barboza B.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
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
