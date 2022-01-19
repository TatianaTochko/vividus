/*
 * Copyright 2019-2022 the original author or authors.
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

package org.vividus.zephyr.databind;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;

import org.springframework.stereotype.Component;
import org.vividus.zephyr.model.CucumberTestStep;
import org.vividus.zephyr.model.ZephyrTestCase;

@Component
public class TestStepsSerializer extends AbstractTestCaseSerializer
{
    @Override
    protected void serializeCustomFields(ZephyrTestCase testCase, JsonGenerator generator) throws IOException
    {
        List<CucumberTestStep> steps = testCase.getTestSteps();

        generator.writeObjectFieldStart(getJiraFieldsMapping().getTestSteps());
        generator.writeArrayFieldStart("tests");

        for (int i = 0; i < steps.size(); i++)
        {
            CucumberTestStep testStep = steps.get(i);
            generator.writeStartObject();
            generator.writeStringField("Test Step", testStep.getTestStep());
            generator.writeEndObject();
        }
        generator.writeEndArray();
        generator.writeEndObject();
    }
}
