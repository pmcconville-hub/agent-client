# Step 1a.6 Learnings: Defaults Philosophy Documentation

## What Was Built

`agent-client/defaults-philosophy.mdx` in mintlify-docs — a single reference page explaining:
- The problem (inconsistent defaults across providers)
- AgentClientMode enum (LOOSE/STRICT)
- What each mode does per provider (currently only Codex has different behavior)
- Precedence rules with examples
- "STRICT is a baseline, not a lock" with Warning callout
- SDK-neutral architectural invariant
- Migration note for pre-0.14.0 users

Also updated `mint.json` navigation to include the page under Agent Client group.

## Convention: Mintlify page structure

Observed from `sessions.mdx` and applied:
- YAML frontmatter: `title`, `description`, `sidebarTitle`
- Components: `<Tabs>`/`<Tab>` for mode comparison, `<Warning>` for the precedence gotcha, `<Note>` for version info
- Tables for provider comparison
- Code blocks for YAML configuration examples

## What Carries Forward

- This page is referenced from `samples/create-file-multi-provider/README.md`
- Future reference pages (`claude-reference.mdx`, etc.) should link back to this for precedence rules
- The CHANGELOG draft is not yet written as a separate file — it will be finalized in Stage 1b when user-facing artifacts ship
