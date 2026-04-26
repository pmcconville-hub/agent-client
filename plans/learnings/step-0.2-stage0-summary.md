# Step 0.2 Learnings: Stage 0 Summary

## Stage 0 Outcome

Baseline captured from code analysis rather than API calls. The structural evidence from existing IT tests was sufficient to validate Track 1 need without running expensive evals.

## Key Findings

1. Codex is the only provider with a non-git directory gate (`skipGitCheck=false` blocks even when `fullAuto=true`)
2. The workaround was already in our test code — `CodexAgentLocalSandboxIT` line 49 did `git init` explicitly
3. Customer screenshot from Joachim Pasquali confirmed the real-world impact

## What Carries Forward

- The baseline result for hello-world/Codex is "BLOCKED by skipGitCheck" — this is the data point Track 1 fixes
- Tasks 2-5 need Docker infrastructure for full eval — deferred to Stage 4
- Pattern established: derive baselines from code structure when evidence is clear
