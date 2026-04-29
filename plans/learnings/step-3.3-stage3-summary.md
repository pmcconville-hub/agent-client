# Step 3.3 Learnings: Stage 3 Summary

## What Stage 3 Delivered

### How-to Pages (3)
- `howto/getting-started.mdx` — Plain Java as primary, Spring Boot as secondary
- `howto/switching-providers.mdx` — Maven profile pattern
- `howto/structured-output.mdx` — JSON schema (Claude only)

### Tutorial Pages (3)
- `tutorial/index.mdx` — Learning path with Steps component
- `tutorial/01-first-task.mdx` — Create file, standalone Java
- `tutorial/02-multi-provider.mdx` — Same task, 3 providers

### Diataxis Navigation Restructure
All agent-client docs reorganized into four-quadrant subdirectories:
```
agent-client/{tutorial,howto,reference,explanation}/
```
16 cross-references updated across 6 files.

## Architecture Issues Identified

1. **agent-client-core depends on spring-boot-autoconfigure** — for one file. Should be extracted (Step 5.0).
2. **AgentModel is a misleading name** — wraps CLI agent, not a model. Rename to AgentApi with deprecation shims (Step 5.1).

## Deferred

- **Onramp metric** (Step 3.3 original work item) — measuring ApplicationStartedEvent → AgentClientResponse time. Deferred because the tutorial now shows plain Java (no Spring Boot), so the metric definition needs rethinking.

## What Carries Forward to Stage 4

- All 11 doc pages are written and organized by Diataxis quadrant
- Reference pages link to how-to and tutorial content
- Tutorial shows plain Java — validates that AgentClient works without Spring Boot
- Stage 4 (terminal-bench eval) can reference the tutorial patterns for test setup
