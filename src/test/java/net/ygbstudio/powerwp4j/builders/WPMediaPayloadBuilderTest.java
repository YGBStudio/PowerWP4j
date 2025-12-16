package net.ygbstudio.powerwp4j.builders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

class WPMediaPayloadBuilderTest {

  private WPMediaPayloadBuilder builder;

  @BeforeEach
  void setUp() {
    builder = WPMediaPayloadBuilder.builder();
    builder
        .altText("this is alt text")
        .caption("this is a caption")
        .description("this is a description");
  }

  @Test
  void mediaPayloadBuildNoCamelCaseTest() {
    String camelCasePayloadStr =
        """
                {"altText":"this is alt text","caption":"this is a caption","description":"this is a description"}""";
    JsonNode payload = builder.build();
    assertNotEquals(camelCasePayloadStr, payload.toString());
  }

  @Test
  void mediaPayloadBuildSnakeCaseTest() {
    String snakeCasePayloadStr =
        """
            {"alt_text":"this is alt text","caption":"this is a caption","description":"this is a description"}""";
    JsonNode payload = builder.build();
    assertEquals(snakeCasePayloadStr, payload.toString());
  }
}
