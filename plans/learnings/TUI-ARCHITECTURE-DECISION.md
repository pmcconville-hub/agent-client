# Learning: TUI Should Be a Separate Project

**Date**: 2026-01-13
**Decision**: Interactive TUI/CLI belongs in a separate `spring-ai-agent-tui` project, not in spring-ai-agent-client or agent-harness

---

## Context

We evaluated where to place an interactive TUI (Terminal User Interface) with Plan Mode support - similar to how Claude Code operates with its plan mode, approval flows, and interactive execution.

---

## Architectural Layers

```
┌─────────────────────────────────────────────────────────────────┐
│  APPLICATIONS (use the libraries)                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │  TUI/CLI    │  │  Web App    │  │  Batch Job  │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
├─────────────────────────────────────────────────────────────────┤
│  ORCHESTRATION                                                   │
│  ┌─────────────────────────────────────────────────┐            │
│  │  spring-ai-agent-client (AgentClient API)       │            │
│  └─────────────────────────────────────────────────┘            │
├─────────────────────────────────────────────────────────────────┤
│  PATTERNS                          │  EVALUATION                 │
│  ┌─────────────────────────┐      │  ┌─────────────────────┐    │
│  │  agent-harness          │      │  │  spring-ai-judge    │    │
│  │  (loops, strategies)    │      │  │  (jury, scoring)    │    │
│  └─────────────────────────┘      │  └─────────────────────┘    │
├─────────────────────────────────────────────────────────────────┤
│  FOUNDATION                                                      │
│  ┌─────────────────────────────────────────────────┐            │
│  │  Spring AI (ChatClient, tools, memory)          │            │
│  └─────────────────────────────────────────────────┘            │
└─────────────────────────────────────────────────────────────────┘
```

**Key insight**: TUI sits at the APPLICATION layer - it's a consumer of libraries, not a library itself.

---

## Options Evaluated

| Option | Pros | Cons | Verdict |
|--------|------|------|---------|
| **A: spring-ai-agent-tui** (new repo) | Clean separation, clear purpose, OSS | Another repo to maintain | ✅ Best fit |
| **B: Module in agent-harness** | Keeps patterns + usage together | Muddies pattern library with app code | ❌ |
| **C: Module in agent-client** | Near the client it uses | Muddies client API with app code | ❌ |
| **D: tuvium-agent-cli** | Proprietary value-add | Can't be OSS | Maybe later |
| **E: Samples/examples dir** | Low commitment | Not a real project, hard to evolve | ❌ |

---

## The Purity Argument

**spring-ai-agent-client** is being renamed to focus on the programmatic API for invoking agents. Adding a TUI would break that focus:

- `AgentClient` = library for programmatic agent invocation
- `TUI` = application with UI, state machines, approval flows

These are different concerns. Applications shouldn't live in libraries.

---

## Plan Mode vs Planner Component

Two related but distinct concepts:

| Concept | What It Is | Where It Lives |
|---------|------------|----------------|
| **Planner component** | Programmatic task decomposition before execution | AgentClient (library) |
| **Plan Mode** | Interactive state where human reviews/approves plan | TUI application |

- **Planner**: Every AgentClient can have one. Automatic, no human approval.
- **Plan Mode**: Only for interactive CLI agents. Requires human at keyboard.

---

## Proposed TUI Project Structure

```
spring-ai-agent-tui/
├── pom.xml                     # Depends on agent-client, agent-harness
├── src/main/java/
│   └── org/springaicommunity/agents/tui/
│       ├── TuiApplication.java      # Entry point
│       ├── mode/
│       │   ├── PlanMode.java        # Plan mode state machine
│       │   ├── ExecuteMode.java     # Normal execution
│       │   └── ModeTransition.java  # State transitions
│       ├── approval/
│       │   ├── ApprovalFlow.java    # Human approval gates
│       │   └── PlanReview.java      # Plan display + approval
│       ├── ui/
│       │   ├── Terminal.java        # JLine wrapper
│       │   ├── StatusBar.java       # Status display
│       │   └── PlanPanel.java       # Plan rendering
│       └── config/
│           └── TuiConfig.java       # Configuration
└── src/main/resources/
    └── application.yml
```

---

## Dependencies

```xml
<dependencies>
    <!-- Uses AgentClient for agent invocation -->
    <dependency>
        <groupId>org.springaicommunity.agents</groupId>
        <artifactId>spring-ai-agent-client</artifactId>
    </dependency>

    <!-- Uses patterns for loop execution -->
    <dependency>
        <groupId>org.springaicommunity.agents</groupId>
        <artifactId>agent-harness-patterns</artifactId>
    </dependency>

    <!-- Terminal UI -->
    <dependency>
        <groupId>org.jline</groupId>
        <artifactId>jline</artifactId>
    </dependency>
</dependencies>
```

---

## Claude Code Reference

Claude Code's plan mode (from decompiled analysis at `/home/mark/tuvium/claude-code-analysis/`):

- **EnterPlanMode tool**: Transitions agent to plan mode
- **ExitPlanMode tool**: Submits plan for approval
- **Plan file**: Written to `~/.claude/plans/{random-name}.md`
- **Approval flow**: Plan sent to team lead in multi-agent scenarios
- **State tracking**: `hasExitedPlanMode`, `needsPlanModeExitAttachment`

This is application-level logic, not library-level.

---

## Future: Tuvium Premium

Potential split:
- `spring-ai-agent-tui` - OSS basic TUI
- `tuvium-agent-cli` - Premium features (team mode, advanced approval flows, integrations)

---

## Decision

**Create `spring-ai-agent-tui` as a separate springaicommunity project** when TUI implementation is prioritized. Do not add TUI code to spring-ai-agent-client or agent-harness.

---

*This learning captures why TUI belongs in its own project and the architectural reasoning behind the separation.*
