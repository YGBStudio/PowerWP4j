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

package net.ygbstudio.powerwp4j.builders;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import net.ygbstudio.powerwp4j.base.AbstractMediaPayloadBuilder;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
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

  WPMediaPayloadBuilder() {}

  /**
   * Creates a new instance of {@link WPMediaPayloadBuilder}.
   *
   * @return A new instance of {@link WPMediaPayloadBuilder}.
   */
  @Contract(" -> new")
  public static @NonNull WPMediaPayloadBuilder builder() {
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
