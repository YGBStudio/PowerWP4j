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

package net.ygbstudio.powerwp4j.models.entities;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

/**
 * A record that represents basic site information required to interact with a WordPress site.
 *
 * @param fullyQualifiedDomainName the fully qualified domain name of the WordPress site
 * @param wpUser the user name for the WordPress site
 * @param wpAppPass the application password for the WordPress site
 */
public record WPSiteInfo(String fullyQualifiedDomainName, String wpUser, String wpAppPass) {

  /**
   * Returns the base URL of the WordPress REST API.
   *
   * @return the base URL of the WordPress REST API
   */
  @Contract(pure = true)
  public @NonNull String apiBaseUrl() {
    return String.format("https://%s/wp-json/wp/v2", this.fullyQualifiedDomainName);
  }
}
