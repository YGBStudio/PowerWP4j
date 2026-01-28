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

package net.ygbstudio.powerwp4j.exceptions;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

/**
 * CacheConstructionException is an exception that is thrown when there is an error constructing the
 * cache.
 *
 * @author Yoham Gabriel B. @ YGBStudio
 */
public class CacheConstructionException extends RuntimeException {
  public CacheConstructionException(String message) {
    super(message);
  }

  public CacheConstructionException(@NotNull Supplier<String> message) {
    super(message.get());
  }
}
