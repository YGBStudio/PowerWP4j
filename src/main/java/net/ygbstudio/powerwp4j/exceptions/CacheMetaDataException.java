package net.ygbstudio.powerwp4j.exceptions;

import java.util.function.Supplier;
import org.jspecify.annotations.NullMarked;

/**
 * CacheMetaDataException is an exception that is thrown when there is an error
 * with the cache metadata or its creation.
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
