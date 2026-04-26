# Step 1b.1 Learnings: Create Multi-Provider Sample

## Sample Structure

Created `samples/create-file-multi-provider/` with Maven profiles for provider switching. Key patterns:

- **Maven profiles** activate provider starters: `claude` (default), `codex`, `gemini`
- **Profile-specific YAML**: `application-{provider}.yml` shows each provider's properties
- **Portable Java code**: `CreateFileRunner` uses only `AgentClient.Builder` — zero provider-specific imports
- **README documents mode switching**: Shows `spring.ai.agents.codex.mode=strict` for restoring old behavior

## Profile Activation

Maven profiles need explicit deactivation of the default when activating another:
```bash
mvn compile -Pcodex -P'!claude'    # Deactivate default claude profile
```
Or use Spring profiles (cleaner for users):
```bash
mvn spring-boot:run -Dspring.profiles.active=codex
```
The Spring profile approach doesn't need Maven profile deactivation since it just controls which YAML gets loaded. But the Maven profile controls which starter JAR is on the classpath. Both matter.

## Version Alignment

Sample uses `0.14.0-SNAPSHOT` to match current development version. For a published sample or tutorial repo, this would need to reference the latest released version instead.

## What Carries Forward

- This sample is the "Joachim scenario" reproducer — the thing that failed and now works
- The README's mode switching documentation should be referenced from `defaults-philosophy.mdx`
- Pattern for future samples: Maven profiles for provider selection, Spring profiles for config
