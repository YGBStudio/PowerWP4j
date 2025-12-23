package net.ygbstudio.powerwp4j.models.entities;

import org.jspecify.annotations.NonNull;

/**
 * A record that represents a 1-to-1 mapping between a key and a value.
 *
 * <p>This class is immutable and cannot be modified once created.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public record WPClassMapping<K, V>(@NonNull K key, @NonNull V value) {}
