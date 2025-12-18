/*
 * PowerWP4j - Power WP for Java
 * Copyright (C) 2025 Yoham Gabriel Barboza B.
 *
 * This file is part of PowerWP4j.
 *
 * PowerWP4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PowerWP4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package net.ygbstudio.powerwp4j.engine;

import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Optional;
import net.ygbstudio.powerwp4j.base.extension.PostStatusEnum;
import net.ygbstudio.powerwp4j.builders.WPBasicPayloadBuilder;
import net.ygbstudio.powerwp4j.exceptions.MediaUploadError;
import net.ygbstudio.powerwp4j.models.entities.WPSiteInfo;
import net.ygbstudio.powerwp4j.services.RestClientService;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
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

  protected WPRestClient(WPSiteInfo siteInfo) {
    this.siteInfo = siteInfo;
  }

  /**
   * Creates a new instance of {@link WPRestClient} using the provided {@link WPSiteInfo}.
   *
   * @param siteInfo the site information for the WordPress site
   * @return the newly created {@link WPRestClient} instance
   */
  @Contract(value = "_ -> new", pure = true)
  public static @NonNull WPRestClient of(WPSiteInfo siteInfo) {
    return new WPRestClient(siteInfo);
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
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), payload);
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
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), postId);
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
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), postId, builder.build());
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
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), payload);
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
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), payload);
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
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), attachmentPath, null);
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
        siteInfo.apiBaseUrl(), siteInfo.wpUser(), siteInfo.wpAppPass(), attachmentPath, payload);
  }
}
