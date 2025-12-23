package net.ygbstudio.powerwp4j.models.entities;

import java.util.Set;
import org.jspecify.annotations.NonNull;

/**
 * A record that represents a group of values grouped by a key.
 *
 * @param <T> the type of the key
 * @param <U> the type of the values
 */
public record WPClassGroup<T, U>(@NonNull T groupByKey, @NonNull Set<U> groupedValues) {
  public WPClassGroup {
    groupedValues = Set.copyOf(groupedValues);
  }
}
