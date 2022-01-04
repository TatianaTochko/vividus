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

package org.vividus.zephyr.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.vividus.model.jbehave.Scenario;
import org.vividus.model.jbehave.Step;
import org.vividus.model.jbehave.Story;
import org.vividus.zephyr.convertor.CucumberStoryScenarioConverter;
import org.vividus.zephyr.model.CucumberTestStep;
import org.vividus.zephyr.model.ZephyrTestCase;

class CucumberStoryConverterTests
{
    private static final String STEP_VALUE = "When I perform action ";
    private static final String SCENARIO_TITLE = "Verify values ";

    @Test
    void shouldConvertStory()
    {
        Story story = new Story();
        List<Scenario> scenarios = new ArrayList<>();
        List<String> scenarioTitles = new ArrayList<>();
        IntStream.range(0, 3)
                .forEach(i ->
                {
                    List<Step> steps = List.of(
                            createStep(STEP_VALUE + i),
                            createStep(STEP_VALUE + i + 1)
                    );
                    scenarioTitles.add(SCENARIO_TITLE + i);
                    scenarios.add(createScenario(scenarioTitles.get(i), steps));
                });

        story.setScenarios(scenarios);
        ZephyrTestCase zephyrTest = new ZephyrTestCase();
        zephyrTest.setTestSteps(CucumberStoryScenarioConverter.convertStory(story));
        List<String> zephyrSteps = zephyrTest.getTestSteps()
                .stream().map(CucumberTestStep::getTestStep).collect(Collectors.toList());
        assertEquals(scenarioTitles, zephyrSteps);
    }

    private Step createStep(String value)
    {
        Step step = new Step();
        step.setValue(value);
        return step;
    }

    private Scenario createScenario(String title, List<Step> steps)
    {
        Scenario scenario = new Scenario();
        scenario.setSteps(steps);
        scenario.setTitle(title);
        return scenario;
    }
}
