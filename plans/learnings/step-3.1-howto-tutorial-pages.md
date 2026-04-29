# Step 3.1 Learnings: How-to + Tutorial Pages

## What Was Built

6 pages in `~/community/mintlify-docs/agent-client/`:

### How-to (3 pages)
| Page | Content |
|------|---------|
| `howto/getting-started.mdx` | Plain Java hello world as primary path, Spring Boot as secondary |
| `howto/switching-providers.mdx` | Maven profile pattern for provider switching |
| `howto/structured-output.mdx` | JSON schema structured responses (Claude only currently) |

### Tutorial (3 pages)
| Page | Content |
|------|---------|
| `tutorial/index.mdx` | Learning path overview with Steps component |
| `tutorial/01-first-task.mdx` | Create a file — build model, create client, run goal |
| `tutorial/02-multi-provider.mdx` | Same task with 3 providers — only model construction changes |

## Key Decision: No Spring Boot in Tutorial

User identified that AgentClient doesn't require Spring Boot — it has static factory methods (`AgentClient.create()`, `AgentClient.builder()`) that work standalone. The tutorial shows plain Java as the primary path:

```java
ClaudeAgentModel model = ClaudeAgentModel.builder()...build();
AgentClient client = AgentClient.create(model);
AgentClientResponse response = client.run(goal);
```

Spring Boot is shown as a secondary "With Spring Boot" section in getting-started.

## Architecture Issue Discovered

`agent-client-core` depends on `spring-boot-autoconfigure` for a single file (`AgentClientAutoConfiguration.java`). This should be extracted to a separate module. Added as Step 5.0 in the roadmap.

## Naming Issue Identified

`AgentModel` is misleading — it wraps a CLI agent, not a model. Rename to `AgentApi` planned for Step 5.1 with deprecation shims for one release cycle.
