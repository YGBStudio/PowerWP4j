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

package net.ygbstudio.powerwp4j.engine;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Optional;
import net.ygbstudio.powerwp4j.base.extension.enums.PostStatusEnum;
import net.ygbstudio.powerwp4j.builders.WPBasicPayloadBuilder;
import net.ygbstudio.powerwp4j.exceptions.MediaUploadError;
import net.ygbstudio.powerwp4j.models.entities.WPPost;
import net.ygbstudio.powerwp4j.models.entities.WPSiteInfo;
import net.ygbstudio.powerwp4j.services.RestClientService;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.JsonNode;

/**
 * This class provides a facade for accessing the WordPress REST API. It wraps the {@link
 * RestClientService} class and provides a more user-friendly interface for interacting with the
 * WordPress site.
 *
 * @author Yoham Gabriel @ YGBStudio
 */
public class WPRestClient {
  private final WPSiteInfo siteInfo;
  private boolean ignoreSSL;

  protected WPRestClient(WPSiteInfo siteInfo) {
    this.siteInfo = siteInfo;
  }

  protected WPRestClient(WPSiteInfo siteInfo, boolean ignoreSSL) {
    this(siteInfo);
    this.ignoreSSL = ignoreSSL;
  }

  /**
   * Creates a new instance of {@link WPRestClient} using the provided {@link WPSiteInfo}.
   *
   * @param siteInfo the site information for the WordPress site
   * @return the newly created {@link WPRestClient} instance
   */
  @Contract(value = "_ -> new", pure = true)
  public static @NotNull WPRestClient of(WPSiteInfo siteInfo) {
    return new WPRestClient(siteInfo);
  }

  /**
   * Creates a new instance of {@link WPRestClient} using the provided {@link WPSiteInfo}. This
   * method is intended for testing purposes and can ignore SSL certificate issues for all REST API
   * calls.
   *
   * @param siteInfo the site information for the WordPress site
   * @param ignoreSSL whether to ignore SSL certificate issues. Suitable for local development only.
   * @return the newly created {@link WPRestClient} instance
   */
  @Contract(value = "_, _ -> new", pure = true)
  public static @NotNull WPRestClient testingOf(WPSiteInfo siteInfo, boolean ignoreSSL) {
    return new WPRestClient(siteInfo, ignoreSSL);
  }

  /**
   * Creates a new post in the WordPress site using the REST API.
   *
   * @param payload The JSON payload representing the post to be created.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   */
  public Optional<HttpResponse<String>> createPost(JsonNode payload) {
    return RestClientService.postCreate(
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), payload, ignoreSSL);
  }

  /**
   * Deletes a post from the WordPress site using the REST API.
   *
   * @param postId The ID of the post to be deleted.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   */
  public Optional<HttpResponse<String>> deletePost(long postId) {
    return RestClientService.postDelete(
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), postId, ignoreSSL);
  }

  /**
   * Changes the status of a post on the WordPress site using the REST API.
   *
   * @param postId The ID of the post to have its status changed.
   * @param status The new status of the post.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   */
  public Optional<HttpResponse<String>> changePostStatus(long postId, PostStatusEnum status) {
    WPBasicPayloadBuilder builder = WPBasicPayloadBuilder.builder();
    builder.status(status);
    return RestClientService.changePostStatus(
        siteInfo.apiBaseUrl(),
        siteInfo.wpUser(),
        siteInfo.wpAppPass(),
        postId,
        builder.build(),
        ignoreSSL);
  }

  /**
   * Adds a tag to the WordPress site using the REST API.
   *
   * @param payload The JSON payload representing the new tag.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   */
  public Optional<HttpResponse<String>> addTag(JsonNode payload) {
    return RestClientService.addTag(
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), payload, ignoreSSL);
  }

  /**
   * Adds a category to the WordPress site using the REST API.
   *
   * @param payload The JSON payload representing the new category.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   */
  public Optional<HttpResponse<String>> addCategory(JsonNode payload) {
    return RestClientService.addCategory(
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), payload, ignoreSSL);
  }

  /**
   * Uploads a media file to the WordPress site using the REST API.
   *
   * @param attachmentPath The file path of the media to be uploaded.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   * @throws MediaUploadError if the media upload request fails.
   */
  public Optional<HttpResponse<String>> uploadMedia(Path attachmentPath) {
    return RestClientService.uploadMedia(
        siteInfo.apiBaseUrl(),
        siteInfo.wpUser(),
        siteInfo.wpAppPass(),
        attachmentPath,
        null,
        ignoreSSL);
  }

  /**
   * Uploads a media file to the WordPress site using the REST API and attaches a payload to the
   * media file.
   *
   * @param attachmentPath The file path of the media to be uploaded.
   * @param payload The JSON payload representing the media.
   * @return An Optional containing the JSON response from the server if the request was successful,
   *     or an empty Optional if the request failed.
   * @throws MediaUploadError if the media upload request fails.
   */
  public Optional<HttpResponse<String>> uploadMedia(Path attachmentPath, JsonNode payload) {
    return RestClientService.uploadMedia(
        siteInfo.apiBaseUrl(),
        siteInfo.wpUser(),
        siteInfo.wpAppPass(),
        attachmentPath,
        payload,
        ignoreSSL);
  }

  /**
   * Fetches a single post by its ID.
   *
   * @param id The ID of the post to fetch.
   * @return An Optional containing the {@link WPPost} if found, or empty if not found or on error.
   */
  public Optional<WPPost> getPost(long id) {
    return RestClientService.postGet(
            siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), id, ignoreSSL)
        .flatMap(res -> JsonSupport.deserialize(res, WPPost.class));
  }
}
