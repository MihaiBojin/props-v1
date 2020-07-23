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

import static java.util.Objects.isNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.spy;

import com.mihaibojin.props.core.AbstractProp;
import com.mihaibojin.props.core.Props;
import com.mihaibojin.props.core.RefactoredProp;
import com.mihaibojin.props.core.converters.PropTypeConverter;
import com.mihaibojin.props.core.resolvers.ClasspathPropertyFileResolver;
import com.mihaibojin.props.core.resolvers.InMemoryResolver;
import com.mihaibojin.props.core.types.AbstractBooleanProp;
import com.mihaibojin.props.core.types.AbstractIntegerProp;
import com.mihaibojin.props.core.types.AbstractNumericDurationProp;
import com.mihaibojin.props.core.types.AbstractPathProp;
import com.mihaibojin.props.core.types.AbstractStringProp;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class ExtendedTypesExamplesTest {

  private Props props;

  @BeforeEach
  void setUp() {
    // initialize the Props registry
    props =
        Props.factory()
            .withResolver(new ClasspathPropertyFileResolver("/examples/extended_types.properties"))
            .build();
  }

  @Test
  void customProp() {
    // initialize a prop
    var aProp = props.bind(new DaysProp());

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        aProp.value().get(),
        equalTo(Duration.ofDays(1)));
  }

  @Test
  void customEncoder() {
    // initialize a prop
    var aProp = props.bind(new Base64Prop());

    byte[] expectedValue = {123};

    assertThat(
        "Encoding/decoding should yield the same value",
        aProp.decode(aProp.encode(expectedValue)),
        equalTo(expectedValue));

    assertThat(
        "Expected to read and correctly cast the property",
        aProp.value().get(),
        equalTo(expectedValue));
  }

  @Test
  void customValidation() {
    // initialize a prop
    var aProp = props.bind(new Base64PropWithValidation());

    byte[] expectedValue = {123};

    assertThat(
        "Expected to read and correctly cast the property",
        aProp.value().get(),
        equalTo(expectedValue));
  }

  @Test
  void customValidationWithFailure() {
    // the prop will fail to bind, as prop values are validated before being set
    Assertions.assertThrows(
        IllegalStateException.class, () -> props.bind(new Base64PropWithBadEncoder()));
  }

  @Test
  @Timeout(5)
  void updatedPropValue() {
    // initialize an in-memory resolver
    InMemoryResolver resolver = new InMemoryResolver();
    resolver.set("a.value", "one");

    // initialize a Props registry which updates its values every minute
    Props props =
        Props.factory().withResolver(resolver).refreshInterval(Duration.ofMillis(100)).build();

    var aProp = props.bind(spy(new SimpleProp("a.value")));

    // then update the value
    String updatedValue = "two";
    resolver.set("a.value", updatedValue);

    // and wait until the Prop's value is updated
    Assertions.assertTimeout(
        Duration.ofSeconds(5),
        () -> {
          while (Objects.equals(aProp.value().get(), "one")) {
            Thread.sleep(100);
          }
        });

    assertThat(
        "Finally, check that the value was correctly updated",
        aProp.value().get(),
        equalTo(updatedValue));
  }

  @Test
  void pathProp() {
    // initialize a prop
    var aProp = props.bind(new PathProp("a.path.prop", false));

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        aProp.value().get(),
        equalTo(Path.of("/tmp")));
  }

  @Test
  void pathPropHomeDir() {
    // initialize a prop
    var aProp = props.bind(new PathProp("a.path.homedir", true));

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        aProp.value().get(),
        equalTo(Path.of(System.getProperty("user.home"))));
  }

  @Test
  void pathPropHomeDirNotExpanded() {
    // initialize a prop
    var aProp = props.bind(new PathProp("a.path.homedir", false));

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property",
        aProp.value().get(),
        equalTo(Path.of("~/")));
  }

  @Test
  void refactoredPropPrefersNewerValue() {
    // construct the prop object
    // specifying a converter which can transform integers to booleans
    RefactoredProp<Boolean, Integer> aProp =
        new RefactoredProp<>(
            props, new NewProp("a.new.prop"), new OldProp("an.old.prop"), value -> value != 0);

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property", aProp.value().get(), equalTo(false));
  }

  @Test
  void refactoredPropCanConvertOlderValue() {
    // construct the prop object
    // specifying a converter which can transform integers to booleans
    RefactoredProp<Boolean, Integer> aProp =
        new RefactoredProp<>(
            props,
            new NewProp("a.new.prop.unknown"),
            new OldProp("an.old.prop"),
            value -> value != 0);

    // assert that the value is retrieved
    assertThat(
        "Expected to read and correctly cast the property", aProp.value().get(), equalTo(true));
  }

  @Test
  void refactoredPropCannotFindValue() {
    // construct the prop object
    // specifying a converter which can transform integers to booleans
    RefactoredProp<Boolean, Integer> aProp =
        new RefactoredProp<>(
            props,
            new NewProp("a.new.prop.unknown"),
            new OldProp("an.old.prop.unknown"),
            value -> value != 0);

    // assert that the value is retrieved
    assertThat(
        "Expected the property to not be resolved", aProp.value().isPresent(), equalTo(false));
  }

  /** Custom prop class that reads a numeric value in days. */
  private static class DaysProp extends AbstractNumericDurationProp {

    public DaysProp() {
      super("a.value.in.days", null, null, false, false);
    }

    @Override
    public ChronoUnit unit() {
      return ChronoUnit.DAYS;
    }
  }

  /** Customer prop class that encodes/decodes values in base64. */
  private static class Base64Prop extends AbstractProp<byte[]>
      implements PropTypeConverter<byte[]> {

    private static final Charset CHARSET = Charset.defaultCharset();

    public Base64Prop() {
      this("a.base64.prop");
    }

    public Base64Prop(String key) {
      super(key, new byte[] {}, null, false, false);
    }

    @Override
    public byte[] decode(String value) {
      return Base64.getMimeDecoder().decode(value.getBytes(CHARSET));
    }

    @Override
    public String encode(byte[] value) {
      return Base64.getMimeEncoder().encodeToString(value);
    }
  }

  /** Customer prop class that encodes/decodes values in base64. */
  private static class Base64PropWithValidation extends Base64Prop {

    public Base64PropWithValidation() {
      // we cannot bind more than one Prop class to the same key
      super("a.base64.prop.with.validation");
    }

    @Override
    protected void validateBeforeSet(byte[] value) {
      super.validateBeforeSet(value);

      // ensure the input is valid
      if (isNull(value)) {
        throw new IllegalStateException("Valid bytes must be provided and cannot be a null object");
      }

      // custom validation logic
      // ensure that the encode() function is an inverse of decode()
      if (!Arrays.equals(value, decode(encode(value)))) {
        throw new IllegalStateException(
            "A value that is encoded and subsequently decoded should result in the same original"
                + " value");
      }
    }
  }

  /** Customer prop class that encodes/decodes values in base64. */
  private static class Base64PropWithBadEncoder extends Base64Prop {

    public Base64PropWithBadEncoder() {
      // we cannot bind more than one Prop class to the same key
      super("a.base64.prop.with.validation2");
    }

    @Override
    public String encode(byte[] value) {
      // this implementation is BAD, since it is not the inverse of the base64 decode
      return new String(value, Charset.defaultCharset());
    }

    @Override
    protected void validateBeforeSet(byte[] value) {
      // custom validation logic
      // this should fail because of the bad encode() method above
      if (!Arrays.equals(value, decode(encode(value)))) {
        throw new IllegalStateException(
            "A value that is encoded and subsequently decoded should result in the same original"
                + " value");
      }
    }
  }

  /** Customer prop class that encodes/decodes values in base64. */
  private static class SimpleProp extends AbstractStringProp {

    protected SimpleProp(String key) {
      super(key, null, null, false, false);
    }

    @Override
    public String decode(String value) {
      // unfortunately Mockito doesn't work well with default methods
      // and overriding this method call is required for the spied object to correctly decode its
      // values
      return super.decode(value);
    }
  }

  /** Custom prop class that reads a numeric value in days. */
  private static class PathProp extends AbstractPathProp {

    private final boolean expandHomeDir;

    protected PathProp(String key, boolean expandHomeDir) {
      super(key, null, null, false, false);
      this.expandHomeDir = expandHomeDir;
    }

    @Override
    public boolean expandUserHomeDir() {
      return expandHomeDir;
    }
  }

  /** A 'newer' prop of type Boolean. */
  private static class NewProp extends AbstractBooleanProp {

    protected NewProp(String key) {
      super(key, null, null, false, false);
    }
  }

  /** An 'older' prop of type Integer. */
  private static class OldProp extends AbstractIntegerProp {

    protected OldProp(String key) {
      super(key, null, null, false, false);
    }
  }
}
