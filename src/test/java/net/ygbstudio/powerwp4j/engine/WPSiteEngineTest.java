package net.ygbstudio.powerwp4j.engine;

import static net.ygbstudio.powerwp4j.utils.Helpers.getPropertiesFromResources;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.ygbstudio.powerwp4j.base.extension.QueryParamEnum;
import net.ygbstudio.powerwp4j.builders.WPBasicPayloadBuilder;
import net.ygbstudio.powerwp4j.builders.WPMediaPayloadBuilder;
import net.ygbstudio.powerwp4j.models.schema.WPPostType;
import net.ygbstudio.powerwp4j.models.schema.WPQueryParam;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.models.schema.WPStatus;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import net.ygbstudio.powerwp4j.utils.functional.TypedTrigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

class WPSiteEngineTest {

  private String user;
  private String appPass;
  private String fqdm;
  private WPSiteEngine wpSite;
  private final File cacheFile = new File("wp-posts.json");
  private final WPBasicPayloadBuilder payloadBuilder = WPBasicPayloadBuilder.builder();

  private static final Logger wpSiteEngineTestLogger =
      LoggerFactory.getLogger(WPSiteEngineTest.class);

  @BeforeEach
  void setUp() {
    // Create this file in the resources folder
    Optional<Properties> props = getPropertiesFromResources("appConfig.properties");
    if (props.isPresent()) {
      Properties appProps = props.get();
      this.user = appProps.getProperty("wp.user");
      this.appPass = appProps.getProperty("wp.applicationPass");
      this.fqdm = appProps.getProperty("wp.fullyQualifiedDomainName");
    }
    wpSite = new WPSiteEngine(fqdm, user, appPass, cacheFile.toPath());
  }

  String makeSiteUrl(WPSiteEngine engineInstance, long pageNumber) {
    Map<WPQueryParam, String> queryParams = new EnumMap<>(WPQueryParam.class);
    if (pageNumber > 0) queryParams.put(WPQueryParam.PAGE, String.valueOf(pageNumber));
    queryParams.put(WPQueryParam.PER_PAGE, "10");
    return engineInstance.getApiBasePath()
        + WPRestPath.POSTS
        + QueryParamEnum.joinQueryParams(queryParams);
  }

  @Test
  void connectWPTest() {
    assertThat(user, not(emptyOrNullString()));
    assertThat(appPass, not(emptyOrNullString()));
    // Constructor without cachePath
    WPSiteEngine wpSiteEngine = new WPSiteEngine(fqdm, user, appPass);
    Map<QueryParamEnum, String> queryParams = Map.of(WPQueryParam.PER_PAGE, "10");
    try {
      Optional<HttpResponse<String>> wpSiteEngineResponse =
          wpSiteEngine.connectWP(queryParams, WPRestPath.POSTS);
      if (wpSiteEngineResponse.isPresent()) {
        Map<String, List<String>> headers = wpSiteEngineResponse.get().headers().map();
        Long wpTotal = Long.parseLong(headers.get("x-wp-total").getFirst());
        Long wpTotalPages = Long.parseLong(headers.get("x-wp-totalpages").getFirst());
        assertThat(wpTotal, notNullValue());
        assertThat(wpTotalPages, notNullValue());
      }
    } catch (Exception e) {
      wpSiteEngineTestLogger.error("Exception caught in connectWPTest", e);
    }
  }

