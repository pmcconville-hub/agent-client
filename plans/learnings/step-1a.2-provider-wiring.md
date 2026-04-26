# Step 1a.2 Learnings: Wire Parity TCK into Provider IT Modules

## What Was Built

Three provider-specific parity IT classes, each following the same pattern:
- `ClaudeProviderParityIT` — uses `ClaudeAgentModel.builder()`, haiku model, yolo=true
- `CodexProviderParityIT` — uses `CodexClient.create()` + `CodexAgentModel`, skipGitCheck=true
- `GeminiProviderParityIT` — uses `GeminiClient.create()` + `GeminiAgentModel`, yolo=true

## Pattern: Provider-specific setUp but portable tests

The concrete ITs only differ in their `@BeforeEach` setUp (which creates the provider-specific client/model) and `getProvider()` return value. All test scenarios are inherited from `ProviderParityTCK` — no provider-specific test methods.

## Skip mechanisms

- Claude: `assumeTrue(isClaudeCliAvailable())` via `ClaudeCliDiscovery`
- Codex: `@DisabledIfEnvironmentVariable(CI)` + `assumeTrue(OPENAI_API_KEY)`
- Gemini: `@EnabledIf("hasGeminiApiKey")` + `assumeTrue(agentModel.isAvailable())`

Each provider uses a slightly different skip mechanism based on its existing IT pattern. This is fine — the important thing is that all skip gracefully without failing.

## Note: Not run locally yet

Tests compile but weren't run locally (would require all three CLIs + API keys + real API calls). The CI parity matrix in Step 1a.3 will be the first real execution.
