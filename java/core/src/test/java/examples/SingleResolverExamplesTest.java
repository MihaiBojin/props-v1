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
import static org.hamcrest.Matchers.notNullValue;

import com.mihaibojin.props.core.Props;
import com.mihaibojin.props.core.resolvers.ClasspathPropertyFileResolver;
import com.mihaibojin.props.core.resolvers.EnvResolver;
import com.mihaibojin.props.core.resolvers.PropertyFileResolver;
import com.mihaibojin.props.core.resolvers.SystemPropertyResolver;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SingleResolverExamplesTest {
  private static final String propKey = "prop";
  @TempDir static File tempDir;
  private static Path propFile;

  @BeforeAll
  static void setUp() {
    // ARRANGE
    // store a Java property into a temporary file
    propFile = tempDir.toPath().resolve("file.properties").toAbsolutePath();
    Helpers.storePropertyInfile(propKey, "a_value", propFile.toFile());
  }

  @Test
  void readPropFromFile() {
    // initialize the Props registry
    Props props = Props.factory().withResolver(new PropertyFileResolver(propFile)).build();

    // initialize an Integer prop and read its value once
    Optional<String> maybeValue = props.prop(propKey).readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expecting the property's value to be successfully retrieved",
        maybeValue.orElse(null),
        equalTo("a_value"));
  }

  @Test
  void readPropFromClassPath() {
    // initialize the Props registry
    Props props =
        Props.factory()
            .withResolver(new ClasspathPropertyFileResolver("/examples/single_resolver.properties"))
            .build();

    // initialize a String prop and read its value once
    Optional<String> maybeValue = props.prop("file.encoding").readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to read the property from the classpath",
        maybeValue.orElse(null),
        equalTo("UPDATED"));
  }

  @Test
  void readPropFromEnv() {
    // initialize the Props registry
    Props props = Props.factory().withResolver(new EnvResolver()).build();

    // initialize a String prop and read its value once
    Optional<String> maybeValue = props.prop("PATH").readOnce();

    // assert that the value is retrieved
    assertThat("Expected to find PATH in the environment", maybeValue.orElse(null), notNullValue());
  }

  @Test
  void readPropFromSystem() {
    // initialize the Props registry
    Props props = Props.factory().withResolver(new SystemPropertyResolver()).build();

    // initialize a String prop and read its value once
    Optional<String> maybeValue = props.prop("file.encoding").readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to find a 'file.encoding' defined in the System Properties",
        maybeValue.isPresent(),
        equalTo(true));
  }
}
