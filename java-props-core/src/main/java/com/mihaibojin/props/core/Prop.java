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

package com.mihaibojin.props.core;

import com.mihaibojin.props.core.annotations.Nullable;
import com.mihaibojin.props.core.converters.Converter;
import java.util.Optional;
import java.util.concurrent.Flow.Publisher;

/**
 * Interface with common property methods.
 *
 * @param <T> the property's type
 */
public interface Prop<T> extends Converter<T>, Publisher<T> {

  /**
   * Identifies the {@link Prop}.
   *
   * @return a string id
   */
  String key();

  /**
   * Returns the property's current value.
   *
   * @return the {@link Prop}'s current value, or an empty Optional if one could not be determined.
   * @throws ValidationException if the value could not be validated
   */
  Optional<T> value();

  /**
   * Returns a short description explaining what this prop is used for.
   *
   * @return a short, developer-friendly description
   */
  @Nullable
  String description();

  /**
   * Returns <code>true</code> if this {@link Prop} requires a value.
   *
   * @return true if the property should have a value or a default
   */
  boolean isRequired();

  /**
   * Returns <code>true</code> if this {@link Prop} represents a secret and its value should be
   * redacted when printed.
   *
   * @return true if the property is a 'secret'
   */
  boolean isSecret();
}
