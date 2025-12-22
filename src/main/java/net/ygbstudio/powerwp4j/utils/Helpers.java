/*
 * PowerWP4j - Power WP for Java
 *
 * Copyright 2025 Yoham Gabriel Barboza B. (YGBStudio)
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.ygbstudio.powerwp4j.base.FriendlyEnum;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Utility class for reusable logic in the project.
 *
 * <p>This class contains methods that can be used throughout the application to perform common
 * tasks, such as retrieving JSON-B property values from annotated classes or other utility
 * functions. It is designed to be a utility class, so it should not be instantiated.
 *
 * @author Yoham Gabriel @ YGB Studio
 */
@NullMarked
public final class Helpers {

  private Helpers() {
    throw new AssertionError("Cannot instantiate utility class");
  }

  /**
   * Loads properties from a specified property file located in the resources' directory.
   *
   * @param propertyFileName The name of the property file to load.
   * @return A {@link Properties} object containing the loaded properties.
   * @throws RuntimeException if an error occurs while reading the property file.
   */
  public static Optional<Properties> getPropertiesFromResources(String propertyFileName) {
    Properties properties = new Properties();
    Logger propertiesLogger = Logger.getLogger("Helpers: getPropertiesFromResources");

    try (InputStream in = Helpers.class.getResourceAsStream("/" + propertyFileName)) {
      if (Objects.isNull(in)) {
        propertiesLogger.warning("Properties file not found...");
        throw new FileNotFoundException(
            "Property file '" + propertyFileName + "' not found in resources.");
      }
      propertiesLogger.info("Properties file loaded successfully...");
      properties.load(in);
    } catch (IOException ioex) {
      propertiesLogger.warning(
          () ->
              "Error while loading property file. "
                  + ioex.getMessage()
                  + " "
                  + ioex.getCause()
                  + " "
                  + Arrays.toString(ioex.getStackTrace()));
      return Optional.empty();
    }

    return Optional.of(properties);
  }

  /**
   * Writes properties to a specified property file located in the resources directory, updating
   * existing properties or adding new ones.
   *
   * <p>Exceptions are relayed to the caller for handling.
   *
   * @param propertyFileName The name of the property file to write to.
   * @param properties An array of property names to write.
   * @param fileComment A comment to include in the property file.
   * @param values An array of values corresponding to the properties.
   * @throws IOException if an error occurs while writing to the property file.
   * @throws URISyntaxException if the URI of the property file cannot be resolved.
   */
  public static void writePropertyFile(
      String propertyFileName, List<String> properties, List<String> values, String fileComment)
      throws IOException, URISyntaxException {
    Optional<Properties> propsFromResources = getPropertiesFromResources(propertyFileName);
    URI resourceURI = Helpers.class.getResource("/" + propertyFileName).toURI();
    File propsFile = new File(resourceURI);
    if (propsFile.exists()) {
      try (FileInputStream in = new FileInputStream(propsFile.toString())) {
        if (propsFromResources.isPresent()) propsFromResources.get().load(in);
      }
    }

    List<Entry<String, String>> propertiesEntries = zip(properties, values).toList();

    if (propsFromResources.isPresent()) {
      propertiesEntries.forEach(
          entry -> propsFromResources.get().setProperty(entry.getKey(), entry.getValue()));

      try (FileOutputStream out = new FileOutputStream(propsFile)) {
        propsFromResources.get().store(out, fileComment);
      }
    }
  }

  /**
   * Zips two lists into a stream of map entries, pairing elements from both lists by their indices.
   * Contents are returned as a stream to allow for further processing or collection strategies.
   *
   * <p>The resulting stream will contain entries where the key is from the first list and the value
   * is from the second list.
   *
   * <p>if the lists are of different lengths, the resulting stream will only contain entries up to
   * the length of the shorter list.
   *
   * @param <K> type parameter for the key element
   * @param <V> type parameter for the value element
   * @param elementOne A list of elements to be used as keys in the map entries.
   * @param elementTwo A list of elements to be used as values in the map entries.
   * @return A stream of map entries where each entry pairs an element from the first list with an
   *     element from the second list.
   */
  public static <K, V> Stream<Map.Entry<K, V>> zip(
      List<? extends K> elementOne, List<? extends V> elementTwo) {
    return IntStream.range(0, Math.min(elementOne.size(), elementTwo.size()))
        .mapToObj(i -> Map.entry(elementOne.get(i), elementTwo.get(i)));
  }

  /**
   * Checks if any enum constant's transformed value matches a given condition.
   *
   * @param <T> The type of the enum.
   * @param <R> The type of the transformed value.
   * @param enumType The class of the enum to check.
   * @param transformer A function that transforms an enum constant to a value of type R.
   * @param condition A predicate that tests the transformed value.
   * @return true if any transformed value matches the condition, false otherwise.
   */
  public static <T extends Enum<T>, R> boolean isInEnum(
      Class<T> enumType,
      Function<? super T, ? extends R> transformer,
      Predicate<? super R> condition) {
    return Arrays.stream(enumType.getEnumConstants()).map(transformer).anyMatch(condition);
  }

  /**
   * Retrieves an enum constant from a string value, with an option to match irrespectively of case.
   * This method uses a regex pattern to match the string representation of enum constants.
   *
   * <p>It can be used as validator for enums in a similar way as {@link Helpers#isInEnum}, however,
   * this helper not only checks for existence but also returns the enum constant and provides more
   * handling opportunities available for Optional values.
   *
   * @see Helpers#isInEnum
   * @param <T> The type of the enum.
   * @param enumType The class of the enum to search.
   * @param strEnumKey The string value to match against the enum constants.
   * @param ignoreCase If true, the match is case-insensitive; otherwise, it is case-sensitive.
   * @return An Optional containing the matching enum constant, or empty if no match is found.
   */
  public static <T extends FriendlyEnum> Optional<T> enumFromValue(
      Class<T> enumType, @Nullable String strEnumKey, boolean ignoreCase) {
    if (strEnumKey == null || strEnumKey.isBlank()) return Optional.empty();

    Predicate<String> valuePattern =
        Pattern.compile(strEnumKey, ignoreCase ? Pattern.CASE_INSENSITIVE : 0).asPredicate();
    return Arrays.stream(enumType.getEnumConstants())
        .filter(key -> valuePattern.test(key.value()))
        .findFirst();
  }

  /**
   * Cleans a string by removing special characters and underscores and joining the remaining words
   * with a delimiter.
   *
   * <p>As part of the process, excess whitespace is removed and each word is trimmed. Useful for
   * tag cleaning and slug generation.
   *
   * @param toBeCleaned the string of text to be inspected and cleaned
   * @param delimiter the delimiter to use for joining the remaining words
   * @return the cleaned string joined by your delimiter
   */
  public static String specialCharCleanJoin(String toBeCleaned, String delimiter) {
    return Pattern.compile("[\\W_]")
        .splitAsStream(toBeCleaned)
        .filter(s -> !s.isEmpty())
        .map(String::trim)
        .collect(Collectors.joining(delimiter));
  }
}
