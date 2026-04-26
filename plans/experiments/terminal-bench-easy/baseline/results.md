# Baseline Results (2026-04-24)

> Captured against `main` at commit `024b5df` (pre-Track 1 defaults change)

## Task 1: hello-world (Create a file)

### Evidence Source

Existing IT tests exercise `testSimpleFileCreation` (TCK) — "Create a file named 'greeting.txt' with content 'Hello, Spring AI!'" in a JUnit `@TempDir` (non-git directory).

| Provider | Result | Notes |
|----------|--------|-------|
| **Claude** | PASS | `ClaudeAgentMcpIT` uses `@TempDir` without git init. Works out of the box with `yolo=true` (default). |
| **Codex** | **BLOCKED** | `CodexAgentLocalSandboxIT` line 49: requires explicit `git init` workaround. Without it: "Not inside a trusted directory and --skip-git-repo-check was not specified." Default `skipGitCheck=false` blocks non-git directories. |
| **Gemini** | PASS | `GeminiLocalSandboxIT` uses `@TempDir` without git init. Works with `yolo=true` (default). |

### Customer Incident (Joachim Pasquali, 2026-04-24)

Screenshot at `~/Pictures/Screenshots/Screenshot from 2026-04-24 13-25-53.png` shows:
```
: Executing Codex agent with goal: Create a file named hello.txt with the content 'Hello from Spring AI Agent Client!'
: Goal execution failed: Not inside a trusted directory and --skip-git-repo-check was not specified.
```
Working directory: `/Users/joachim.pasquali/git/spring-agent`

## Autonomous Mode Defaults Comparison

| Provider | Autonomous Flag | Default | Non-Git Dir | CLI Flag Generated |
|----------|----------------|---------|-------------|-------------------|
| Claude | `yolo` | `true` | Works | `--permission-mode DANGEROUSLY_SKIP_PERMISSIONS` |
| Codex | `fullAuto` | `true` | **Blocked** by `skipGitCheck=false` | `--full-auto` (but no `--skip-git-repo-check`) |
| Gemini | `yolo` | `true` | Works | `-y` |

## Key Finding

Codex is the only provider where `fullAuto=true` doesn't guarantee "just works" out of the box. The `skipGitCheck` flag is an independent gate that blocks execution even in autonomous mode. This is the primary motivation for Track 1 (AgentClientMode LOOSE/STRICT).

## Tasks 2-5: Deferred

Tasks 2-5 (fix-git, broken-python, fix-permissions, heterogeneous-dates) require Docker-based terminal-bench infrastructure to evaluate properly. These will be run as full AgentClient integration tests in Stage 4 after the mode system is in place.

The baseline for task 1 is sufficient to validate Track 1 — the remaining tasks test capability breadth, not defaults friction.
