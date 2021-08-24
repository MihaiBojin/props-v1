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

package com.mihaibojin.props.core.layers;

import com.mihaibojin.props.core.Props;
import com.mihaibojin.props.core.executors.BackgroundExecutorFactory;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodicRefreshLayer implements Layer {
  public static final Duration GRACE_PERIOD = Duration.ofSeconds(5);

  private final Source source;
  private final ScheduledExecutorService executor;
  private final Duration refreshInterval;

  private final Map<String, String> store = new HashMap<>();
  private final Set<Props> readers = new HashSet<>();

  private PeriodicRefreshLayer(
      Source source, Duration refreshInterval, Duration shutdownGracePeriod) {
    this.source = source;

    this.refreshInterval = refreshInterval;
    this.executor = BackgroundExecutorFactory.create(1, shutdownGracePeriod);

    // TODO: move this out of the constructor
    Runnable runnable = () -> Helper.updateSource(source, store, this::sendEvent);
    executor.scheduleAtFixedRate(runnable, 0, refreshInterval.toMillis(), TimeUnit.MILLISECONDS);
  }

  public static PeriodicRefreshLayer of(Source source, Duration refreshInterval) {
    return new PeriodicRefreshLayer(source, refreshInterval, GRACE_PERIOD);
  }

  public static PeriodicRefreshLayer of(
      Source source, Duration refreshInterval, Duration shutdownGracePeriod) {
    return new PeriodicRefreshLayer(source, refreshInterval, shutdownGracePeriod);
  }

  void sendEvent(String key) {
    // TODO:
  }

  void registerReader(Props reader) {
    readers.add(reader);
  }

  void unregisterReader(Props reader) {
    readers.remove(reader);
  }
}
