---
name: web-review
description: Review a change under web/ or scripts/web-* — the token converters, the generated CSS and TypeScript, the SVG assets, and the @teya/lemonade-mobile-ds package surface. Use when reviewing or self-reviewing a web diff, before opening a web PR, and whenever a converter, a generated file, package.json or a CI workflow changes. Not for KMP or SwiftUI public API — that is `binary-compatibility`.
allowed-tools: Read Grep Glob Bash(git diff:*) Bash(git status:*) Bash(scripts/web-check.sh:*)
---

# Reviewing a web change

Web is a fourth consumer of the same Figma token export that feeds KMP and SwiftUI. Almost
everything under `web/` is generated, so most defects are in a converter or in how a converter
is wired, not in the file a reader is looking at. One rule carries the rest: **a failure that
leaves every check green is the one to hunt for.**

Run `scripts/web-check.sh` first — it proves the pipeline, the package and drift in one command.
The review is what the script cannot check: whether the change is wired up, named well, and
honest about what it guarantees.

## When to use

| Scenario | This skill? |
|---|---|
| A converter under `scripts/web-*` changed | Yes |
| A file under `web/styles/`, `web/src/*.generated.ts` or `web/assets/` changed | Yes — and check the converter, not the output |
| `web/package.json`, its lockfile, or `web_ci.yml` / `web_release.yml` changed | Yes |
| A Kotlin or Swift public declaration changed | No — `binary-compatibility` |
| Comments in the diff read as narration | No — `comment-review` |
| Prose under `.claude/` or a README changed | No — `writing-docs` |

## Who owns what

| Layer | Owns | Committed? |
|---|---|---|
| Figma export (`tokens/*.tokens.json`, `svg/`) | Every value and every asset | Yes — design's, not web's |
| `scripts/web-*.main.kts` | Naming, unit conversion, what the output contains | Yes |
| `web/styles/`, `web/src/*.generated.ts`, `web/assets/`, `web/llms.txt`, `web/tokens.json` | Nothing — they are output | **Yes**, so `token_drift.yml` can diff them |
| `web/build/*.mjs` | Byte size only: woff2 and svgo | No — writes into `dist/` |
| `web/dist/` | Build output | No |

A change to a value belongs in Figma. A change to a name or a format belongs in a converter.
Neither ever belongs in a generated file.

## What to check

### Wiring — the failure that stays green

A converter nobody runs produces output that silently goes stale while the drift job passes,
because the job only compares what it regenerates.

- A new or renamed converter reaches `converters_for()` in
  `.claude/skills/generate-tokens/scripts/run-converters.sh` **and** a step in
  `.github/workflows/token_drift.yml`.
- `token_drift.yml` **asserts** over the paths it triggers on. A path added to `on.paths` but
  missing from the final `git status --porcelain` check means the job runs and always passes.
- `web-css-bundle` runs after the converters whose output it concatenates.
- Converters driven by something other than `tokens/*.tokens.json` — `web-text-style-converter`
  reads `text-styles.json`, `web-svg-converter` reads `svg/` — are absent from `converters_for()`
  by design, so they need their own drift step instead.

### Determinism

Non-deterministic output makes the drift job flap and buries the real change in noise.

- Every directory listing and every map traversal is sorted explicitly. `File.listFiles()` is
  filesystem order; `org.json.JSONObject` is hash order, not insertion order.
- Two runs produce byte-identical output. That check alone is not sufficient: hash order is
  stable for a fixed key set, so it only reshuffles when a token is added.

### Values

The traps are in the export format, not in the arithmetic.

- **Alpha is not in the hex.** DTCG colour objects carry `components`, `alpha` and a `hex` that
  drops the alpha. `content-primary` is 92.5% opaque. Colours emit `rgb(R G B / A)`.
- **Opacity is authored 0–100.** `opacity-50` is `50`; CSS needs `0.5`.
- **`rem` for anything that scales with text** — spacing, size, radius, font size, line height.
  **`px` for optical constants** — border widths and shadow geometry.
- **Font weights are strings** in the export (`SemiBold`), numbers in CSS.

### Naming

- Leaf names are not unique. `border-selected` is both a border width and a colour; all 17 themed
  hues reuse `background`, `content` and `border`. The duplicate-property guard in
  `writeTokensCss` is load-bearing — a change that trips it is a naming bug, not a guard to relax.
