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

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Helper {

  /** Update a store from source and notify on any changed keys. */
  public static void updateSource(
      Source source, Map<String, String> store, Consumer<String> notify) {
    final Map<String, String> freshValues = source.read();

    for (String existingKey : store.keySet()) {
      if (!freshValues.containsKey(existingKey)) {
        // key was deleted
        store.remove(existingKey);
        notify.accept(existingKey);
        continue;
      }

      String updatedValue = freshValues.get(existingKey);
      if (!Objects.equals(store.get(existingKey), updatedValue)) {
        // key was modified
        // update it
        store.put(existingKey, updatedValue);
        notify.accept(existingKey);
        // and remove it from the new map
        freshValues.remove(existingKey);
      }
    }

    // only new keys are left
    store.putAll(freshValues);
    freshValues.keySet().forEach(notify);
  }
}
