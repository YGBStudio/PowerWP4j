package net.ygbstudio.powerwp4j.exceptions;

import java.util.function.Supplier;

/**
 * CacheConstructionException is an exception that is thrown when there is an error
 * constructing the cache.
 *
 * @author Yoham Gabriel B. @ YGBStudio
 */
public class CacheConstructionException extends RuntimeException {
  public CacheConstructionException(String message) {
    super(message);
  }

  public CacheConstructionException(Supplier<String> message) {
    super(message.get());
  }
}
