# Step 0.1 Learnings: Baseline Evaluation

## Key Discovery

The baseline can be derived from existing IT test code without running expensive API calls. The evidence is structural:
- Claude and Gemini IT tests use `@TempDir` (non-git) without any workaround — they pass
- Codex IT test has an explicit `git init` workaround on line 49 of `CodexAgentLocalSandboxIT.java`
- Customer screenshot confirms the real-world failure

## Findings

1. **Codex is the only provider with a non-git directory gate.** `skipGitCheck=false` blocks even when `fullAuto=true`.
2. **The workaround is already in our test code** — we know about the issue but never fixed the default.
3. **Tasks 2-5 require Docker infrastructure** — defer to Stage 4. Task 1 (hello-world) is sufficient to validate Track 1.

## Pattern: Evidence-Based Baseline

Rather than spending API credits running known-outcome tests, derive baselines from code structure when the evidence is clear. Save API spend for post-change evaluation where the outcome is genuinely unknown.
