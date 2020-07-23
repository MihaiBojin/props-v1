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

import static com.mihaibojin.props.core.resolvers.ResolverUtils.readResolverConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.mihaibojin.props.core.Props.Factory;
import com.mihaibojin.props.core.converters.IntegerConverter;
import com.mihaibojin.props.core.resolvers.ClasspathPropertyFileResolver;
import com.mihaibojin.props.core.resolvers.EnvResolver;
import com.mihaibojin.props.core.resolvers.SystemPropertyResolver;
import org.junit.jupiter.api.Test;

public class PropsTest {

  @Test
  public void loadValueFromClassPath() {
    // ARRANGE
    Props props =
        Props.factory()
            .withResolver(new SystemPropertyResolver())
            .withResolver(new EnvResolver())
            .withResolver(new ClasspathPropertyFileResolver("/propfiles/config1.properties"))
            .build();

    // ACT
    Integer aValue = props.prop("prop.id", new IntegerConverter() {}).build().value().get();

    // ASSERT
    assertThat(aValue, equalTo(1));
  }

  @Test
  public void resolverPriorityOrder() {
    // ARRANGE
    Props props =
        Props.factory()
            .withResolver(new ClasspathPropertyFileResolver("/propfiles/config1.properties"))
            .withResolver(new ClasspathPropertyFileResolver("/propfiles/config2.properties"))
            .build();

    // ACT
    Integer aValue = props.prop("prop.id", new IntegerConverter() {}).build().value().get();

    // ASSERT
    assertThat(aValue, equalTo(2));
  }

  @Test
  public void getValueFromSpecificResolver() {
    // ARRANGE
    Props props =
        Props.factory()
            .withResolver(new ClasspathPropertyFileResolver("/propfiles/config1.properties"))
            .withResolver(new ClasspathPropertyFileResolver("/propfiles/config2.properties"))
            .build();

    // ACT
    Integer aValue =
        props
            .prop("prop.id", new IntegerConverter() {})
            .resolver("/propfiles/config1.properties")
            .build()
            .value()
            .get();

    // ASSERT
    assertThat(aValue, equalTo(1));
  }

  @Test
  void shutdownHookIsRegistered() {
    // ARRANGE
    Factory factory = spy(Props.factory());

    // ACT
    Props props = factory.withResolver(new EnvResolver()).registerShutdownHook(true).build();

    // ASSERT
    verify(factory).registerShutdownHook(props);
  }

  @Test
  void shutdownHookIsNotRegistered() {
    // ARRANGE
    Factory factory = spy(Props.factory());

    // ACT
    Props props = factory.withResolver(new EnvResolver()).registerShutdownHook(false).build();

    // ASSERT
    verify(factory, never()).registerShutdownHook(props);
  }

  @Test
  public void loadResolverConfig() {
    // ARRANGE
    Props props =
        Props.factory()
            .withResolver(new SystemPropertyResolver())
            .withResolver(new EnvResolver())
            .withResolvers(
                readResolverConfig(
                    getClass().getResourceAsStream("/resolver-config/resolvers.config")))
            .build();

    // ACT
    String aValue1 = props.prop("a.string1").build().value().get();
    String aValue2 = props.prop("a.string2").build().value().get();

    // ASSERT
    assertThat(aValue1, equalTo("one"));
    assertThat(aValue2, equalTo("two"));
  }
}
