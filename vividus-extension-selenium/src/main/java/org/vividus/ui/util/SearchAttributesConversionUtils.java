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

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.vividus.selenium.LocatorFactory;
import org.vividus.ui.action.search.IElementAction;
import org.vividus.ui.action.search.IElementActionService;
import org.vividus.ui.action.search.SearchAttributes;
import org.vividus.ui.action.search.Visibility;

public class SearchAttributesConversionUtils
{
    private static final int SEARCH_VALUE_GROUP = 2;
    private static final int ATTRIBURTE_TYPE_GROUP = 1;
    private static final String EMPTY = "";
    private static final int ELEMENT_TYPE_GROUP = 3;
    private static final char CLOSING_BRACKET = ']';
    private static final String LOCATOR_FORMAT = "(?:By\\.)?([a-zA-Z]+)\\((.+?)\\):?([a-zA-Z]*)";
    private static final Pattern SEARCH_ATTRIBUTE_PATTERN = Pattern.compile(LOCATOR_FORMAT);
    private static final Pattern FILTER_PATTERN = Pattern.compile("([a-zA-Z]+)(?:\\()([^()]*)(?:\\))");

    private final IElementActionService elementActionService;

    public SearchAttributesConversionUtils(IElementActionService elementActionService)
    {
        this.elementActionService = elementActionService;
    }

    public Set<SearchAttributes> convertToSearchAttributesSet(String locatorsAsString)
    {
        return LocatorFactory.convertStringToLocators(locatorsAsString).stream()
                .map(locator -> convertToSearchAttributes(locator.getType(), locator.getValue()))
                .collect(Collectors.toSet());
    }

    public SearchAttributes convertToSearchAttributes(String locatorAsString)
    {
        String[] locatorParts = locatorAsString.split("->filter\\.");
        String locator = locatorParts.length == 0 ? locatorAsString : locatorParts[0];
        String filters = locatorParts.length == 2 ? locatorParts[1] : EMPTY;
        Matcher matcher = SEARCH_ATTRIBUTE_PATTERN.matcher(locator);
        if (matcher.matches())
        {
            String actionAttributeType = matcher.group(ATTRIBURTE_TYPE_GROUP).toLowerCase();
            String searchValue = matcher.group(SEARCH_VALUE_GROUP);
            String elementType = matcher.group(ELEMENT_TYPE_GROUP);
            SearchAttributes searchAttributes = convertToSearchAttributes(actionAttributeType, searchValue);
            if (!elementType.isEmpty())
            {
                searchAttributes.getSearchParameters().setVisibility(Visibility.getElementType(elementType));
            }
            if (!filters.isEmpty())
            {
                Matcher filterMatcher = FILTER_PATTERN.matcher(filters);
                while (filterMatcher.find())
                {
                    applyFilter(searchAttributes, filterMatcher.group(1), filterMatcher.group(2));
                }
            }
            return searchAttributes;
        }
        throw new IllegalArgumentException("Invalid locator format. Expected matches ["
                + LOCATOR_FORMAT + CLOSING_BRACKET + " Actual: [" + locatorAsString + CLOSING_BRACKET);
    }

    private void applyFilter(SearchAttributes searchAttributes, String filterType, String filterValue)
    {
        createSearchAttributes(elementActionService.getFilterActions(),
            type -> searchAttributes.addFilter(type.getAttributeType(), filterValue), "filter", filterType);
    }

    private SearchAttributes convertToSearchAttributes(String searchType, String searchValue)
    {
        return createSearchAttributes(elementActionService.getSearchActions(),
            type -> new SearchAttributes(type.getAttributeType(), searchValue), "locator", searchType);
    }

    private static SearchAttributes createSearchAttributes(Set<IElementAction> actions,
            Function<IElementAction, SearchAttributes> mapper, String operatorType, String searchType)
    {
        return findElementAction(actions, searchType)
                .map(mapper)
                .orElseThrow(() -> new IllegalArgumentException(
                    String.format("Unsupported %s type: %s", operatorType, searchType)));
    }

    private static Optional<IElementAction> findElementAction(Set<IElementAction> actions, String type)
    {
        String typeInLowerCase = type.toLowerCase();
        return actions.stream()
                    .filter(t -> StringUtils.replace(t.getAttributeType().getKey().toLowerCase(), "_", "")
                            .equals(typeInLowerCase))
                    .findFirst();
    }
}
