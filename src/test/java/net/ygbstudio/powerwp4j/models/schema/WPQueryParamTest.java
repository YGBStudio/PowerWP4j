package net.ygbstudio.powerwp4j.models.schema;

import static org.assertj.core.api.Assertions.assertThatException;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import net.ygbstudio.powerwp4j.base.extension.enums.QueryParamEnum;
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
    String resultingPath = QueryParamEnum.joinQueryParams(queryParamMap);
    assertThat(resultingPath, StringRegularExpression.matchesRegex(expectedRegEx));
  }

  @Test
  void joinQueryParamsMapReversed() {
    LinkedHashMap<QueryParamEnum, String> reverseMap = new LinkedHashMap<>(queryParamMap);
    reverseMap.reversed();
    String resultingPathReversedParams = QueryParamEnum.joinQueryParams(reverseMap);
    assertThat(resultingPathReversedParams, StringRegularExpression.matchesRegex(expectedRegEx));
  }

  @Test
  void joinQueryParamsWithTimestamp() {
    Map<QueryParamEnum, String> queryParams =
        Map.of(
            WPQueryParam.PAGE, "3",
            WPQueryParam.PER_PAGE, "8",
            WPQueryParam.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
    String resultingPath = QueryParamEnum.joinQueryParams(queryParams);
    assertThat(resultingPath, StringRegularExpression.matchesRegex(expectedRegEx));
  }

  @Test
  void joinQueryParamEmptyMapTest() {
    assertThatException()
        .isThrownBy(() -> QueryParamEnum.joinQueryParams(Collections.emptyMap()))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
