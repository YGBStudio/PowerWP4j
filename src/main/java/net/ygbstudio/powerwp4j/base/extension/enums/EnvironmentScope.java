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
 * Enumeration that represents the keys for the environment variables required to connect to a
 * WordPress site.
 *
 * @implNote This enum implements the {@link FriendlyEnum} interface to enforce a user-friendly
 *     string representation of the enum constants.
 * @author Yoham Gabriel @ YGBStudio
 */
public enum EnvironmentScope implements FriendlyEnum {
  WP_USER_PROP("wp.user"),
  WP_APPLICATION_PASS_PROP("wp.appPass"),
  WP_FULLY_QUALIFIED_DOMAIN_NAME_PROP("wp.fqdm"),
  WP_USER_ENV("WP_USER"),
  WP_APPLICATION_PASS_ENV("WP_APP_PASS"),
  WP_FULLY_QUALIFIED_DOMAIN_NAME_ENV("WP_FQDM");

  private final String value;

  EnvironmentScope(String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
