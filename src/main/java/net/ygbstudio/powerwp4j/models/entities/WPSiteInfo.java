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

import static net.ygbstudio.powerwp4j.utils.Helpers.getPropertiesFromResources;

import java.util.Optional;
import java.util.Properties;
import net.ygbstudio.powerwp4j.base.EnvironmentScope;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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

  /**
   * Returns an Optional of {@link WPSiteInfo} loaded from the specified configuration resource
   * properties file.
   *
   * @param fileName the name of the resource file to load properties from
   * @return an Optional of {@link WPSiteInfo} loaded from the specified configuration resource file
   */
  public static Optional<WPSiteInfo> fromConfigResource(String fileName) {
    Optional<Properties> props = getPropertiesFromResources(fileName);
    return props.map(
        appProps ->
            new WPSiteInfo(
                appProps.getProperty(EnvironmentScope.WP_FULLY_QUALIFIED_DOMAIN_NAME_PROP.toString()),
                appProps.getProperty(EnvironmentScope.WP_USER_PROP.toString()),
                appProps.getProperty(EnvironmentScope.WP_APPLICATION_PASS_PROP.toString())));
  }

  /**
   * Returns a {@link WPSiteInfo} loaded from the environment variables outlined in {@link
   * EnvironmentScope}.
   *
   * @return a {@link WPSiteInfo} loaded from the environment variables
   */
  public static @Nullable WPSiteInfo fromEnv() {
    String fqdm = System.getenv(EnvironmentScope.WP_FULLY_QUALIFIED_DOMAIN_NAME_ENV.toString());
    String wpUser = System.getenv(EnvironmentScope.WP_USER_ENV.toString());
    String wpAppPass = System.getenv(EnvironmentScope.WP_APPLICATION_PASS_ENV.toString());
    if (fqdm == null || wpUser == null || wpAppPass == null) {
      return null;
    }
    return new WPSiteInfo(fqdm, wpUser, wpAppPass);
  }
}
