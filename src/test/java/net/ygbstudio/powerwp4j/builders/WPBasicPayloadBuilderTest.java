package net.ygbstudio.powerwp4j.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;
import net.ygbstudio.powerwp4j.models.schema.WPCommentStatus;
import net.ygbstudio.powerwp4j.models.schema.WPStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

class WPBasicPayloadBuilderTest {

  private WPBasicPayloadBuilder builder;

  @BeforeEach
  void setUp() {
    builder = WPBasicPayloadBuilder.builder();
    builder
        .title("Test")
        .content("Test")
        .slug("test")
        .status(WPStatus.PUBLISH)
        .commentStatus(WPCommentStatus.OPEN)
        .categories(List.of(1))
        .tags(List.of(1))
        .author(1)
        .excerpt("Test");
  }

  @Test
  void basicPayloadBuildNoCamelCaseTest() {
    StringBuilder strBuilder =
        new StringBuilder("{")
            .append("\"author\":1")
            .append(",\"categories\":[1]")
            .append(",\"commentStatus\":\"open\"")
            .append(",\"content\":\"Test\"")
            .append(",\"excerpt\":\"Test\"")
            .append(",\"slug\":\"test\"")
            .append(",\"status\":\"publish\"")
            .append(",\"tags\":[1]")
            .append(",\"title\":\"Test\"")
            .append("}");

    JsonNode payload = builder.build();
    assertNotEquals(strBuilder.toString(), payload.toString());
  }

  @Test
  void basicPayloadBuildSnakeCaseTest() {
    StringBuilder strBuilder =
        new StringBuilder("{")
            .append("\"author\":1")
            .append(",\"categories\":[1]")
            .append(",\"comment_status\":\"open\"")
            .append(",\"content\":\"Test\"")
            .append(",\"excerpt\":\"Test\"")
            .append(",\"slug\":\"test\"")
            .append(",\"status\":\"publish\"")
            .append(",\"tags\":[1]")
            .append(",\"title\":\"Test\"")
            .append("}");

    JsonNode payload = builder.build();
    assertEquals(strBuilder.toString(), payload.toString());
  }
}
