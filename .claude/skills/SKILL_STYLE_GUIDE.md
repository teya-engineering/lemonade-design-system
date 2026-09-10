# Skill Style Guide

> **Audience:** anyone writing or editing a skill in `.claude/skills/` or
> `kmp/.claude/skills/`. AI assistants and humans both. Read this once before
> writing or editing a skill.

A skill exists so a future session can act correctly on a task without
re-deriving the team's decisions. It teaches the concept and the judgement calls,
not just the file paths.

---

## What a good skill does

A future engineer or AI session reading a skill cold should come away knowing:

1. **What this is**, in one paragraph.
2. **When to reach for it, and when not to** — including which skill or tool
   handles the case this one doesn't.
3. **Who owns what** — component vs consumer vs generator vs CI, wherever that
   split is the thing people get wrong.
4. **The decisions that already have answers**, so nobody relitigates them.
5. **What the pattern lets people get wrong**, and the correct alternative.
6. **Where to see it in real code**, cited by path.

Not every skill needs all six. A procedure skill (`publish-version`,
`export-icons`) is mostly steps and guardrails. A judgement skill
(`binary-compatibility`) is mostly decisions and
anti-patterns. A reference skill (`kotlin-language-version-features`) is mostly a
lookup protocol over `references/`. Write the sections that carry weight for the
skill in front of you and skip the rest — an empty section under a mandated
heading teaches nothing.

---

## The two things every skill must get right

### Frontmatter

```yaml
---
name: <skill-name>
description: <what it does>. <concrete trigger contexts>. <when NOT to use, naming the alternative>.
---
```

The description is the only part the model sees before deciding whether to load
the skill, so it is the highest-leverage line in the file.

- One paragraph. The library runs from ~20 to ~100 words; longer is fine when the
  trigger surface is genuinely wide (`kotlin-language-version-features` is the
  longest at ~96 words, because it has to fence off Kotlin ≤2.1 and deprecation
  queries).
- Lead with what the skill does, then list concrete trigger contexts: "Use when
  X, Y, or Z", "Use even if the user doesn't say <pattern name>".
- Be pushy. Skills under-trigger far more often than they over-trigger.
- Name the sister skill a reader might confuse this with: "Not for X — use
  `other-skill`."
- If the skill is not the default behaviour in this repo, say so in the
  description, not just in the body.

### Progressive disclosure

`SKILL.md` is what gets loaded. Everything that is only needed once the task is
already underway goes in `references/`, and the body points at it.

```
my-skill/
├── SKILL.md          # the body — loaded on trigger
├── references/       # deep dives — loaded only when the task reaches them
└── scripts/          # executables the body invokes by path
```

Seven skills carry `references/`, six carry `scripts/`. A reference file is a
standalone read; cite it from the body with a one-line description of *when* to
open it, so the reader can decide without loading it.

---

## Sections that have earned their place

These are the sections that keep proving useful. Treat this as a menu, not a
running order — pick the ones that carry weight, and name them however the skill
reads best.

**Title + opening.** Three to five lines: the pattern in one sentence, the single
rule that matters most, and a pointer to the when-to-use table. This is what the
reader sees in the first ten seconds. Most skills here run it as an unlabelled
paragraph straight under the title; `generate-tokens` and `export-icons` give it a
`## TL;DR` heading, because for those the one-command answer is what most sessions
come for.

**When to use.** A table with at least two columns — scenario, and whether this
pattern applies. Always include negative cases; a skill that documents only the
positive path gets misapplied.

```markdown
| Scenario | Pattern? |
|---|---|
| Adding a parameter to a published component | Yes — `@Deprecated(HIDDEN)` overload (`binary-compatibility`) |
| Editing the unpublished sample app (`composeApp`) | No — it's exempt from the BCV baseline, edit freely |
| Renaming a public property | No — real ABI break; stop and escalate per `binary-compatibility` |
```

**Mental model.** Plain English, no code. The ownership table — who owns what
across the layers — is usually the most valuable table in the skill.

**Anatomy.** The pattern in code: file layout, then annotated skeletons for each
role. Show enough to teach intent, not so much that the skill duplicates the
codebase.

