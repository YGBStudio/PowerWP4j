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

package net.ygbstudio.powerwp4j.utils;

import java.io.File;
import java.io.Reader;
import org.jspecify.annotations.NullMarked;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * JsonSupport provides JSON serialization and deserialization support using Jackson.
 *
 * <p>This class exists to reuse common JSON operations across different classes without code
 * duplication. It also provides a pre-configured ObjectMapper instance for reuse, consistency and
 * maximum performance gain.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@NullMarked
public final class JsonSupport {

  private static final ObjectMapper jsonMapper = configureMapper();

  private JsonSupport() {}

  /**
   * Configures and returns a pre-configured ObjectMapper instance.
   *
   * <p>This instance is configured to:
   * <li>Indent output for better readability.
   * <li>Fail on empty beans during serialization.
   * <li>Write dates as timestamps.
   * <li>Ignore unknown properties during deserialization.
   * <li>Accept empty strings as null objects during deserialization.
   *
   *     <p>The configured ObjectMapper is intended for reuse across different classes to ensure
   *     consistency and performance.
   *
   * @return a configured ObjectMapper instance
   */
  public static ObjectMapper configureMapper() {
    return JsonMapper.builder()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .configure(EnumFeature.WRITE_ENUMS_TO_LOWERCASE, true)
        .build();
  }

  /**
   * Returns the configured ObjectMapper instance that is reused throughout the application.
   *
   * @return the configured ObjectMapper instance
   */
  public static ObjectMapper getMapper() {
    return jsonMapper;
  }

  /**
   * Deserializes a JSON string into an object of the specified class.
   *
   * @param json the JSON string to deserialize
   * @param clazz the class of the object to deserialize into
   * @param <T> the type of the object to deserialize into
   * @return the deserialized object
   */
  public static <T> T readValueFromJson(String json, Class<T> clazz) {
    return jsonMapper.readValue(json, clazz);
  }

  /**
   * Serializes an object into a JSON string.
   *
   * @param obj the object to serialize
   * @return the JSON string representation of the object
   */
  public static String toJsonString(Object obj) {
    return jsonMapper.writeValueAsString(obj);
  }

  /**
   * Reads a JSON file from the filesystem and deserializes it into an object of the specified
   * class.
   *
   * @param jsonFile the JSON file to read
   * @param clazz the class of the object to deserialize into
   * @param <T> the type of the object to deserialize into
   * @return the deserialized object
   */
  public static <T> T readJsonFs(File jsonFile, Class<T> clazz) {
    return jsonMapper.readValue(jsonFile, clazz);
  }

  /**
   * Reads a JSON string from a Reader and deserializes it into an object of the specified class.
   *
   * @param source Reader instance for the JSON string to read from
   * @param clazz the class of the object to deserialize into
   * @return the deserialized object
   * @param <T> the type of the object to deserialize into
   */
  public static <T> T jsonReader(Reader source, Class<T> clazz) {
    return jsonMapper.readValue(source, clazz);
  }

  /**
   * Serializes an object and writes it to a JSON file on the filesystem.
   *
   * @param jsonFile the JSON file to write to
   * @param obj the object to serialize
   * @param <T> the type of the object to serialize
   */
  public static <T> void writeJsonFs(File jsonFile, T obj) {
    jsonMapper.writeValue(jsonFile, obj);
  }

  /**
   * Parses a JSON string into a JsonNode.
   *
   * @param jsonString the JSON string to parse
   * @return the parsed JsonNode
   */
  public static JsonNode getTreeNode(String jsonString) {
    return jsonMapper.readTree(jsonString);
  }

  /**
   * Reads a JSON file from the filesystem and parses it into a JsonNode.
   *
   * @param jsonFile the JSON file to read
   * @return the parsed JsonNode
   */
  public static JsonNode getTreeNode(File jsonFile) {
    return jsonMapper.readTree(jsonFile);
  }
}
