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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.ygbstudio.powerwp4j.base.extension.QueryParamEnum;
import net.ygbstudio.powerwp4j.builders.WPBasicPayloadBuilder;
import net.ygbstudio.powerwp4j.models.entities.WPSiteInfo;
import net.ygbstudio.powerwp4j.models.schema.WPQueryParam;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.utils.functional.TypedTrigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WPCacheManagerTest {

  private WPSiteInfo siteInfo;
  private WPCacheManager wpSite;
  private final File cacheFile = new File("wp-posts.json");
  private final WPBasicPayloadBuilder payloadBuilder = WPBasicPayloadBuilder.builder();

  private static final Logger wpCacheManagerTestLogger =
      LoggerFactory.getLogger(WPCacheManagerTest.class);

  @BeforeEach
  void setUp() {
    // Create this file in the resources folder
    Optional<Properties> props = getPropertiesFromResources("appConfig.properties");
      props.ifPresent(appProps -> this.siteInfo =
              new WPSiteInfo(
                      appProps.getProperty("wp.fullyQualifiedDomainName"),
                      appProps.getProperty("wp.user"),
                      appProps.getProperty("wp.applicationPass")));
    wpSite = new WPCacheManager(siteInfo, cacheFile.toPath());
  }

  String makeSiteUrl(WPCacheManager engineInstance, long pageNumber) {
    Map<WPQueryParam, String> queryParams = new EnumMap<>(WPQueryParam.class);
    if (pageNumber > 0) queryParams.put(WPQueryParam.PAGE, String.valueOf(pageNumber));
    queryParams.put(WPQueryParam.PER_PAGE, "10");
    return siteInfo.apiBaseUrl() + WPRestPath.POSTS + QueryParamEnum.joinQueryParams(queryParams);
  }

  @Test
  void connectWPTest() {
    assertThat(siteInfo.wpUser(), not(emptyOrNullString()));
    assertThat(siteInfo.wpAppPass(), not(emptyOrNullString()));
    // Constructor without cachePath
    WPCacheManager wpCacheManager = new WPCacheManager(siteInfo);
    Map<QueryParamEnum, String> queryParams = Map.of(WPQueryParam.PER_PAGE, "10");
    try {
      Optional<HttpResponse<String>> wpSiteEngineResponse =
          wpCacheManager.connectWP(queryParams, WPRestPath.POSTS);
      if (wpSiteEngineResponse.isPresent()) {
        Map<String, List<String>> headers = wpSiteEngineResponse.get().headers().map();
        Long wpTotal = Long.parseLong(headers.get("x-wp-total").getFirst());
        Long wpTotalPages = Long.parseLong(headers.get("x-wp-totalpages").getFirst());
        assertThat(wpTotal, notNullValue());
        assertThat(wpTotalPages, notNullValue());
      }
    } catch (Exception e) {
      wpCacheManagerTestLogger.error("Exception caught in connectWPTest", e);
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
    WPCacheManager wpCacheManager = new WPCacheManager("example.com", "user", "pass");
    String url = makeSiteUrl(wpCacheManager, 1);
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
        ex -> wpCacheManagerTestLogger.error("Exception caught in fetchJsonCacheTest", ex);
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
}
