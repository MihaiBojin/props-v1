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

import com.mihaibojin.props.core.annotations.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Useful for tests, when the implementation requires overriding values. */
public class InMemoryResolver implements Resolver {

  private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<>();
  private final Set<String> updatedKeys = new HashSet<>();

  /** Stores the specified (key, value) pair in memory. */
  public void set(String key, String value) {
    store.put(key, value);

    synchronized (this) {
      updatedKeys.add(key);
    }
  }

  @Override
  @Nullable
  public String get(String key) {
    return store.get(key);
  }

  @Override
  public boolean isReloadable() {
    return true;
  }

  @Override
  public Set<String> reload() {
    synchronized (this) {
      Set<String> results = new HashSet<>(updatedKeys);
      updatedKeys.clear();
      return results;
    }
  }

  @Override
  public String id() {
    return "MEMORY";
  }
}
