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
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.mihaibojin.props.core.Props;
import com.mihaibojin.props.core.resolvers.InMemoryResolver;
import com.mihaibojin.props.core.types.AbstractStringProp;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AsyncUpdateExamplesTest {
  private static final String KEY = "async";
  private Props props;
  private InMemoryResolver resolver;

  @BeforeEach
  void setUp() {
    resolver = new InMemoryResolver();

    props = Props.factory().withResolver(resolver).refreshInterval(Duration.ofMillis(50)).build();
  }

  @Test
  void testSubscription() {
    // ARRANGE

    // initialize consumer
    final int elements = 10;
    ArrayDeque<Object> data = new ArrayDeque<>(elements);
    Consumer<String> consumer = data::add;

    StringProp prop = spy(new StringProp(KEY));
    prop.onUpdate(consumer, e -> {});
    props.bind(prop);

    // ACT
    for (int i = 0; i < elements; i++) {
      String toUpdate = "value" + i;
      resolver.set(KEY, toUpdate);
      verify(prop, timeout(10000).times(1)).validateBeforeSet(toUpdate);
    }

    // ASSERT
    assertThat(data, hasSize(elements));
    assertThat(data.getLast(), equalTo("value" + (elements - 1)));
  }

  @AfterEach
  void tearDown() {
    props.close();
  }

  private class StringProp extends AbstractStringProp {
    protected StringProp(String key) {
      super(key, null, null, false, false);
    }

    @Override
    protected void validateBeforeSet(String value) {
      super.validateBeforeSet(value);
    }

    // Mockito doesn't work well with default methods
    @Override
    public String decode(String value) {
      return super.decode(value);
    }
  }
}
