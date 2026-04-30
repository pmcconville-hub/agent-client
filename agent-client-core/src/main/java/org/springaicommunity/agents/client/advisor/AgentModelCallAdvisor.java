/*
 * Copyright 2024 Spring AI Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springaicommunity.agents.client.advisor;

import org.springaicommunity.agents.client.AgentClientRequest;
import org.springaicommunity.agents.client.AgentClientResponse;
import org.springaicommunity.agents.client.advisor.api.AgentCallAdvisor;
import org.springaicommunity.agents.client.advisor.api.AgentCallAdvisorChain;
import org.springaicommunity.agents.model.AgentApi;
import org.springaicommunity.agents.model.AgentTaskRequest;

/**
 * Terminal advisor that converts client-layer requests to agent API requests and invokes
 * the actual {@link AgentApi}.
 *
 * <p>
 * This advisor is typically last in the chain and performs the actual agent call. It
 * should have the lowest precedence to run after all other advisors (context injection,
 * validation, etc.).
 *
 * @author Mark Pollack
 * @since 0.1.0
 */
public class AgentModelCallAdvisor implements AgentCallAdvisor {

	private static final String NAME = AgentModelCallAdvisor.class.getName();

	private static final int ORDER = LOWEST_PRECEDENCE;

	private final AgentApi agentApi;

	public AgentModelCallAdvisor(AgentApi agentApi) {
		this.agentApi = agentApi;
	}

	@Override
	public AgentClientResponse adviseCall(AgentClientRequest request, AgentCallAdvisorChain chain) {
		// Convert client request to agent API request
		AgentTaskRequest taskRequest = new AgentTaskRequest(request.goal().getContent(), request.workingDirectory(),
				request.options());

		// Call the agent API (terminal operation)
		var agentResponse = this.agentApi.call(taskRequest);

		// Wrap in client response with context
		return new AgentClientResponse(agentResponse, request.context());
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

}
