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
