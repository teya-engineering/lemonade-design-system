# Skill Style Guide

> **Audience:** anyone writing or editing a skill in `.claude/skills/`. AI assistants and humans both. Read this once before writing/editing any skill.

This guide locks in the structure every skill in this library follows. The goal is a *teaching* knowledge base — readers come to a skill for the **concept and decisions**, not just file paths. Use this as a checklist when authoring or reviewing skills.

---

## What a "good skill" does

Concretely, a future engineer or AI session reading the skill cold should be able to answer all of these without re-asking the team:

1. **What is this pattern, in one paragraph?**
2. **When do I use it? When do I NOT use it (what's the alternative)?**
3. **Who owns what?** (the ownership table — component vs consumer vs generator vs CI)
4. **What are the variants/shapes of the pattern, and when do I pick each?**
5. **Why was X decided over Y?** (the Q&A section — captured grilling answers)
6. **What are common mistakes the pattern lets engineers make?** (anti-patterns)
7. **Where can I see this pattern in real code?** (worked example with file paths)

If a reader has to crawl the codebase to answer any of these, the skill failed.

---

## The 11-section template (every SKILL.md follows this)

Order matters. Section names are fixed. Sections may be empty/omitted only when explicitly noted.

### 1. YAML frontmatter

```yaml
---
name: <skill-name>
description: <what + when>. <pushy trigger phrasing>. <when NOT to use, with alternative skill named>.
---
```

**Description rules:**
- One paragraph. ~80–150 words.
- Lead with what the skill does. Follow with concrete trigger contexts ("Use when X, Y, or Z", "Use even if the user doesn't explicitly say 'pattern name'").
- Be **pushy** — Claude tends to under-trigger skills. Over-specify the contexts.
- If there's a sister skill the reader might confuse this with, name it: "Not for X — use `other-skill` for that."
- If the skill is NOT the default in this repo, say so prominently in the description.

### 2. Title + TL;DR

```markdown
# <Pattern Name>

<3–5 line TL;DR. The pattern in one sentence + the single rule that matters most + a pointer
to the When-To-Use table below.>
```

The TL;DR is what the reader sees in the first 10 seconds. Make it count.

### 3. When to use this pattern

A table or short list with at least 2 columns: **scenario** + **use this pattern? (yes / no — use X instead)**.

Always include negative cases. If you only document positive cases, readers misuse the pattern.

```markdown
| Scenario | Pattern? |
|---|---|
| Adding a parameter to a published component | Yes — `@Deprecated(HIDDEN)` overload (`binary-compatibility`) |
| Editing the unpublished sample app (`composeApp`) | No — it's exempt from the BCV baseline, edit freely |
| Renaming a public property | No — real ABI break; stop and escalate per `binary-compatibility` |
```

### 4. Mental model

Explain the pattern in plain English. No code in this section. Three sub-parts:

- **The core concept** — one paragraph, plain language.
- **Ownership table** — who owns what across the layers (typical columns: Concern / Owner / Why). This is the most important table in the skill.
- **State category → primitive mapping** — when relevant (e.g., config enum → the component's `when`, slot content → trailing lambda). A 3-column table.

### 5. Anatomy

The pattern in code.

- File / class layout (tree diagram)
- Annotated code skeletons for each role
- Skeletons show enough to teach intent, not so much that we duplicate the codebase

```markdown
For a component named `Foo`:

kmp/ui/src/commonMain/kotlin/com/teya/lemonade/Foo.kt   # public composable + KDoc `## Usage` block
kmp/ui/api/ui.api                                       # BCV baseline picks up the new symbols
swiftui/Sources/Lemonade/Foo.swift                      # SwiftUI counterpart
kmp/composeApp/…/FooScreen.kt                           # sample-app showcase screen
```

### 6. Variants & alternatives

Most patterns have at least two shapes. Document them with a decision rule.

```markdown
| Variant | Use when | Trade-offs |
|---|---|---|
| Variant A — eager retention | UI always renders all children | Simpler; pays for unused children |
| Variant B — lazy retention | UI decides which children to render | Cheaper; needs `key()` discipline |
```

If the pattern has no variants, say so explicitly: "This pattern has one canonical shape. No variants."

### 7. Pre-answered decisions (Q&A)

The grilling-answer log. Free-form Q/A/Why. Group by sub-topic when the section grows long.

Provenance doesn't matter — questions answered in grilling sessions, PR reviews, Slack, design docs, all live here. One section per skill, regardless of source.

```markdown
**Q: Why keep the old signature as a `@Deprecated(HIDDEN)` overload instead of just removing it?**
A: Removing a public symbol breaks every compiled consumer at link time. The hidden overload keeps
the binary symbol alive while hiding it from new source code, so the classifier reads the change
as additive and consumers recompile on their own schedule.

**Q: Why append new `data class` properties instead of inserting them in a sensible position?**
A: `copy$default` and the constructor descriptor encode parameter order. Inserting shifts every
later parameter and breaks the ABI even though the source API looks unchanged.

**Why this matters:** both answers exist because a change that compiles fine against the source can
still crash a consumer built against the previous binary. The skill captures them so nobody has to
re-derive the rule from a broken release.
```

### 8. Anti-patterns

Things engineers/AI try and shouldn't. One bullet per anti-pattern, each with the **correct alternative**.

```markdown
- **Regenerating the baseline to silence `apiCheck` on a rename.** The baseline is the contract,
  not the obstacle. Keep the old symbol as a `@Deprecated(HIDDEN)` overload instead.
- **Hand-editing a generated file.** The next pipeline run overwrites it. Fix the converter or the
  Figma export instead.
```

### 9. Worked example

One concrete case from this repo, cited by file path. No code repeated — just point to the file and explain what role it plays.

```markdown
**CountryFlag:** `kmp/ui/src/commonMain/kotlin/com/teya/lemonade/CountryFlag.kt:60` is the canonical
`@Deprecated(HIDDEN)` overload: the pre-release signature delegates to the new one, so the binary
symbol survives while new source only sees the current API.
```

### 10. References

Pointers, not content. Link to:

- Other skills that complement or extend this one
- The root `CLAUDE.md` when a rule there is the trigger
- `.claude/README.md` for how the whole configuration fits together

If the skill has bundled `references/` deep dives, list them here with a one-line description of when to read each.

### 11. (Optional) Bundled references

`SKILL.md` body should be **<500 lines**. When that's not enough, split into a `references/` folder.

```
my-skill/
├── SKILL.md          # 300–500 lines, sections 1–10
└── references/
    ├── deep-dive-A.md
    └── deep-dive-B.md
```

Each reference file: standalone read, ~150–300 lines, with its own ToC if >300 lines. Cite them from §10.

---

## Voice & style conventions

- **Imperative for instructions.** "Use X." Not "You should use X."
- **Active voice.** "The component owns the `when`." Not "The `when` is owned by the component."
- **Prefer concrete examples over abstract claims.** "The icons page has two sibling frames" beats "The Figma file has a specific structure."
- **Cite real files with line numbers** when referencing specific patterns. E.g., `CountryFlag.kt:60`.
- **Tables for structured data.** When data is "thing → property → property", use a table. When it's prose, use prose.
- **Headings sentence case.** Not Title Case. Match the rest of the repo's docs.
- **Avoid throat-clearing.** "It's important to note that…" → just say it.

## Code snippet conventions

- **Skeletons for novel concepts.** Show class signatures, the 1–2 method bodies that teach intent, omit boilerplate.
- **Full snippets for small standalone pieces** that are self-contained and worth showing whole.
- **No method bodies that are >5 lines unless they teach a non-obvious flow.**
- **No imports in skill snippets** unless the import itself is the lesson. Imports rot first.
- **Cite a real file path** alongside any non-trivial snippet so the reader can read the actual implementation.

## Length targets

| Skill type | Target SKILL.md lines |
|---|---|
| Foundation skill (cited by many) | 300–500 |
| Pattern-specific skill | 200–400 |
| Cross-cutting concern (testing, lint, etc.) | 150–250 |

Going over 500 is a smell — split into `references/`. Going under 100 is a smell — the skill probably underexplains and needs to absorb a couple of decisions/Q&As.

## Cross-skill etiquette

- **Don't redefine concepts another skill owns.** If `binary-compatibility` defines the `@Deprecated(HIDDEN)` shim pattern, don't redefine it; cite it.
- **One canonical home per concept.** If two skills both want to teach concept X, one is canonical and the other points.

## When to amend this style guide

When you discover the template doesn't fit a real skill. Don't bend the skill — amend the guide and re-apply across the library. The guide is a working document; treat it like code.

---

## Checklist (use when authoring or reviewing)

- [ ] Frontmatter is pushy with concrete trigger contexts and names alternative skills
- [ ] TL;DR fits in 5 lines
- [ ] When-to-use table includes negative cases
- [ ] Mental-model section has the ownership table; no code
- [ ] Anatomy uses skeletons (not duplicate codebase)
- [ ] At least one variant, OR explicit "no variants" note
- [ ] Pre-answered decisions section has at least 3 Q/A entries
- [ ] Anti-patterns each name the correct alternative
- [ ] Worked example cites a real file path with line numbers
- [ ] References section, no orphan dangling pointers
- [ ] SKILL.md under 500 lines (or split into `references/`)
- [ ] Voice: imperative, active, sentence case headings
- [ ] No throat-clearing, no "it's important to note"
