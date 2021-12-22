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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vividus.jira.JiraConfigurationException;
import org.vividus.jira.JiraFacade;

public class CommonExporterFacade
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExporterFacade.class);
    private final JiraFacade jiraFacade;

    public CommonExporterFacade(JiraFacade jiraFacade)
    {
        this.jiraFacade = jiraFacade;
    }

    public void createTestsLink(String testCaseId, String requirementId) throws IOException, JiraConfigurationException
    {
        String linkType = "Tests";
        LOGGER.atInfo().addArgument(linkType)
                .addArgument(testCaseId)
                .addArgument(requirementId)
                .log("Create '{}' link from {} to {}");
        jiraFacade.createIssueLink(testCaseId, requirementId, linkType);
    }
}
