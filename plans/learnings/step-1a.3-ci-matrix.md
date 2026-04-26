# Step 1a.3 Learnings: CI Parity Matrix Workflow

## What Was Built

`.github/workflows/parity.yml` — a matrix workflow that runs ProviderParityTCK across Claude, Codex, and Gemini on every PR touching agent-models, provider-sdks, or agent-client-core.

## Design Decisions

### Matrix structure
Used a matrix of objects (not separate provider/scenario axes) because each provider needs different setup (CLI install command, secret env var, Maven module path, test class name). A flat provider matrix is cleaner than a nested provider x scenario matrix — the scenarios are inherited from the TCK class, not parameterized in CI.

### continue-on-error on test step
Set `continue-on-error: true` on the test run so the summary job always executes. Individual provider failures don't block other providers. The summary job shows which providers passed.

### Secret gating
All three API key secrets are passed to every matrix entry. The tests themselves skip via JUnit Assumptions when the key is missing — no need for conditional steps in the workflow.

### Summary job limitations
The summary job can only see the overall matrix result (`success`/`failure`), not per-provider details. Per-scenario PASS/FAIL/NOT_APPLICABLE breakdown requires downloading surefire artifacts. This is acceptable for v1 — if the matrix is green, all scenarios passed; if red, check artifacts.

## What Carries Forward

- The workflow needs `OPENAI_API_KEY` added as a repo secret (currently only ANTHROPIC and GEMINI exist)
- The Codex IT has `@DisabledIfEnvironmentVariable(CI=true)` — this will skip in CI. May need to remove that annotation or set up differently for the parity workflow to actually test Codex.
