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

package org.vividus.ui.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.vividus.testdouble.TestActionAttributeType;
import org.vividus.testdouble.TestElementFilter;
import org.vividus.testdouble.TestElementSearch;
import org.vividus.ui.action.search.ElementActionService;
import org.vividus.ui.action.search.IActionAttributeType;
import org.vividus.ui.action.search.SearchAttributes;
import org.vividus.ui.action.search.SearchParameters;
import org.vividus.ui.action.search.Visibility;

class SearchAttributesConversionUtilsTests
{
    private static final String VALUE = "value";
    private static final String INVALID_LOCATOR_MESSAGE = "Invalid locator format. "
            + "Expected matches [(?:By\\.)?([a-zA-Z]+)\\((.+?)\\):?([a-zA-Z]*)] Actual: [";
    private static final char CLOSING_BRACKET = ']';
    private static final String INVALID_LOCATOR = "To.xpath(.a)";

    private SearchAttributesConversionUtils utils;

    @BeforeEach
    void init()
    {
        utils = new SearchAttributesConversionUtils(new ElementActionService(Set.of(
            new TestElementSearch(),
            new TestElementFilter()
        )));
    }

    static Stream<Arguments> actionAttributeSource()
    {
        return Stream.of(
            Arguments.of(createAttributes(TestActionAttributeType.SEARCH, VALUE, Visibility.INVISIBLE),
                "By.search(value):i"),
            Arguments.of(createAttributes(TestActionAttributeType.SEARCH, VALUE, Visibility.INVISIBLE),
                "search(value):i"),
            Arguments.of(createAttributes(TestActionAttributeType.SEARCH, VALUE, Visibility.ALL),
                "By.SeArCH(value):all"),
            Arguments.of(createAttributes(TestActionAttributeType.SEARCH, VALUE, Visibility.VISIBLE), "search(value)"),
            Arguments.of(new SearchAttributes(TestActionAttributeType.SEARCH, VALUE), "By.search(value)"),
            Arguments.of(createAttributes(TestActionAttributeType.SEARCH, VALUE, Visibility.INVISIBLE,
                TestActionAttributeType.FILTER, VALUE), "By.search(value):i->filter.filter(value)"),
            Arguments.of(createAttributes(TestActionAttributeType.SEARCH, VALUE, Visibility.ALL,
                TestActionAttributeType.FILTER, VALUE), "By.search(value):all->filter.filter(value)"),
            Arguments.of(createAttributes(TestActionAttributeType.SEARCH, VALUE, Visibility.INVISIBLE,
                TestActionAttributeType.FILTER, VALUE)
                .addFilter(TestActionAttributeType.FILTER, VALUE)
                .addFilter(TestActionAttributeType.FILTER, VALUE),
                "By.search(value):i->filter.filter(value).filter(value).filter(value)")
        );
    }

    @ParameterizedTest
    @MethodSource("actionAttributeSource")
    void testConvertToSearchAttributes(SearchAttributes expected, String testValue)
    {
        assertEquals(expected, utils.convertToSearchAttributes(testValue));
    }

    @Test
    void testConvertToSearchAttributesInvalidLocatorType()
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> utils.convertToSearchAttributes("By.jquery(.a)"));
        assertEquals("Unsupported locator type: jquery", exception.getMessage());
    }

    @Test
    void testConvertToSearchAttributesInvalidFilterType()
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> utils
                .convertToSearchAttributes("By.search(id)->filter.filter(enabled).filter(text).notFilter(any)"));
        assertEquals("Unsupported filter type: notFilter", exception.getMessage());
    }

    @Test
    void testConvertToSearchAttributesInvalidLocatorFormat()
    {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> utils.convertToSearchAttributes(INVALID_LOCATOR));
        assertEquals(INVALID_LOCATOR_MESSAGE + INVALID_LOCATOR + CLOSING_BRACKET, exception.getMessage());
    }

    @Test
    void testConvertToSearchAttributesEmptyStringLocator()
    {
        assertThrows(IllegalArgumentException.class,
            () -> utils.convertToSearchAttributes(StringUtils.EMPTY),
            INVALID_LOCATOR_MESSAGE + CLOSING_BRACKET);
    }

    @Test
    void testConvertToSearchAttributesSet()
    {
        SearchAttributes searchAttributesId = new SearchAttributes(TestActionAttributeType.SEARCH, VALUE + 1);
        SearchAttributes searchAttributesClassName = new SearchAttributes(TestActionAttributeType.SEARCH, VALUE + 2);
        assertEquals(new HashSet<>(Arrays.asList(searchAttributesId, searchAttributesClassName)),
                utils.convertToSearchAttributesSet("By.search(value1), By.search(value2)"));
    }

    private static SearchAttributes createAttributes(IActionAttributeType type, String value, Visibility elementType)
    {
        SearchParameters searchParameters = new SearchParameters(value, elementType);
        return new SearchAttributes(type, searchParameters);
    }

    private static SearchAttributes createAttributes(IActionAttributeType type, String value, Visibility elementType,
            IActionAttributeType filter, String filterValue)
    {
        return createAttributes(type, value, elementType).addFilter(filter, filterValue);
    }
}
