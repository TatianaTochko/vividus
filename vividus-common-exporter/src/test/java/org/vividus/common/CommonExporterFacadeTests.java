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

package org.vividus.common;

import static com.github.valfirst.slf4jtest.LoggingEvent.info;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vividus.jira.JiraConfigurationException;
import org.vividus.jira.JiraFacade;

@ExtendWith({ MockitoExtension.class, TestLoggerFactoryExtension.class })
public class CommonExporterFacadeTests
{
    @Mock
    private JiraFacade jiraFacade;

    private final TestLogger logger = TestLoggerFactory.getTestLogger(CommonExporterFacade.class);

    @Test
    void shouldCreateTestsLink() throws IOException, JiraConfigurationException
    {
        CommonExporterFacade exporterFacade = new CommonExporterFacade(jiraFacade);
        String requirementId = "requirement id";
        String linkType = "Tests";
        String testCaseId = "TEST_1";

        exporterFacade.createTestsLink(testCaseId, requirementId);

        verify(jiraFacade).createIssueLink(testCaseId, requirementId, linkType);
        assertThat(logger.getLoggingEvents(),
                is(List.of(info("Create '{}' link from {} to {}", linkType, testCaseId, requirementId))));
    }
}
