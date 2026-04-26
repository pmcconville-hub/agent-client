# Step 1a.1 Learnings: Provider Parity TCK Infrastructure

## What Was Built

- `Provider` enum — all 7 providers (Claude, Codex, Gemini, Amazon Q, Amp, Qwen Code, SWE Agent)
- `@ProviderCapability` annotation — method-level, declares which providers support a scenario
- `ProviderParityTCK` abstract class — extends `AbstractAgentModelTCK`, adds 4 parity scenarios

## Design Decision: @BeforeEach with TestInfo for capability check

The `@ProviderCapability` annotation is evaluated in a `@BeforeEach` method using JUnit's `TestInfo` to get the test method. When the provider is not listed, `Assumptions.assumeTrue(false)` skips the test. This produces "skipped" status in surefire XML output — distinct from "passed" or "failed".

Alternative considered: JUnit 5 `ExecutionCondition` extension. Rejected because Assumptions is simpler and the surefire XML distinction (skipped vs disabled) is the same for our CI summary purpose.

## Design Decision: ProcessBuilder for git init in test

Used `ProcessBuilder` for `git init` in `testSimpleFileCreationInGitDirectory` rather than zt-exec. This is test infrastructure code, not production code — the CLAUDE.md mandate for zt-exec applies to production process management. Tests need a simple one-liner, not resilience features.

## 4 Initial Scenarios

1. `testSimpleFileCreationInGitDirectory` — baseline (git init, then create file)
2. `testSimpleFileCreationInNonGitDirectory` — the Joachim case (no git, must work in LOOSE mode)
3. `testSimpleFileCreationInReadOnlyParent` — permission edge (writable subdirectory)
4. `testSimpleFileCreationInNestedWorkspace` — working directory resolution (3 levels deep)

All 4 annotated with `@ProviderCapability(providers = {CLAUDE, CODEX, GEMINI})`.

## What Carries Forward

- Concrete provider IT classes need to extend `ProviderParityTCK` and implement `getProvider()` + setUp — done in Step 1a.2
- The `testSimpleFileCreationInNonGitDirectory` test is the regression net for the Joachim bug class
