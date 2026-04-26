# Step 1a.4 Learnings: AgentClientMode Enum

## Design Decision: Location in agent-model, not agent-client-core

The plan specified `agent-client-core` for `AgentClientMode`. During design review (step 1a.0), discovered that provider auto-config modules depend on `agent-model`, not `agent-client-core`. Placed the enum in `agent-models/agent-model/src/main/java/org/springaicommunity/agents/model/AgentClientMode.java` to avoid adding new dependencies.

## Design Decision: No central properties class

Rather than creating a central `AgentClientProperties` class to bind `spring.ai.agents.mode`, the mode is read directly in each provider's `*Properties` class. This keeps the mode system self-contained within each provider's auto-config — no cross-module property injection needed.

## Design Decision: Nullable skipGitCheck with derivation

Changed `CodexAgentProperties.skipGitCheck` from `boolean` (primitive, always has a value) to `Boolean` (nullable). This allows distinguishing "explicitly set by user" from "not set, derive from mode":
- `null` -> derive from mode (LOOSE=true, STRICT=false, default=true)
- `true`/`false` -> explicit override wins

This implements the "STRICT is a baseline, not a lock" precedence rule.

## What Carries Forward

- The enum is minimal (2 values, good Javadoc) — ready for additional providers to reference
- SDK layer (`ExecuteOptions`) kept `skipGitCheck=false` as its native default — mode translation happens only in `CodexAgentProperties.isSkipGitCheck()`
