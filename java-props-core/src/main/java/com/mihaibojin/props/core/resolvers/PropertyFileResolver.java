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

package com.mihaibojin.props.core.resolvers;

import static java.lang.String.format;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;

import com.mihaibojin.props.core.annotations.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class PropertyFileResolver implements Resolver {

  private static final Logger log = Logger.getLogger(PropertyFileResolver.class.getName());

  private final Map<String, String> store = new HashMap<>();
  private final Path location;
  private final boolean isReloadable;

  /** Constructs a {@link Resolver} which should only read the properties file once. */
  public PropertyFileResolver(Path location) {
    this(location, false);
  }

  public PropertyFileResolver(Path location, boolean isReloadable) {
    this.location = location;
    this.isReloadable = isReloadable;
  }

  @Override
  public boolean isReloadable() {
    return isReloadable;
  }

  @Override
  @Nullable
  public String get(String key) {
    return store.get(key);
  }

  @Override
  public Set<String> reload() {
    if (!Files.exists(location)) {
      if (log.isLoggable(FINE)) {
        log.fine(
            () ->
                format("Skipping %s; file not found at %s", getClass().getSimpleName(), location));
      }
      return Set.of();
    }

    try (InputStream stream = Files.newInputStream(location)) {
      return ResolverUtils.mergeMapsInPlace(store, ResolverUtils.loadPropertiesFromStream(stream));

    } catch (IOException | IllegalArgumentException e) {
      log.log(SEVERE, e, () -> format("Could not read configuration from %s", location));
    }

    return Set.of();
  }

  @Override
  public String id() {
    return location.toString();
  }
}
