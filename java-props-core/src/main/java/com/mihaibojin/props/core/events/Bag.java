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

package com.mihaibojin.props.core.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Bag<V> {
  private final AtomicReference<Holder<String, V>> ref =
      new AtomicReference<>(new Holder<>(new HashMap<>(), new HashMap<>()));
  private final List<AtomicLong> accessCounters = new ArrayList<>();

  public Map<String, V> reader() {
    AtomicLong counter = new AtomicLong(0L);
    accessCounters.add(counter);
    return new LockFreeMap<>(counter, ref);
  }

  public List<Op<V>> flush() {
    // switch reader/writer
    ref.getAndUpdate(Holder::swap);

    int startIdx = 0;
    int iter = 0;
    boolean shouldStop = false;
    while (!shouldStop) {
      // assume this is the last time we loop
      shouldStop = true;

      for (int i = startIdx; i < accessCounters.size(); i++) {
        AtomicLong counter = accessCounters.get(i);
        if (Long.remainderUnsigned(counter.get(), 2L) == 0) {
          // the reader is not currently reading
          continue;
        }

        // this reader is in-flight, let's retry from here
        startIdx = i;
        iter++;
        if (iter > 20) {
          // start yielding if we've retried too many times
          Thread.yield();
        }
        // we can't stop looping just yet
        shouldStop = false;

        // retry from this reader and keep retrying while we can move past it
        break;
      }
    }

    // at this point, all the readers should have switched
    // apply the necessary changes
    return calculateChangeSet(ref.get());
  }

  /** Update a store from source and notify on any changed keys. */
  private List<Op<V>> calculateChangeSet(Holder<String, V> holder) {
    List<Op<V>> ops = new ArrayList<>();

    final Map<String, V> freshValues = holder.reader;
    for (String existingKey : holder.writer.keySet()) {
      if (!freshValues.containsKey(existingKey)) {
        // key was deleted
        ops.add(new Op<>(Kind.DELETE, holder.writer.get(existingKey)));
        holder.writer.remove(existingKey);
        continue;
      }

      V updatedValue = freshValues.get(existingKey);
      if (!Objects.equals(holder.writer.get(existingKey), updatedValue)) {
        // key was modified
        // update it
        holder.writer.put(existingKey, updatedValue);
        ops.add(new Op<>(Kind.UPDATE, updatedValue));
        // and remove it from the new map
        freshValues.remove(existingKey);
      }
    }

    // only new keys are left
    holder.writer.putAll(freshValues);
    for (Entry<String, V> entry : freshValues.entrySet()) {
      ops.add(new Op<>(Kind.UPDATE, entry.getValue()));
    }

    return ops;
  }
}
