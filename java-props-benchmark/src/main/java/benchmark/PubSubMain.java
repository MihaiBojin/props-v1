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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.infra.Blackhole;

public class PubSubMain {
  // tune this to decide how many properties to create
  public static final int PROP_COUNT = 10_000;

  // tune this to determine how often Props refreshes values from the resolvers
  public static final long REFRESH_MILLIS = 100;

  // default sleep duration between phases
  public static final long SLEEP_MILLIS = 10_000;

  // how many subscribers register for updates; set to a large number to overload the common FJ pool
  public static final long MAX_SUBSCRIBERS = 250;

  // determines which parts of the benchmark run
  public static final boolean RUN_SYNC = true;
  public static final boolean RUN_ASYNC = true;

  public static final String DUMMY = "01233456789";
  public static final String DUMMY2 = "12334567890";
  public static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("HH:mm:ss.A").withZone(ZoneId.systemDefault());

  /** Main entry point. */
  public static void main(String[] args) throws InterruptedException {
    log(
        "Waiting for the JVM to finish loading classes and allowing the user to attach"
            + " profilers...");
    sleep(SLEEP_MILLIS);

    log("Initializing the environment...");

    // cache key names to avoid recreating objects
    String[] keys = new String[PROP_COUNT];
    for (int i = 0; i < PROP_COUNT; i++) {
      keys[i] = String.format("key%s", i);
    }

    // create property values
    InMemoryResolver resolver = new InMemoryResolver();
    for (int i = 0; i < PROP_COUNT; i++) {
      resolver.set(keys[i], DUMMY);
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
            "Today's password is swordfish. I understand instantiating Blackholes directly is"
                + " dangerous.");

    log("Initializing Prop objects...");
    List<Prop<String>> allProps = new ArrayList<>(PROP_COUNT);
    for (int i = 0; i < PROP_COUNT; i++) {
      allProps.add(props.prop(keys[i]).build());
    }

    log("Scheduling Prop updates...");
    String[] values = {DUMMY, DUMMY2};
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.scheduleAtFixedRate(
        () -> {
          // randomly set props to one of the two values, effectively triggering constant updating
          // of the values
          String upd = values[(int) (System.currentTimeMillis() % 2)];
          for (int i = 0; i < PROP_COUNT; i++) {
            resolver.set(keys[i], upd);
          }
        },
        0,
        REFRESH_MILLIS,
        TimeUnit.MILLISECONDS);

    if (RUN_SYNC) {
      sleep(SLEEP_MILLIS);

      log(
          String.format(
              "Iterating through props and reading their values for %dms...", 3 * SLEEP_MILLIS));
      long now = System.currentTimeMillis();
      long it = 0;
      while (System.currentTimeMillis() - now < 3 * SLEEP_MILLIS) {
        Prop<String> prop = allProps.get(Math.floorMod(it++, PROP_COUNT));
        blackhole.consume(prop.value());
      }
    }

    if (RUN_ASYNC) {
      sleep(SLEEP_MILLIS);

      log("Subscribing consumers to all defined Prop objects");
      for (int i = 0; i < MAX_SUBSCRIBERS; i++) {
        allProps.forEach(p -> p.onUpdate(blackhole::consume, (e) -> {}));
      }

      // delimit the initial iteration phase
      sleep(SLEEP_MILLIS);

      // monitor the system for a while, then exit
      log(String.format("Monitoring for %dms...", 3 * SLEEP_MILLIS));
      sleep(3 * SLEEP_MILLIS);
    }

    scheduler.shutdownNow();
    log("Done.");
    sleep(SLEEP_MILLIS);
  }

  private static void log(String s) {
    System.out.println(String.format("%s: %s", formatter.format(Instant.now()), s));
  }

  private static void sleep(long duration) throws InterruptedException {
    log(String.format("Sleeping for %ds...", duration));
    Thread.sleep(duration);
  }
}
