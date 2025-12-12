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

package net.ygbstudio.powerwp4j.exceptions;

import java.util.function.Supplier;
import org.jspecify.annotations.NullMarked;

/**
 * CacheMetaDataException is an exception that is thrown when there is an error with the cache
 * metadata or its creation.
 *
 * @author Yoham Gabriel B. @ YGBStudio
 */
@NullMarked
public class CacheMetaDataException extends RuntimeException {
  public CacheMetaDataException(String message) {
    super(message);
  }

  public CacheMetaDataException(Supplier<String> message) {
    super(message.get());
  }
}
