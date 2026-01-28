package net.ygbstudio.powerwp4j.models.entities;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * A record that represents a group of values grouped by a key.
 *
 * @param <T> the type of the key
 * @param <U> the type of the values
 */
public record WPClassGroup<T, U>(@NotNull T groupByKey, @NotNull Set<U> groupedValues) {
  public WPClassGroup {
    groupedValues = Set.copyOf(groupedValues);
  }
}
