/*
 * PowerWP4j - Power WP for Java
 *
 * Copyright 2025-2026 Yoham Gabriel Barboza B. (YGBStudio)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package net.ygbstudio.powerwp4j.services;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Optional;
import net.ygbstudio.powerwp4j.exceptions.MediaUploadError;
import net.ygbstudio.powerwp4j.models.schema.WPRestPath;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import net.ygbstudio.powerwp4j.utils.functional.TypedTrigger;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * The RestClientService class provides methods for interacting with the WordPress REST API for
 * creating, reading, updating, and deleting posts, pages, and other entities.
 *
 * <p>This class is a utility service that provides convenience methods for common operations on the
 * WordPress REST API. It is not intended to be instantiated, and its methods are static.
 *
 * @author Yoham Gabriel B.
 */
public final class RestClientService {
  private static final Logger restClientServiceLogger =
      LoggerFactory.getLogger(RestClientService.class);

  private RestClientService() {}

  /**
   * Creates a new post in the WordPress site using the REST API.
   *
   * @param apiBasePath The base path of the WordPress REST API.
   * @param username The username for authentication.
   * @param applicationPassword The application password for authentication.
   * @param postPayload The JSON payload representing the post to be created.
   * @param ignoreSSLHandshakeException Whether to ignore SSL Handshake Exception, useful for
   *     testing purposes or local environments.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   */
  public static Optional<HttpResponse<String>> postCreate(
      String apiBasePath,
      String username,
      String applicationPassword,
      JsonNode postPayload,
      boolean ignoreSSLHandshakeException) {
    String url = HttpRequestService.makeRequestURL(apiBasePath, null, WPRestPath.POSTS);
    HttpRequest request =
        HttpRequestService.buildWpPostRequest(
            postPayload, url, username, applicationPassword, restClientServiceLogger);
    return HttpRequestService.clientSend(
        request, restClientServiceLogger, ignoreSSLHandshakeException);
  }

  /**
   * Deletes a post from the WordPress site using the REST API.
   *
   * @param apiBasePath The base path of the WordPress REST API.
   * @param username The username for authentication.
   * @param applicationPassword The application password for authentication.
   * @param postId The ID of the post to be deleted.
   * @param ignoreSSLHandshakeException Whether to ignore SSL Handshake Exception, useful for
   *     testing purposes or local environments.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   */
  public static Optional<HttpResponse<String>> postDelete(
      String apiBasePath,
      String username,
      String applicationPassword,
      long postId,
      boolean ignoreSSLHandshakeException) {
    String url =
        HttpRequestService.makeRequestURL(apiBasePath, null, WPRestPath.POSTS) + "/" + postId;
    HttpRequest deleteRequest =
        HttpRequestService.buildWpDeleteRequest(
            url, username, applicationPassword, restClientServiceLogger);
    return HttpRequestService.clientSend(
        deleteRequest, restClientServiceLogger, ignoreSSLHandshakeException);
  }

  /**
   * Changes the status of a post on the WordPress site using the REST API.
   *
   * @param apiBasePath The base path of the WordPress REST API.
   * @param username The username for authentication.
   * @param applicationPassword The application password for authentication.
   * @param postId The ID of the post to have its status changed.
   * @param payload The JSON payload representing the new status of the post.
   * @param ignoreSSLHandshakeException Whether to ignore SSL Handshake Exception, useful for
   *     testing purposes or local environments.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   */
  public static Optional<HttpResponse<String>> changePostStatus(
      String apiBasePath,
      String username,
      String applicationPassword,
      long postId,
      JsonNode payload,
      boolean ignoreSSLHandshakeException) {
    String url =
        HttpRequestService.makeRequestURL(apiBasePath, null, WPRestPath.POSTS) + "/" + postId;
    HttpRequest changeStatusRequest =
        HttpRequestService.buildWpPostRequest(
            payload, url, username, applicationPassword, restClientServiceLogger);
    return HttpRequestService.clientSend(
        changeStatusRequest, restClientServiceLogger, ignoreSSLHandshakeException);
  }

  /**
   * Adds a tag to the WordPress site using the REST API.
   *
   * @param apiBasePath The base path of the WordPress REST API.
   * @param username The username for authentication.
   * @param applicationPassword The application password for authentication.
   * @param payload The JSON payload representing the new tag.
   * @param ignoreSSLHandshakeException Whether to ignore SSL Handshake Exception, useful for
   *     testing purposes or local environments.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   */
  public static Optional<HttpResponse<String>> addTag(
      String apiBasePath,
      String username,
      String applicationPassword,
      JsonNode payload,
      boolean ignoreSSLHandshakeException) {
    String url = HttpRequestService.makeRequestURL(apiBasePath, null, WPRestPath.TAGS);
    HttpRequest addTagRequest =
        HttpRequestService.buildWpPostRequest(
            payload, url, username, applicationPassword, restClientServiceLogger);
    return HttpRequestService.clientSend(
        addTagRequest, restClientServiceLogger, ignoreSSLHandshakeException);
  }

