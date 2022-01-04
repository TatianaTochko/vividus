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

package org.vividus.zephyr.convertor;

import static java.lang.System.lineSeparator;

import java.util.List;
import java.util.stream.Collectors;

import org.vividus.model.jbehave.Parameters;
import org.vividus.model.jbehave.Scenario;
import org.vividus.model.jbehave.Step;
import org.vividus.model.jbehave.Story;
import org.vividus.zephyr.model.CucumberTestStep;

public final class CucumberStoryScenarioConverter
{
    private static final String CUCUMBER_SEPARATOR = "|";
    private static final String CUCUMBER_EXAMPLES = "Examples:";

    private CucumberStoryScenarioConverter()
    {
    }

    public static List<CucumberTestStep> convertScenario(Scenario scenario)
    {
        List<CucumberTestStep> cucumberSteps = scenario.collectSteps()
                .stream()
                .map(Step::getValue)
                .map(CucumberTestStep::new)
                .collect(Collectors.toList());
        if (scenario.getExamples() == null)
        {
            return cucumberSteps;
        }
        CucumberTestStep exampleStep = new CucumberTestStep(CUCUMBER_EXAMPLES);
        exampleStep.setTestData(buildScenarioExamplesTable(scenario.getExamples().getParameters()));
        cucumberSteps.add(exampleStep);
        return cucumberSteps;
    }

    public static List<CucumberTestStep> convertStory(Story story)
    {
        return story.getScenarios()
                .stream()
                .map(Scenario::getTitle)
                .map(CucumberTestStep::new)
                .collect(Collectors.toList());
    }

    private static String buildScenarioExamplesTable(Parameters parameters)
    {
        return joinTableRow(parameters.getNames())
                + parameters.getValues().stream()
                .map(CucumberStoryScenarioConverter::joinTableRow)
                .collect(Collectors.joining());
    }

    private static String joinTableRow(List<String> values)
    {
        return values.stream().collect(
                Collectors.joining(CUCUMBER_SEPARATOR, CUCUMBER_SEPARATOR, CUCUMBER_SEPARATOR + lineSeparator()));
    }
}
