# Learning: Sandbox Extraction to Standalone Project

**Date**: 2026-01-16

## Context

During the extraction of `spring-ai-judge`, we created a `ProcessRunner` abstraction in `spring-ai-judge-exec` to avoid depending on `spring-ai-agents`. This revealed that the `Sandbox` abstraction from `spring-ai-agents` is a general-purpose utility that multiple projects need.

## Discovery

### Duplicate Abstractions

| spring-ai-judge-exec | spring-ai-agents |
|---------------------|------------------|
| `ProcessRunner` | `Sandbox` |
| `ProcessSpec` | `ExecSpec` |
| `ProcessResult` | `ExecResult` |

These are nearly identical abstractions solving the same problem: execute a command, get results.

### Sandbox is Richer

The `Sandbox` abstraction has features `ProcessRunner` lacks:
- `AutoCloseable` lifecycle management
- `startInteractive()` for bidirectional I/O (CLI tools)
- `DockerSandbox` for container isolation
- Environment variables in spec

### Missing Features

Both abstractions lack workspace setup features that exist in:
1. **Gemini CLI's TestRig** - file creation, directory setup for integration tests
2. **WorkspaceManager** in agent-harness - bulk file setup for test scenarios

## Decision

Extract `Sandbox` to a standalone `spring-ai-sandbox` project with enhanced workspace setup features.

### Why Extract?

1. **Eliminates duplication** - `spring-ai-judge-exec` can depend on `spring-ai-sandbox` instead of maintaining `ProcessRunner`
2. **Shared utility** - Both judge and agent projects need process execution
3. **Clean dependencies** - Neither project depends on the other; both depend on the shared utility
4. **Feature consolidation** - Workspace setup features benefit all consumers

### Why Not Just a Minimal Interface?

Considered extracting a minimal `ProcessRunner` interface with `Sandbox` extending it. Rejected because:
- Over-engineering for current needs
- Lifecycle methods are no-ops for simple use cases anyway
- One abstraction is easier to maintain and document

## Target Architecture

```
spring-ai-sandbox (new standalone project)
├── spring-ai-sandbox-core
│   ├── Sandbox interface
│   ├── ExecSpec, ExecResult, FileSpec
│   ├── LocalSandbox (zt-exec)
│   └── File/workspace setup methods
└── spring-ai-sandbox-docker (optional)
    └── DockerSandbox (testcontainers)

spring-ai-judge-exec
└── depends on spring-ai-sandbox-core
    └── removes ProcessRunner, ProcessSpec, ProcessResult

spring-ai-agents
└── depends on spring-ai-sandbox-core (or -docker)
    └── removes sandbox package
```

## Enhanced Sandbox API

### Design Pattern: Accessor Method

Following Spring's fluent API conventions (RestClient, WebClient, RestTestClient), file operations are grouped under a `files()` accessor method. This keeps the main `Sandbox` interface clean and provides better IDE discoverability.

**Spring precedent:**
- `RestClient.get().uri().headers().retrieve().body()` - grouped by concern
- `RestTestClient.expectStatus().isOk().and().expectHeader()` - accessor returns to parent
- `WebClient.post().contentType().body().retrieve()` - chained specs

### Sandbox Interface (Enhanced)

```java
public interface Sandbox extends AutoCloseable {
    // Core execution (unchanged)
    ExecResult exec(ExecSpec spec);
    Process startInteractive(ExecSpec spec);
    Path workDir();
    boolean isClosed();
    void close();
    boolean shouldCleanupOnClose();

    // Single accessor for all file operations
    SandboxFiles files();
}
```

### SandboxFiles Accessor (New)

```java
/**
 * Accessor for file operations within a sandbox.
 * Keeps the main Sandbox interface clean while providing
 * comprehensive file management.
 */
public interface SandboxFiles {

    // Write operations - return this for chaining
    SandboxFiles create(String relativePath, String content);
    SandboxFiles createDirectory(String relativePath);
    SandboxFiles setup(List<FileSpec> files);

    // Read operations
    String read(String relativePath);
    boolean exists(String relativePath);

    // Return to sandbox for continued chaining
    Sandbox and();
}
```

### FileSpec Record (New)

```java
public record FileSpec(String path, String content) {
    public static FileSpec of(String path, String content) {
        return new FileSpec(path, content);
    }
}
```

### Builder Enhancement (New)

```java
LocalSandbox sandbox = LocalSandbox.builder()
    .tempDirectory("test-")
    .withFile("src/Main.java", "public class Main {}")
    .withFile("pom.xml", "<project>...</project>")
    .build();
```

### Usage Examples

**Grouped file operations:**
```java
sandbox.files()
    .create("src/Main.java", javaCode)
    .create("pom.xml", pomContent)
    .createDirectory("target")
    .and()  // return to Sandbox
    .exec(ExecSpec.of("mvn", "compile"));
```

**Verification after execution:**
```java
ExecResult result = sandbox.exec(ExecSpec.of("mvn", "test"));

if (result.success()) {
    assertTrue(sandbox.files().exists("target/surefire-reports"));
    String report = sandbox.files().read("target/surefire-reports/summary.txt");
}
```

**Full test scenario:**
```java
try (Sandbox sandbox = LocalSandbox.builder()
        .tempDirectory("test-")
        .withFile("src/Main.java", "public class Main {}")
        .withFile("pom.xml", "<project>...</project>")
        .build()) {

    ExecResult result = sandbox.exec(ExecSpec.of("mvn", "compile"));

    assertTrue(sandbox.files().exists("target/classes/Main.class"));
}  // Auto-cleanup on close
```

## Implementation Notes

### Project Setup
- Use Maven wrapper (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/*`)
- Copy wrapper files from `~/acp/acp-java` as reference
- Ensures consistent Maven version across environments

### LocalSandbox
- File operations use `java.nio.file.Files` directly
- `createFile()` creates parent directories automatically
- Temp directories are cleaned up on `close()`

### DockerSandbox
- File operations use `container.copyFileToContainer()` / `copyFileFromContainer()`
- Container is destroyed on `close()`

### Error Handling
- Wrap `IOException` in `SandboxException` (runtime)
- Wrap `TimeoutException` in `TimeoutException` (runtime)

## Reference Implementations

- **WorkspaceManager.createSetupFiles()**: `agent-harness-cli/harness-test/.../workspace/WorkspaceManager.java:133-154`
- **TestRig.createFile()**: `gemini-cli/integration-tests/test-helper.ts:339-343`

## Benefits

1. **Test ergonomics** - Easy workspace setup for integration tests
2. **Consistent API** - Same interface for local and Docker execution
3. **Clean dependencies** - No circular or awkward dependencies
4. **Single source of truth** - One process execution abstraction to maintain