- A new CSS custom property is public API of an unpublished package. Mirror the KMP shape where one
  exists: `themed.<hue>.subtle.<slot>` became `--lmnd-color-themed-<hue>-subtle-<slot>`.

### Package surface

- Every path in `exports` appears in `files`. `web/tests/package.test.ts` asserts this; a subpath
  exported but unpackaged is simply absent from the tarball.
- `main`, `module` and `types` sit alongside `exports`. TypeScript under
  `moduleResolution: "node"` ignores `exports` entirely.
- No `dependencies` and no `peerDependencies`. The package is framework-agnostic on purpose.
- `sideEffects` lists `*.css`, or a bundler drops a bare stylesheet import and the page renders
  unstyled.
- Verify with `npm ci`, never a regenerated lockfile: `npm install` resolves different transitive
  versions than the committed lockfile, which is what CI installs.

### Assets

- Icons carry `currentColor` and **no** inline `style`. Source icons are black twice, as a `fill`
  attribute and an inline `style`; an inline style beats every stylesheet rule, so rewriting only
  the attribute leaves icons black regardless of CSS.
- Flags and brand logos keep their inline fills. They are multicolour artwork; stripping their
  styles flattens 265 flags into silhouettes, which passes every count check.
- `svgo` runs family-aware: aggressive for icons, conservative for artwork, `removeViewBox`
  disabled everywhere.

### Claims

A comment or a doc that promises a guarantee it does not deliver is worse than silence, because the
next reader stops looking.

- A config option said to protect something: confirm it does. `convertColors`'
  `currentColor: false` is svgo's default and protects nothing — `currentColor` survives because
  svgo never rewrites an existing one.
- A test's red state came from a real assertion failure, not from a deleted import.
- A check cannot pass vacuously. If a token lookup returns null or a list comes back empty, the
  check fails loudly rather than reporting success over nothing.

## Anti-patterns

- **Hand-editing a generated file.** The next run overwrites it and the drift job fails. Change the
  converter.
- **Relaxing the duplicate-property guard to make a converter run.** It is catching two tokens
  collapsing into one name. Fix the naming.
- **Adding a converter without a drift step.** Its output can then go stale forever with CI green.
- **Letting a build write into a committed path.** A build that dirties the tree invites someone to
  commit optimized bytes that no converter reproduces. Build output goes to `dist/`.
- **Regenerating the lockfile to fix an install.** That hides a real mismatch; CI runs `npm ci`.
- **Shipping a deprecated upstream API.** The package has no consumers yet, so a style deprecated on
  KMP and SwiftUI is skipped rather than mirrored.

## Pre-answered decisions

**Q: Why are the converters Kotlin rather than TypeScript, in a web package?**
A: The three native loaders each carry their own copy of the DTCG parser, and
`check-loader-parity.py` holds those copies identical. A TypeScript loader would be a fourth copy
in a language that guard cannot read, so web could disagree with the other platforms about a
token's name or value with every platform's own tests green.

**Q: Why are generated files committed rather than built?**
A: `token_drift.yml` detects staleness by regenerating and diffing. Gitignored output has nothing
to diff, which removes the guarantee that justifies the Kotlin decision above.

**Q: Why are colours CSS-only, with no JS colour map?**
A: A JS map cannot follow `data-lmnd-theme`, so it would be a second source of truth that goes
stale the moment someone toggles dark mode. `tokens` carries scalars, which is what arithmetic
needs.

**Q: Contrast is below WCAG AA on some light-theme text. Is that a bug?**
A: No. Those levels are the design team's decision, recorded in §10 of the web design spec.
Contrast is validated in Figma, where the tokens are authored.

## Gotchas

- Scripts run on **Kotlin 2.3.20**. Newer Kotlin crashes `.main.kts` with a FIR compiler error.
- `const val` does not compile at the top level of a `.main.kts`, private or not. Use `val`.
- Helpers added below the `Figma native (DTCG) support` banner in a loader are compared by
  `check-loader-parity.py` across all four platforms. Web-only helpers need an `EXPECTED_PARTIAL`
  entry.

## References

- `scripts/web-check.sh` — the whole verification sequence; `--fast` skips the full converter run
  and the tarball install.
- `.claude/skills/generate-tokens/SKILL.md` — how to regenerate after a Figma export.
- `.claude/specs/2026-08-21-lemonade-web-support-design.md` — the design decisions, including the
  generated-vs-built rule and the contrast decision.
- `web/README.md` — the consumer-facing surface and the repository layout table.