  @Test
  void makeRequestNoLeadingParameterTest() {
    assertThatException()
        .isThrownBy(() -> makeSiteUrl(wpSite, 0))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void makeRequestTest() {
    WPSiteEngine wpSiteEngine = new WPSiteEngine("example.com", "user", "pass");
    String url = makeSiteUrl(wpSiteEngine, 1);
    assertThat(url, is("https://example.com/wp-json/wp/v2/posts?page=1&per_page=10"));
  }

  /**
   * Tests the fetchJsonCache method. This is a controlled test that requires the user to add a post
   * and then run the {@code cacheSync} method to make sure it updates the cache as expected. That
   * is the reason why this test is disabled by default due to manual intervention.
   *
   * <p>The following tests have also been disabled for a similar reason as every WordPress site is
   * different.
   */
  @Test
  @Disabled
  void fetchJsonCacheTest() {
    TypedTrigger<Exception> exceptionMessage =
        ex -> wpSiteEngineTestLogger.error("Exception caught in fetchJsonCacheTest", ex);
    try {
      wpSite.fetchJsonCache(true);
      boolean updatePerformed = wpSite.cacheSync();
      // No update took place
      assertThat(updatePerformed, is(false));
      System.out.println("Sleeping....");
      // Add a post - Adjust the time as needed
      TimeUnit.SECONDS.sleep(45);
      updatePerformed = wpSite.cacheSync();
      // Then the cacheSync algorithm must detect the change and update
      assertThat(updatePerformed, is(true));
      // No update will take place because the cache is up-to-date.
      updatePerformed = wpSite.cacheSync();
      assertThat(updatePerformed, is(false));
    } catch (IOException e) {
      exceptionMessage.activate(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      exceptionMessage.activate(e);
    }
  }

  @Test
  @Disabled
  void wpSiteEngineCreatePostTest() {
    // Create a post
    payloadBuilder
        .clear()
        .title("Test Post")
        .status(WPStatus.DRAFT)
        .slug("test-posts-2025")
        .content("This is a test post")
        .featuredMedia(0)
        .type(WPPostType.POST)
        .categories(List.of(1, 2))
        .tags(List.of(45, 89));
    wpSiteEngineTestLogger.info(payloadBuilder.build().toString());
    Optional<HttpResponse<String>> response = wpSite.createPost(payloadBuilder.build());
    assertThat(response.isPresent(), is(true));
    assertThat(response.get().statusCode(), is(201));
    assertThat(response.get().body(), not(emptyOrNullString()));
    wpSiteEngineTestLogger.info(response.get().body());

    // Change post status
    ObjectMapper mapper = JsonSupport.getMapper();
    response = wpSite.createPost(payloadBuilder.build());
    long createdId = 0;
    if (response.isPresent()) {
      System.out.println(response.get().body());
      createdId =
          response
              .map(HttpResponse::body)
              .map(mapper::readTree)
              .map(item -> item.get("id").asLong())
              .get();
    }

    response = wpSite.changePostStatus(createdId, WPStatus.TRASH);
    wpSiteEngineTestLogger.info(response.get().body());

    if (createdId > 0) {
      // Delete post if created
      response = wpSite.deletePost(createdId);
      System.out.println(response.get().body());
    } else {
      wpSiteEngineTestLogger.error("Post not created");
    }
  }

  @Test
  @Disabled
  void wpSiteEngineUploadMediaTest() {
    WPMediaPayloadBuilder mediaPayloadBuilder = WPMediaPayloadBuilder.builder();
    mediaPayloadBuilder
        .altText("this is a sample image")
        .caption("this is a sample image")
        .description("screenshot");
    wpSiteEngineTestLogger.info(mediaPayloadBuilder.build().toString());
    // Make sure to change this to the file you'll be uploading
    Optional<HttpResponse<String>> response =
        wpSite.uploadMedia(Path.of("sample.png"), mediaPayloadBuilder.build());
    assertThat(response.isPresent(), is(true));
    wpSiteEngineTestLogger.info(response.get().body());
  }

  @Test
  @Disabled
  void wpEngineAddTagTest() {
    payloadBuilder.clear().name("powerwp4j");
    Optional<HttpResponse<String>> response = wpSite.addTag(payloadBuilder.build());
    assertThat(response.isEmpty(), is(false));
    wpSiteEngineTestLogger.info(response.get().body());
  }

  @Test
  @Disabled
  void wpEngineAddCategoryTest() {
    payloadBuilder.clear().name("powerwp4j-category");
    Optional<HttpResponse<String>> response = wpSite.addCategory(payloadBuilder.build());
    assertThat(response.isEmpty(), is(false));
    wpSiteEngineTestLogger.info(response.get().body());
  }
}
