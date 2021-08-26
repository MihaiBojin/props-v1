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

import java.util.Map;

/** Holder class that ensures we can atomically swap the reader and writer */
public class Holder<K, V> {
  final Map<K, V> reader;
  final Map<K, V> writer;

  /** Simple constructor */
  public Holder(Map<K, V> reader, Map<K, V> writer) {
    this.reader = reader;
    this.writer = writer;
  }

  /** Returns a swapped holder */
  public Holder<K, V> swap() {
    return new Holder<K, V>(writer, reader);
  }
}

