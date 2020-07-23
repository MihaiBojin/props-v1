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

package com.mihaibojin.props.core.converters;

import java.nio.file.Path;

/**
 * Converter that returns the inputted {@link String}.
 */
public interface PathConverter extends Converter<Path> {

  /**
   * If true, <code>~</code> will expand to the user's home directory, as returned by <code>
   * System.getProperty("user.home")</code>.
   */
  boolean expandUserHomeDir();


  /**
   * Parses the specified input string into a <code>Path</code>.
   *
   * <p>This method supports expanding <code>~</code> to the user's home directory (as returned by
   * <code>System.getProperty("user.home")</code>), if {@link #expandUserHomeDir()} returns <code>
   * true</code>.
   *
   * <p>This method does NOT support the <code>~username</code> shorthand (which will incorrectly
   * expand to <code>/path/to/home/usernameusername</code>.
   */
  @Override
  default Path decode(String value) {
    // determine if we are allowed to expand '~' to the user's home dir
    if (expandUserHomeDir()) {
      return Path.of(replaceTildeWithUserHomeDir(value));
    }
    return Path.of(value);
  }

  /**
   * Prefixes the specified path with the user's home directory, if the input starts with <code>~
   * </code>.
   *
   * <p>It is the caller's responsibility to prevent any edge-cases or bad input (e.g., '~~'); this
   * method will simply replace the first tilda character with the user's home directory.
   */
  static String replaceTildeWithUserHomeDir(String path) {
    // if the specified path starts with '~'
    if (path.startsWith("~")) {
      // prefix the path with the user's home directory
      return System.getProperty("user.home") + path.substring(1);
    }

    return path;
  }
}
