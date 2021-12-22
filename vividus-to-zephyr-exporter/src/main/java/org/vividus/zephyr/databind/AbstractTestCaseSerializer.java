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

package org.vividus.zephyr.databind;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.vividus.zephyr.configuration.JiraFieldsMapping;
import org.vividus.zephyr.model.ZephyrTestCase;

public abstract class AbstractTestCaseSerializer<T extends ZephyrTestCase> extends JsonSerializer<T>
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

        writeObjectWithField(generator, jiraFieldsMapping.getStoryType(), "value", "Task");

        writeObjectWithField(generator, "status", NAME, "Backlog");

        generator.writeEndObject();
        generator.writeEndObject();
    }

    private static void writeJsonArray(JsonGenerator generator, String startField, Collection<String> values,
                                       boolean wrapValuesAsObjects) throws IOException
    {
        if (values != null)
        {
            generator.writeArrayFieldStart(startField);
            for (String value : values)
            {
                if (wrapValuesAsObjects)
                {
                    generator.writeStartObject();
                    generator.writeStringField(NAME, value);
                    generator.writeEndObject();
                }
                else
                {
                    generator.writeString(value);
                }
            }
            generator.writeEndArray();
        }
    }

    protected abstract void serializeCustomFields(T testCase, JsonGenerator generator) throws IOException;

    private static void writeObjectWithField(JsonGenerator generator, String objectKey, String fieldName,
                                             String fieldValue) throws IOException
    {
        generator.writeObjectFieldStart(objectKey);
        generator.writeStringField(fieldName, fieldValue);
        generator.writeEndObject();
    }

    protected JiraFieldsMapping getJiraFieldsMapping()
    {
        return jiraFieldsMapping;
    }
}
