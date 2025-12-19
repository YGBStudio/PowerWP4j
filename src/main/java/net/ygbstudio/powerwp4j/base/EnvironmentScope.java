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
  public String toString() {
    return value;
  }
}
