package net.ygbstudio.powerwp4j.engine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import net.ygbstudio.powerwp4j.builders.WPBasicPayloadBuilder;
import net.ygbstudio.powerwp4j.builders.WPMediaPayloadBuilder;
import net.ygbstudio.powerwp4j.models.entities.WPSiteInfo;
import net.ygbstudio.powerwp4j.models.schema.WPPostType;
import net.ygbstudio.powerwp4j.models.schema.WPStatus;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.ObjectMapper;

public class WPRestClientTest {

  private WPSiteInfo siteInfo;
  private final WPBasicPayloadBuilder payloadBuilder = WPBasicPayloadBuilder.builder();

  private static final Logger wpRestClientTestLogger =
      LoggerFactory.getLogger(WPRestClientTest.class);

  @BeforeEach
  void setUp() {
    // Create this file in the resources folder
    WPSiteInfo.fromConfigResource("appConfig.properties").ifPresent(site -> siteInfo = site);
  }

  @Test
  @Disabled
  void wpRestClientCreatePostTest() {
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
    wpRestClientTestLogger.info(payloadBuilder.build().toString());
    WPRestClient client = WPRestClient.of(siteInfo);
    Optional<HttpResponse<String>> response = client.createPost(payloadBuilder.build());
    assertThat(response.isPresent(), is(true));
    assertThat(response.get().statusCode(), is(201));
    assertThat(response.get().body(), not(emptyOrNullString()));
    wpRestClientTestLogger.info(response.get().body());

    // Change post status
    ObjectMapper mapper = JsonSupport.getMapper();
    response = client.createPost(payloadBuilder.build());
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

    response = client.changePostStatus(createdId, WPStatus.TRASH);
    wpRestClientTestLogger.info(response.get().body());

    if (createdId > 0) {
      // Delete post if created
      response = client.deletePost(createdId);
      System.out.println(response.get().body());
    } else {
      wpRestClientTestLogger.error("Post not created");
    }
  }

  @Test
  @Disabled
  void wpRestClientUploadMediaTest() {
    WPMediaPayloadBuilder mediaPayloadBuilder = WPMediaPayloadBuilder.builder();
    mediaPayloadBuilder
        .altText("this is a sample image")
        .caption("this is a sample image")
        .description("screenshot");
    wpRestClientTestLogger.info(mediaPayloadBuilder.build().toString());
    // Make sure to change this to the file you'll be uploading
    WPRestClient client = WPRestClient.of(siteInfo);
    Optional<HttpResponse<String>> response =
        client.uploadMedia(Path.of("sample.png"), mediaPayloadBuilder.build());
    assertThat(response.isPresent(), is(true));
    wpRestClientTestLogger.info(response.get().body());
  }

  @Test
  @Disabled
  void wpRestClientAddTagTest() {
    payloadBuilder.clear().name("powerwp4j");
    WPRestClient client = WPRestClient.of(siteInfo);
    Optional<HttpResponse<String>> response = client.addTag(payloadBuilder.build());
    assertThat(response.isEmpty(), is(false));
    wpRestClientTestLogger.info(response.get().body());
  }

  @Test
  @Disabled
  void wpRestClientAddCategoryTest() {
    payloadBuilder.clear().name("powerwp4j-category");
    WPRestClient client = WPRestClient.of(siteInfo);
    Optional<HttpResponse<String>> response = client.addCategory(payloadBuilder.build());
    assertThat(response.isEmpty(), is(false));
    wpRestClientTestLogger.info(response.get().body());
  }
}
