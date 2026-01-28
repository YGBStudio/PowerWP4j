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

package net.ygbstudio.powerwp4j.base.extension.enums;

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
 * <p>Note that not all {@link ClassMarkerEnum} are paired with numerical values <br>
 * that {@link ClassValueKeyEnum} is supposed to help you distinguish and aggregate, although a
 * value key is a cache key by default.
 *
 * <p>Example: {@code "tags": [12, 34, 56]} or {@code "categories": [2, 5]}
 *
 * @author Yoham Gabriel B. @YGBStudio
 */
public non-sealed interface ClassValueKeyEnum extends FriendlyEnum, CacheKeyEnum {}
