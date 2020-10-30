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

package com.mihaibojin.props.core.internal;

import java.util.HashSet;
import java.util.Set;

public class TemplateStringUtils {

  /**
   * Parses a string which contains tokens for other props.
   *
   * <p>The accepted format is "I am a {token}". Token delimiters ({ and }) can be commented with
   * the '\' character, e.g.: "I am not a \{token}; neither am I a {token\}".
   */
  public static Set<String> parseTokens(String template) {
    char[] allChars = template.toCharArray();

    Set<String> tokens = new HashSet<>();
    boolean found = false;
    int start = -1;
    int end;

    // if the string starts with an escape (\), skip the first token
    int startingPosition = 1;
    if (allChars[0] == '\\') {
      startingPosition = 2;
    } else if (allChars[0] == '{') {
      // found a '{', mark token start
      start = 1;
      found = true;
    }

    // iterate through all of the template's chars and find all valid tokens
    for (int i = startingPosition; i < allChars.length; i++) {
      // found a '{', mark token start
      if (allChars[i] == '{' && allChars[i - 1] != '\\') {
        start = i + 1;
        found = true;
        continue;
      }

      // if we've found a '{' and we've also found an uncommented '}', create a token
      if (allChars[i] == '}' && allChars[i - 1] != '\\' && found) {
        end = i - start;
        String key = new String(allChars, start, end);
        tokens.add(key);
        found = false;
        start = -1;
      }

      // only certain characters are allowed to form tokens
      if (found && !isIdentifierChar(allChars[i])) {
        found = false;
        start = -1;
      }
    }

    return tokens;
  }

  /** Shorthand method for deciding which characters are allowed as "valid" token identifiers. */
  private static boolean isIdentifierChar(char c) {
    return Character.isLetterOrDigit(c) || c == '_' || c == '.' || c == '-';
  }
}
