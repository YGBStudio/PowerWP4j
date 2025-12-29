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

package net.ygbstudio.powerwp4j.base.extension.builders;

import tools.jackson.databind.JsonNode;

/**
 * Abstract builder for media attachment payloads.
 *
 * @see AbstractPayloadBuilder
 * @author Yoham Gabriel @ YGB Studio
 */
public abstract class AbstractMediaPayloadBuilder<T extends AbstractMediaPayloadBuilder<T>> {
  protected String altText;
  protected String caption;
  protected String description;

  /**
   * Returns the current instance of the payload builder. This is a protected method used for
   * chaining method calls.
   *
   * <p>The cast to type T is safe because the method is only called within the class hierarchy and
   * this class is parameterized with the type of the subclass.
   *
   * @return the current instance of the payload builder
   */
  @SuppressWarnings("unchecked")
  protected T self() {
    return (T) this;
  }

  /**
   * Sets the alternative text for the media attachment.
   *
   * @param altText Alternative text of the media attachment.
   * @return The current instance of AbstractMediaPayloadBuilder.
   */
  public T altText(String altText) {
    this.altText = altText;
    return self();
  }

  /**
   * Sets the caption for the media attachment.
   *
   * @param caption Caption of the media attachment.
   * @return The current instance of AbstractMediaPayloadBuilder.
   */
  public T caption(String caption) {
    this.caption = caption;
    return self();
  }

  /**
   * Sets the description for the media attachment.
   *
   * @param description Description of the media attachment.
   * @return The current instance of AbstractMediaPayloadBuilder.
   */
  public T description(String description) {
    this.description = description;
    return self();
  }

  /**
   * Resets all fields of the builder to their initial state. This method is marked as final to
   * ensure consistent behavior across all subclasses.
   *
   * @return the current builder instance for method chaining
   * @see #self()
   */
  public final T clear() {
    this.altText = null;
    this.caption = null;
    this.description = null;
    return self();
  }

  /**
   * Builds the media attachment payload as a JsonNode.
   *
   * @return JsonNode representing the media attachment payload.
   */
  public abstract JsonNode build();
}
