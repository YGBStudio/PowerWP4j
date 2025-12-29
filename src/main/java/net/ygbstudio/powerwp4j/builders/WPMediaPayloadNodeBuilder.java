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

import net.ygbstudio.powerwp4j.base.extension.builders.AbstractPayloadNodeBuilder;
import net.ygbstudio.powerwp4j.models.schema.WPCacheKey;

/**
 * WPMediaPayloadNodeBuilder is a builder class for constructing a WordPress media payload. It
 * extends the {@link AbstractPayloadNodeBuilder} class and provides methods for setting various
 * properties of the media payload such as alternative text, caption, and description. For more
 * information on this alternative implementation, refer to the documentation of the parent abstract
 * class.
 *
 * @see WPPayloadNodeBuilder
 */
public class WPMediaPayloadNodeBuilder
    extends AbstractPayloadNodeBuilder<WPMediaPayloadNodeBuilder> {

  private WPMediaPayloadNodeBuilder() {}

  public static WPMediaPayloadNodeBuilder builder() {
    return new WPMediaPayloadNodeBuilder().clear();
  }

  /**
   * Sets the alternative text for the media attachment.
   *
   * @param altText Alternative text of the media attachment.
   * @return The current instance of WPMediaPayloadNodeBuilder.
   */
  public WPMediaPayloadNodeBuilder altText(String altText) {
    return add(WPCacheKey.ALT_TEXT, altText);
  }

  /**
   * Sets the caption for the media attachment.
   *
   * @param caption Caption of the media attachment.
   * @return The current instance of WPMediaPayloadNodeBuilder.
   */
  public WPMediaPayloadNodeBuilder caption(String caption) {
    return add(WPCacheKey.CAPTION, caption);
  }

  /**
   * Sets the description for the media attachment.
   *
   * @param description Description of the media attachment.
   * @return The current instance of WPMediaPayloadNodeBuilder.
   */
  public WPMediaPayloadNodeBuilder description(String description) {
    return add(WPCacheKey.DESCRIPTION, description);
  }
}
