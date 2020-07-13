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

public class ResolverPrecedenceExamplesTest {
  @TempDir static File tempDir;
  private static Path propFile;

  @BeforeAll
  static void setUp() {
    // ARRANGE
    // store a Java property into a temporary file
    propFile = tempDir.toPath().resolve("file.properties").toAbsolutePath();
    Helpers.storePropertyInfile("file.encoding", "FILE", propFile.toFile());
  }

  @Test
  void valuePrecedenceHighestPriority() {
    // initialize the Props registry
    Props props =
        Props.factory()
            .withResolver(new SystemPropertyResolver())
            .withResolver(new EnvResolver())
            .withResolver(new PropertyFileResolver(propFile))
            .withResolver(
                new ClasspathPropertyFileResolver("/examples/resolver_precedence.properties"))
            .build();

    // initialize an Integer prop and read its value once
    Optional<String> maybeValue = props.prop("file.encoding").readOnce();

    // assert that the value is dictated by the Resolver with the highest priority
    // (last to be defined)
    assertThat(
        "Expected to find a 'file.encoding' set by the classpath resolver",
        maybeValue.orElse(null),
        equalTo("CLASSPATH"));
  }

  @Test
  void valuePrecedenceSpecificResolver() {
    // initialize the Props registry
    Props props =
        Props.factory()
            .withResolver(new SystemPropertyResolver())
            .withResolver(new EnvResolver())
            // resolvers can be assigned custom IDs
            .withResolver(new PropertyFileResolver(propFile))
            .withResolver(
                new ClasspathPropertyFileResolver("/examples/resolver_precedence.properties"))
            .build();

    // initialize an Integer prop and read its value once
    Optional<String> maybeValue =
        props
            .prop("file.encoding")
            // values can be retrieved from a specific resolver, by id
            .resolver(propFile.toString())
            .readOnce();

    // assert that the value is retrieved
    assertThat(
        "Expected to find a 'file.encoding' set by the file resolver",
        maybeValue.orElse(null),
        equalTo("FILE"));
  }

  @Test
  void valueNotFound() {
    // initialize the Props registry
    Props props =
        Props.factory()
            .withResolver(new SystemPropertyResolver())
            .withResolver(new EnvResolver())
            .withResolver(new PropertyFileResolver(propFile))
            .withResolver(
                new ClasspathPropertyFileResolver("/examples/resolver_precedence.properties"))
            .build();

    // initialize an Integer prop and read its value once
    Optional<String> maybeValue = props.prop("a_key_which_does_not_exist").readOnce();

    // assert that the value is dictated by the Resolver with the highest priority
    // (last to be defined)
    assertThat(
        "Expected to not find a value for a key which is not set",
        maybeValue.isEmpty(),
        equalTo(true));
  }
}
