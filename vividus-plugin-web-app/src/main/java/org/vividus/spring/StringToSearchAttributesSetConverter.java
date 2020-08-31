/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.spring;

import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.vividus.ui.action.search.SearchAttributes;
import org.vividus.ui.util.SearchAttributesConversionUtils;

public class StringToSearchAttributesSetConverter implements Converter<String, Set<SearchAttributes>>
{
    private SearchAttributesConversionUtils conversionUtils;

    @Override
    public Set<SearchAttributes> convert(String source)
    {
        return conversionUtils.convertToSearchAttributesSet(source);
    }

    public void setConversionUtils(SearchAttributesConversionUtils conversionUtils)
    {
        this.conversionUtils = conversionUtils;
    }
}
