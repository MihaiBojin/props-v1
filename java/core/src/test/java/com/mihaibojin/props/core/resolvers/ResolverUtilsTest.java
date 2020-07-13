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

package com.mihaibojin.props.core.resolvers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ResolverUtilsTest {

  @Test
  void readConfigLineClassPath() {
    // ACT
    Resolver resolver = ResolverUtils.readConfigLine("classpath=config1.properties");

    // ASSERT
    assertThat(resolver.id(), equalTo("config1.properties"));
    assertThat(resolver.isReloadable(), equalTo(false));
    assertThat(resolver, instanceOf(ClasspathPropertyFileResolver.class));
  }

  @Test
  void readConfigLineFile() {
    // ACT
    Resolver resolver = ResolverUtils.readConfigLine("file=config1.properties,false");

    // ASSERT
    assertThat(resolver.id(), equalTo("config1.properties"));
    assertThat(resolver.isReloadable(), equalTo(false));
    assertThat(resolver, instanceOf(PropertyFileResolver.class));
  }

  @Test
  void readConfigLineReloadableResolver() {
    // ACT
    Resolver resolver = ResolverUtils.readConfigLine("file=config1.properties,true");

    // ASSERT
    assertThat(resolver.id(), equalTo("config1.properties"));
    assertThat(resolver.isReloadable(), equalTo(true));
    assertThat(resolver, instanceOf(PropertyFileResolver.class));
  }

  @Test
  void readConfigLineSystem() {
    // ACT
    Resolver resolver = ResolverUtils.readConfigLine("system");

    // ASSERT
    assertThat(resolver.id(), equalTo("SYSTEM"));
    assertThat(resolver.isReloadable(), equalTo(false));
    assertThat(resolver, instanceOf(SystemPropertyResolver.class));
  }

  @Test
  void readConfigLineEnv() {
    // ACT
    Resolver resolver = ResolverUtils.readConfigLine("env");

    // ASSERT
    assertThat(resolver.id(), equalTo("ENV"));
    assertThat(resolver.isReloadable(), equalTo(false));
    assertThat(resolver, instanceOf(EnvResolver.class));
  }

  @Test
  void verifyInvalidConfigLines() {
    // ASSERT
    assertThrows(
        IllegalArgumentException.class,
        () -> ResolverUtils.readConfigLine("some invalid string"),
        "Should not read invalid line");

    assertThrows(
        IllegalArgumentException.class,
        () -> ResolverUtils.readConfigLine("unknown=type"),
        "Should not read unknown types");

    assertThrows(
        IllegalArgumentException.class,
        () -> ResolverUtils.readConfigLine("file=file.properties,invalid"),
        "Only true or false are allowed as values for 'reloadable'");
  }
}