  /**
   * Adds a category to the WordPress site using the REST API.
   *
   * @param apiBasePath The base path of the WordPress REST API.
   * @param username The username for authentication.
   * @param applicationPassword The application password for authentication.
   * @param payload The JSON payload representing the new category.
   * @param ignoreSSLHandshakeException Whether to ignore SSL Handshake Exception, useful for
   *     testing purposes or local environments.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   */
  public static Optional<HttpResponse<String>> addCategory(
      String apiBasePath,
      String username,
      String applicationPassword,
      JsonNode payload,
      boolean ignoreSSLHandshakeException) {
    String url = HttpRequestService.makeRequestURL(apiBasePath, null, WPRestPath.CATEGORIES);
    HttpRequest addCategoryRequest =
        HttpRequestService.buildWpPostRequest(
            payload, url, username, applicationPassword, restClientServiceLogger);
    return HttpRequestService.clientSend(
        addCategoryRequest, restClientServiceLogger, ignoreSSLHandshakeException);
  }

  /**
   * Fetches a single post by its ID from the WordPress site using the REST API.
   *
   * @param apiBasePath The base path of the WordPress REST API.
   * @param username The username for authentication.
   * @param applicationPassword The application password for authentication.
   * @param postId The ID of the post to fetch.
   * @param ignoreSSLHandshakeException Whether to ignore SSL Handshake Exception.
   * @return An Optional containing the JSON response from the server if the request was successful.
   */
  public static Optional<HttpResponse<String>> postGet(
      String apiBasePath,
      String username,
      String applicationPassword,
      long postId,
      boolean ignoreSSLHandshakeException) {
    String url =
        HttpRequestService.makeRequestURL(apiBasePath, null, WPRestPath.POSTS) + "/" + postId;
    HttpRequest getRequest =
        HttpRequestService.buildWpGetRequest(
            url, username, applicationPassword, restClientServiceLogger);
    return HttpRequestService.clientSend(
        getRequest, restClientServiceLogger, ignoreSSLHandshakeException);
  }

  /**
   * Uploads a media file to the WordPress site using the REST API.
   *
   * <p>Adding a payload to a media file is supported, however, the method will process two requests
   * to the WordPress REST API. The first request will upload the media file, and the second request
   * will update the media file with the provided payload.
   *
   * <p>It is possible to set the payload to {@code null} and only upload the media file, in which
   * case the method will do so in one request.
   *
   * @param apiBasePath The base path of the WordPress REST API.
   * @param username The username for authentication.
   * @param applicationPassword The application password for authentication.
   * @param attachmentPath The file path of the media to be uploaded.
   * @param ignoreSSLHandshakeException Whether to ignore SSL Handshake Exception, useful for
   *     testing purposes or local environments.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   * @throws MediaUploadError if the media upload request fails.
   */
  public static Optional<HttpResponse<String>> uploadMedia(
      String apiBasePath,
      String username,
      String applicationPassword,
      Path attachmentPath,
      @Nullable JsonNode payload,
      boolean ignoreSSLHandshakeException) {

    TypedTrigger<String> mediaUploadError =
        url -> {
          throw new MediaUploadError(
              () ->
                  String.format(
                      "Media Upload to %s failed. Check your connection or site options and try again",
                      url));
        };
    String url = HttpRequestService.makeRequestURL(apiBasePath, null, WPRestPath.MEDIA);
    Optional<HttpRequest> mediaUpload =
        HttpRequestService.buildWpPostRequest(
            url, username, applicationPassword, attachmentPath, restClientServiceLogger);
    if (mediaUpload.isEmpty()) mediaUploadError.activate(url);
    if (payload == null) {
      return HttpRequestService.clientSend(
          mediaUpload.get(), restClientServiceLogger, ignoreSSLHandshakeException);
    }
    ObjectMapper mapper = JsonSupport.getMapper();
    Optional<Long> mediaId =
        HttpRequestService.clientSend(
                mediaUpload.get(), restClientServiceLogger, ignoreSSLHandshakeException)
            .map(HttpResponse::body)
            .map(mapper::readTree)
            .map(item -> item.get("id").asLong());
    if (mediaId.isEmpty()) mediaUploadError.activate(url);
    url = url + "/" + mediaId.get();
    HttpRequest mediaUpdate =
        HttpRequestService.buildWpPostRequest(
            payload, url, username, applicationPassword, restClientServiceLogger);
    return HttpRequestService.clientSend(
        mediaUpdate, restClientServiceLogger, ignoreSSLHandshakeException);
  }
}
