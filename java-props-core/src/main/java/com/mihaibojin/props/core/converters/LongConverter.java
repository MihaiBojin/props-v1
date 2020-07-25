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

import static com.mihaibojin.props.core.converters.ConverterUtils.safeParseNumber;
import static java.util.Objects.requireNonNull;

/**
 * Converter that casts the inputted {@link String} to an {@link Long} value.
 */
public interface LongConverter extends Converter<Long> {

  @Override
  default Long decode(String value) {
    return requireNonNull(safeParseNumber(value)).longValue();
  }
}