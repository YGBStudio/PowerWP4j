package net.ygbstudio.powerwp4j.builders;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import net.ygbstudio.powerwp4j.models.schema.WPCacheKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

class WPMediaPayloadNodeBuilderTest {
  private final String altText = "image alt text";
  private final String caption = "image caption";
  private final String description = "test image";

  private final WPMediaPayloadNodeBuilder builder =
      WPMediaPayloadNodeBuilder.builder()
          .altText(altText)
          .caption(caption)
          .description(description);

  private JsonNode payloadNode;

  @BeforeEach
  void buildPayloadTest() {
    payloadNode = builder.build();
  }

  @Test
  void altTextTest() {
    assertThat(payloadNode.get(WPCacheKey.ALT_TEXT.value()).asString(), is(altText));
  }

  @Test
  void captionTest() {
    assertThat(payloadNode.get(WPCacheKey.CAPTION.value()).asString(), is(caption));
  }

  @Test
  void descriptionTest() {
    assertThat(payloadNode.get(WPCacheKey.DESCRIPTION.value()).asString(), is(description));
  }

  @Test
  void clearTest() {
    builder.clear();
    assertThat(builder.build().isEmpty(), is(true));
  }
}
