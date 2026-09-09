# Claude Code configuration

How this repo's Claude context is organised, and where to add to it.

## The loading rule

Context costs tokens on every request, so almost nothing is always-on.

| Surface | Loads | Holds |
| --- | --- | --- |
| `CLAUDE.md` (repo root) | every session | binary-compatibility gotchas, the comment policy, and PR hygiene |
| `skills/` | on demand, when the task matches | workflows, procedures, and team opinions |
| `kmp/.claude/skills/` | on demand, when working under `kmp/` | module-scoped Kotlin conventions |

Nothing lives in two places. If a rule belongs to a workflow, it goes in that workflow's skill.
`CLAUDE.md` keeps only what would cause a wrong action before any skill could trigger.

Skills use progressive disclosure: a short `SKILL.md` loads first, and its `references/` files load
only when the task actually needs them.

## Common tasks

| What you want | Skill |
| --- | --- |
| Export new or updated icons from Figma | `export-icons` |
| Regenerate token code from `tokens/*.tokens.json` | `generate-tokens` |
| Cut a KMP / SwiftUI release | `publish-version` |
| Diagnose an `apiCheck` or API Stability failure | `binary-compatibility` (run `scripts/bcv-check.sh --ci` before any PR) |
| Clean up comments in a file or branch diff | `comment-review` |

## Skills

Invoke a skill rather than reconstructing its procedure. The description in each `SKILL.md`
frontmatter decides when it triggers, so that line is the important one to get right.

**Repo workflows** — `binary-compatibility`, `export-icons`, `generate-tokens`, `publish-version`

**Comments and docs** — `comment-review`, `ktdoc-quality`, `writing-docs`

**KMP toolchain** — `kotlin-language-version-features`

**Swift** — `swift-concurrency-expert`, `swiftui-performance-audit`, `swiftui-liquid-glass`

A module-scoped skill also lives at `kmp/.claude/skills/kotlin-conventions/` and loads only when
working under `kmp/`.

## Adding to this setup

- **New skill:** `skills/<name>/SKILL.md` with `name` and `description` frontmatter. Write the
  description for *when to trigger*, not what it contains. Follow `skills/SKILL_STYLE_GUIDE.md`;
  split anything long into `references/`.
- **New command, agent, or hook:** none exist yet. A command (`commands/<name>.md`) is a thin typed
  entry point that delegates to a skill; an agent is an autonomous multi-step job that needs its
  own context window; a hook belongs only to a consequence that is non-obvious and file-specific.
- **New gotcha:** add it to the root `CLAUDE.md` only if it would cause a wrong action before any
  skill could trigger. Otherwise it belongs in a skill.
