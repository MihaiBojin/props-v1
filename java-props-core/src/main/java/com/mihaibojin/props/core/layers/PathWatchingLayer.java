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

import static java.lang.String.format;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import com.mihaibojin.props.core.executors.BackgroundExecutorFactory;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class PathWatchingLayer implements Layer {
  private static final Logger log = Logger.getLogger(PathWatchingLayer.class.getName());

  private final WatchService watcher;
  private final Source source;
  private final Path filePath;

  private final ScheduledExecutorService executor;
  private final Duration refreshInterval;

  private final Map<String, String> store = new HashMap<>();

  /** Construct a layer that watches paths for changes. */
  public PathWatchingLayer(
      Source source, Path filePath, Duration refreshInterval, Duration shutdownGracePeriod)
      throws IOException {
    this.source = source;
    this.filePath = filePath;
    this.refreshInterval = refreshInterval;
    this.watcher = FileSystems.getDefault().newWatchService();

    this.executor = BackgroundExecutorFactory.create(1, shutdownGracePeriod);
  }

  void update() throws IOException, InterruptedException {
    final WatchKey key =
        filePath.getParent().register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

    for (WatchEvent<?> event : key.pollEvents()) {
      // retrieve all events but ignore overflows
      WatchEvent.Kind<?> kind = event.kind();
      if (kind == OVERFLOW) {
        continue;
      }

      @SuppressWarnings("unchecked")
      WatchEvent<Path> ev = (WatchEvent<Path>) event;
      if (!Objects.equals(filePath, ev.context())) {
        // ignore events for any other files except the configured one
        continue;
      }

      // refresh the source
      Helper.updateSource(source, store, this::sendEvent);
    }

    // reset the key so that it can be reused
    boolean valid = key.reset();
    if (!valid) {
      log.warning(
          () -> format("Watched key %s no longer valid; was the parent directory deleted?", key));
    }
  }

  void sendEvent(String key) {
    // TODO:
  }
}
