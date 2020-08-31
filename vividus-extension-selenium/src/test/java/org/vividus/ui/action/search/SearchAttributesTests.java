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

package org.vividus.ui.action.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vividus.testdouble.TestActionAttributeType;

class SearchAttributesTests
{
    private static final String VALUE = "value";

    private SearchAttributes searchAttributes;

    @BeforeEach
    void beforeEach()
    {
        searchAttributes = new SearchAttributes(TestActionAttributeType.SEARCH, VALUE);
    }

    @Test
    void testAddCompetingFilter()
    {
        searchAttributes.addFilter(TestActionAttributeType.FILTER, VALUE);
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
            () -> searchAttributes.addFilter(TestActionAttributeType.COMPETING_FILTER, VALUE));
        assertEquals("Competing attributes: 'Competing Filter' and 'Filter'", exception.getMessage());
    }

    @Test
    void testAddCompetingSearchAttribute()
    {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
            () -> searchAttributes.addFilter(TestActionAttributeType.COMPETING_FILTER, VALUE));
        assertEquals("Competing attributes: 'Competing Filter' and 'Search'", exception.getMessage());
    }

    @Test
    void testAddFilter()
    {
        searchAttributes.addFilter(TestActionAttributeType.FILTER, VALUE);
        assertEquals(1, searchAttributes.getFilterAttributes().size());
    }

    @Test
    void testAddTwoFiltersNoCompetence()
    {
        searchAttributes.addFilter(TestActionAttributeType.FILTER, VALUE);
        searchAttributes.addFilter(TestActionAttributeType.ADDITIONAL_FILTER, VALUE);
        assertEquals(2, searchAttributes.getFilterAttributes().size());
    }

    @Test
    void testAddFilterNotApplicable()
    {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class,
            () -> searchAttributes.addFilter(TestActionAttributeType.SEARCH, VALUE));
        assertEquals("Filter by attribute 'SEARCH' is not supported", exception.getMessage());
    }

    @Test
    void testAddOneFilterWithMultipleValues()
    {
        searchAttributes.addFilter(TestActionAttributeType.FILTER, VALUE);
        searchAttributes.addFilter(TestActionAttributeType.FILTER, VALUE);
        Map<IActionAttributeType, List<String>> filterAttributes = searchAttributes.getFilterAttributes();
        assertEquals(1, filterAttributes.size());
        assertEquals(VALUE, filterAttributes.get(TestActionAttributeType.FILTER).get(0));
        assertEquals(VALUE, filterAttributes.get(TestActionAttributeType.FILTER).get(1));
    }

    @Test
    void testGetSearchAttributeType()
    {
        assertEquals(TestActionAttributeType.SEARCH, searchAttributes.getSearchAttributeType());
    }

    @Test
    void testGetSearchParameters()
    {
        assertEquals(new SearchParameters(VALUE), searchAttributes.getSearchParameters());
    }

    @Test
    void testToString()
    {
        assertEquals(" Search: 'value'; Visibility: VISIBLE;", searchAttributes.toString());
    }
}
