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
