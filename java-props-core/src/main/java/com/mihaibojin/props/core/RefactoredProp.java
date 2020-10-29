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

import static java.util.Objects.nonNull;
import static java.util.logging.Level.FINE;

import com.mihaibojin.props.core.annotations.Nullable;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Helper class which attempts to resolve two properties, converting the second property's value to
 * the first property's type.
 *
 * <p>If any of the two {@link Prop}s are <code>required</code> the call to {@link #value()} will
 * result in a {@link ValidationException} when neither of the two properties can resolve a value.
 *
 * @param <T1> the type of the first property
 * @param <T2> the type of the second property
 */
public class RefactoredProp<T1, T2> {

  private static final Logger log = Logger.getLogger(RefactoredProp.class.getName());

  private final Prop<T1> prop1;
  private final Prop<T2> prop2;
  private final Function<T2, T1> converter;

  /**
   * Shorthand constructor with a null <code>resolverId</code>.
   *
   * @throws BindException if any of the two {@link Prop}s are already bound to the specified {@link
   *     Props} registry
   */
  public RefactoredProp(
      Props registry, Prop<T1> prop1, Prop<T2> prop2, Function<T2, T1> converter) {
    this(registry, null, prop1, prop2, converter);
  }

  /**
   * Class constructor which specifies a <code>resolverId</code> from which {@link Prop}s should be
   * resolved.
   *
   * <p>This constructor accepts unbound <code>Prop</code>s. Passing already bound {@link Prop}
   * objects will result in a {@link BindException}.
   *
   * @throws BindException if any of the two {@link Prop}s are already bound to the specified {@link
   *     Props} registry
   */
  public RefactoredProp(
      Props registry,
      @Nullable String resolverId,
      Prop<T1> prop1,
      Prop<T2> prop2,
      Function<T2, T1> converter) {
    this.prop1 = prop1;
    this.prop2 = prop2;
    this.converter = converter;
    registry.bind(prop1, resolverId);
    registry.bind(prop2, resolverId);
  }

  /**
   * This method attempts the following actions.
   * <li>If the first property has a value it is returned
   * <li>next, it tries to load the second property's value
   * <li>finally, the class returns an empty {@link Optional} of type <code>T1</code> or it throws
   *     an exception
   *
   * @throws ValidationException if a value could not be loaded from either of the two {@link Props}
   *     and validation failed at least once
   */
  @Nullable
  public T1 value() {
    ValidationException firstException = null;
    T1 result;

    try {
      // attempt to load the first property
      result = prop1.value();

      // if found
      if (nonNull(result)) {
        // return it
        return result;
      }

    } catch (ValidationException e) {
      firstException = e;
    }

    // otherwise, attempt to retrieve the second property
    try {
      // if a value is present
      T2 oldValue = prop2.value();
      if (nonNull(oldValue)) {
        // convert the old type to the new type and return it
        return converter.apply(oldValue);
      }

    } catch (ValidationException secondException) {
      // if an error was encountered, log it
      log.log(FINE, "The second property failed to be resolved", secondException);

      // attempt to throw the first exception (if one was present)
      // otherwise throw the current exception
      throw (nonNull(firstException)) ? firstException : secondException;
    }

    // throw any validation exceptions
    if (nonNull(firstException)) {
      throw firstException;
    }

    // a value could not be resolved
    return null;
  }
}
