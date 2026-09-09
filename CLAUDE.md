# Lemonade Design System

## Binary compatibility (KMP) — read before changing public API

The published modules — `core`, `tokens`, `ui`, `expressive`, `calendar` — ship to
real consumers. Their public surface is locked by the Binary Compatibility
Validator: every public symbol is dumped to `kmp/<module>/api/*.api` and
`*.klib.api`, and CI fails if your change drifts from that baseline. The
unpublished sample app (`composeApp`) is exempt.

When a change touches public API, work through the **binary-compatibility** skill
(`.claude/skills/binary-compatibility/SKILL.md`) — it has the diagnosis script and
the full decision table. The short version:

- **`apiCheck` failing** just means the baseline is stale. Run `./gradlew apiDump`
  from `kmp/`, review the regenerated `api/` files, and commit them.
- **Adding a default parameter, or renaming a function** → keep the old signature
  as a `@Deprecated(level = DeprecationLevel.HIDDEN)` overload that delegates to
  the new one. This preserves the binary symbol. Pattern lives in
  `kmp/ui/.../CountryFlag.kt`. `HIDDEN` adds a `synthetic` flag; the classifier
  recognises a `synthetic`-only change as additive (the descriptor is unchanged),
  so this auto-passes. Still mention it in the PR.
- **Adding a property to a public `data class`** → append it to the primary
  constructor with a default, then restore the old symbols with two
  `@Deprecated(HIDDEN)` shims: a secondary constructor for the old params, and a
  `copy(...)` whose first arg has a default (`copy(a: String = this.a)`) — the
  default is what regenerates the old `copy$default`. Verified additions-only and
  fully ABI-safe (`<init>`, `copy`, `copy$default` all preserved). Append, never
  insert; keep one shim pair per previously released shape. See the skill for the
  full pattern and the `@Poko` alternative.
- **Adding an enum entry** → fine for config enums (the component owns the
  `when`). Make sure every internal `when` handles it. Auto-passes CI.
- **Renaming/removing a property, an interface member, or an enum entry** → real
  ABI break with no clean workaround. STOP. Don't reshape code to silence
  `apiCheck`. Raise it with the user and get one of the required reviewers
  (@caiqueslp, @williankl, @DevSrSouza) to sign off.

Never move, rename, or delete a public declaration just to make the build green.

## Comments

Near-zero comments. A comment survives only when it says something the code
can't: a non-obvious *why*, a real footgun or invariant, or public-API KDoc that
adds what the signature can't show. Component KDoc `## Usage` blocks on the
published modules are canonical documentation for downstream repos — keep them,
keep them current. Never explain why something is absent or empty, and never
narrate a change in a comment; rationale goes in the commit message and PR
description. Generated files (the `DO NOT MODIFY THIS FILE MANUALLY` banner) are
exempt — fix the generator, not the file. Full rules and cleanup procedure:
`.claude/skills/comment-review/`.

## Opening a pull request

CI runs `.github/workflows/kmp_ci.yml`. The **API Stability Review** job diffs the
`api/*.api` / `*.klib.api` baseline against the base branch and classifies it as
`NO_CHANGES`, `ADDITIONS_ONLY`, or `BREAKING`. A `BREAKING` verdict blocks merge
until a named maintainer approves the exact head commit.

Before opening or updating a PR, run the classifier locally so you know the verdict
ahead of CI:

```bash
.claude/skills/binary-compatibility/scripts/bcv-check.sh --ci
```

Then fill in the **API Dump** section of the PR description. It is mandatory
whenever the baseline files change, and it should let a reviewer approve without
re-deriving the diff. Cover:

- the verdict;
- the changes that are *not* a concern, and why — interface additions (local-only
  interfaces), enum entry additions (config-only enums the component switches on),
  any `@Deprecated(HIDDEN)` overload whose `-` line is just the `synthetic`
  flag with an unchanged descriptor, and any name-mangled `internal` line that
  moved (e.g. a Compose `getLambda$<hash>$<module>` singleton re-hashing when a
  `@Composable` gains a parameter); these are internal and never consumer-visible,
  so the classifier counts them as additive;
- any genuine `BREAKING` change, who needs to approve it, and why it's acceptable.

If the baseline didn't change, write "No public API changes" and leave it at that.
