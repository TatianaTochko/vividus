package org.vividus.model.jbehave;

import org.apache.commons.lang3.StringUtils;

public class InvalidScenarioTestCaseIdValue extends Exception
{
    private static final long serialVersionUID = 6922249951195170204L;

    public InvalidScenarioTestCaseIdValue(String storyValue, Iterable<String> scenarioValues)
    {
        super(String.format("Expected meta testCaseId value to be equal to '%s' for all scenarios, but got: %s",
            storyValue, StringUtils.join(scenarioValues, ", ")));
    }

    public InvalidScenarioTestCaseIdValue(Iterable<String> scenarioValues)
    {
        super(String.format("Expected meta testCaseId value to be empty for all scenarios, but got: %s",
            StringUtils.join(scenarioValues, ", ")));
    }
}
