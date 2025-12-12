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

import java.net.URISyntaxException;

/**
 * Exception thrown when an invalid API URL is provided and any of the internal methods of PowerWP4j
 * detect a {@link URISyntaxException}.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
public class InvalidApiUrlError extends Error {
  public InvalidApiUrlError(String message) {
    super(message);
  }

  public InvalidApiUrlError(String message, URISyntaxException uriSyntaxEx) {
    super(message, uriSyntaxEx);
  }
}
