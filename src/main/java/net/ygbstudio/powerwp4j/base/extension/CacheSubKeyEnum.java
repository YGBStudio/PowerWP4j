/*
 * PowerWP4j - Power WP for Java
 *
 * Copyright 2025 Yoham Gabriel Barboza B. (YGBStudio)
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

package net.ygbstudio.powerwp4j.base.extension;

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
public non-sealed interface CacheSubKeyEnum extends FriendlyEnum {}
