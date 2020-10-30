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

import com.mihaibojin.props.core.converters.Cast;
import com.mihaibojin.props.core.converters.Converter;
import com.mihaibojin.props.core.converters.DurationConverter;
import com.mihaibojin.props.core.resolvers.ClasspathPropertyFileResolver;
import com.mihaibojin.props.core.resolvers.EnvResolver;
import com.mihaibojin.props.core.resolvers.SystemPropertyResolver;
import com.mihaibojin.props.core.types.AbstractStringProp;
import java.time.Duration;
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
    Integer aValue = props.prop("prop.id", Cast.asInteger()).build().value();

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
    Integer aValue = props.prop("prop.id", Cast.asInteger()).build().value();

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
            .prop("prop.id", Cast.asInteger())
            .resolver("/propfiles/config1.properties")
            .build()
            .value();

    // ASSERT
    assertThat(aValue, equalTo(1));
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
    String aValue1 = props.prop("a.string1").build().value();
    String aValue2 = props.prop("a.string2").build().value();

    // ASSERT
    assertThat(aValue1, equalTo("one"));
    assertThat(aValue2, equalTo("two"));
  }

  @Test
  void renderPropTemplate() {
    // ARRANGE
    Props props =
        Props.factory()
            .withResolver(new ClasspathPropertyFileResolver("/propfiles/template.properties"))
            .build();

    // ACT
    String rendered = props.renderTemplate("prop.template", null);

    // ASSERT
    assertThat(rendered, equalTo("My name is Mihai and I try to sleep PT8H a night!"));
  }

  @Test
  void renderPropTemplatePretty() {
    // ARRANGE
    Props props =
        Props.factory()
            .withResolver(new ClasspathPropertyFileResolver("/propfiles/template.properties"))
            .build();

    // bind a Prop which defines a custom toString() output
    props.bind(new PrettyDuration("hours.sleep"));

    // ACT
    String rendered = props.renderTemplate("prop.template", null);

    // ASSERT
    assertThat(rendered, equalTo("My name is Mihai and I try to sleep 8H:0M:0S a night!"));
  }

  @Test
  void renderPropTemplateWithNullValues() {
    // ARRANGE
    Props props =
        Props.factory()
            .withResolver(new ClasspathPropertyFileResolver("/propfiles/template.properties"))
            .build();

    // ACT
    String rendered = props.renderTemplate("prop.template.with.nulls", null);

    // ASSERT
    assertThat(rendered, equalTo("null is null"));
  }

  @Test
  void renderPropTemplateFromResolver() {
    // ARRANGE
    Props props =
        Props.factory()
            .withResolver(new ClasspathPropertyFileResolver("/propfiles/template1.properties"))
            .withResolver(new ClasspathPropertyFileResolver("/propfiles/template2.properties"))
            .build();

    // ACT
    String anyResolver = props.renderTemplate("prop.template", "/propfiles/template2.properties");
    String someKeys = props.renderTemplate("prop.template", "/propfiles/template1.properties");
    String allKeys = props.renderTemplate("prop.template", "/propfiles/template2.properties");

    // ASSERT
    assertThat(anyResolver, equalTo("My name is Mihai and my age is 999"));
    assertThat(someKeys, equalTo("My name is Mihai and my age is null"));
    assertThat(allKeys, equalTo("My name is Mihai and my age is 999"));
  }

  /**
   * Defines a custom decoder for the Duration, returning it as a String.
   *
   * <p>This method works and it pretty-prints the Duration property into a template, but has the
   * negative side-effect that it binds this Prop implementation. If the user subsequently requires
   * the actual {@link Duration}, their only choice is to use an unbounded prop ({@link
   * Props#resolveByKey(String, Converter, String)}.
   */
  private static class PrettyDuration extends AbstractStringProp {
    protected PrettyDuration(String key) {
      super(key, null, null, false, false);
    }

    @Override
    public String decode(String value) {
      Duration d = new DurationConverter() {}.decode(value);
      return String.format("%dH:%dM:%dS", d.toHoursPart(), d.toMinutesPart(), d.toSecondsPart());
    }
  }
}
