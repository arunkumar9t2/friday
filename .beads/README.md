# Beads - AI-Native Issue Tracking

Welcome to Beads! This repository uses **Beads** for issue tracking - a modern, AI-native tool designed to live directly in your codebase alongside your code.

## What is Beads?

Beads is issue tracking that lives in your repo, making it perfect for AI coding agents and developers who want their issues close to their code. No web UI required - everything works through the CLI and integrates seamlessly with git.

**Learn more:** [github.com/steveyegge/beads](https://github.com/steveyegge/beads)

## Quick Start

### Essential Commands

```bash
# Create new issues
bd create "Add user authentication"

# View all issues
bd list

# View issue details
bd show <issue-id>

# Update issue status
bd update <issue-id> --status in_progress
bd update <issue-id> --status done

# Sync with git remote
bd sync
```

### Working with Issues

Issues in Beads are:
- **Git-native**: Stored in `.beads/issues.jsonl` and synced like code
- **AI-friendly**: CLI-first design works perfectly with AI coding agents
- **Branch-aware**: Issues can follow your branch workflow
- **Always in sync**: Auto-syncs with your commits

## Why Beads?

✨ **AI-Native Design**
- Built specifically for AI-assisted development workflows
- CLI-first interface works seamlessly with AI coding agents
- No context switching to web UIs

🚀 **Developer Focused**
- Issues live in your repo, right next to your code
- Works offline, syncs when you push
- Fast, lightweight, and stays out of your way

🔧 **Git Integration**
- Automatic sync with git commits
- Branch-aware issue tracking
- Intelligent JSONL merge resolution

## Get Started with Beads

Try Beads in your own projects:

```bash
# Install Beads
curl -sSL https://raw.githubusercontent.com/steveyegge/beads/main/scripts/install.sh | bash

# Initialize in your repo
bd init

# Create your first issue
bd create "Try out Beads"
```

## Learn More

- **Documentation**: [github.com/steveyegge/beads/docs](https://github.com/steveyegge/beads/tree/main/docs)
- **Quick Start Guide**: Run `bd quickstart`
- **Examples**: [github.com/steveyegge/beads/examples](https://github.com/steveyegge/beads/tree/main/examples)

---

*Beads: Issue tracking that moves at the speed of thought*

## Planning Workflow (Agent-Friendly, Project-Agnostic)

We use this pattern to keep work scoped to one domain while staying tool-agnostic. Adopt it as-is or tweak per team.

1) **Shape epics**
- Create an epic per stream (e.g., Navigation, Theming): `bd create --type epic "Navigation"`
- Attach existing work: `bd update <issue-id> --parent <epic-id>`
- Create new work with the parent set: `bd create "Task title" --type feature --parent <epic-id>`

2) **Label for focus**
- Add 1-2 domain labels: `bd label add <id> nav`
- Normalize priority (P0-P2 for active items) to keep listings meaningful

3) **Work inside the epic**
- Pull the queue scoped to your current epic: `bd list --parent <epic-id> --ready`
- Check progress and blockers: `bd epic status <epic-id>`
- Prefer this over global `bd ready` to avoid cross-domain hopping

4) **Sequence with dependencies**
- Add ordering: `bd dep add blocks <blocked-id> <prereq-id>`
- Use `bd blocked` to see what to unblock; scoped `bd ready` will hide items until deps clear

5) **Keep WIP small**
- Mark only the tasks you're actively doing as `in_progress`: `bd update <id> --status in_progress`
- After finishing one, rerun the scoped ready list to pick the next

6) **Regular hygiene**
- Auto-close finished epics: `bd epic close-eligible`
- Clean duplicates or stale items: `bd duplicates`, `bd cleanup`, `bd compact`

Suggested daily loop (replace placeholders):
```bash
# Pick an epic to focus
bd epic status <epic-id>

# See next ready items in that epic
bd list --parent <epic-id> --ready --limit 5

# Start the top item
bd update <issue-id> --status in_progress

# When done
bd close <issue-id>
bd epic close-eligible
```
