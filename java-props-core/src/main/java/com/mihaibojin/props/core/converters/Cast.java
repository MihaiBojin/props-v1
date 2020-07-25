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

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Helper class that makes the API a little bit nicer, e.g.: <code>
 * Prop&lt;Boolean&gt; booleanProp = Props.factory().build().prop("key", Converters.bool());
 * </code>
 */
public class Cast {

  /** Returns <code>Converter&lt;Boolean&gt;</code>. */
  public static Converter<Boolean> asBoolean() {
    return new BooleanConverter() {};
  }

  /** Returns <code>Converter&lt;ChronoUnit&gt;</code>. */
  public static Converter<ChronoUnit> asChronoUnit() {
    return new ChronoUnitConverter() {};
  }

  /** Returns <code>Converter&lt;Date&gt;</code>. */
  public static Converter<Date> asDate() {
    return new DateConverter() {};
  }

  /** Returns <code>Converter&lt;Double&gt;</code>. */
  public static Converter<Double> asDouble() {
    return new DoubleConverter() {};
  }

  /** Returns <code>Converter&lt;Duration&gt;</code>. */
  public static Converter<Duration> asDuration() {
    return new DurationConverter() {};
  }

  /** Returns <code>Converter&lt;Float&gt;</code>. */
  public static Converter<Float> asFloat() {
    return new FloatConverter() {};
  }

  /** Returns <code>Converter&lt;Instant&gt;</code>. */
  public static Converter<Instant> asInstant() {
    return new InstantConverter() {};
  }

  /** Returns <code>Converter&lt;Integer&gt;</code>. */
  public static Converter<Integer> asInteger() {
    return new IntegerConverter() {};
  }

  /** Returns <code>Converter&lt;List&lt;Double&gt;&gt;</code>. */
  public static Converter<List<Double>> asListOfDouble() {
    return new ListOfDoubleConverter() {};
  }

  /** Returns <code>Converter&lt;List&lt;Float&gt;&gt;</code>. */
  public static Converter<List<Float>> asListOfFloat() {
    return new ListOfFloatConverter() {};
  }

  /** Returns <code>Converter&lt;List&lt;Integer&gt;&gt;</code>. */
  public static Converter<List<Integer>> asListOfInteger() {
    return new ListOfIntegerConverter() {};
  }

  /** Returns <code>Converter&lt;List&lt;Long&gt;&gt;</code>. */
  public static Converter<List<Long>> asListOfLong() {
    return new ListOfLongConverter() {};
  }

  /** Returns <code>Converter&lt;List&lt;String&gt;&gt;</code>. */
  public static Converter<List<String>> asListOfString() {
    return new ListOfStringConverter() {};
  }

  /** Returns <code>Converter&lt;Long&gt;</code>. */
  public static Converter<Long> asLong() {
    return new LongConverter() {};
  }

  /**
   * Returns <code>Converter&lt;NumericDuration&gt;</code>, where the unit is {@link
   * ChronoUnit#SECONDS}.
   */
  public static Converter<Duration> asNumericDuration() {
    return asNumericDuration(ChronoUnit.SECONDS);
  }

  /**
   * Returns <code>Converter&lt;NumericDuration&gt;</code>.
   *
   * @param unit specifies the {@link ChronoUnit} in which the numeric value is measured
   */
  public static Converter<Duration> asNumericDuration(ChronoUnit unit) {
    return (NumericDurationConverter) () -> unit;
  }

  /** Returns <code>Converter&lt;Path&gt;</code>. */
  public static Converter<Path> asPath() {
    return asPath(true);
  }

  /**
   * Returns <code>Converter&lt;Path&gt;</code>.
   *
   * @param expandHomeDirectory if true, expands <code>~</code> to the user's home directory
   */
  public static Converter<Path> asPath(boolean expandHomeDirectory) {
    return (PathConverter) () -> expandHomeDirectory;
  }

  /** Returns <code>Converter&lt;String&gt;</code>. */
  public static Converter<String> asString() {
    return new StringConverter() {};
  }
}
