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
import net.ygbstudio.powerwp4j.models.schema.WPQueryParam;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.utils.functional.TypedTrigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WPSiteEngineTest {

  private String user;
  private String appPass;
  private String fqdm;

  private static final Logger wpSiteEngineTestLogger =
      LoggerFactory.getLogger(WPSiteEngineTest.class);

  @BeforeEach
  void setUp() {
    Optional<Properties> props = getPropertiesFromResources("appConfig.properties");
    if (props.isPresent()) {
      Properties appProps = props.get();
      this.user = appProps.getProperty("wp.user");
      this.appPass = appProps.getProperty("wp.applicationPass");
      this.fqdm = appProps.getProperty("wp.fullyQualifiedDomainName");
    }
  }

  String makeSiteUrl(WPSiteEngine engineInstance, long pageNumber) {
    Map<WPQueryParam, String> queryParams = new EnumMap<>(WPQueryParam.class);
    if (pageNumber > 0) queryParams.put(WPQueryParam.PAGE, String.valueOf(pageNumber));
    queryParams.put(WPQueryParam.PER_PAGE, "10");
    return engineInstance.getApiBasePath()
        + WPRestPath.POSTS
        + WPQueryParam.joinQueryParams(queryParams);
  }

  @Test
  void connectWPTest() {
    assertThat(user, not(emptyOrNullString()));
    assertThat(appPass, not(emptyOrNullString()));
    WPSiteEngine wpSiteEngine = new WPSiteEngine(fqdm, user, appPass);
    Map<WPQueryParam, String> queryParams = Map.of(WPQueryParam.PER_PAGE, "10");
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
  void makeRequestTest() {
    WPSiteEngine wpSiteEngine = new WPSiteEngine("https://example.com", user, appPass);
    String url = makeSiteUrl(wpSiteEngine, 0);
    assertThat(url, is("https://example.com/wp-json/wp/v2/posts?per_page=10"));
  }

  @Test
  @Disabled
  void fetchJsonCacheTest() {
    File cache = new File("wp-posts.json");
    WPSiteEngine wpSiteEngine = new WPSiteEngine(fqdm, user, appPass, cache.toPath());
    try {
      wpSiteEngine.fetchJsonCache(true);
    } catch (IOException e) {
      wpSiteEngineTestLogger.error("Exception caught in fetchJsonCacheTest", e);
    } finally {
      cache.deleteOnExit();
    }
  }
}
