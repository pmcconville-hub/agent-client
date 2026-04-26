# Step 1a.0 Learnings: Design Review

## Key Discovery: AgentClientMode location

The plan specified `agent-client-core` for `AgentClientMode`, but provider auto-config modules (`agent-codex`, `agent-claude`, `agent-gemini`) depend on `agent-model`, not `agent-client-core`. 

**Decision**: Place `AgentClientMode` in `agent-models/agent-model/src/main/java/org/springaicommunity/agents/model/AgentClientMode.java` alongside `AgentOptions`. This avoids adding new dependencies to provider modules.

The properties binding (`spring.ai.agents.mode`) can be read directly in each provider's `*Properties` class since they already use `@ConfigurationProperties`. No need for a central `AgentClientProperties` class — the mode is just another property each provider reads.

## Extension Points Confirmed

- `AbstractAgentModelTCK` in `agent-models/agent-tck/` is the right base for `ProviderParityTCK`
- `AgentClientAutoConfiguration` in `agent-client-core` is minimal — just creates a builder from the model bean
- Each provider auto-config reads from its own `*Properties` class — mode injection happens there
- Codex auto-config lines 46-51 and 61-66 are where skipGitCheck flows through
