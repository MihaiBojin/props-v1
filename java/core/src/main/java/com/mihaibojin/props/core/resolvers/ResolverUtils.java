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

import static java.util.logging.Level.SEVERE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ResolverUtils {
  private static final Logger log = Logger.getLogger(ResolverUtils.class.getName());
  private static final Pattern configLinePattern =
      Pattern.compile(
          "^(?<type>[a-z]+)(?:=(?<path>[^,]+)(?:,(?<reload>(true|false)))?)?$",
          Pattern.CASE_INSENSITIVE);

  /**
   * Loads a {@link Properties} object from the passed {@link InputStream} and returns a {@link Map}
   * containing all key->value mappings.
   *
   * @throws NullPointerException if a null <code>InputStream</code> was passed
   * @throws IOException if the <code>InputStream</code> cannot be read
   */
  public static Map<String, String> loadPropertiesFromStream(InputStream stream)
      throws IOException {
    Properties properties = new Properties();
    properties.load(Objects.requireNonNull(stream));
    return readPropertiesToMap(properties);
  }

  /**
   * Iterates over all the input {@link Properties} and returns a {@link Map} containing all
   * key->value mappings.
   */
  private static Map<String, String> readPropertiesToMap(Properties properties) {
    Map<String, String> store = new HashMap<>();
    for (String key : properties.stringPropertyNames()) {
      store.put(key, properties.getProperty(key));
    }
    return store;
  }

  /**
   * Merges the <code>collector</code> and <code>updated</code> maps by.
   * <li/>- deleting any keys which are no longer defined in <code>updated</code>
   * <li/>- updating any keys whose values have changed in <code>updated</code>
   * <li/>- setting any new keys whose values have been added in <code>updated</code>
   *
   * @return the {@link Set} of new, updated, and deleted keys
   */
  public static Set<String> mergeMapsInPlace(
      Map<String, String> collector, Map<String, String> updated) {
    var toDel = new HashSet<String>();

    // if the key doesn't exist in the updated set, mark it for deletion
    for (Entry<String, String> val : collector.entrySet()) {
      if (!updated.containsKey(val.getKey())) {
        toDel.add(val.getKey());
      }
    }

    // delete all keys which do not appear in the updated list
    for (String key : toDel) {
      collector.remove(key);
    }

    // set all updated values
    for (Entry<String, String> newVal : updated.entrySet()) {
      if (!Objects.equals(collector.get(newVal.getKey()), newVal.getValue())) {
        collector.put(newVal.getKey(), newVal.getValue());
        // store each updated key into the same 'toDel' set, to avoid creating a new object
        toDel.add(newVal.getKey());
      }
    }

    // return all deleted, new, and updated keys
    return toDel;
  }

  /**
   * Reads all lines from an {@link InputStream} that specifies multiple resolver configurations.
   *
   * @return a list of {@link Resolver}s
   */
  public static List<Resolver> readResolverConfig(InputStream stream) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
      return reader.lines().map(ResolverUtils::readConfigLine).collect(Collectors.toList());

    } catch (Exception e) {
      log.log(SEVERE, "Could not read resolver configuration", e);
      return List.of();
    }
  }

  /**
   * Parses a config line and instantiates a resolver.
   *
   * @throws IllegalArgumentException if the line is invalid, or an unknown <code>type</code> was
   *     specified
   */
  static Resolver readConfigLine(String line) {
    Matcher matcher = configLinePattern.matcher(line);

    if (!matcher.matches()) {
      throw new IllegalArgumentException("Cannot read config line, syntax incorrect: " + line);
    }

    String type = Optional.ofNullable(matcher.group("type")).map(String::toLowerCase).orElse(null);
    String path = matcher.group("path");
    boolean reload = Boolean.parseBoolean(matcher.group("reload"));

    if (Objects.equals(type, "file")) {
      return new PropertyFileResolver(Paths.get(path), reload);
    } else if (Objects.equals(type, "classpath")) {
      return new ClasspathPropertyFileResolver(path, reload);
    } else if (Objects.equals(type, "system")) {
      return new SystemPropertyResolver();
    } else if (Objects.equals(type, "env")) {
      return new EnvResolver();
    } else {
      throw new IllegalArgumentException("Did not recognize " + type + " in: " + line);
    }
  }
}
