package net.ygbstudio.powerwp4j.engine;

import static net.ygbstudio.powerwp4j.utils.Helpers.zip;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import net.ygbstudio.powerwp4j.exceptions.CacheFileSystemException;
import net.ygbstudio.powerwp4j.models.entities.WPCacheKeySnapshot;
import net.ygbstudio.powerwp4j.models.entities.WPClassGroup;
import net.ygbstudio.powerwp4j.models.entities.WPClassMapping;
import net.ygbstudio.powerwp4j.models.schema.WPCacheKey;
import net.ygbstudio.powerwp4j.models.taxonomies.TaxonomyMarker;
import net.ygbstudio.powerwp4j.models.taxonomies.TaxonomyValues;
import org.hamcrest.core.StringRegularExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WPCacheAnalyzerTest {

  private WPCacheAnalyzer analyzer;

  @BeforeEach
  void setUp() {
    String testCacheFilePath = "wp-posts-test.json";
    URL location = WPCacheAnalyzerTest.class.getClassLoader().getResource(testCacheFilePath);
    Path testCachePath = Path.of(location.getPath());
    analyzer = new WPCacheAnalyzer(testCachePath);
  }

  @Test
  void loadLocalCacheFileNotFoundTest() {
    WPCacheAnalyzer analyzerUniq = new WPCacheAnalyzer();
    assertThatException()
        .isThrownBy(() -> analyzerUniq.loadLocalCache(Path.of("does-not-exist.json")))
        .isInstanceOf(CacheFileSystemException.class);
  }

  @Test
  void testGetClassValueCount() {
    long count = analyzer.getClassValueCount(TaxonomyValues.TAGS);
    // A total of 24 tags in the test file
    assertThat(count, is(24L));
  }

  @Test
  void testGetTagCount() {
    long count = analyzer.getTagCount();
    assertThat(count, is(24L));
  }

  @Test
  void testGetCategoryCount() {
    long count = analyzer.getCategoryCount();
    assertThat(count, is(32L));
  }

  @Test
  void testGetPostCount() {
    long count = analyzer.getPostCount();
    assertThat(count, is(100L));
  }

  @Test
  void testGetSlugCount() {
    long count = analyzer.getSlugCount();
    assertThat(count, is(100L));
  }

  @Test
  void testPostsVsSlugs() {
    long postCount = analyzer.getPostCount();
    long slugCount = analyzer.getSlugCount();
    assertThat(postCount, is(slugCount));
  }

  @Test
  void testGetSlugVsCount() {
    Set<String> slugs = analyzer.getSlugs();
    long slugCount = analyzer.getSlugCount();
    assertThat(slugs.size(), is(Math.toIntExact(slugCount)));
  }

  @Test
  void testGetCategorySetVsCount() {
    Set<String> categories = analyzer.getCategories();
    long categoryCount = analyzer.getCategoryCount();
    assertThat(categories.size(), is(Math.toIntExact(categoryCount)));
  }

  @Test
  void testGetTagSetVsCount() {
    Set<String> tags = analyzer.getTags();
    long tagCount = analyzer.getTagCount();
    assertThat(tags.size(), is(Math.toIntExact(tagCount)));
  }

  @Test
  void testGetGuidsVsPostCount() {
    Set<WPCacheKeySnapshot<String>> guids = analyzer.getGuids();
    long postCount = analyzer.getPostCount();
    assertThat(guids.size(), is(Math.toIntExact(postCount)));
  }

  @Test
  void testGetContentsVsPostCountLessThanOrEqualTo() {
    Set<WPCacheKeySnapshot<String>> contents = analyzer.getContents();
    long postCount = analyzer.getPostCount();
    // As long as this number is not higher, it's fine, as not all posts are expected to have
    // content
    assertThat(contents.size(), is(lessThanOrEqualTo(Math.toIntExact(postCount))));
  }

  @Test
  void testMapClassWPClassIdSetVsTagCount() {
    UnaryOperator<String> cleanOperator =
        tag -> tag.replaceFirst("^tag-", "").replaceAll("[^a-zA-Z0-9]", " ").trim();
    Set<WPClassMapping<String, Long>> tags =
        analyzer
            .mapWPClassId(cleanOperator, TaxonomyMarker.TAG, TaxonomyValues.TAGS)
            .collect(Collectors.toSet());
    long tagCount = analyzer.getTagCount();
    assertThat(tags.size(), is(Math.toIntExact(tagCount)));
  }

  @Test
  void testMapClassWPClassIdSetVsCategoryCount() {
    Set<WPClassMapping<String, Long>> categories =
        analyzer
            .mapWPClassId(
                UnaryOperator.identity(), TaxonomyMarker.CATEGORY, TaxonomyValues.CATEGORIES)
            .collect(Collectors.toSet());
    long categoryCount = analyzer.getCategoryCount();
    assertThat(categories.size(), is(Math.toIntExact(categoryCount)));
  }

  @Test
  void testGroupClassMarkerByPostIdsTransformVsPostCount() {
    // Sample class marker transformation
    UnaryOperator<String> cleanMarkers =
        marker -> marker.replaceFirst("\\w+-\\b", "").replaceAll("-", " ").trim();
    Set<WPClassGroup<Long, String>> postIdClassLists =
        analyzer.groupClassMarkerByPostId(cleanMarkers).collect(Collectors.toSet());
    long postCount = analyzer.getPostCount();
    assertThat(postIdClassLists.size(), is(Math.toIntExact(postCount)));
    // Test the transformation step
    postIdClassLists.forEach(
        entry ->
            entry
                .groupedValues()
                .forEach(
                    marker ->
                        assertThat(
                            marker, StringRegularExpression.matchesRegex("\\w+(?:\\s\\w+)*"))));
  }

  @Test
  void testGroupClassMarkerByPostIdsVsClassListCounts() {
    List<Integer> postIdClassListCounts =
        analyzer
            .groupClassMarkerByPostId(UnaryOperator.identity())
            .map(group -> group.groupedValues().size())
            .sorted(Integer::compareTo)
            .toList();
    List<Long> classListElemCounts =
        analyzer
            .getCacheKeyArrayStream(WPCacheKey.CLASS_LIST)
            .map(arrayNode -> arrayNode.valueStream().count())
            .sorted(Long::compareTo)
            .toList();
    assertThat(postIdClassListCounts.size(), is(classListElemCounts.size()));
    zip(postIdClassListCounts, classListElemCounts)
        .forEach(elem -> assertThat(elem.getKey(), is(Math.toIntExact(elem.getValue()))));
  }

  @Test
  void testGroupPostIdsByClassMarker() {
    Set<WPClassGroup<Long, WPClassMapping<String, Long>>> groupSet =
        analyzer
            .groupPostIdsByClassMarker(TaxonomyMarker.TAG, TaxonomyValues.TAGS)
            .collect(Collectors.toSet());
    assertThat(groupSet.size(), is(Math.toIntExact(analyzer.getPostCount())));
  }

  @Test
  void testCalculateTermFrequencyByClassMarker() {
    long tagFrequency =
        analyzer.calculateTermFrequencyByClassMarker(
            TaxonomyMarker.TAG, new WPClassMapping<>("tag-iste-suscipit-at-et", 23L), false);
    assertThat(tagFrequency, is(10L));
  }

  @Test
  void testCalculateTermFrequencyByClassMarkerPartialMatch() {
    long partialTagFrequency =
        analyzer.calculateTermFrequencyByClassMarker(
            // Part of a real tag and not a real value
            TaxonomyMarker.TAG, new WPClassMapping<>("tag-iste", 23L), true);
    assertThat(partialTagFrequency, is(22L));
  }

  @Test
  void testCalculateTermFrequencyByClassValue() {
    long tagFrequency =
        analyzer.calculateTermFrequencyByClassValue(
            TaxonomyValues.TAGS, new WPClassMapping<>("tag-veniam", 36L));
    assertThat(tagFrequency, is(14L));
  }

  @Test
  void testCalculateTermFrequencyValueVsMarker() {
    long tagFrequency =
        analyzer.calculateTermFrequencyByClassMarker(
            TaxonomyMarker.TAG, new WPClassMapping<>("tag-veniam", 36L), false);
    assertThat(tagFrequency, is(14L));
  }
}
