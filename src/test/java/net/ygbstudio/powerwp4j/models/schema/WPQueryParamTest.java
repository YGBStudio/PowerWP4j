package net.ygbstudio.powerwp4j.models.schema;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import net.ygbstudio.powerwp4j.extension.QueryParamEnum;
import org.hamcrest.core.StringRegularExpression;
import org.junit.jupiter.api.Test;

class WPQueryParamTest {

  private final Map<QueryParamEnum, String> queryParamMap =
      Map.of(
          WPQueryParam.PAGE, "3",
          WPQueryParam.PER_PAGE, "8");

  private final Pattern expectedRegEx =
      Pattern.compile("\\?[a-zA-Z0-9_-]+=[a-zA-Z0-9_-]+(?:&[a-zA-Z0-9_-]+=[a-zA-Z0-9_-]+)*$");

  @Test
  void joinQueryParamsTest() {
    String resultingPath = WPQueryParam.joinQueryParams(queryParamMap);
    assertThat(resultingPath, StringRegularExpression.matchesRegex(expectedRegEx));
  }

  @Test
  void joinQueryParamsMapReversed() {
    LinkedHashMap<WPQueryParam, String> reverseMap = new LinkedHashMap<>(queryParamMap);
    reverseMap.reversed();
    String resultingPathReversedParams = WPQueryParam.joinQueryParams(reverseMap);
    assertThat(resultingPathReversedParams, StringRegularExpression.matchesRegex(expectedRegEx));
  }

  @Test
  void joinQueryParamsWithTimestamp() {
    Map<WPQueryParam, String> queryParams =
        Map.of(
            WPQueryParam.PAGE, "3",
            WPQueryParam.PER_PAGE, "8",
            WPQueryParam.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
    String resultingPath = WPQueryParam.joinQueryParams(queryParams);
    assertThat(resultingPath, StringRegularExpression.matchesRegex(expectedRegEx));
  }

  @Test
  void joinQueryParamEmptyMapTest() {
    String resultingPath = WPQueryParam.joinQueryParams(Collections.emptyMap());
    assertThat(resultingPath, is(""));
  }
}
