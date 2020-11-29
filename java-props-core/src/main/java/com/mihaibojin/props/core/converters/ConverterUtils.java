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

import static java.util.logging.Level.SEVERE;

import com.mihaibojin.props.core.annotations.Nullable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ConverterUtils {

  private static final Logger log = Logger.getLogger(ConverterUtils.class.getName());

  /**
   * Attempts to parse a {@link String} to a {@link Number} and returns <code>null</code> if it
   * cannot.
   *
   * <p>This methods logs a {@link java.util.logging.Level#SEVERE} event instead of throwing {@link
   * ParseException}s.
   */
  @Nullable
  static Number safeParseNumber(String value) {
    try {
      return NumberFormat.getInstance().parse(value);
    } catch (ParseException e) {
      log.log(SEVERE, e, () -> "Could not parse " + value + " to a number");
      return null;
    }
  }

  /**
   * Attempts to parse a {@link String} to an {@link ChronoUnit} and returns <code>null</code> if it
   * cannot.
   *
   * <p>This methods logs a {@link java.util.logging.Level#SEVERE} event instead of throwing {@link
   * IllegalArgumentException}s or {@link NullPointerException}s.
   */
  @Nullable
  static ChronoUnit safeParseChronoUnit(String value) {
    try {
      return ChronoUnit.valueOf(value);
    } catch (IllegalArgumentException | NullPointerException e) {
      log.log(SEVERE, e, () -> "Could not parse " + value + " as a ChronoUnit");
      return null;
    }
  }

  /**
   * Attempts to parse a {@link String} to an {@link Duration} and returns <code>null</code> if it
   * cannot.
   *
   * <p>This methods logs a {@link java.util.logging.Level#SEVERE} event instead of throwing {@link
   * DateTimeParseException}s.
   */
  @Nullable
  static Duration safeParseDuration(String value) {
    try {
      return Duration.parse(value);
    } catch (DateTimeParseException e) {
      log.log(SEVERE, e, () -> "Could not parse " + value + " as a valid Duration");
      return null;
    }
  }

  /**
   * Attempts to parse a {@link String} to an {@link Instant} and returns <code>null</code> if it
   * cannot.
   *
   * <p>This methods logs a {@link java.util.logging.Level#SEVERE} event instead of throwing {@link
   * DateTimeParseException}s.
   */
  @Nullable
  static Instant safeParseInstant(String value) {
    try {
      return OffsetDateTime.parse(value).toInstant();
    } catch (DateTimeParseException e) {
      log.log(SEVERE, e, () -> "Could not parse " + value + " as a valid DateTime");
      return null;
    }
  }

  /** Splits a {@link String} by the given <code>separator</code>. */
  static List<String> splitString(String input, String separator) {
    return List.of(input.split(Pattern.quote(separator)));
  }

  /**
   * Splits a {@link String} by the given <code>separator</code>, casts every item using the
   * specified <code>mapper</code> func and returns a {@link List} of numbers.
   */
  static <T extends Number> List<T> splitStringAsNumbers(
      String input, String separator, Function<Number, T> mapper) {
    return Stream.of(input.split(Pattern.quote(separator)))
        .map(ConverterUtils::safeParseNumber)
        .filter(Objects::nonNull)
        .map(mapper)
        .collect(Collectors.toList());
  }
}
