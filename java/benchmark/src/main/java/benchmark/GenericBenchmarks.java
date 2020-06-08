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

package benchmark;

import com.mihaibojin.props.core.Prop;
import com.mihaibojin.props.core.Props;
import com.mihaibojin.props.core.resolvers.ClasspathPropertyFileResolver;
import java.util.Optional;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;

public class GenericBenchmarks {

  /** Loads a String {@link com.mihaibojin.props.core.Prop}. */
  @Benchmark
  @Fork(value = 1, warmups = 1)
  @BenchmarkMode(Mode.Throughput)
  public void loadStringProp() {
    Props props =
        Props.factory()
            .withResolver(new ClasspathPropertyFileResolver("/benchmark.properties"))
            .build();

    try (props) {
      Prop<String> prop = props.prop("a.string").build();
      Optional<String> value = prop.value();
    }
  }
}
