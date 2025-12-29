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

package net.ygbstudio.powerwp4j.models.entities;

import static net.ygbstudio.powerwp4j.utils.Helpers.getPropertiesFromResources;

import java.util.Optional;
import java.util.Properties;
import net.ygbstudio.powerwp4j.base.extension.enums.EnvironmentScope;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * A record that represents basic site information required to interact with a WordPress site.
 *
 * @param fullyQualifiedDomainName the fully qualified domain name of the WordPress site
 * @param wpUser the username for the WordPress site
 * @param wpAppPass the application password for the WordPress site
 */
@NullMarked
public record WPSiteInfo(String fullyQualifiedDomainName, String wpUser, String wpAppPass) {

  /**
   * Returns the base URL of the WordPress REST API.
   *
   * @return the base URL of the WordPress REST API
   */
  @Contract(pure = true)
  public String apiBaseUrl() {
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
                appProps.getProperty(EnvironmentScope.WP_FULLY_QUALIFIED_DOMAIN_NAME_PROP.value()),
                appProps.getProperty(EnvironmentScope.WP_USER_PROP.value()),
                appProps.getProperty(EnvironmentScope.WP_APPLICATION_PASS_PROP.value())));
  }

  /**
   * Returns a {@link WPSiteInfo} loaded from the environment variables outlined in {@link
   * EnvironmentScope}.
   *
   * @return a {@link WPSiteInfo} loaded from the environment variables
   */
  public static Optional<WPSiteInfo> fromEnv() {
    String fqdm = System.getenv(EnvironmentScope.WP_FULLY_QUALIFIED_DOMAIN_NAME_ENV.value());
    String wpUser = System.getenv(EnvironmentScope.WP_USER_ENV.value());
    String wpAppPass = System.getenv(EnvironmentScope.WP_APPLICATION_PASS_ENV.value());
    if (fqdm == null || wpUser == null || wpAppPass == null) {
      return Optional.empty();
    }
    return Optional.of(new WPSiteInfo(fqdm, wpUser, wpAppPass));
  }
}
