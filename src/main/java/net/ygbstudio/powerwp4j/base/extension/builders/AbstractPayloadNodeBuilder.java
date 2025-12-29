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

package net.ygbstudio.powerwp4j.base.extension.builders;

import java.util.Collection;
import java.util.function.Supplier;
import net.ygbstudio.powerwp4j.base.extension.enums.CacheKeyEnum;
import net.ygbstudio.powerwp4j.base.extension.enums.FriendlyEnum;
import net.ygbstudio.powerwp4j.utils.JsonSupport;
import org.jspecify.annotations.NonNull;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * Abstract base class for payload builders based on {@code ObjectNode}.
 *
 * <p>This class provides common functionality for building payload nodes used in the WordPress REST
 * API. It implements the builder pattern and provides methods to set various properties of the
 * payload node. It serves as an alternative to {@link AbstractPayloadBuilder} or the media payload
 * builder {@link AbstractMediaPayloadBuilder} class and offers a different, low-level approach to
 * payload creation by using a centralized {@link ObjectNode}, making it even easier to create
 * specialized builders with specific boundaries.
 *
 * <p>{@link AbstractPayloadNodeBuilder} was created to allow for extra fields that are not part of
 * the WordPress default payload. For example, if you have custom fields/keys, you can implement a
 * payload builder that allows you to work effortlessly with those fields. In theory, you can do
 * that with {@link AbstractPayloadBuilder} and extend it, however, if you want to implement a
 * payload builder that suits your needs more and does not give you default fields, this one is a
 * good alternative.
 *
 * <p>An implementation of builders following this abstract class will be provided in the <br>
 * {@link net.ygbstudio.powerwp4j.builders} package to demonstrate its suggested usage.
 *
 * @param <T> The type of the concrete builder subclass.
 */
public abstract class AbstractPayloadNodeBuilder<T extends AbstractPayloadNodeBuilder<T>> {
  protected ObjectNode payloadNode;

  /**
   * Returns the current instance of the payload builder. This is a protected method used for
   * chaining method calls.
   *
   * <p>The cast to type T is safe because the method is only called within the class hierarchy and
   * this class is parameterized with the type of the subclass.
   *
   * @return the current instance of the payload builder
   */
  @SuppressWarnings("unchecked")
  protected T self() {
    return (T) this;
  }

  /**
   * Returns the payload node object used to build the JSON payload.
   *
   * @return the payload node object
   */
  protected ObjectNode getPayloadNode() {
    return payloadNode;
  }

  /**
   * Sets the payload node object used to build the JSON payload.
   *
   * @param payloadNode the payload node object to set
   */
  protected void setPayloadNode(ObjectNode payloadNode) {
    this.payloadNode = payloadNode;
  }

  /**
   * Adds a property with the specified name and value to the payload node.
   *
   * @param propertyName the name of the property
   * @param value the value of the property
   * @return the current instance of the payload builder
   */
  protected T add(String propertyName, String value) {
    payloadNode.put(propertyName, value);
    return self();
  }

  /**
   * Adds a property with the specified name and long value to the payload node.
   *
   * @param propertyName the name of the property
   * @param value the long value of the property
   * @return the current instance of the payload builder
   */
  protected T add(String propertyName, long value) {
    payloadNode.put(propertyName, value);
    return self();
  }

  /**
   * Adds a property with the specified name and int value to the payload node.
   *
   * @param propertyName the name of the property
   * @param value the int value of the property
   * @return the current instance of the payload builder
   */
  protected T add(String propertyName, int value) {
    payloadNode.put(propertyName, value);
    return self();
  }

  /**
   * Adds a property with the specified name and short value to the payload node.
   *
   * @param propertyName the name of the property
   * @param value the short value of the property
   * @return the current instance of the payload builder
   */
  protected T add(String propertyName, short value) {
    payloadNode.put(propertyName, value);
    return self();
  }

  /**
   * Adds a property with the specified name and boolean value to the payload node.
   *
   * @param propertyName the name of the property
   * @param value the boolean value of the property
   * @return the current instance of the payload builder
   */
  protected T add(String propertyName, boolean value) {
    payloadNode.put(propertyName, value);
    return self();
  }

