# Step 1a.5 Learnings: Wire Mode into Provider Auto-Configs

## Design Decision: No Claude/Gemini mode wiring

The plan called for wiring mode into Claude and Gemini auto-configs as no-ops with TODO comments. During review, this was rejected as speculative — a field that does nothing today shouldn't be there. Mode wiring was applied only to Codex where it actually changes behavior.

Claude and Gemini properties files remain unchanged. When a real STRICT knob emerges for either (backed by Track 4 eval evidence), that's when the field gets added with actual behavior.

## Codex IT Test Update

Removed the `git init` workaround from `CodexAgentLocalSandboxIT`. The test now uses `skipGitCheck=true` explicitly, matching the LOOSE-mode behavior users get out of the box. The `ProcessExecutor` import for `git init` was also removed.

## Verification

- `./mvnw clean compile` passes
- `./mvnw spring-javaformat:apply` clean
- All changes committed in `a21e029`

## What Carries Forward

- The SDK layer (`ExecuteOptions.skipGitCheck`) stays at its CLI-native default (`false`). The agent-models layer derives the effective value from mode. This preserves the architectural invariant: SDK neutral, agent-models translates.
- Only Codex has mode-dependent behavior today. Adding future providers requires only touching that provider's `*Properties` class.
