---
name: comment-review
description: Audit comments in a file or branch diff for redundancy, narration, staleness, AI-flavored prose, and untracked TODOs, then apply the cleanup, preferring to extract well-named functions over keeping comments. Use when reviewing comments, after generating code, or when the user says "review the comments" or "clean up comments".
argument-hint: "[file-path | branch]"
allowed-tools: Read Edit Grep Glob Bash(git diff:*)
---

# Comment review

Audit and clean the comments in scope against `references/comment-rules.md`. Default scope is the current branch diff. Pass a file path to scope to one file.

The default verdict is **remove**, not keep. A comment survives only if it says something the code physically cannot: a why, a footgun, or a public-API contract. Everything else is a finding to fix, and **every finding is blocking** (see the rule's Enforcement section). When a comment narrates *what* code does, the fix is almost never to reword it. It's to extract a function whose name carries the meaning, then delete the comment.

## Step 1: Resolve scope

- Arg is a file path → review that file only.
- Arg is `branch` or empty → review files changed on this branch:
  ```bash
  git diff --name-only main...HEAD -- '*.kt' '*.swift'
  ```
- Drop any file whose header carries the `DO NOT MODIFY THIS FILE MANUALLY` banner (generated tokens, themes, and icons under `kmp/` and `swiftui/` — anything the `generate-tokens` / `export-icons` pipelines regenerate), and anything under `kmp/*/api/`. Keep only `.kt` and `.swift` files.

## Step 2: Find the comments

Read each in-scope file. Collect every `//`, `/* */`, KDoc `/** */`, and Swift `///` comment with its line number and the code it sits above or beside.

Skip: license/copyright headers, and notes that intentionally preserve a deliberate naming mismatch. These are not stale.

## Step 3: Classify each comment

Run the delete tests from the rule with the burden of proof on keeping. Tag every comment:

- **EXTRACT** — the comment narrates *what* a block of code does (a step in a function, a section header like `// validate` / `// build the list`, a "// then charge"). The fix is to pull that block into a private function named for what the comment says, then delete the comment. This is the most common verdict and the one the rule cares about most: a cluster of these in one function is a decomposition defect, not a comment problem.
- **DELETE** — restates a single line the code already says (`// map the dto` above `dto.toDomain()`), or is otherwise pure noise that no extraction is needed for. This includes comments explaining why something is absent, empty, or not done (`// no-op`, `// nothing to dispose`) — leave empty blocks empty — and comments narrating the change you just made (`// now uses the semantic token`); rationale for a change lives in the commit message and PR description. Just remove it.
- **STALE** — describes behavior the code no longer has (wrong count, renamed call, removed branch, outdated "currently"). Read the code it points at and confirm the mismatch. Highest priority.
- **NON-LOCAL** — references another module, a design file, or any code this file doesn't control ("mirrors X", "matches Figma", "matches Y service"). Even if accurate now, it's coupled to external state that can change silently. Rewrite as the local invariant, or delete.
- **REWRITE** — the intent is worth keeping (a genuine why/footgun/API contract) but the prose is unclear, inflated, or AI-flavored; or formatting is wrong (standalone not a full sentence, inline not a lowercase fragment).
- **TODO-NO-TICKET** — a `TODO`/`FIXME` with no GitHub issue reference (`#NNN`).
- **KEEP** — explains a why, a footgun, or a public-API contract the signature can't show, and is already clean. This bar is high: if you can make the code clearer instead, the verdict is EXTRACT or DELETE, not KEEP. Internal declarations almost never qualify, because they're read with their body in view. Public component KDoc with a `## Usage` block on a published module (`core`, `tokens`, `ui`, `expressive`, `calendar`) is always KEEP territory — downstream repos treat it as the canonical component reference. Review it for staleness against the signature, never for existence.

For STALE, quote the line of code that contradicts the comment in your report.

## Step 4: Humanize survivors

Rewrite every KEEP and REWRITE comment as plain prose. Strip inflated stakes, hedging, rule-of-three padding, and filler. A comment is one or two plain sentences a tired teammate would write.

## Step 5: Apply

Edit the files in the working tree:

- EXTRACT → pull the narrated block into a private function named for what the comment said, replace the block with the call, and delete the comment. Keep the function small and at the same level of abstraction as its siblings. If a single function has several section-header comments, split it into one function per section. When the extraction is genuinely non-trivial or risks changing behavior, make the smallest safe split and note in the report what's left.
- DELETE → remove the comment.
- STALE → rewrite to match the code, or delete if the code now says it.
- NON-LOCAL → rewrite as the local invariant, or delete if it carried no local meaning.
- REWRITE → replace with the humanized version, fix formatting.
- TODO-NO-TICKET → ask the user for an issue, or flag for removal. Do not invent an issue number.

Leave KEEP comments untouched.

## Step 6: Report

Every row is a blocking finding. There are no "warning" comments. The verdict is NOT CLEAN until the table is empty of everything but KEEP.

```
## Comment review — {scope}
Files: {n}  ·  Comments: {n}  ·  Blocking findings: {n}

| File:line | Verdict | Note |
|-----------|---------|------|
| Foo.kt:42 | STALE   | says "retries 3", code retries 5 |
| Foo.kt:51 | DELETE  | restates `dto.toDomain()` |
| Foo.kt:60 | EXTRACT | 3 section headers → split into validateInput/buildGrid/applyBounds |

Applied: {x extracted, y deleted, z rewritten}
Needs you: {TODOs missing an issue}
Verdict: CLEAN / NOT CLEAN (blocking)
```

Do not commit. Leave the edits in the working tree for the user to review.
