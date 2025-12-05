package net.ygbstudio.powerwp4j.models.schema;

import java.util.Map;
import java.util.function.Consumer;
import org.jspecify.annotations.NullMarked;

/**
 * WPQueryParam is an enum that represents the query parameters for the WordPress REST API.
 *
 * @author Yoham Gabriel B. @ YGBStudio
 */
@NullMarked
public enum WPQueryParam {
  STATUS("?status="),
  PER_PAGE("?per_page="),
  PAGE("?page="),
  FIELDS_BASE("?_fields=");

  private final String value;

  WPQueryParam(String value) {
    this.value = value;
  }

  public static String joinQueryParams(Map<WPQueryParam, String> wpRestQueriesMap) {
    StringBuilder pathString = new StringBuilder();
    Consumer<Map.Entry<WPQueryParam, String>> appendToPath =
        entry -> {
          if (!pathString.isEmpty()) pathString.append("&");
          pathString.append(entry.getKey());
          pathString.append(entry.getValue());
        };
    wpRestQueriesMap.entrySet().forEach(appendToPath);
    return pathString.toString();
  }

  @Override
  public String toString() {
    return value;
  }
}
