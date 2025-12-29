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
 * ClassValueKeyEnum} and those can be found in a different list depending on the marker as not all
 * markers are supposed to be paired with numerical values. The reason why this is the case resides
 * in that a {@link ClassMarkerEnum} is a categorical marker in nature, and such categories could be
 * aggregated or just used as a marker for other states or metadata concerning post elements.
 *
 * <p>Example: {@code "tag-python", "category-tutorial", "status-published"}
 *
 * @author Yoham Gabriel B. @YGBStudio
 */
public non-sealed interface ClassMarkerEnum extends FriendlyEnum {}
