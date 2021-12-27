/*
 * Copyright 2019-2021 the original author or authors.
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

package org.vividus.model.jbehave;

import static org.vividus.model.MetaWrapper.META_VALUES_SEPARATOR;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;

public class Story
{
    private static final String TEST_CASE_ID = "testCaseId";

    private String path;
    private Lifecycle lifecycle;
    private List<Scenario> scenarios;
    private List<Meta> meta;

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Lifecycle getLifecycle()
    {
        return lifecycle;
    }

    public void setLifecycle(Lifecycle lifecycle)
    {
        this.lifecycle = lifecycle;
    }

    public List<Scenario> getScenarios()
    {
        return scenarios;
    }

    public void setScenarios(List<Scenario> scenarios)
    {
        this.scenarios = scenarios;
    }

    public List<Meta> getMeta()
    {
        return meta;
    }

    public void setMeta(List<Meta> meta)
    {
        this.meta = meta;
    }

    /**
     * Get unique scenarios.
     *
     * All iterations over story-level and scenario-level ExamplesTables are folded into unique scenario instances
     *
     * @return list of values with {@link Scenario} type
     */
    public List<Scenario> getFoldedScenarios()
    {
        if (lifecycle == null || lifecycle.getParameters() == null)
        {
            return scenarios;
        }

        Parameters lifecycleParameters = lifecycle.getParameters();
        int partition = scenarios.size() <= 1 ? 1 : scenarios.size() / lifecycleParameters.getValues().size();
        List<List<Scenario>> scenariosPerExample = Lists.partition(scenarios, partition);

        return IntStream.range(0, partition)
                        .mapToObj(index -> scenariosPerExample.stream()
                                                              .map(l -> l.get(index))
                                                              .collect(Collectors.toList()))
                        .map(s -> s.stream()
                                   .reduce(Story::merge))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .peek(s -> adjustScenarioWithLifecycle(s.getExamples().getParameters(), lifecycleParameters))
                        .collect(Collectors.toList());
    }

    private static Scenario merge(Scenario left, Scenario right)
    {
        List<Example> examples = left.getExamples().getExamples();
        examples.addAll(right.getExamples().getExamples());
        return left;
    }

    private static void adjustScenarioWithLifecycle(Parameters scenarioParameters, Parameters lifecycleParameters)
    {
        List<List<String>> lifecycleValues = lifecycleParameters.getValues();
        scenarioParameters.getNames().addAll(lifecycleParameters.getNames());

        List<List<String>> baseValues = scenarioParameters.getValues();
        if (baseValues.isEmpty())
        {
            baseValues.addAll(lifecycleValues);
            return;
        }

        List<List<String>> adjustedValues = Lists.cartesianProduct(baseValues, lifecycleValues)
                                                 .stream().map(Iterables::concat)
                                                 .map(Lists::newArrayList)
                                                 .collect(Collectors.toList());

        scenarioParameters.setValues(adjustedValues);
    }

    /**
     * Get unique <b>meta</b> value
     *
     * <p>If the <b>meta</b> does not exist or has no value, an empty {@link Optional} will be returned
     *
     * <p><i>Notes</i>
     * <ul>
     * <li><b>meta</b> value is trimmed upon returning</li>
     * <li><i>;</i> char is used as a separator for <b>meta</b> with multiple values</li>
     * </ul>
     *
     * @param metaName the meta name
     * @return the meta value
     * @throws NotUniqueMetaValueException if the <b>meta</b> has more than one value
     */
    public Optional<String> getUniqueMetaValue(String metaName)
        throws NotUniqueMetaValueException, InvalidScenarioTestCaseIdValue
    {
        Set<String> values = getMetaValues(metaName);
        if (values.size() > 1)
        {
            throw new NotUniqueMetaValueException(metaName, values);
        }
        if (metaName.equals(TEST_CASE_ID))
        {
            return getTestCaseIdMetaValue(values);
        }
        return values.isEmpty() ? Optional.empty() : Optional.of(values.iterator().next());
    }

    /**
     * Get all <b>meta</b> values
     *
     * <p><i>Notes</i>
     * <ul>
     * <li><b>meta</b>s without value are ignored</li>
     * <li><b>meta</b> values are trimmed upon returning</li>
     * <li><i>;</i> char is used as a separator for <b>meta</b> with multiple values</li>
     * </ul>
     *
     * @param metaName the meta name
     * @return  the meta values
     */
    public Set<String> getMetaValues(String metaName)
    {
        return getMetaStream().filter(m -> metaName.equals(m.getName()))
                .map(Meta::getValue)
                .filter(StringUtils::isNotEmpty)
                .map(String::trim)
                .map(value -> StringUtils.splitPreserveAllTokens(value, META_VALUES_SEPARATOR))
                .flatMap(Stream::of)
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Stream<Meta> getMetaStream()
    {
        return Optional.ofNullable(getMeta())
                .map(List::stream)
                .orElseGet(Stream::empty);
    }

    private Optional<String> getTestCaseIdMetaValue(Set<String> values) throws InvalidScenarioTestCaseIdValue
    {
        Set<String> scenariosTestCaseIds = getScenariosTestCaseIds();
        if (values.isEmpty())
        {
            if (!scenariosTestCaseIds.isEmpty())
            {
                throw new InvalidScenarioTestCaseIdValue(scenariosTestCaseIds);
            }
            return Optional.empty();
        }
        String storyTestCaseId = values.iterator().next();
        if (scenariosTestCaseIds.stream().anyMatch(id -> !id.equals(storyTestCaseId)))
        {
            throw new InvalidScenarioTestCaseIdValue(storyTestCaseId, scenariosTestCaseIds);
        }
        return Optional.of(storyTestCaseId);
    }

    private Set<String> getScenariosTestCaseIds()
    {
        Set<String> testCaseIds = new LinkedHashSet<>();
        for (Scenario scenario : scenarios)
        {
            testCaseIds.addAll(scenario.getMetaValues(TEST_CASE_ID));
        }
        return testCaseIds;
    }
}