  /**
   * Adds a property with the given {@code propertyEnum} value and {@code value} to the payload
   * node.
   *
   * @param propertyEnum the enum value of the property
   * @param value the value of the property
   * @return the current instance of the payload builder
   */
  protected <E extends CacheKeyEnum> T add(@NonNull E propertyEnum, String value) {
    payloadNode.put(propertyEnum.value(), value);
    return self();
  }

  /**
   * Adds a property with the given {@code propertyEnum} value and a {@code value} enum to the
   * payload node.
   *
   * @param propertyEnum the enum value of the property
   * @param value the value enum of the property
   * @return the current instance of the payload builder
   */
  protected <E extends FriendlyEnum> T add(@NonNull CacheKeyEnum propertyEnum, @NonNull E value) {
    return add(propertyEnum, value.value());
  }

  /**
   * Adds a property with the given {@code propertyEnum} value and {@code int} value to the payload
   * node.
   *
   * @param propertyEnum the enum value of the property
   * @param value the value of the property (int)
   * @return the current instance of the payload builder
   */
  protected <E extends FriendlyEnum> T add(@NonNull E propertyEnum, int value) {
    payloadNode.put(propertyEnum.value(), value);
    return self();
  }

  /**
   * Adds a property with the given {@code propertyEnum} value and {@code long} value to the payload
   * node.
   *
   * @param propertyEnum the enum value of the property
   * @param value the value of the property (long)
   * @return the current instance of the payload builder
   */
  protected <E extends FriendlyEnum> T add(@NonNull E propertyEnum, long value) {
    payloadNode.put(propertyEnum.value(), value);
    return self();
  }

  /**
   * Adds a property with the given {@code propertyEnum} value and {@code short} value to the
   * payload node.
   *
   * @param propertyEnum the enum value of the property
   * @param value the value of the property (short)
   * @return the current instance of the payload builder
   */
  protected <E extends FriendlyEnum> T add(@NonNull E propertyEnum, short value) {
    payloadNode.put(propertyEnum.value(), value);
    return self();
  }

  /**
   * Adds a property with the given {@code propertyEnum} value and {@code boolean} value to the
   * payload node.
   *
   * @param propertyEnum the enum value of the property
   * @param value the value of the property (boolean)
   * @return the current instance of the payload builder
   */
  protected <E extends FriendlyEnum> T add(@NonNull E propertyEnum, boolean value) {
    return add(propertyEnum.value(), value);
  }

  /**
   * Adds an integer array property with the specified {@code propertyEnum} value and {@code int}
   * value array to the payload node. In order for a collection to be added, its wildcard will be
   * captured as part of the following {@link Number} subtypes:
   *
   * <p>{@link Integer}, {@link Long} or {@link Short}.
   *
   * @param propertyEnum the enum value of the property
   * @param valueArray the value array of the property (int)
   * @return the current instance of the payload builder
   */
  protected T add(
      @NonNull FriendlyEnum propertyEnum, @NonNull Collection<? extends Number> valueArray) {
    ArrayNode propertyArray = payloadNode.putArray(propertyEnum.value());
    valueArray.forEach(
        value -> {
          if (value instanceof Integer) {
            propertyArray.add((Integer) value);
          } else if (value instanceof Long) {
            propertyArray.add((Long) value);
          } else if (value instanceof Short) {
            propertyArray.add((Short) value);
          } else {
            Supplier<String> errorMessage =
                () -> "Unable to take Collection with numbers of type: " + value.getClass();
            throw new IllegalArgumentException(errorMessage.get());
          }
        });
    return self();
  }

  /**
   * Clears the payload node object, resetting it to an empty object.
   *
   * @return the current instance of the payload builder
   */
  public T clear() {
    setPayloadNode(JsonSupport.getMapper().createObjectNode());
    return self();
  }

  /**
   * Builds the payload node object as a JSON node.
   *
   * @return the JSON node representing the payload node object
   */
  public JsonNode build() {
    return getPayloadNode();
  }
}
