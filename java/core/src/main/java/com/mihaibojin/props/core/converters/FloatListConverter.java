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

import static com.mihaibojin.props.core.converters.ConverterUtils.splitStringAsNumbers;

import java.util.List;

/**
 * Converter that splits the inputted {@link String} into a {@link List} of {@link Float}s. The
 * separator can be configured by overriding {@link ListConverter#separator()}.
 */
public interface FloatListConverter extends PropTypeConverter<List<Float>>, ListConverter {

  @Override
  default List<Float> decode(String value) {
    return splitStringAsNumbers(value, separator(), Number::floatValue);
  }
}
