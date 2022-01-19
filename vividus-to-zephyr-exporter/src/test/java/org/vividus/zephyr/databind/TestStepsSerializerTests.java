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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashSet;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vividus.util.ResourceUtils;
import org.vividus.zephyr.configuration.JiraFieldsMapping;
import org.vividus.zephyr.model.CucumberTestStep;
import org.vividus.zephyr.model.ZephyrTestCase;

@ExtendWith(MockitoExtension.class)
public class TestStepsSerializerTests
{
    @Spy
    private JiraFieldsMapping fields;
    @InjectMocks
    private TestStepsSerializer serializer;

    @BeforeEach
    void init()
    {
        fields.setTestSteps("step 1, step 2");
        fields.setStoryType("story-type");
    }

    @Test
    void shouldSerializeTest() throws IOException
    {
        ZephyrTestCase test = createBaseTest();
        test.setLabels(new LinkedHashSet<>(List.of("label 1", "label 2")));
        test.setComponents(new LinkedHashSet<>(List.of("component 1", "component 2")));

        try (StringWriter writer = new StringWriter())
        {
            JsonFactory factory = new JsonFactory();
            JsonGenerator generator = factory.createGenerator(writer);

            serializer.serialize(test, generator, null);

            generator.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode actual = mapper.readTree(writer.toString());
            JsonNode expected = mapper.readTree(ResourceUtils.loadResource(getClass(), "report.json"));
            assertEquals(expected, actual);
        }
    }

    private static ZephyrTestCase createBaseTest()
    {
        ZephyrTestCase test = new ZephyrTestCase();
        test.setProjectKey("project key");
        test.setSummary("summary");
        test.setTestSteps(List.of(new CucumberTestStep("testStep 1"), new CucumberTestStep("testStep 2")));
        return test;
    }
}
