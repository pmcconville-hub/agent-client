# Experiment: Terminal-Bench Easy Tier via AgentClient

## Design

Run terminal-bench easy-tier tasks through AgentClient with all three major providers (Claude, Codex, Gemini) to measure baseline parity and identify friction points.

### Tasks

| # | Task | Source | Prompt |
|---|------|--------|--------|
| 1 | hello-world | terminal-bench | "Create a file called hello.txt. Write 'Hello, world!' to it." |
| 2 | fix-git | terminal-bench | "Find lost changes and merge them into master." |
| 3 | broken-python | terminal-bench | "Fix the system-wide environment to support the features used in the code." |
| 4 | fix-permissions | terminal-bench | "Fix permission issues on the shared directory." |
| 5 | heterogeneous-dates | terminal-bench | "Parse and normalize date formats in the CSV file." |

### Evaluation Method

- Use AgentClient with each provider's starter and default options
- Verify task completion via file system checks (file exists, content correct)
- Record: pass/fail, options required, error messages, wall time

### Bootstrap Split (Stage 0 only)

- **hello-world with Codex**: Run vanilla (expected: fails on skipGitCheck) — validates Track 1
- **Tasks 2-5 with Codex**: Run with explicit `skipGitCheck=true` workaround — real capability baseline
- **All tasks with Claude/Gemini**: No workarounds needed

### Promotion Rubric

A provider-specific option graduates to portable `AgentOptions` when all three hold:

1. >=2 of 3 providers have a semantic equivalent
2. Absence causes terminal-bench failures on easy tier (evidence, not speculation)
3. The option can be expressed without leaking provider-specific concepts

Options that don't meet the rubric remain provider-specific indefinitely. Re-evaluate only when a new provider adds equivalent capability or results change materially.
