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
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeMap<K, V> implements Map<K, V> {
  private final AtomicLong accessTracker;
  private final AtomicReference<Holder<K, V>> ref;

  public LockFreeMap(AtomicLong accessTracker, AtomicReference<Holder<K, V>> ref) {
    this.accessTracker = accessTracker;
    this.ref = ref;
  }

  @Override
  public int size() {
    try {
      accessTracker.incrementAndGet();
      return ref.get().reader.size();
    } finally {
      accessTracker.incrementAndGet();
    }
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean containsKey(Object key) {
    try {
      accessTracker.incrementAndGet();
      return ref.get().reader.containsKey(key);
    } finally {
      accessTracker.incrementAndGet();
    }
  }

  @Override
  public boolean containsValue(Object value) {
    try {
      accessTracker.incrementAndGet();
      return ref.get().reader.containsValue(value);
    } finally {
      accessTracker.incrementAndGet();
    }
  }

  @Override
  public V get(Object key) {
    try {
      accessTracker.incrementAndGet();
      return ref.get().reader.get(key);
    } finally {
      accessTracker.incrementAndGet();
    }
  }

  @Override
  public V put(K key, V value) {
    throw new IllegalOperationException();
  }

  @Override
  public V remove(Object key) {
    throw new IllegalOperationException();
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    throw new IllegalOperationException();
  }

  @Override
  public void clear() {
    throw new IllegalOperationException();
  }

  @Override
  public Set<K> keySet() {
    try {
      accessTracker.incrementAndGet();
      return new HashSet<>(ref.get().reader.keySet());
    } finally {
      accessTracker.incrementAndGet();
    }
  }

  @Override
  public Collection<V> values() {
    try {
      accessTracker.incrementAndGet();
      return new ArrayList<>(ref.get().reader.values());
    } finally {
      accessTracker.incrementAndGet();
    }
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    try {
      accessTracker.incrementAndGet();
      return new HashSet<>(ref.get().reader.entrySet());
    } finally {
      accessTracker.incrementAndGet();
    }
  }

  /**
   * Thrown when calling an unsupported operation
   */
  public static class IllegalOperationException extends RuntimeException {
    private static final long serialVersionUID = -5318375747058878625L;
  }
}
