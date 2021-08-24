/*
 * Copyright 2021 Mihai Bojin
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

package com.mihaibojin.props.core.executors;

import static java.lang.String.format;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BackgroundExecutorFactory {
  private static final Logger log = Logger.getLogger(BackgroundExecutorFactory.class.getName());

  /**
   * Creates {@link ScheduledExecutorService} based on daemon {@link Thread}s.
   *
   * <p>Any such executors will attempt graceful shutdown when instructed and will wait for any
   * current requests to complete, up to the specified grace period (millisecond granularity).
   */
  public static ScheduledExecutorService create(int threads, Duration shutdownGracePeriod) {
    final var executor = Executors.newScheduledThreadPool(threads, new DaemonThreadFactory());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown(executor, shutdownGracePeriod)));
    log.info(
        () ->
            format(
                "Initialized %s with %d threads and a grace period of %d ms",
                executor, threads, shutdownGracePeriod.toMillis()));
    return executor;
  }

  /**
   * Gracefully terminate the specified {@link ScheduledExecutorService}, waiting for the specified
   * grace period (millisecond granularity).
   */
  public static void shutdown(ExecutorService executor, Duration gracePeriod) {
    boolean ok = true;

    log.info(() -> format("Attempting to shut down %s", executor));
    long t0 = System.currentTimeMillis();
    executor.shutdown();
    try {
      // wait for running threads to complete
      ok = executor.awaitTermination(gracePeriod.toMillis(), TimeUnit.MILLISECONDS);
      log.info(
          () ->
              format(
                  "%s shut down after %d milliseconds", executor, System.currentTimeMillis() - t0));
    } catch (InterruptedException e) {
      log.warning(() -> format("Interrupted while waiting for shutdown %s", executor));
      ok = false;
      Thread.currentThread().interrupt();
    } finally {
      // if the executor could not be shutdown, attempt to forcefully shut it down
      if (!ok) {
        log.info(() -> format("Attempting a forceful shutdown of %s", executor));
        executor.shutdownNow();
      }
    }
  }
}
