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

package net.ygbstudio.powerwp4j.base;

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
