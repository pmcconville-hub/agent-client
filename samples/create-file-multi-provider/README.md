# Create File Multi-Provider Sample

The simplest possible AgentClient task — create a file — demonstrated with all three major providers.

## Prerequisites

Install at least one CLI agent:

| Provider | Install | Auth |
|----------|---------|------|
| Claude Code | `npm install -g @anthropic-ai/claude-code` | `claude auth login` |
| OpenAI Codex | `npm install -g @openai/codex` | Set `OPENAI_API_KEY` |
| Gemini CLI | `npm install -g @anthropic-ai/gemini-cli` | Set `GOOGLE_API_KEY` |

## Running

```bash
cd samples/create-file-multi-provider

# Claude (default profile)
mvn spring-boot:run

# Codex
mvn spring-boot:run -Dspring.profiles.active=codex

# Gemini
mvn spring-boot:run -Dspring.profiles.active=gemini
```

## What It Does

1. Uses Spring Boot auto-configuration to set up the agent provider
2. Sends the goal: "Create a file named hello.txt with the content 'Hello from Spring AI Agent Client!'"
3. Verifies the file was created and logs its content

The Java code uses only the portable `AgentClient.Builder` API — no provider-specific imports. The provider is selected by the Maven profile, which puts the corresponding starter on the classpath.

## Agent Client Mode

By default, AgentClient runs in **LOOSE** mode — permissive defaults that work out of the box in any directory. This means:

- **Claude**: `yolo=true` (bypass permission checks)
- **Codex**: `fullAuto=true` + `skipGitCheck=true` (works in non-git directories)
- **Gemini**: `yolo=true` (bypass permission checks)

To use stricter defaults (e.g., require a git repository for Codex):

```yaml
spring:
  ai:
    agents:
      codex:
        mode: strict
```

Or override individual properties:

```yaml
spring:
  ai:
    agents:
      codex:
        skip-git-check: false
```

See the [defaults philosophy documentation](https://springaicommunity.mintlify.app/agent-client/defaults-philosophy) for details.
