# Step 1a.7 Learnings: Stage 1a Summary

## What Stage 1a Delivered

### Track 0: Provider Parity TCK
- `Provider` enum (7 providers), `@ProviderCapability` annotation, `ProviderParityTCK` (4 scenarios)
- 3 provider-specific parity ITs: Claude, Codex, Gemini
- CI parity matrix workflow (`.github/workflows/parity.yml`)
- Commits: `36fec9b`, `833d6b0`, `04ad457`

### Track 1: AgentClientMode
- `AgentClientMode` enum (LOOSE/STRICT) in `agent-model`
- Codex `skipGitCheck` defaults to `true` in LOOSE mode via nullable `Boolean` derivation
- Claude/Gemini properties unchanged (no speculative wiring)
- `defaults-philosophy.mdx` published in mintlify-docs
- Commits: `a21e029` (mode + sample), `f98be6d` (docs, mintlify-docs repo)

## Open Issues for Stage 1b

1. **Codex `@DisabledIfEnvironmentVariable(CI=true)`** — prevents parity tests from running in CI for Codex. Either remove from `CodexProviderParityIT` specifically, or use a different mechanism.
2. **OPENAI_API_KEY secret** — needs to be added to the spring-ai-community/agent-client repo for CI.
3. **CHANGELOG** — drafted conceptually but not written as a file. Finalize in Stage 1b.

## Key Architectural Decisions Locked In

- SDK layer stays neutral on policy (documented invariant)
- Mode translation happens in provider `*Properties` classes, not in SDK or auto-config
- STRICT is a baseline, not a lock — explicit property overrides always win
- Only wire mode where it changes behavior — no speculative fields
