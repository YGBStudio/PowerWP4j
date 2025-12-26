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

package net.ygbstudio.powerwp4j.base.extension;

import org.jetbrains.annotations.Contract;

/**
 * This interface is designed to enforce a consistent approach to Java enums in the PowerWP4j
 * project. It provides a uniform way to access the value of enum constants, which is the actual
 * string to be used in the API. By extending this interface, enums become more user-friendly and
 * easier to work with. This interface is sealed because all extension enum classes implement it and
 * no other class should implement it to avoid misuse.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public sealed interface FriendlyEnum
    permits CacheKeyEnum,
        CacheSubKeyEnum,
        ClassMarkerEnum,
        ClassValueKeyEnum,
        CommentStatusEnum,
        EnvironmentScope,
        PostFormatEnum,
        PostStatusEnum,
        PostTypeEnum,
        QueryParamEnum,
        RestPathEnum,
        URLFieldsEnum {

  /**
   * Returns the value of the enum constant, which is the actual string to be used in the API.
   *
   * <p>Example: If an enum constant is named {@code STATUS_PENDING}, the value returned by this
   * method would be {@code "pending"}, which is the value provided in its constructor.
   *
   * @return the value of the enum constant
   */
  @Contract(pure = true)
  String value();
}
