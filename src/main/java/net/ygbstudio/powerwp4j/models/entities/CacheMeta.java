package net.ygbstudio.powerwp4j.models.entities;

import java.time.LocalDate;
import java.util.Objects;

/**
 * CacheMeta is a record that represents the metadata of the cache.
 *
 * @author Yoham Gabriel B. @ YGBStudio
 */
public record CacheMeta(long cachedPages, long cachedPosts, LocalDate lastUpdated) {

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    CacheMeta cacheMeta = (CacheMeta) o;
    return cachedPages() == cacheMeta.cachedPages()
        && cachedPosts() == cacheMeta.cachedPosts()
        && Objects.equals(lastUpdated(), cacheMeta.lastUpdated());
  }

  @Override
  public int hashCode() {
    return Objects.hash(cachedPages(), cachedPosts(), lastUpdated());
  }
}
