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
import com.mihaibojin.props.core.converters.Cast;
import com.mihaibojin.props.core.resolvers.ClasspathPropertyFileResolver;
import com.mihaibojin.props.core.resolvers.EnvResolver;
import com.mihaibojin.props.core.resolvers.PropertyFileResolver;
import com.mihaibojin.props.core.resolvers.SystemPropertyResolver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

public class GenericBenchmarks {

  public static final int PROP_COUNT = 3334;

  /** Loads a String {@link com.mihaibojin.props.core.Prop}. */
  @Benchmark
  @BenchmarkMode(Mode.SampleTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  @Fork(value = 1, warmups = 1)
  public void readPropsUpdatedEachSecond(PropsState state, Blackhole blackhole) {
    state.stringProps.stream()
        .map(Prop::maybeValue)
        .filter(Optional::isPresent)
        .forEach(blackhole::consume);

    state.longProps.stream()
        .map(Prop::maybeValue)
        .filter(Optional::isPresent)
        .forEach(blackhole::consume);

    state.longListProps.stream()
        .map(Prop::maybeValue)
        .filter(Optional::isPresent)
        .forEach(blackhole::consume);
  }

  /** Initialize the {@link Props} registry. */
  @State(Scope.Benchmark)
  public static class PropsState {

    Props props;
    File propFile;
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    List<Prop<String>> stringProps = new ArrayList<>(50);
    List<Prop<Long>> longProps = new ArrayList<>(50);
    List<Prop<List<Long>>> longListProps = new ArrayList<>(50);

    /** Helper method for generating pseudo-random properties. */
    public static Properties generateRandomProperties(File file, int count) {
      Properties properties = new Properties();
      long baseValue = Instant.now().toEpochMilli();

      // generate String properties
      for (int i = 0; i < count; i++) {
        properties.setProperty("string." + i, baseValue + "i");
      }

      // generate Long properties
      for (int i = 0; i < count; i++) {
        properties.setProperty("long." + i, String.valueOf(baseValue + i));
      }

      // generate List<Double> properties
      for (int i = 0; i < count; i++) {
        String val = String.valueOf(baseValue + i);
        properties.setProperty("longlist." + i, String.join(",", val, val, val, val, val));
      }

      try (OutputStream fos = new FileOutputStream(file)) {
        properties.store(fos, null);

      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      return properties;
    }

    /** Initialize the benchmark. */
    @Setup
    public void setup() {
      try {
        propFile = File.createTempFile("jmh", "properties");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      generateRandomProperties(propFile, PROP_COUNT);

      // schedule property updates each second
      executor.scheduleAtFixedRate(
          () -> generateRandomProperties(propFile, PROP_COUNT), 0, 1, TimeUnit.SECONDS);

      // initialize the props object
      props =
          Props.factory()
              .withResolver(new SystemPropertyResolver())
              .withResolver(new EnvResolver())
              .withResolver(new ClasspathPropertyFileResolver("/benchmark.properties"))
              .withResolver(new PropertyFileResolver(propFile.toPath()))
              .build();

      initializeProps();
    }

    /** Initialize all property types. */
    public void initializeProps() {
      for (int i = 0; i < PROP_COUNT; i++) {
        stringProps.add(props.prop("string." + i).isRequired(true).build());
      }
      for (int i = 0; i < PROP_COUNT; i++) {
        longProps.add(props.prop("long." + i, Cast.asLong()).isRequired(true).build());
      }
      for (int i = 0; i < PROP_COUNT; i++) {
        longListProps.add(
            props.prop("longlist." + i, Cast.asListOfLong()).isRequired(true).build());
      }
    }

    @TearDown
    public void teardown() {
      executor.shutdownNow();
      props.close();
    }
  }
}
