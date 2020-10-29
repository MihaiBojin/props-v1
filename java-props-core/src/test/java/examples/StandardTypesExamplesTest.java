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
import com.mihaibojin.props.core.converters.Cast;
import com.mihaibojin.props.core.resolvers.ClasspathPropertyFileResolver;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
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
    Boolean maybeValue = props.prop("a.boolean", Cast.asBoolean()).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue, equalTo(true));
  }

  @Test
  void readChronoUnit() {
    // initialize a prop and read its value once
    ChronoUnit maybeValue = props.prop("a.chronounit", Cast.asChronoUnit()).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue,
        equalTo(ChronoUnit.MINUTES));
  }

  @Test
  void readInteger() {
    // initialize a prop and read its value once
    Integer maybeValue = props.prop("an.integer", Cast.asInteger()).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue, equalTo(1));
  }

  @Test
  void readLong() {
    // initialize a prop and read its value once
    Long maybeValue = props.prop("a.long", Cast.asLong()).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue, equalTo(1L));
  }

  @Test
  void readFloat() {
    // initialize a prop and read its value once
    Float maybeValue = props.prop("a.float", Cast.asFloat()).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue, equalTo(1.0f));
  }

  @Test
  void readDouble() {
    // initialize a prop and read its value once
    Double maybeValue = props.prop("a.double", Cast.asDouble()).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue, equalTo(1.0d));
  }

  @Test
  void readString() {
    // initialize a prop and read its value once
    String maybeValue = props.prop("a.string", Cast.asString()).readOnce();

    // assert that the value is retrieved
    assertThat("Expected to read and correctly cast the property", maybeValue, equalTo("one"));
  }

  @Test
  void readDate() {
    // initialize a prop and read its value once
    Date maybeValue = props.prop("a.date", Cast.asDate()).readOnce();

    Date expectedDate =
        Date.from(
            LocalDate.parse("2020-05-12").atStartOfDay().atOffset(ZoneOffset.UTC).toInstant());

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property", maybeValue, equalTo(expectedDate));
  }

  @Test
  void readInstant() {
    // initialize a prop and read its value once
    Instant maybeValue = props.prop("an.instant", Cast.asInstant()).readOnce();

    Instant expectedInstant =
        LocalDate.parse("2020-05-12").atStartOfDay().atOffset(ZoneOffset.UTC).toInstant();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property", maybeValue, equalTo(expectedInstant));
  }

  @Test
  void readDuration() {
    // initialize a prop and read its value once
    Duration maybeValue = props.prop("a.duration", Cast.asDuration()).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue,
        equalTo(Duration.ofDays(1)));
  }

  @Test
  void readNumericDurationSeconds() {
    // initialize a prop and read its value once
    Duration maybeValue =
        props.prop("a.numeric.duration.seconds", Cast.asNumericDuration()).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue,
        equalTo(Duration.ofSeconds(1)));
  }

  @Test
  void readNumericDurationDays() {
    // initialize a prop and read its value once
    Duration maybeValue =
        props.prop("a.numeric.duration.days", Cast.asNumericDuration(ChronoUnit.DAYS)).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue,
        equalTo(Duration.ofDays(1)));
  }

  @Test
  void readIntegerList() {
    // initialize a prop and read its value once
    List<Integer> maybeValue = props.prop("an.integer.list", Cast.asListOfInteger()).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue,
        hasItems(equalTo(1), equalTo(2), equalTo(3)));
  }

  @Test
  void readLongList() {
    // initialize a prop and read its value once
    List<Long> maybeValue = props.prop("a.long.list", Cast.asListOfLong()).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue,
        hasItems(equalTo(1L), equalTo(2L), equalTo(3L)));
  }

  @Test
  void readDoubleList() {
    // initialize a prop and read its value once
    List<Double> maybeValue = props.prop("a.double.list", Cast.asListOfDouble()).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue,
        hasItems(equalTo(1.0d), equalTo(2.0d), equalTo(3.0d)));
  }

  @Test
  void readStringList() {
    // initialize a prop and read its value once
    List<String> maybeValue = props.prop("a.string.list", Cast.asListOfString()).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue,
        hasItems(equalTo("one"), equalTo("two"), equalTo("three")));
  }

  @Test
  void readPath() {
    // initialize a prop and read its value once
    Path maybeValue = props.prop("a.path", Cast.asPath(false)).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property", maybeValue, equalTo(Path.of("/tmp")));
  }

  @Test
  void readHomeDirPath() {
    // initialize a prop and read its value once
    Path maybeValue = props.prop("the.home.dir", Cast.asPath()).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        maybeValue,
        equalTo(Path.of(System.getProperty("user.home"))));
  }
}
