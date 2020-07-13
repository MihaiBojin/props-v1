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
import static java.util.Objects.isNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClasspathPropertyFileResolver implements Resolver {
  private static final Logger log = Logger.getLogger(ClasspathPropertyFileResolver.class.getName());

  private final Map<String, String> store = new HashMap<>();
  private final String location;
  private final boolean isReloadable;

  /** Constructs a {@link Resolver} which should only read properties from the classpath once. */
  public ClasspathPropertyFileResolver(String location) {
    this(location, false);
  }

  public ClasspathPropertyFileResolver(String location, boolean isReloadable) {
    this.location = location;
    this.isReloadable = isReloadable;
  }

  @Override
  public Optional<String> get(String key) {
    return Optional.ofNullable(store.get(key));
  }

  @Override
  public Set<String> reload() {
    try (InputStream stream = getClass().getResourceAsStream(location)) {
      if (isNull(stream)) {
        log.fine(format("Skipping %s; resource not found in classpath", location));
        return Set.of();
      }

      return ResolverUtils.mergeMapsInPlace(store, ResolverUtils.loadPropertiesFromStream(stream));
    } catch (IOException e) {
      log.log(Level.SEVERE, "Could not read properties from classpath: " + location, e);
    }

    return Set.of();
  }

  @Override
  public boolean isReloadable() {
    return isReloadable;
  }

  @Override
  public String id() {
    return location;
  }
}
