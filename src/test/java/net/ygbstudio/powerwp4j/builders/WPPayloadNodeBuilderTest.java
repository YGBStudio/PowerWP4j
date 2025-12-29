package net.ygbstudio.powerwp4j.builders;

import static org.assertj.core.api.Assertions.assertThatException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Set;
import net.ygbstudio.powerwp4j.models.entities.WPClassMapping;
import net.ygbstudio.powerwp4j.models.schema.WPCacheKey;
import net.ygbstudio.powerwp4j.models.schema.WPStatus;
import net.ygbstudio.powerwp4j.models.taxonomies.TaxonomyValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

class WPPayloadNodeBuilderTest {

  private final int author = 1;
  private final String content = "sample content";
  private final String slug = "test-post-1";
  private final List<Integer> categories = List.of(1, 2, 3, 4);
  private final WPStatus status = WPStatus.DRAFT;
  private final Set<WPClassMapping<String, Integer>> tags =
      Set.of(new WPClassMapping<>("tag-1", 3), new WPClassMapping<>("tag-2", 4));

  private final WPPayloadNodeBuilder builder =
      WPPayloadNodeBuilder.builder()
          .author(author)
          .content(content)
          .slug(slug)
          .categories(categories)
          .tags(tags)
          .status(status);

  private JsonNode payloadNode;

  @BeforeEach
  void buildPayloadTest() {
    payloadNode = builder.build();
  }

  @Test
  void failNotAWholeNumberTags() {
    // Only whole numbers Integer, Long or Short are allowed for tags and categories
    assertThatException()
        .isThrownBy(
            () ->
                builder
                    .clear()
                    .tags(
                        Set.of(
                            new WPClassMapping<>("tag-1", 3.4),
                            new WPClassMapping<>("tag-2", 4.8))))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void failNotAWholeNumberCategories() {
    // Only whole numbers Integer, Long or Short are allowed for tags and categories
    assertThatException()
        .isThrownBy(() -> builder.clear().categories(List.of(2.5, 3.8)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void authorTest() {
    assertThat(payloadNode.get(WPCacheKey.AUTHOR.value()).asInt(), is(author));
  }

  @Test
  void contentTest() {
    assertThat(payloadNode.get(WPCacheKey.CONTENT.value()).asString(), is(content));
  }

  @Test
  void slugTest() {
    assertThat(payloadNode.get(WPCacheKey.SLUG.value()).asString(), is(slug));
  }

  @Test
  void categoriesTest() {
    assertThat(payloadNode.get(TaxonomyValues.CATEGORIES.value()).isArray(), is(true));
  }

  @Test
  void tagTest() {
    assertThat(payloadNode.get(TaxonomyValues.TAGS.value()).isArray(), is(true));
  }

  @Test
  void statusTest() {
    assertThat(payloadNode.get(WPCacheKey.STATUS.value()).asString(), is(status.value()));
  }

  @Test
  void clearTest() {
    builder.clear();
    assertThat(builder.build().isEmpty(), is(true));
  }
}
