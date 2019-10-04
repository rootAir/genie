/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.web.apis.rest.v3.controllers;

import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests for Jobs REST API, executing in agent mode.
 *
 * @author mprimi
 * @since 4.0.0
 */
@TestPropertySource(
    properties = {
        JobRestController.AGENT_JOB_EXECUTION_KEY + "=true"
    }
)
public class JobRestControllerAgentExecutionIntegrationTest extends JobRestControllerIntegrationTest {

    /**
     * Constructor.
     */
    public JobRestControllerAgentExecutionIntegrationTest() {
        super(true);
    }
}
