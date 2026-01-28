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

package net.ygbstudio.powerwp4j.builders;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import net.ygbstudio.powerwp4j.base.extension.builders.AbstractMediaPayloadBuilder;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.JsonNode;

/**
 * Builder for WordPress media attachment payloads. Inherits all methods from its abstract
 * superclass to provide a chainable pattern for building media attachment payloads.
 *
 * <p>If you need to use a builder for posts or taxonomies, use {@link WPBasicPayloadBuilder}.
 *
 * @see AbstractMediaPayloadBuilder
 * @author Yoham Gabriel @ YGB Studio
 */
@JsonInclude(Include.NON_EMPTY)
public class WPMediaPayloadBuilder extends AbstractMediaPayloadBuilder<WPMediaPayloadBuilder> {

  private WPMediaPayloadBuilder() {}

  /**
   * Creates a new instance of {@link WPMediaPayloadBuilder}.
   *
   * @return A new instance of {@link WPMediaPayloadBuilder}.
   */
  @Contract(" -> new")
  public static @NotNull WPMediaPayloadBuilder builder() {
    return new WPMediaPayloadBuilder();
  }

  public String getAltText() {
    return altText;
  }

  public String getCaption() {
    return caption;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public JsonNode build() {
    return JsonSupport.getMapper().valueToTree(this);
  }
}
