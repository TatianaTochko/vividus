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

import static org.vividus.exporter.databind.SerializeJsonHelper.writeJsonArray;
import static org.vividus.exporter.databind.SerializeJsonHelper.writeObjectWithField;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.vividus.zephyr.configuration.JiraFieldsMapping;
import org.vividus.zephyr.model.ZephyrTestCase;

public abstract class AbstractTestCaseSerializer extends JsonSerializer<ZephyrTestCase>
{
    private static final String NAME = "name";

    @Autowired
    private JiraFieldsMapping jiraFieldsMapping;

    @Override
    public void serialize(ZephyrTestCase zephyrTestCase, JsonGenerator generator, SerializerProvider serializers)
            throws IOException
    {
        generator.writeStartObject();

        generator.writeObjectFieldStart("fields");

        writeObjectWithField(generator, "project", "key", zephyrTestCase.getProjectKey());

        writeObjectWithField(generator, "issuetype", NAME, "Test");

        writeJsonArray(generator, "labels", zephyrTestCase.getLabels(), false);

        writeJsonArray(generator, "components", zephyrTestCase.getComponents(), true);

        generator.writeStringField("summary", zephyrTestCase.getSummary());

        if (Objects.nonNull(jiraFieldsMapping.getStoryType()) && !jiraFieldsMapping.getStoryType().isEmpty())
        {
            writeObjectWithField(generator, jiraFieldsMapping.getStoryType(), "value", "Task");
        }

        generator.writeEndObject();
        generator.writeEndObject();
    }

    protected abstract void serializeCustomFields(ZephyrTestCase testCase, JsonGenerator generator) throws IOException;

    protected JiraFieldsMapping getJiraFieldsMapping()
    {
        return jiraFieldsMapping;
    }
}