```markdown
For a component named `Foo`:

kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Foo.kt        # public composable + KDoc `## Usage` block
kmp/ui/api/{android,desktop}/ui.api, kmp/ui/api/ui.klib.api  # BCV baselines pick up the new symbols
swiftui/Sources/Lemonade/Components/LemonadeFoo.swift        # SwiftUI counterpart
kmp/composeApp/src/commonMain/kotlin/com/teya/lemonade/FooDisplay.kt   # sample-app showcase
```

**Variants & alternatives.** Most patterns have more than one shape. Give the
decision rule, not just the list. If there is genuinely one shape, say so.

```markdown
| Variant | Use when | Trade-offs |
|---|---|---|
| Variant A — eager retention | UI always renders all children | Simpler; pays for unused children |
| Variant B — lazy retention | UI decides which children to render | Cheaper; needs `key()` discipline |
```

**Pre-answered decisions.** Free-form Q/A. Provenance doesn't matter — grilling
sessions, PR reviews, Slack, design docs all land here.

```markdown
**Q: Why keep the old signature as a `@Deprecated(HIDDEN)` overload instead of just removing it?**
A: Removing a public symbol breaks every compiled consumer at link time. The hidden overload keeps
the binary symbol alive while hiding it from new source code, so the classifier reads the change
as additive and consumers recompile on their own schedule.
```

**Anti-patterns.** One bullet each, every one naming the correct alternative.

```markdown
- **Regenerating the baseline to silence `apiCheck` on a rename.** The baseline is the contract,
  not the obstacle. Keep the old symbol as a `@Deprecated(HIDDEN)` overload instead.
- **Hand-editing a generated file.** The next pipeline run overwrites it. Fix the converter or the
  Figma export instead.
```

**Worked example.** One concrete case from this repo, cited by path. Point at the
file; don't paste it back.

```markdown
**CountryFlag:** `kmp/ui/src/commonMain/kotlin/com/teya/lemonade/CountryFlag.kt:55` is the canonical
`@Deprecated(HIDDEN)` overload — the pre-release signature delegates to the current one, so the
binary symbol survives while new source only sees the current API.
```

**References.** Pointers, not content: sibling skills, the root `CLAUDE.md` when a
rule there is the trigger, `.claude/README.md` for how the configuration fits
together, and each bundled `references/` file with a line on when to read it.

---

## Voice & style conventions

- **Imperative for instructions.** "Use X." Not "You should use X."
- **Active voice.** "The component owns the `when`." Not "The `when` is owned by
  the component."
- **Concrete over abstract.** "The icons page has two sibling frames" beats "The
  Figma file has a specific structure."
- **Cite real files, with line numbers** where a specific declaration is the
  point. Line numbers drift — re-verify them when you touch the skill.
- **Tables for structured data**, prose for prose.
- **Sentence case headings.** Matches the rest of the repo's docs.
- **No throat-clearing.** "It's important to note that…" → just say it.

## Code snippet conventions

- **Skeletons for novel concepts.** Signatures plus the one or two bodies that
  teach intent; omit boilerplate.
- **Full snippets only for small standalone pieces** worth showing whole.
- **No method body over ~5 lines** unless it teaches a non-obvious flow.
- **No imports** unless the import is the lesson. Imports rot first.
- **Cite a real file path** beside any non-trivial snippet.

## Length

`SKILL.md` bodies in this library run from 79 to 310 lines. Over ~500 is a smell —
the material that only some tasks need belongs in `references/`. There is no floor:
`comment-review` says what it needs in 79 lines because the detail lives in its
`references/`, and padding it out would only cost tokens on every trigger.

## Cross-skill etiquette

- **Don't redefine a concept another skill owns.** `binary-compatibility` defines
  the `@Deprecated(HIDDEN)` shim; other skills cite it.
- **One canonical home per concept.** When two skills both want to teach X, one is
  canonical and the other points at it.

## When to amend this guide

When you find a rule here that a real skill has good reason to break. Amend the
guide to match what works, and re-apply it across the library.

---

## Checklist (use when authoring or reviewing)

- [ ] Description leads with what the skill does and lists concrete trigger contexts
- [ ] Description names the alternative for the cases this skill does not cover
- [ ] The opening lines say what the skill is and the one rule that matters most
- [ ] Negative cases are documented, not just the happy path
- [ ] Anti-patterns each name the correct alternative
- [ ] Every file path, script name, task name and line number is current — you checked
- [ ] Nothing here is already owned by another skill; overlaps cite instead of restate
- [ ] Detail only some tasks need lives in `references/`, cited with when to read it
- [ ] No dangling pointer: every referenced file, script and skill exists
- [ ] Voice: imperative, active, sentence case headings, no throat-clearing
