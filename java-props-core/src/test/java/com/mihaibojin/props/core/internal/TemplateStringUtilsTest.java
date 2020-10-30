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

package com.mihaibojin.props.core.internal;

import static java.util.Objects.isNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import com.mihaibojin.props.core.annotations.Nullable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class TemplateStringUtilsTest {

  @Test
  void testTokenizer() {
    singleTokenTest("I am a {token}", "token");
    singleTokenTest("I am not a \\{token}", null);
    singleTokenTest("Neither am I a {token\\}", null);
    singleTokenTest("{validToken}", "validToken");
    singleTokenTest("{valid.token}", "valid.token");
    singleTokenTest("{valid-token}", "valid-token");
    singleTokenTest("\\{invalidToken}", null);
    singleTokenTest("{invalidToken\\}", null);
    singleTokenTest("{invalid token}", null);
    singleTokenTest("{invalid$token}", null);
    singleTokenTest("{invalid$token} but {valid-token}", "valid-token");
    singleTokenTest("\\{invalid{valid-token}", "valid-token");
  }

  @Test
  void testMultipleTokens() {
    // ARRANGE
    String template = "I am a {token} and I am a {token} too";

    // ACT
    Set<String> tokens = TemplateStringUtils.parseTokens(template);

    // ASSERT
    List<String> keys = tokens.stream().map(Object::toString).collect(Collectors.toList());
    assertThat(keys, hasSize(1));
    assertThat(keys, hasItem("token"));
  }

  /** Assertion logic for simple token identification. */
  private void singleTokenTest(String template, @Nullable String expectedToken) {
    // ACT
    Set<String> tokens = TemplateStringUtils.parseTokens(template);

    // ASSERT
    List<String> keys = tokens.stream().map(Object::toString).collect(Collectors.toList());
    if (isNull(expectedToken)) {
      assertThat(keys, hasSize(0));

    } else {
      assertThat(keys, hasItem(expectedToken));
    }
  }
}
