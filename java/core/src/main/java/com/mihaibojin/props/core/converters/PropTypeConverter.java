/*
 * Copyright 2020 Mihai Bojin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mihaibojin.props.core.converters;

import com.mihaibojin.props.core.annotations.Nullable;

@FunctionalInterface
public interface PropTypeConverter<T> {
  /**
   * Decodes and decrypts the passed string value into its final type.
   *
   * @return the value parsed as <code>T</code>
   */
  @Nullable
  T decode(String value);

  /**
   * Encodes the value as a {@link String}.
   *
   * @return the string representation of the passed <code>value</code> argument
   */
  default String encode(T value) {
    return value.toString();
  }
}
