# Java 17/21 Compatibility: Configurable ExecutorService Pattern

## Problem

The spring-ai-agents library initially used `Thread.startVirtualThread()` for async operations, which requires Java 21. This broke compatibility for consumers using Java 17.

## Solution: Configurable ExecutorService Pattern

Instead of directly using virtual threads, we implemented a configurable `Executor` that:
1. Defaults to a cached thread pool with daemon threads (Java 17 compatible)
2. Allows Java 21+ users to plug in virtual thread executors

### Implementation

```java
// Default executor using cached thread pool with daemon threads
private static final ExecutorService DEFAULT_EXECUTOR = Executors.newCachedThreadPool(new ThreadFactory() {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "claude-agent-async-" + counter.incrementAndGet());
        t.setDaemon(true);  // Important: prevents JVM from hanging
        return t;
    }
});

// Instance field allows customization
private final Executor asyncExecutor;

// Builder method for customization
public Builder asyncExecutor(Executor executor) {
    this.asyncExecutor = executor;
    return this;
}
```

### Usage

**Java 17 users** - Default behavior works automatically:
```java
ClaudeAgentModel model = ClaudeAgentModel.builder()
    .workingDirectory(dir)
    .build();
```

**Java 21+ users** - Can opt-in to virtual threads:
```java
ClaudeAgentModel model = ClaudeAgentModel.builder()
    .workingDirectory(dir)
    .asyncExecutor(Executors.newVirtualThreadPerTaskExecutor())
    .build();
```

## Key Design Decisions

### Why Daemon Threads?
Daemon threads allow the JVM to exit even if async tasks are still running. Without this, the JVM would hang waiting for thread pool tasks to complete.

### Why Executor (not ExecutorService)?
`Executor` is the minimal interface needed for task submission. It allows more flexibility - users can provide any `Executor` implementation including custom ones.

### Why Not Multi-Release JARs (MRJAR)?
While Spring Framework uses MRJAR with `src/main/java21/` directories, this adds build complexity. The configurable executor pattern is simpler and achieves the same goal of Java 17 baseline with Java 21 optimization.

## Files Modified

- `ClaudeAgentModel.java` - Added DEFAULT_EXECUTOR, asyncExecutor field, builder method
- `ClaudeAgentSession.java` - Same pattern for session-level async operations

## Alternative Approaches Considered

1. **Multi-Release JARs (MRJAR)**: Spring Framework approach with `src/main/java21/` directories. More complex build setup.

2. **virtual-threads-bridge library**: Third-party library for JDK version detection. Adds external dependency.

3. **Compile-time conditional**: Separate modules for Java 17 and 21. More maintenance burden.

4. **Raw Thread.startVirtualThread()**: Requires Java 21 minimum, breaking backwards compatibility.

## References

- Spring Framework VirtualThreadDelegate pattern: Uses MRJAR for Java 21 optimizations
- Java ExecutorService best practices: Use daemon threads for background tasks
- Reactor Flux patterns: Bridge blocking iterators to reactive streams
