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

package examples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import com.mihaibojin.props.core.Props;
import com.mihaibojin.props.core.converters.BooleanConverter;
import com.mihaibojin.props.core.converters.DateConverter;
import com.mihaibojin.props.core.converters.DoubleConverter;
import com.mihaibojin.props.core.converters.DoubleListConverter;
import com.mihaibojin.props.core.converters.DurationConverter;
import com.mihaibojin.props.core.converters.FloatConverter;
import com.mihaibojin.props.core.converters.InstantConverter;
import com.mihaibojin.props.core.converters.IntegerConverter;
import com.mihaibojin.props.core.converters.IntegerListConverter;
import com.mihaibojin.props.core.converters.LongConverter;
import com.mihaibojin.props.core.converters.LongListConverter;
import com.mihaibojin.props.core.converters.NumericDurationConverter;
import com.mihaibojin.props.core.converters.PathConverter;
import com.mihaibojin.props.core.converters.StringConverter;
import com.mihaibojin.props.core.converters.StringListConverter;
import com.mihaibojin.props.core.resolvers.ClasspathPropertyFileResolver;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StandardTypesExamplesTest {

  private Props props;

  @BeforeEach
  void setUp() {
    // initialize the Props registry
    props =
        Props.factory()
            .withResolver(new ClasspathPropertyFileResolver("/examples/standard_types.properties"))
            .build();
  }

  @Test
  void readBoolean() {
    // initialize a prop and read its value once
    Optional<Boolean> maybeValue = props.prop("a.boolean", new BooleanConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue.get(), equalTo(true));
  }

  @Test
  void readInteger() {
    // initialize a prop and read its value once
    Optional<Integer> maybeValue = props.prop("an.integer", new IntegerConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue.get(), equalTo(1));
  }

  @Test
  void readLong() {
    // initialize a prop and read its value once
    Optional<Long> maybeValue = props.prop("a.long", new LongConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue.get(), equalTo(1L));
  }

  @Test
  void readFloat() {
    // initialize a prop and read its value once
    Optional<Float> maybeValue = props.prop("a.float", new FloatConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue.get(), equalTo(1.0f));
  }

  @Test
  void readDouble() {
    // initialize a prop and read its value once
    Optional<Double> maybeValue = props.prop("a.double", new DoubleConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue.get(), equalTo(1.0d));
  }

  @Test
  void readString() {
    // initialize a prop and read its value once
    Optional<String> maybeValue = props.prop("a.string", new StringConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property", maybeValue.get(), equalTo("one"));
  }

  @Test
  void readDate() {
    // initialize a prop and read its value once
    Optional<Date> maybeValue = props.prop("a.date", new DateConverter() {}).readOnce();

    Date expectedDate =
        Date.from(
            LocalDate.parse("2020-05-12").atStartOfDay().atOffset(ZoneOffset.UTC).toInstant());

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue.get(),
        equalTo(expectedDate));
  }

  @Test
  void readInstant() {
    // initialize a prop and read its value once
    Optional<Instant> maybeValue = props.prop("an.instant", new InstantConverter() {}).readOnce();

    Instant expectedInstant =
        LocalDate.parse("2020-05-12").atStartOfDay().atOffset(ZoneOffset.UTC).toInstant();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue.get(),
        equalTo(expectedInstant));
  }

  @Test
  void readDuration() {
    // initialize a prop and read its value once
    Optional<Duration> maybeValue = props.prop("a.duration", new DurationConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue.get(),
        equalTo(Duration.ofDays(1)));
  }

  @Test
  void readNumericDurationSeconds() {
    // initialize a prop and read its value once
    Optional<Duration> maybeValue =
        props.prop("a.numeric.duration.seconds", new NumericDurationConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue.get(),
        equalTo(Duration.ofSeconds(1)));
  }

  @Test
  void readNumericDurationDays() {
    // initialize a prop and read its value once
    Optional<Duration> maybeValue =
        props
            .prop(
                "a.numeric.duration.days",
                new NumericDurationConverter() {
                  @Override
                  public ChronoUnit unit() {
                    return ChronoUnit.DAYS;
                  }
                })
            .readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue.get(),
        equalTo(Duration.ofDays(1)));
  }

  @Test
  void readIntegerList() {
    // initialize a prop and read its value once
    Optional<List<Integer>> maybeValue =
        props.prop("an.integer.list", new IntegerListConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue.get(),
        hasItems(equalTo(1), equalTo(2), equalTo(3)));
  }

  @Test
  void readLongList() {
    // initialize a prop and read its value once
    Optional<List<Long>> maybeValue =
        props.prop("a.long.list", new LongListConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue.get(),
        hasItems(equalTo(1L), equalTo(2L), equalTo(3L)));
  }

  @Test
  void readDoubleList() {
    // initialize a prop and read its value once
    Optional<List<Double>> maybeValue =
        props.prop("a.double.list", new DoubleListConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue.get(),
        hasItems(equalTo(1.0d), equalTo(2.0d), equalTo(3.0d)));
  }

  @Test
  void readStringList() {
    // initialize a prop and read its value once
    Optional<List<String>> maybeValue =
        props.prop("a.string.list", new StringListConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue.get(),
        hasItems(equalTo("one"), equalTo("two"), equalTo("three")));
  }

  @Test
  void readPath() {
    // initialize a prop and read its value once
    Optional<Path> maybeValue = props.prop("a.path", new PathConverter() {}).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue.get(),
        equalTo(Path.of("/tmp")));
  }
}
