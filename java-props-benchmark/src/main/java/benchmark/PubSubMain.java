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
import com.mihaibojin.props.core.resolvers.InMemoryResolver;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.openjdk.jmh.infra.Blackhole;

public class PubSubMain {
  // tune this to decide how many properties to create
  public static final int PROP_COUNT = 10_000;

  // tune this to determine how often Props refreshes values from the resolvers
  public static final long REFRESH_MILLIS = 100;

  // default sleep duration between phases
  public static final long SLEEP_MILLIS = 10_000;

  public static final String DUMMY = "01233456789";
  public static final String DUMMY2 = "12334567890";

  /** Main entry point. */
  public static void main(String[] args) throws InterruptedException {
    log(
        "Waiting for the JVM to finish loading classes and allowing the user to attach profilers...");
    sleep(2 * SLEEP_MILLIS);

    log("Initializing the environment...");
    // create property values
    InMemoryResolver resolver = new InMemoryResolver();
    for (int i = 0; i < PROP_COUNT; i++) {
      resolver.set(key(i), DUMMY);
    }

    // initialize the Props registry
    Props props =
        Props.factory()
            .withResolver(resolver)
            .refreshInterval(Duration.ofMillis(REFRESH_MILLIS))
            .build();

    // initialize a blackhole to avoid JIT from optimizing the unused prop value
    Blackhole blackhole =
        new Blackhole(
            "Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");
    Consumer<String> consumer = blackhole::consume;

    // delimit the environment init phase
    sleep(SLEEP_MILLIS);

    log("Initializing Prop objects...");
    List<Prop<String>> allProps = new ArrayList<>(PROP_COUNT);
    for (int i = 0; i < PROP_COUNT; i++) {
      allProps.add(props.prop(key(i)).build());
    }

    // delimit the Prop init phase
    sleep(SLEEP_MILLIS);

    log(
        String.format(
            "Iterating through props and reading their values for %dms...", 3 * SLEEP_MILLIS));
    long now = System.currentTimeMillis();
    int it = 0;
    while (System.currentTimeMillis() - now < 3 * SLEEP_MILLIS) {
      Prop<String> prop = allProps.get(Math.floorMod(it++, PROP_COUNT));
      consumer.accept(prop.rawValue());
    }

    // delimit the initial iteration phase
    sleep(SLEEP_MILLIS);

    log("Scheduling Prop updates...");
    String[] values = {DUMMY, DUMMY2};
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.scheduleAtFixedRate(
        () -> {
          // randomly set props to one of the two values, effectively triggering updates
          String upd = values[(int) (System.currentTimeMillis() % 2)];
          for (int i = 0; i < PROP_COUNT; i++) {
            resolver.set(key(i), upd);
          }
        },
        0,
        REFRESH_MILLIS,
        TimeUnit.MILLISECONDS);

    // initialize PubSub
    log(
        String.format(
            "Subscribing a consumer to all defined Prop objects and monitoring for %dms...",
            3 * SLEEP_MILLIS));
    allProps.forEach(p -> p.onUpdate(consumer, (e) -> {}));

    // monitor the system for a while, then exit
    sleep(3 * SLEEP_MILLIS);

    props.close();
    scheduler.shutdownNow();
    log("Done.");
  }

  private static void log(String s) {
    System.out.println(String.format("%d: %s", Instant.now().getNano(), s));
  }

  private static void sleep(long duration) throws InterruptedException {
    log(String.format("Sleeping for %ds...", duration));
    Thread.sleep(duration);
  }

  private static String key(int i) {
    return String.format("key%s", i);
  }
}
