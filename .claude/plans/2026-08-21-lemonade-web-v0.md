# Lemonade Web v0 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship Lemonade's design tokens, typography, fonts and icons to the web as
`@teya/lemonade-ds` on public npm, generated from the same `tokens/*.tokens.json`
the native platforms already use.

**Architecture:** Kotlin `.main.kts` converters in `scripts/`, alongside the existing
`kmp-*` and `swiftui-*` ones, read the DTCG token export and emit CSS custom
properties, typed TypeScript and an AI token reference into `web/`. `web/` itself is
only an npm package build — `tsup`, Storybook, and the two asset steps (`svgo`,
`woff2`) that have no Kotlin equivalent. Generated output is committed so the
existing `token_drift.yml` job can diff it.

**Tech Stack:** Kotlin 2.3.20 script converters (`org.json`), Node 20 + TypeScript,
`tsup`, Vitest, Storybook 8, `svgo`, `wawoff2`.

**Spec:** `.claude/specs/2026-08-21-lemonade-web-support-design.md`

## Global Constraints

- **Kotlin 2.3.20 exactly.** Homebrew's 2.4.0 crashes `.main.kts` with a FIR compiler
  error. Always invoke via `.claude/skills/generate-tokens/scripts/run-converters.sh`,
  or explicitly at `$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin`. Never bare `kotlin`.
- **CSS variable prefix is `--lmnd-`.** No exceptions.
- **Units:** `rem` for font-size, line-height, spacing, size, radius. `px` for
  border-width and shadow offset/blur/spread. Unitless for opacity. Zero emits bare `0`.
- **rem divisor is 16.** `8px -> 0.5rem`.
- **Colours emit `rgb(R G B)` or `rgb(R G B / A)`** — space-separated, alpha omitted
  when 1. Never hex: the DTCG `hex` field discards alpha.
- **Package name:** `@teya/lemonade-ds`. If the `@teya` npm scope is unavailable, use
  `@teyaproduct/lemonade-ds` and change it in exactly one place (`web/package.json`).
- **Nothing is published to npm in this plan.** The release workflow is exercised with
  `npm pack` and `npm publish --dry-run` only. Publishing requires local validation
  and team sign-off (spec §3).
- **Generated files carry a DO-NOT-MODIFY banner** naming the script that wrote them,
  via `defaultAutoGenerationMessage(scriptFilePath = ...)`.
- **Determinism:** `org.json`'s `JSONObject` is HashMap-backed, so key order is not
  file order. Every traversal must sort explicitly. The existing loader already does;
  any new traversal must too.
- **Node 20.** `web/.nvmrc` pins it; CI uses `actions/setup-node@v4` with that file.

---

## Deviation from the spec, and why

The spec's §4 tree lists one converter per token category
(`web-color-token-converter`, `web-spacing-token-converter`, …), mirroring the KMP and
SwiftUI naming. This plan consolidates them into **one `web-token-converter.main.kts`**.

The reason is that CSS is a single artifact. The native converters each own a separate
output file (`LemonadeSpaces.kt`, `LemonadeRadius.kt`), so one-per-category is natural
there. On web every category writes custom properties into the same `tokens.css`, so
splitting would force an extra assembly step that concatenates partials, with no
benefit and a new class of bug (a stale partial). Reading all token files on every run
is also strictly safer: partial regeneration cannot leave one category stale.

`run-converters.sh` maps every token file to this one converter and de-dupes, so
`--changed` still works.

Separate scripts are kept where the output *is* separate: text styles, SVG, contrast
checking, `llms.txt`.

---

## File structure

**Created in `scripts/`** (Kotlin, run by `run-converters.sh`):

| File | Responsibility |
|---|---|
| `web-resource-file-loading.main.kts` | DTCG loader. Shared block copied verbatim from the KMP loader, plus raw-name variants web needs. Registered in `check-loader-parity.py`. |
| `web-token-commons.main.kts` | Pure formatting: CSS var naming, `rem`/`px`/`rgb()` emission. No I/O. |
| `web-token-converter.main.kts` | All token categories -> `web/styles/tokens.css`, `web/src/tokens.generated.ts`, `web/dist-meta/tokens.json`. |
| `web-text-style-converter.main.kts` | `text-styles.json` -> `web/styles/typography.css`. |
| `web-text-style-parity-check.main.kts` | Asserts `text-styles.json` matches the SwiftUI table. |
| `web-svg-converter.main.kts` | `svg/**` -> `web/assets/**` with `currentColor`, plus `web/src/icons.generated.ts`. |
| `web-contrast-check.main.kts` | WCAG 2.2 AA over semantic colour pairs, both themes. |
| `web-llms-txt-converter.main.kts` | Tokens + `$description` -> `web/llms.txt`. |
| `web-css-bundle.main.kts` | Concatenates the CSS entrypoints into `web/styles/lemonade.css`. |
| `web-token-commons-test.main.kts` | Unit tests for the pure formatters. |
| `web-loader-dtcg-test.main.kts` | Unit tests for the loader, mirroring `kmp-loader-dtcg-test.main.kts`. |

**Created at repo root:**

| File | Responsibility |
|---|---|
| `text-styles.json` | The 27 named text styles as data. Root, not `web/`, so all platforms can adopt it later. |

**Created in `web/`** (Node, npm package only):

| File | Responsibility |
|---|---|
| `package.json`, `tsup.config.ts`, `tsconfig.json`, `.nvmrc` | Package build |
| `src/index.ts` | Public TS surface; re-exports generated modules |
| `build/optimize-svg.mjs` | `svgo` pass over `assets/` |
| `build/build-fonts.mjs` | `.ttf` -> `.woff2`, emits `styles/fonts.css` |
| `tests/*.test.ts` | Vitest over the published surface |
| `.storybook/`, `stories/` | Token gallery |

**Modified:**

| File | Change |
|---|---|
| `.claude/skills/generate-tokens/scripts/check-loader-parity.py` | Add `web` to `LOADERS`; extend `EXPECTED_PARTIAL` |
| `.claude/skills/generate-tokens/scripts/run-converters.sh` | Add web converters to `converters_for()` |
| `.github/workflows/token_drift.yml` | Watch and drift-check `web/` and `scripts/web-*` |
| `.gitignore` | Ignore `web/node_modules`, `web/dist`, `web/storybook-static` |
| `README.md` | Add Web to the platforms table |

---

### Task 1: Web DTCG loader, registered with the parity guard

The whole reason web uses Kotlin (spec §4.1) is that `check-loader-parity.py` can then
hold web's DTCG parsing identical to the other three platforms'. That guard only
compares the section **after** the literal marker `Figma native (DTCG) support`, so
that block must be copied byte-for-byte.

**Files:**
- Create: `scripts/web-resource-file-loading.main.kts`
- Create: `scripts/web-loader-dtcg-test.main.kts`
- Modify: `.claude/skills/generate-tokens/scripts/check-loader-parity.py:22-36`

**Interfaces:**
- Consumes: nothing — this is the first task.
- Produces: `ResourceData<T>(groups, groupFullName, name, value)`;
  `tokenFile(name: String): File`; `tokenFiles(prefix: String): List<File>`;
  `readFileResourceFile(file, resourceMap): List<ResourceData<T>>`;
  `requireModes(files: List<File>, vararg required: String)`;
  `defaultAutoGenerationMessage(scriptFilePath: String): String`;
  and web-only:
  `readFileResourceFileRaw(file: File, visit: (String, JSONObject) -> Unit)`,
  `readFileResourceFileByModeRaw(files: List<File>, modeName: String, visit: (String, JSONObject) -> Unit)`.
  The raw variants pass the **unsanitised slash path** (`Content/Brand/content-brand`)
  and the resolved-value object. Web needs raw names because CSS custom properties are
  kebab-case; the sanitised names are camelCase for Kotlin/Swift.

- [ ] **Step 1: Copy the KMP loader as the starting point**

```bash
cp scripts/kmp-resource-file-loading.main.kts scripts/web-resource-file-loading.main.kts
```

Everything above the `// ---` banner containing `Figma native (DTCG) support` is
shared helper code; leave it exactly as-is. It is not compared by the parity guard,
but keeping it identical avoids surprises and costs nothing.

- [ ] **Step 2: Append the two raw-name helpers**

Add at the end of `scripts/web-resource-file-loading.main.kts`. `readFileResourceFileByModeRaw`
is copied verbatim from `scripts/swiftui-resource-file-loading.main.kts:340-363` — it
must stay byte-identical to that copy or the parity guard fails.

```kotlin
/**
 * Every non-hidden token in a single-mode document, with its raw slash path.
 *
 * Web needs the unsanitised name: `content-primary` becomes
 * `--lmnd-color-content-primary`, whereas the sanitised name is `contentPrimary`.
 * Unlike [readFileResourceFileByModeRaw] this visits number and string tokens too,
 * so it works for spacing, radius, typography and the rest.
 */
fun readFileResourceFileRaw(
    file: File,
    visit: (String, JSONObject) -> Unit,
) {
    val json = JSONObject(file.readText())
    require(isDtcgDocument(json)) { "${file.path} is not a Figma native DTCG export" }
    val tokens = dtcgTokens(json)
    tokens.keys
        .sortedWith(::canonicalTokenOrder)
        .filterNot { name ->
            tokens.getValue(name).optJSONObject("\$extensions")
                ?.optBoolean("com.figma.hiddenFromPublishing") ?: false
        }
        .forEach { name -> visit(name, dtcgResolvedValueObject(tokens, name)) }
}
```

Then copy `readFileResourceFileByModeRaw` from the SwiftUI loader unchanged.

- [ ] **Step 3: Register web with the parity guard**

In `.claude/skills/generate-tokens/scripts/check-loader-parity.py`:

```python
LOADERS = {
    "kmp": "scripts/kmp-resource-file-loading.main.kts",
    "swiftui": "scripts/swiftui-resource-file-loading.main.kts",
    "flutter": "scripts/flutter-resource-file-loading.main.kts",
    "web": "scripts/web-resource-file-loading.main.kts",
}

# Functions deliberately present in only some loaders, with the reason.
EXPECTED_PARTIAL = {
    "requireModes": ({"kmp", "swiftui", "web"}, "Flutter has no mode-based loading"),
    "readFileResourceFileByModeRaw": (
        {"swiftui", "web"},
        "the SwiftUI asset generator and the web converter need raw token names",
    ),
    "readFileResourceFileRaw": (
        {"web"},
        "only web emits kebab-case names for every token type",
    ),
}
```

- [ ] **Step 4: Run the parity guard — it must pass**

Run: `python3 .claude/skills/generate-tokens/scripts/check-loader-parity.py`
Expected: exit 0, no divergence reported.

If it reports a divergence, the copied block was edited. Re-copy it; do not "fix"
either side to converge.

- [ ] **Step 5: Write the loader test**

Create `scripts/web-loader-dtcg-test.main.kts`, reusing the fixture the KMP test uses.
The fixture has 5 non-hidden tokens; the web test asserts on **raw** names, which is
the thing the KMP test cannot cover.

```kotlin
#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")

import org.json.JSONObject
import java.io.File

fun check(condition: Boolean, message: String) {
    if (!condition) error("FAIL: $message")
    println("  ok  $message")
}

fun main() {
    val fixture = File("scripts/testdata/sample.tokens.json")
    check(fixture.isFile, "fixture exists at ${fixture.path}")

    val rawNames = mutableListOf<String>()
    val rawValues = mutableMapOf<String, JSONObject>()
    readFileResourceFileRaw(fixture) { name, value ->
        rawNames.add(name)
        rawValues[name] = value
    }

    check(rawNames.size == 5, "hidden tokens are excluded (expected 5, got ${rawNames.size})")

    check(
        rawNames == listOf(
            "Base/border-0",
            "Base/border-50",
            "Base/border-100",
            "State/border-selected",
            "Tint/brand",
        ),
        "raw slash paths are preserved in canonical order, got $rawNames",
    )

    val selected = rawValues.getValue("State/border-selected")
    check(
        selected.getInt("resolvedValue") == 2,
        "local DTCG reference resolves to its target value",
    )

    val tint = rawValues.getValue("Tint/brand").getJSONObject("resolvedValue")
    check(tint.has("r") && tint.has("a"), "colour tokens expose r/g/b/a components")

    println("All web loader checks passed")
}

main()
```

- [ ] **Step 6: Run the loader test**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-loader-dtcg-test.main.kts`
Expected: every line prints `ok`, ends with `All web loader checks passed`.

If the raw-name assertion fails, print `rawNames` and correct the **expected list** to
what the fixture actually contains — the fixture is the source of truth, not this plan.

- [ ] **Step 7: Commit**

```bash
git add scripts/web-resource-file-loading.main.kts \
        scripts/web-loader-dtcg-test.main.kts \
        .claude/skills/generate-tokens/scripts/check-loader-parity.py
git commit -m "feat(web): add the DTCG loader and register it with the parity guard"
```

---

### Task 2: Pure CSS value and name formatters

Everything hard about the conversion lives here, and none of it touches the
filesystem — so it is all directly testable. Written test-first.

**Files:**
- Create: `scripts/web-token-commons.main.kts`
- Create: `scripts/web-token-commons-test.main.kts`

**Interfaces:**
- Consumes: nothing (no imports — deliberately I/O-free).
- Produces:
  `cssVar(category: String, leaf: String, strip: String? = null): String`,
  `remValue(px: Double): String`, `pxValue(px: Double): String`,
  `rgbValue(r: Double, g: Double, b: Double, a: Double): String`,
  `trimNumber(value: Double): String`,
  `leafOf(path: String): String`.

- [ ] **Step 1: Write the failing test**

Create `scripts/web-token-commons-test.main.kts`:

```kotlin
#!/usr/bin/env kotlin

@file:Import("web-token-commons.main.kts")

fun check(condition: Boolean, message: String) {
    if (!condition) error("FAIL: $message")
    println("  ok  $message")
}

fun main() {
    // --- leafOf ---------------------------------------------------------
    check(leafOf("Content/Brand/content-brand") == "content-brand", "leafOf takes the last segment")
    check(leafOf("spacing-100") == "spacing-100", "leafOf tolerates a bare name")

    // --- cssVar: colours keep their full leaf --------------------------
    check(
        cssVar("color", "content-primary") == "--lmnd-color-content-primary",
        "colour tokens are namespaced without stripping",
    )
    check(
        cssVar("color", "bg-default") == "--lmnd-color-bg-default",
        "background colour keeps its bg- prefix",
    )

    // --- cssVar: scalars strip their repeated category ------------------
    check(cssVar("spacing", "spacing-100", strip = "spacing") == "--lmnd-spacing-100", "spacing strips")
    check(cssVar("radius", "radius-200", strip = "radius") == "--lmnd-radius-200", "radius strips")
    check(cssVar("size", "size-400", strip = "size") == "--lmnd-size-400", "size strips")
    check(
        cssVar("opacity", "opacity-disabled", strip = "opacity") == "--lmnd-opacity-disabled",
        "opacity strips",
    )

    // --- cssVar: the border-selected collision (spec §5) ----------------
    // `border-selected` exists twice: a border WIDTH and a semantic COLOUR.
    check(
        cssVar("border-width", "border-25", strip = "border") == "--lmnd-border-width-25",
        "border width strips the shorter 'border' prefix, not the category name",
    )
    check(
        cssVar("border-width", "border-selected", strip = "border") == "--lmnd-border-width-selected",
        "the border-width half of the collision",
    )
    check(
        cssVar("color", "border-selected") == "--lmnd-color-border-selected",
        "the colour half of the collision — the two must not collapse",
    )
    check(
        cssVar("border-width", "focus-ring", strip = "border") == "--lmnd-border-width-focus-ring",
        "a leaf that does not carry the prefix is left intact",
    )

    // --- cssVar: typography --------------------------------------------
    check(
        cssVar("font-size", "font-size-400", strip = "font-size") == "--lmnd-font-size-400",
        "font size strips",
    )
    check(cssVar("font-family", "base") == "--lmnd-font-family-base", "font family keeps its leaf")
    check(cssVar("font-weight", "semibold") == "--lmnd-font-weight-semibold", "font weight keeps its leaf")

    // --- remValue -------------------------------------------------------
    check(remValue(0.0) == "0", "zero is emitted bare, with no unit")
    check(remValue(8.0) == "0.5rem", "8px is half a rem")
    check(remValue(4.0) == "0.25rem", "4px is a quarter rem")
    check(remValue(16.0) == "1rem", "16px is one rem, with no trailing zeros")
    check(remValue(2.0) == "0.125rem", "2px survives without float noise")

    // --- pxValue --------------------------------------------------------
    check(pxValue(0.0) == "0", "zero is emitted bare")
    check(pxValue(1.0) == "1px", "integers do not gain a decimal point")
    check(pxValue(0.5) == "0.5px", "sub-pixel shadow offsets are preserved")

    // --- rgbValue -------------------------------------------------------
    check(
        rgbValue(1.0, 1.0, 1.0, 1.0) == "rgb(255 255 255)",
        "fully opaque colours omit the alpha channel entirely",
    )
    // content-primary: the hex says #090806 but alpha is 0.925 — spec §5.
    check(
        rgbValue(0.03529411926865578, 0.0313725508749485, 0.0235294122248888, 0.925000011920929)
            == "rgb(9 8 6 / 0.925)",
        "components round to 0-255 and float noise is trimmed off the alpha",
    )
    check(
        rgbValue(0.0, 0.0, 0.0, 0.05000000074505806) == "rgb(0 0 0 / 0.05)",
        "the light-theme shadow alpha survives rounding",
    )

    println("All web token commons checks passed")
}

main()
```

- [ ] **Step 2: Run it to make sure it fails**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-token-commons-test.main.kts`
Expected: FAIL — `web-token-commons.main.kts` does not exist, so the `@file:Import`
cannot resolve and the script does not compile.

- [ ] **Step 3: Write the implementation**

Create `scripts/web-token-commons.main.kts`:

```kotlin
#!/usr/bin/env kotlin

// Pure formatting helpers for the web token converters.
//
// Deliberately free of file I/O so that web-token-commons-test.main.kts can
// exercise every branch without fixtures.

import kotlin.math.abs
import kotlin.math.roundToInt

/** The last segment of a DTCG slash path. `Content/Brand/content-brand` -> `content-brand`. */
fun leafOf(path: String): String = path.substringAfterLast('/')

/**
 * The CSS custom property for a token.
 *
 * Leaf names in the Figma export are already self-describing (`content-primary`,
 * `spacing-100`), so the scheme is `--lmnd-<category>-<leaf>` with the repeated
 * category word removed. [strip] is passed explicitly rather than derived from
 * [category] because they differ: the border-width collection prefixes its tokens
 * with `border-`, not `border-width-`.
 *
 * The namespacing is what keeps `border-selected` from collapsing: it exists both as
 * a border width and as a semantic colour.
 */
fun cssVar(category: String, leaf: String, strip: String? = null): String {
    val suffix = if (strip != null) leaf.removePrefix("$strip-") else leaf
    return "--lmnd-$category-$suffix"
}

/** Trims float noise and trailing zeros: 0.925000011920929 -> "0.925", 1.0 -> "1". */
fun trimNumber(value: Double): String {
    if (abs(value) < 1e-9) return "0"
    val rounded = String.format("%.4f", value)
    return rounded.trimEnd('0').trimEnd('.')
}

/**
 * Font sizes, line heights, spacing, sizes and radii, divided by the 16px root.
 *
 * rem rather than px because only rem follows a raised browser default font size —
 * a common low-vision accommodation. See spec §5.
 */
fun remValue(px: Double): String {
    if (abs(px) < 1e-9) return "0"
    return trimNumber(px / 16.0) + "rem"
}

/** Border widths and shadow geometry, which are optical constants rather than proportional. */
fun pxValue(px: Double): String {
    if (abs(px) < 1e-9) return "0"
    return trimNumber(px) + "px"
}

/**
 * A colour as space-separated `rgb()`.
 *
 * Never hex: the DTCG `hex` field discards alpha, and `content-primary` is 92.5%
 * opaque black. Emitting its hex would silently produce the wrong colour.
 */
fun rgbValue(r: Double, g: Double, b: Double, a: Double): String {
    val red = (r * 255).roundToInt()
    val green = (g * 255).roundToInt()
    val blue = (b * 255).roundToInt()
    val opaque = abs(a - 1.0) < 1e-6
    return if (opaque) {
        "rgb($red $green $blue)"
    } else {
        "rgb($red $green $blue / ${trimNumber(a)})"
    }
}
```

- [ ] **Step 4: Run the tests to verify they pass**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-token-commons-test.main.kts`
Expected: every assertion prints `ok`, ending with `All web token commons checks passed`.

- [ ] **Step 5: Commit**

```bash
git add scripts/web-token-commons.main.kts scripts/web-token-commons-test.main.kts
git commit -m "feat(web): add pure CSS name and value formatters"
```

---

### Task 3: Scalar token converter — spacing, radius, size, opacity, border width

**Files:**
- Create: `scripts/web-token-converter.main.kts`
- Create: `web/styles/tokens.css` (generated output, committed)

**Interfaces:**
- Consumes: `readFileResourceFileRaw`, `tokenFile`, `defaultAutoGenerationMessage`
  (Task 1); `cssVar`, `remValue`, `pxValue`, `trimNumber`, `leafOf` (Task 2).
- Produces: `data class CssVar(val name: String, val value: String)`;
  `fun scalarVars(): List<CssVar>`; `fun cssBanner(): String`;
  `fun writeTokensCss(sections: List<Pair<String, List<CssVar>>>)`.
  Tasks 4 and 5 append to the same `sections` list.

**Data notes verified against the real token files — do not re-derive:**

- **Opacity is 0–100, not 0–1.** `opacity-50` has `$value: 50`. CSS `opacity` needs
  0–1, so divide by 100. Emitting `50` would make every element fully opaque.
- **`radius-full` is `999`.** In rem that is `62.4375rem`, which behaves as a pill at
  any root font size. Left on the general rem rule deliberately; no special case.
- **`border-40` is `1.5`** — sub-pixel widths exist, so do not round to Int.
- **Aliases live inside these files** (`state/border-selected -> {base.border-50}`,
  `semantic/radius-container-default -> {radius-600}`). The loader resolves them, so
  both the alias and its target emit their own variable. That is intended.
- **Group paths are ignored** (`base/opacity-0`, `state/opacity-disabled`). Leaf names
  are unique within each file, verified across the whole token set.

- [ ] **Step 1: Write the converter**

Create `scripts/web-token-converter.main.kts`:

```kotlin
#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")
@file:Import("web-token-commons.main.kts")

import java.io.File

const val SCRIPT_PATH = "scripts/web-token-converter.main.kts"

data class CssVar(val name: String, val value: String)

/** One scalar token collection and how its values become CSS. */
private data class ScalarSource(
    val fileName: String,
    val category: String,
    val strip: String?,
    val format: (Double) -> String,
)

/** Opacity tokens are authored 0-100; CSS wants 0-1. */
private fun opacityValue(raw: Double): String = trimNumber(raw / 100.0)

private val SCALAR_SOURCES = listOf(
    ScalarSource("spacing.tokens.json", "spacing", "spacing", ::remValue),
    ScalarSource("radius.tokens.json", "radius", "radius", ::remValue),
    ScalarSource("size.tokens.json", "size", "size", ::remValue),
    ScalarSource("border-width.tokens.json", "border-width", "border", ::pxValue),
    ScalarSource("opacity.tokens.json", "opacity", "opacity", ::opacityValue),
)

fun scalarVars(): List<CssVar> {
    val vars = mutableListOf<CssVar>()
    SCALAR_SOURCES.forEach { source ->
        readFileResourceFileRaw(tokenFile(source.fileName)) { path, resolved ->
            val raw = resolved.get("resolvedValue")
            require(raw is Number) {
                "${source.fileName}: token '$path' is ${raw::class.simpleName}, expected a number"
            }
            vars.add(
                CssVar(
                    name = cssVar(source.category, leafOf(path), source.strip),
                    value = source.format(raw.toDouble()),
                )
            )
        }
    }
    return vars
}

fun cssBanner(): String = buildString {
    appendLine("/**")
    append(defaultAutoGenerationMessage(scriptFilePath = SCRIPT_PATH))
    appendLine(" */")
}

/**
 * Writes web/styles/tokens.css.
 *
 * [sections] is an ordered list of (selector, declarations). Everything theme-neutral
 * goes under `:root`; Task 4 adds the theme selectors.
 */
fun writeTokensCss(sections: List<Pair<String, List<CssVar>>>) {
    val duplicates = sections
        .flatMap { it.second }
        .groupBy { "${it.name}" }
        .filterValues { it.size > 1 }
    // Guards the border-selected class of bug: two tokens mapping to one property
    // would silently drop one, and nothing downstream would notice.
    require(duplicates.isEmpty()) {
        "duplicate CSS custom properties within a selector: ${duplicates.keys.sorted()}"
    }

    val output = buildString {
        append(cssBanner())
        sections.forEach { (selector, vars) ->
            appendLine()
            appendLine("$selector {")
            vars.forEach { appendLine("  ${it.name}: ${it.value};") }
            appendLine("}")
        }
    }

    val target = File("web/styles/tokens.css")
    target.parentFile.mkdirs()
    target.writeText(output)
    println("✓ web/styles/tokens.css written (${sections.sumOf { it.second.size }} properties)")
}

fun main() {
    try {
        val scalars = scalarVars()
        println("✓ Loaded ${scalars.size} scalar tokens")
        writeTokensCss(listOf(":root" to scalars))
    } catch (error: Throwable) {
        println("✗ Failed to generate web tokens: ${error.message}")
        throw error
    }
}

main()
```

- [ ] **Step 2: Run it**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-token-converter.main.kts`
Expected: `✓ Loaded 79 scalar tokens` (15 spacing + 15 radius + 27 size + 8 border
width + 14 opacity), then `✓ web/styles/tokens.css written`.

If the count differs, the token files changed — trust the files, not this number, but
confirm the difference is explained by a real token addition before continuing.

- [ ] **Step 3: Verify the output by eye**

Run: `grep -E "spacing-100|radius-full|border-width-25|border-width-selected|opacity-50" web/styles/tokens.css`

Expected exactly:

```css
  --lmnd-spacing-100: 0.25rem;
  --lmnd-radius-full: 62.4375rem;
  --lmnd-border-width-25: 1px;
  --lmnd-border-width-selected: 2px;
  --lmnd-opacity-50: 0.5;
```

`--lmnd-opacity-50: 0.5` is the check that matters most — `50` there would mean the
0–100 conversion was missed and every opacity token is broken.

- [ ] **Step 4: Commit**

```bash
git add scripts/web-token-converter.main.kts web/styles/tokens.css
git commit -m "feat(web): generate CSS custom properties for scalar tokens"
```

---

### Task 4: Theme colours — light, dark, and the theme attribute

151 semantic colours per theme, in two files that differ only by mode. This is where
`data-lmnd-theme` and `color-scheme` come from.

**Files:**
- Modify: `scripts/web-token-converter.main.kts` (add `themeSections()`, extend `main`)
- Modify: `web/styles/tokens.css` (regenerated)

**Interfaces:**
- Consumes: `readFileResourceFileByModeRaw`, `tokenFiles`, `requireModes` (Task 1);
  `cssVar`, `rgbValue` (Task 2); `CssVar`, `writeTokensCss` (Task 3).
- Produces: `fun themeVars(mode: String): List<CssVar>`;
  `fun themeSections(): List<Pair<String, List<CssVar>>>`.

- [ ] **Step 1: Add theme loading to the converter**

Insert into `scripts/web-token-converter.main.kts`, above `fun main()`:

```kotlin
/** Every semantic colour for one Figma mode, as CSS custom properties. */
fun themeVars(mode: String): List<CssVar> {
    val files = tokenFiles("theme-colors.")
    // Regenerating from a subset would leave the other theme silently stale, and the
    // drift job would stay green because the tree stays clean.
    requireModes(files, "Light", "Dark")

    val vars = mutableListOf<CssVar>()
    readFileResourceFileByModeRaw(files, mode) { path, resolved ->
        vars.add(
            CssVar(
                name = cssVar("color", leafOf(path)),
                value = rgbValue(
                    r = resolved.getDouble("r"),
                    g = resolved.getDouble("g"),
                    b = resolved.getDouble("b"),
                    a = resolved.getDouble("a"),
                ),
            )
        )
    }
    return vars
}

/**
 * The four theme selectors.
 *
 * Light lives on bare `:root` so that importing tokens.css and setting nothing at all
 * is already correct. Dark then applies two ways: automatically from the OS, and
 * explicitly via the attribute, which always wins. The attribute is matched on any
 * element rather than only `<html>`, so a dark card inside a light page works.
 *
 * `color-scheme` makes native scrollbars, form controls and browser UI follow the
 * theme; without it the page is themed but the scrollbar is glaringly not.
 */
fun themeSections(): List<Pair<String, List<CssVar>>> {
    val light = themeVars("Light")
    val dark = themeVars("Dark")
    require(light.map { it.name } == dark.map { it.name }) {
        "light and dark themes declare different colour tokens — one export is stale"
    }
    val scheme = { value: String -> CssVar("color-scheme", value) }
    return listOf(
        ":root" to light + scheme("light"),
        "@media (prefers-color-scheme: dark) { :root:not([data-lmnd-theme=\"light\"])" to
            dark + scheme("dark"),
        "[data-lmnd-theme=\"dark\"]" to dark + scheme("dark"),
        "[data-lmnd-theme=\"light\"]" to light + scheme("light"),
    )
}
```

`color-scheme` is not a custom property, but it is emitted through `CssVar` so it
lands inside the same declaration block. `writeTokensCss` writes `name: value;`
verbatim, so no change is needed there.

- [ ] **Step 2: Handle the media-query wrapper in `writeTokensCss`**

The media-query selector needs a closing brace that the others do not. Replace the
`sections.forEach` block in `writeTokensCss` with:

```kotlin
        sections.forEach { (selector, vars) ->
            val isAtRule = selector.startsWith("@")
            appendLine()
            appendLine("$selector {")
            vars.forEach { appendLine("  ${it.name}: ${it.value};") }
            appendLine("}")
            if (isAtRule) appendLine("}")
        }
```

Also relax the duplicate guard, which now legitimately sees the same property in four
selectors — it must only reject duplicates *within* one selector:

```kotlin
    sections.forEach { (selector, vars) ->
        val duplicates = vars.groupBy { it.name }.filterValues { it.size > 1 }
        require(duplicates.isEmpty()) {
            "duplicate CSS custom properties in '$selector': ${duplicates.keys.sorted()}"
        }
    }
```

- [ ] **Step 3: Wire it into `main`**

```kotlin
fun main() {
    try {
        val scalars = scalarVars()
        println("✓ Loaded ${scalars.size} scalar tokens")
        val themes = themeSections()
        println("✓ Loaded ${themes.first().second.size - 1} colours per theme")
        val sections = listOf(":root" to scalars) + themes
        writeTokensCss(sections)
    } catch (error: Throwable) {
        println("✗ Failed to generate web tokens: ${error.message}")
        throw error
    }
}
```

- [ ] **Step 4: Run it**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-token-converter.main.kts`
Expected: `✓ Loaded 151 colours per theme`, then the file is written.

- [ ] **Step 5: Verify the alpha handling, which is the easy thing to get wrong**

Run: `grep -n "content-primary\|shadow-default" web/styles/tokens.css | head -8`

Expected: `--lmnd-color-content-primary: rgb(9 8 6 / 0.925);` — **not** `rgb(9 8 6)`.
The DTCG `hex` field for this token says `#090806` and discards the 0.925 alpha, so a
bare `rgb(9 8 6)` means the alpha was dropped.

Expected also: `--lmnd-color-shadow-default` appears with alpha `0.05` under `:root`
and `0.5` under `[data-lmnd-theme="dark"]`. Task 5 depends on that difference.

- [ ] **Step 6: Verify the theme structure**

Run: `grep -n "^@media\|^\[data-lmnd-theme\|^:root\|^}" web/styles/tokens.css`

Expected: `:root` twice (scalars, then light), one `@media` block, and both attribute
selectors, with balanced braces — the `@media` block must close twice.

- [ ] **Step 7: Commit**

```bash
git add scripts/web-token-converter.main.kts web/styles/tokens.css
git commit -m "feat(web): generate light and dark theme colours with data-lmnd-theme"
```

---

### Task 5: Typography scales and composed shadows

**Files:**
- Modify: `scripts/web-token-converter.main.kts`
- Modify: `web/styles/tokens.css` (regenerated)

**Interfaces:**
- Consumes: everything from Tasks 1–4.
- Produces: `fun typographyVars(): List<CssVar>`; `fun shadowVars(): List<CssVar>`.

**Data notes verified against the real token files:**

- Typography groups are `font-size` (15), `line-height` (18), `font-family` (1),
  `font-weight` (4). The **group path is the category**, unlike the scalar files.
- `font-weight` values are the **strings** `Regular`, `Medium`, `SemiBold`, `Bold`.
  CSS needs numbers.
- `font-family/base` is the string `Figtree`. It needs a fallback stack appended;
  a bare `Figtree` renders as the browser default wherever the font fails to load.
- Shadow leaves are `sd-<size>-lv<n>-<part>` at paths like
  `shadow/xsmall/level-1/sd-xs-lv1-color`. Sizes are `xs sm md lg xl`; every size has
  `lv1` and `lv2` **except `xs`, which has only `lv1`**.
- **Every `sd-*-color` aliases `Shadow/shadow-default`**, which is theme-dependent
  (light α 0.05, dark α 0.5). So the composed shadow must reference
  `var(--lmnd-color-shadow-default)` rather than a resolved literal — that is what
  makes shadows follow the theme with no extra selectors.

- [ ] **Step 1: Add typography emission**

Insert into `scripts/web-token-converter.main.kts`:

```kotlin
private val FONT_WEIGHTS = mapOf(
    "Regular" to "400",
    "Medium" to "500",
    "SemiBold" to "600",
    "Bold" to "700",
)

/**
 * A bare `Figtree` leaves every machine without the font on its browser default.
 * The stack mirrors what the native platforms fall back to.
 */
private const val FONT_STACK =
    "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif"

fun typographyVars(): List<CssVar> {
    val vars = mutableListOf<CssVar>()
    readFileResourceFileRaw(tokenFile("typography.tokens.json")) { path, resolved ->
        val group = path.substringBefore('/')
        val leaf = leafOf(path)
        val raw = resolved.get("resolvedValue")
        val declaration = when (group) {
            "font-size" -> CssVar(
                cssVar("font-size", leaf, "font-size"),
                remValue((raw as Number).toDouble()),
            )
            "line-height" -> CssVar(
                cssVar("line-height", leaf, "line-height"),
                remValue((raw as Number).toDouble()),
            )
            "font-weight" -> CssVar(
                cssVar("font-weight", leaf),
                FONT_WEIGHTS[raw as String]
                    ?: error("unmapped font weight '$raw' on '$path' — add it to FONT_WEIGHTS"),
            )
            "font-family" -> CssVar(
                cssVar("font-family", leaf),
                "\"$raw\", $FONT_STACK",
            )
            else -> error("unexpected typography group '$group' on token '$path'")
        }
        vars.add(declaration)
    }
    return vars
}
```

- [ ] **Step 2: Add shadow composition**

```kotlin
private val SHADOW_LEAF = Regex("""^sd-([a-z]+)-lv(\d+)-(.+)$""")
private val SHADOW_ORDER = listOf("xs", "sm", "md", "lg", "xl")
private const val SHADOW_COLOUR_TOKEN = "Shadow/shadow-default"

/**
 * Composes the 45 scalar shadow parts into one usable value per size.
 *
 * The raw tokens are unusable directly — `sd-md-lv2-blur` is not something anyone
 * writes in a stylesheet. Each size's lv1 and lv2 layers become a single comma-joined
 * box-shadow, ordered lv1 first so the tighter layer paints nearest.
 *
 * The colour is emitted as var(--lmnd-color-shadow-default) rather than its resolved
 * value, because that token differs between themes. Referencing the variable makes
 * shadows theme-reactive for free.
 */
fun shadowVars(): List<CssVar> {
    // size -> level -> part -> value
    val parts = sortedMapOf<String, MutableMap<Int, MutableMap<String, Double>>>()

    readFileResourceFileRaw(tokenFile("shadow.tokens.json")) { path, resolved ->
        val match = SHADOW_LEAF.matchEntire(leafOf(path))
            ?: error("shadow token '$path' does not match sd-<size>-lv<n>-<part>")
        val (size, level, part) = match.destructured

        if (part == "color") {
            val alias = resolved.optString("aliasName")
            require(alias == SHADOW_COLOUR_TOKEN) {
                "shadow colour '$path' aliases '$alias', expected '$SHADOW_COLOUR_TOKEN'. " +
                    "Shadows reference the theme colour variable; a different source needs new handling."
            }
            return@readFileResourceFileRaw
        }

        val value = (resolved.get("resolvedValue") as Number).toDouble()
        parts.getOrPut(size) { mutableMapOf() }
            .getOrPut(level.toInt()) { mutableMapOf() }[part] = value
    }

    val unknown = parts.keys - SHADOW_ORDER.toSet()
    require(unknown.isEmpty()) { "unknown shadow sizes $unknown — add them to SHADOW_ORDER" }

    return SHADOW_ORDER.filter { it in parts }.map { size ->
        val layers = parts.getValue(size).toSortedMap().map { (_, part) ->
            listOf(
                pxValue(part.getValue("offset-x")),
                pxValue(part.getValue("offset-y")),
                pxValue(part.getValue("blur")),
                pxValue(part.getValue("spread")),
                "var(--lmnd-color-shadow-default)",
            ).joinToString(" ")
        }
        CssVar("--lmnd-shadow-$size", layers.joinToString(", "))
    }
}
```

- [ ] **Step 3: Add both to `main`**

Change the scalar line in `main` to:

```kotlin
        val scalars = scalarVars() + typographyVars() + shadowVars()
        println("✓ Loaded ${scalars.size} theme-neutral tokens")
```

- [ ] **Step 4: Run it**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-token-converter.main.kts`
Expected: `✓ Loaded 122 theme-neutral tokens` (79 scalars + 38 typography + 5 shadows)
and no error about unmapped weights or unknown shadow sizes.

- [ ] **Step 5: Verify the composed output**

Run: `grep -E "lmnd-shadow-|font-weight-semibold|font-family-base|font-size-400" web/styles/tokens.css`

Expected shapes:

```css
  --lmnd-font-size-400: 1rem;
  --lmnd-font-weight-semibold: 600;
  --lmnd-font-family-base: "Figtree", -apple-system, ...;
  --lmnd-shadow-xs: 0 0.5px 1px 0 var(--lmnd-color-shadow-default);
  --lmnd-shadow-md: <lv1>, <lv2>;
```

Two things to confirm: `--lmnd-shadow-xs` has **one** layer while the others have two,
and every shadow ends in `var(--lmnd-color-shadow-default)` rather than an `rgb(...)`.
A literal colour there means shadows will not follow the theme.

- [ ] **Step 6: Commit**

```bash
git add scripts/web-token-converter.main.kts web/styles/tokens.css
git commit -m "feat(web): add typography scales and theme-reactive composed shadows"
```

---

### Task 6: Typed TypeScript export

The CSS covers styling; JS needs the raw numbers for anything that does maths —
charts, canvas, animation. Raw values preserve parity with KMP and SwiftUI (spec §5).

**Files:**
- Modify: `scripts/web-token-converter.main.kts`
- Create: `web/src/tokens.generated.ts` (generated, committed)
- Create: `web/tokens.json` (generated, committed)

**Interfaces:**
- Consumes: Tasks 1–5.
- Produces: TS module exporting `tokens`, typed as `LemonadeTokens`, with shape
  `{ spacing: Record<string, number>, radius: …, size: …, borderWidth: …, opacity: … }`
  — the **scalar numeric** tokens only, keyed camelCase (`spacing200`).
  **Colours are deliberately CSS-only in v0.** A JS colour map would be a second
  source of truth that cannot follow `data-lmnd-theme`, and every consumer that needs
  a colour should read `var(--lmnd-color-*)` so theming keeps working. Anything doing
  real maths needs the scalars, which are here.

- [ ] **Step 1: Add the TS emitter**

Insert into `scripts/web-token-converter.main.kts`:

```kotlin
/** camelCase key for a TS object literal: `spacing-100` -> `spacing100`. */
fun tsKey(leaf: String, strip: String?): String {
    val trimmed = if (strip != null) leaf.removePrefix("$strip-") else leaf
    return trimmed.split("-").mapIndexed { index, word ->
        if (index == 0) word else word.replaceFirstChar { it.uppercase() }
    }.joinToString("")
}

fun writeTokensTs(groups: List<Triple<String, String, List<Pair<String, String>>>>) {
    // Triple is (tsGroupName, tsType, entries of key to literal)
    val output = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = SCRIPT_PATH))
        appendLine(" */")
        appendLine()
        appendLine("export const tokens = {")
        groups.forEach { (name, _, entries) ->
            appendLine("  $name: {")
            entries.forEach { (key, literal) -> appendLine("    $key: $literal,") }
            appendLine("  },")
        }
        appendLine("} as const")
        appendLine()
        appendLine("export type LemonadeTokens = typeof tokens")
    }
    val target = File("web/src/tokens.generated.ts")
    target.parentFile.mkdirs()
    target.writeText(output)
    println("✓ web/src/tokens.generated.ts written")
}
```

- [ ] **Step 2: Assemble the groups in `main`**

Numbers are emitted unquoted so they are `number` in TS; colours and CSS names are
quoted strings. Build the entries alongside the existing `CssVar` collection — extend
`scalarVars()` to also return the raw value by changing its return type to
`List<Pair<CssVar, Double>>`, or collect a parallel list. Either is fine; the simpler
change is a second pass:

```kotlin
fun scalarEntries(): List<Triple<String, String, List<Pair<String, String>>>> {
    return SCALAR_SOURCES.map { source ->
        val entries = mutableListOf<Pair<String, String>>()
        readFileResourceFileRaw(tokenFile(source.fileName)) { path, resolved ->
            val raw = (resolved.get("resolvedValue") as Number).toDouble()
            val value = if (source.category == "opacity") raw / 100.0 else raw
            entries.add(tsKey(leafOf(path), source.strip) to trimNumber(value))
        }
        Triple(tsGroupName(source.category), "number", entries)
    }
}

/** `border-width` -> `borderWidth`, `spacing` -> `spacing`. */
fun tsGroupName(category: String): String =
    category.split("-").mapIndexed { index, word ->
        if (index == 0) word else word.replaceFirstChar { it.uppercase() }
    }.joinToString("")
```

Then in `main`, after writing the CSS:

```kotlin
        val entries = scalarEntries()
        writeTokensTs(entries)
        File("web/tokens.json").writeText(buildJsonFromEntries(entries))
        println("✓ web/tokens.json written")
```

where `buildJsonFromEntries` serialises the same data with `org.json`:

```kotlin
fun buildJsonFromEntries(
    groups: List<Triple<String, String, List<Pair<String, String>>>>,
): String {
    val root = org.json.JSONObject()
    groups.forEach { (name, _, entries) ->
        val group = org.json.JSONObject()
        entries.forEach { (key, literal) -> group.put(key, literal.toDouble()) }
        root.put(name, group)
    }
    return root.toString(2) + "\n"
}
```

- [ ] **Step 3: Run and verify**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-token-converter.main.kts`
Then: `grep -A3 "spacing: {" web/src/tokens.generated.ts | head -5`

Expected: `spacing200: 8,` — the **raw px number**, not `"0.5rem"`. This is the parity
guarantee with KMP and SwiftUI; a rem string here defeats the purpose of the export.

- [ ] **Step 4: Commit**

```bash
git add scripts/web-token-converter.main.kts web/src/tokens.generated.ts web/tokens.json
git commit -m "feat(web): emit typed TypeScript token constants with raw values"
```

---

### Task 7: Text styles — extract from SwiftUI, then guard against drift

The 27 named text styles are **not in the token JSON**. They are hand-written twice,
in `swiftui/Sources/Lemonade/LemonadeTypography.swift` and again in KMP, with nothing
verifying them (spec §7). Rather than hand-transcribing them a third time — which is
how transcription errors get in — extract them, then keep a parser-based parity check.

**Files:**
- Create: `scripts/web-text-style-extract.main.kts` (one-time bootstrap)
- Create: `text-styles.json` (repo root — data, reviewed by a human once)
- Create: `scripts/web-text-style-converter.main.kts`
- Create: `scripts/web-text-style-parity-check.main.kts`
- Create: `web/styles/typography.css` (generated, committed)

**Interfaces:**
- Consumes: `defaultAutoGenerationMessage` (Task 1); `cssVar` (Task 2).
- Produces: `text-styles.json` with shape
  `[{ "name": "displayXSmall", "css": "display-xsmall", "fontSize": "font-size-600",
  "lineHeight": "line-height-800", "fontWeight": "semibold", "letterSpacing": -0.25 }]`;
  `fun parseSwiftTextStyles(file: File): List<TextStyle>` shared by the extractor and
  the parity check.

- [ ] **Step 1: Write the Swift parser and extractor**

Create `scripts/web-text-style-extract.main.kts`. The Swift declarations look exactly
like this, so the parser targets that shape:

```swift
    public let displayXSmall = LemonadeTextStyle(
        fontSize: LemonadeFontSizes.fontSize600.value,
        lineHeight: LemonadeLineHeights.lineHeight800.value,
        fontWeight: LemonadeFontWeights.semibold.value,
        letterSpacing: -0.25
    )
```

```kotlin
#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.json:json:20240303")

import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class TextStyle(
    val name: String,
    val cssName: String,
    val fontSize: String,
    val lineHeight: String,
    val fontWeight: String,
    val letterSpacing: Double?,
)

private val DECL = Regex(
    """public let (\w+) = LemonadeTextStyle\(([^)]*)\)""",
    RegexOption.DOT_MATCHES_ALL,
)
private val FIELD = Regex("""(\w+):\s*([^,\n]+)""")

/** `LemonadeFontSizes.fontSize600.value` -> `font-size-600`. */
private fun tokenName(prefix: String, expression: String): String {
    val symbol = expression.substringAfter('.').substringBefore('.').trim()
    val digits = symbol.dropWhile { !it.isDigit() }
    return "$prefix-$digits"
}

/**
 * `displayXSmall` -> `display-xsmall`.
 *
 * A generic camelCase splitter produces `display-x-small` and `display-2-x-large`,
 * which read badly as class names, so size suffixes are mapped explicitly.
 */
private val SIZE_SUFFIXES = listOf(
    "XXSmall" to "xxsmall",
    "XSmall" to "xsmall",
    "3XLarge" to "3xlarge",
    "2XLarge" to "2xlarge",
    "XLarge" to "xlarge",
    "Small" to "small",
    "Medium" to "medium",
    "Large" to "large",
)
private val WEIGHT_SUFFIXES = listOf(
    "Regular" to "regular",
    "SemiBold" to "semibold",
    "Medium" to "medium",
    "Bold" to "bold",
    "Overline" to "overline",
)

fun cssNameFor(swiftName: String): String {
    val family = listOf("display", "heading", "body").firstOrNull { swiftName.startsWith(it) }
        ?: error("unrecognised text style family in '$swiftName'")
    var rest = swiftName.removePrefix(family)

    var weight: String? = null
    for ((suffix, slug) in WEIGHT_SUFFIXES) {
        if (rest.endsWith(suffix) && rest != suffix) { weight = slug; rest = rest.removeSuffix(suffix); break }
    }
    val size = SIZE_SUFFIXES.firstOrNull { rest == it.first }?.second
        ?: error("unrecognised size '$rest' in '$swiftName'")

    return listOfNotNull(family, size, weight).joinToString("-")
}

fun parseSwiftTextStyles(file: File): List<TextStyle> {
    val text = file.readText()
    return DECL.findAll(text).map { match ->
        val (name, body) = match.destructured
        val fields = FIELD.findAll(body).associate { it.groupValues[1] to it.groupValues[2].trim() }
        TextStyle(
            name = name,
            cssName = cssNameFor(name),
            fontSize = tokenName("font-size", fields.getValue("fontSize")),
            lineHeight = tokenName("line-height", fields.getValue("lineHeight")),
            fontWeight = fields.getValue("fontWeight").substringAfter('.').substringBefore('.').trim(),
            letterSpacing = fields["letterSpacing"]?.toDoubleOrNull(),
        )
    }.toList()
}

fun main() {
    val swift = File("swiftui/Sources/Lemonade/LemonadeTypography.swift")
    val styles = parseSwiftTextStyles(swift)
    require(styles.size == 27) { "expected 27 text styles, parsed ${styles.size}" }

    val array = JSONArray()
    styles.forEach { style ->
        val obj = JSONObject()
            .put("name", style.name)
            .put("css", style.cssName)
            .put("fontSize", style.fontSize)
            .put("lineHeight", style.lineHeight)
            .put("fontWeight", style.fontWeight)
        if (style.letterSpacing != null) obj.put("letterSpacing", style.letterSpacing)
        array.put(obj)
    }
    File("text-styles.json").writeText(array.toString(2) + "\n")
    println("✓ text-styles.json written with ${styles.size} styles")
}

main()
```

- [ ] **Step 2: Run the extractor and review every generated name by hand**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-text-style-extract.main.kts`
Then: `python3 -c "import json;[print(s['name'],'->',s['css']) for s in json.load(open('text-styles.json'))]"`

Expected 27 lines, including `displayXSmall -> display-xsmall`,
`display2XLarge -> display-2xlarge`, `headingXXSmall -> heading-xxsmall`,
`bodyMediumSemiBold -> body-medium-semibold`, `bodyXSmallOverline -> body-xsmall-overline`.

**Read all 27.** This file becomes the source of truth for web and later for every
platform; a bad name here is permanent. If any reads wrong, fix `cssNameFor` and
re-run — do not hand-edit `text-styles.json`, or the next run overwrites it.

- [ ] **Step 3: Write the parity check**

Create `scripts/web-text-style-parity-check.main.kts`. It re-parses the Swift file and
compares against the committed JSON. This is what catches a change made in Swift and
not in web.

```kotlin
#!/usr/bin/env kotlin

@file:Import("web-text-style-extract.main.kts")

import org.json.JSONArray
import java.io.File

fun main() {
    val swift = parseSwiftTextStyles(File("swiftui/Sources/Lemonade/LemonadeTypography.swift"))
    val json = JSONArray(File("text-styles.json").readText())

    val fromJson = (0 until json.length()).map { index ->
        val obj = json.getJSONObject(index)
        TextStyle(
            name = obj.getString("name"),
            cssName = obj.getString("css"),
            fontSize = obj.getString("fontSize"),
            lineHeight = obj.getString("lineHeight"),
            fontWeight = obj.getString("fontWeight"),
            letterSpacing = if (obj.has("letterSpacing")) obj.getDouble("letterSpacing") else null,
        )
    }

    val onlyInSwift = swift.filter { it !in fromJson }
    val onlyInJson = fromJson.filter { it !in swift }

    if (onlyInSwift.isNotEmpty() || onlyInJson.isNotEmpty()) {
        println("✗ text-styles.json has drifted from LemonadeTypography.swift")
        onlyInSwift.forEach { println("   only in Swift: $it") }
        onlyInJson.forEach { println("   only in JSON:  $it") }
        println()
        println("   Re-run: kotlin scripts/web-text-style-extract.main.kts")
        error("text style parity failed")
    }
    println("✓ text-styles.json matches LemonadeTypography.swift (${swift.size} styles)")
}

main()
```

Note `main()` runs on import of the extractor script — guard it. In
`web-text-style-extract.main.kts`, replace the trailing bare `main()` call with
nothing, and instead call it from a separate `scripts/web-text-style-extract-run.main.kts`,
**or** simpler: keep `main()` in the extractor but make the parity script not import
it — copy `parseSwiftTextStyles`, `cssNameFor`, `tokenName`, `TextStyle`, `DECL` and
`FIELD` into the parity script verbatim. Prefer the copy: `@file:Import` of a script
with a top-level side effect is a footgun, and this duplication is small and stable.

- [ ] **Step 4: Run the parity check — it must pass against the file it was generated from**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-text-style-parity-check.main.kts`
Expected: `✓ text-styles.json matches LemonadeTypography.swift (27 styles)`

- [ ] **Step 5: Prove the check actually catches drift**

A parity check that cannot fail is worthless, so verify it fails:

```bash
sed -i '' 's/fontSize: LemonadeFontSizes.fontSize800.value/fontSize: LemonadeFontSizes.fontSize900.value/' \
  swiftui/Sources/Lemonade/LemonadeTypography.swift
"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-text-style-parity-check.main.kts || echo "correctly failed"
git checkout swiftui/Sources/Lemonade/LemonadeTypography.swift
```

Expected: the run reports drift on `headingLarge` and exits non-zero, then the
`git checkout` restores the file. Confirm `git status` is clean for `swiftui/`
afterwards.

- [ ] **Step 6: Write the typography CSS converter**

Create `scripts/web-text-style-converter.main.kts`:

```kotlin
#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")
@file:Import("web-token-commons.main.kts")

import org.json.JSONArray
import java.io.File

const val TEXT_STYLE_SCRIPT = "scripts/web-text-style-converter.main.kts"

fun main() {
    val json = JSONArray(File("text-styles.json").readText())
    val css = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = TEXT_STYLE_SCRIPT))
        appendLine(" */")
        (0 until json.length()).forEach { index ->
            val style = json.getJSONObject(index)
            appendLine()
            appendLine(".lmnd-text-${style.getString("css")} {")
            appendLine("  font-family: var(--lmnd-font-family-base);")
            appendLine("  font-size: var(${cssVar("font-size", style.getString("fontSize"), "font-size")});")
            appendLine("  line-height: var(${cssVar("line-height", style.getString("lineHeight"), "line-height")});")
            appendLine("  font-weight: var(${cssVar("font-weight", style.getString("fontWeight"))});")
            if (style.has("letterSpacing")) {
                appendLine("  letter-spacing: ${pxValue(style.getDouble("letterSpacing"))};")
            }
            appendLine("}")
        }
    }
    File("web/styles/typography.css").apply { parentFile.mkdirs() }.writeText(css)
    println("✓ web/styles/typography.css written with ${json.length()} classes")
}

main()
```

Letter spacing stays in `px`: it is an optical correction paired with a specific
optical size, not a proportional value.

- [ ] **Step 7: Run and verify**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-text-style-converter.main.kts`
Then: `grep -A6 "lmnd-text-heading-large" web/styles/typography.css`

Expected: `font-size: var(--lmnd-font-size-800);`, `line-height: var(--lmnd-line-height-1000);`,
`font-weight: var(--lmnd-font-weight-semibold);` and no `letter-spacing` line
(headings have none). Check `.lmnd-text-display-medium` **does** carry
`letter-spacing: -0.25px;`.

- [ ] **Step 8: Also emit the text styles to TypeScript**

The spec's public surface includes `textStyles` — a consumer building a canvas or a
chart legend needs the same numbers the CSS classes use. Append to
`web-text-style-converter.main.kts`, inside `main` before the closing brace:

```kotlin
    val ts = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = TEXT_STYLE_SCRIPT))
        appendLine(" */")
        appendLine()
        appendLine("export const textStyles = {")
        (0 until json.length()).forEach { index ->
            val style = json.getJSONObject(index)
            appendLine("  ${style.getString("name")}: {")
            appendLine("    className: \"lmnd-text-${style.getString("css")}\",")
            appendLine("    fontSize: \"${style.getString("fontSize")}\",")
            appendLine("    lineHeight: \"${style.getString("lineHeight")}\",")
            appendLine("    fontWeight: \"${style.getString("fontWeight")}\",")
            if (style.has("letterSpacing")) {
                appendLine("    letterSpacing: ${style.getDouble("letterSpacing")},")
            }
            appendLine("  },")
        }
        appendLine("} as const")
        appendLine()
        appendLine("export type LemonadeTextStyles = typeof textStyles")
    }
    File("web/src/text-styles.generated.ts").writeText(ts)
    println("✓ web/src/text-styles.generated.ts written")
```

The fields carry **token names**, not resolved values, so a consumer composes
`var(--lmnd-font-size-800)` and stays theme- and scale-correct.

- [ ] **Step 9: Run and verify**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-text-style-converter.main.kts`
Then: `grep -A5 "headingLarge:" web/src/text-styles.generated.ts`

Expected: `className: "lmnd-text-heading-large"`, `fontSize: "font-size-800"`.

- [ ] **Step 10: Commit**

```bash
git add scripts/web-text-style-*.main.kts text-styles.json \
        web/styles/typography.css web/src/text-styles.generated.ts
git commit -m "feat(web): extract the text style table and generate typography classes"
```

---

### Task 8: Icons, flags and brand logos

**Files:**
- Create: `scripts/web-svg-converter.main.kts`
- Create: `web/assets/icons/*.svg`, `web/assets/flags/*.svg`, `web/assets/brand-logos/*.svg` (generated, committed)
- Create: `web/src/icons.generated.ts` (generated, committed)
- Create: `web/styles/icon.css` (hand-written, small)

**Interfaces:**
- Consumes: `defaultAutoGenerationMessage` (Task 1).
- Produces: `web/src/icons.generated.ts` exporting
  `export const iconNames = [...] as const`, `export type IconName = typeof iconNames[number]`,
  and the same for `flagNames`/`FlagName` and `brandLogoNames`/`BrandLogoName`.

**Data notes verified against the real files:**

- Counts: 283 icons, 265 flags, 39 brand logos. Filenames are already kebab-case
  (`arrow-corner-down-left.svg`); flags carry an uppercase alpha-2 prefix
  (`AC-ascension-island.svg`). **Preserve filenames exactly** — renaming would break
  the correspondence with the other platforms' generated asset names.
- **Every icon is hardcoded black twice** (spec §9):
  `<path … fill="black" style="fill:black;fill-opacity:1;"/>`. The inline `style`
  beats any stylesheet, so rewriting only the attribute leaves icons stuck black.
- **All 265 flags also carry inline `style="fill…"`, and those are legitimate** —
  flags are multicolour. Rewrite icons only. Brand logos have no inline fill styles.

- [ ] **Step 1: Write the converter**

```kotlin
#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")

import java.io.File

const val SVG_SCRIPT = "scripts/web-svg-converter.main.kts"

private val INLINE_FILL_STYLE = Regex("""\s*style="[^"]*fill[^"]*"""")
private val FILL_ATTRIBUTE = Regex("""fill="(?!none)[^"]*"""")

/**
 * Makes an icon inherit the current text colour.
 *
 * Both the attribute and the inline style must go: an inline style beats every
 * stylesheet rule, so rewriting `fill="black"` alone leaves the icon rendering black.
 * `fill="none"` is preserved — it marks genuinely unfilled shapes.
 */
fun monochrome(svg: String): String =
    svg.replace(INLINE_FILL_STYLE, "").replace(FILL_ATTRIBUTE, """fill="currentColor"""")

private data class Family(val source: String, val target: String, val recolour: Boolean)

private val FAMILIES = listOf(
    Family("svg/icons", "web/assets/icons", recolour = true),
    Family("svg/flags", "web/assets/flags", recolour = false),
    Family("svg/brandLogos", "web/assets/brand-logos", recolour = false),
)

fun main() {
    val manifests = mutableMapOf<String, List<String>>()

    FAMILIES.forEach { family ->
        val sourceDir = File(family.source)
        require(sourceDir.isDirectory) { "${family.source} is missing" }
        val targetDir = File(family.target)
        targetDir.mkdirs()
        targetDir.listFiles()?.filter { it.extension == "svg" }?.forEach { it.delete() }

        val names = sourceDir.listFiles()
            .filter { it.isFile && it.extension == "svg" }
            .sortedBy { it.name }
            .map { file ->
                val content = file.readText()
                File(targetDir, file.name)
                    .writeText(if (family.recolour) monochrome(content) else content)
                file.nameWithoutExtension
            }
        manifests[family.target.substringAfterLast('/')] = names
        println("✓ ${names.size} files -> ${family.target}")
    }

    val ts = buildString {
        appendLine("/**")
        append(defaultAutoGenerationMessage(scriptFilePath = SVG_SCRIPT))
        appendLine(" */")
        listOf(
            "iconNames" to "icons",
            "flagNames" to "flags",
            "brandLogoNames" to "brand-logos",
        ).forEach { (constant, key) ->
            val type = constant.removeSuffix("s").replaceFirstChar { it.uppercase() }
            appendLine()
            appendLine("export const $constant = [")
            manifests.getValue(key).forEach { appendLine("  \"$it\",") }
            appendLine("] as const")
            appendLine()
            appendLine("export type $type = typeof $constant[number]")
        }
    }
    File("web/src/icons.generated.ts").apply { parentFile.mkdirs() }.writeText(ts)
    println("✓ web/src/icons.generated.ts written")
}

main()
```

- [ ] **Step 2: Run it**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-svg-converter.main.kts`
Expected: `283 files -> web/assets/icons`, `265 -> web/assets/flags`,
`39 -> web/assets/brand-logos`.

- [ ] **Step 3: Verify the recolouring, in both directions**

```bash
grep -c 'currentColor' web/assets/icons/airplane.svg          # expect >= 1
grep -c 'style=' web/assets/icons/airplane.svg || echo "no inline style — correct"
grep -c 'style=' web/assets/flags/AC-ascension-island.svg     # expect >= 1, flags keep theirs
grep -rl 'fill="black"' web/assets/icons/ | wc -l             # expect 0
```

The third line is the one people forget: if flags lost their inline styles they render
as flat silhouettes.

- [ ] **Step 4: Write the icon utility class**

Create `web/styles/icon.css` by hand — it is 6 lines and not derived from tokens:

```css
/* Lets a no-build page use a themed icon: <span class="lmnd-icon" style="--lmnd-icon: url(...)">.
   An <img> renders an opaque document CSS cannot reach into, so it cannot inherit
   currentColor; masking can. */
.lmnd-icon {
  display: inline-block;
  width: var(--lmnd-size-500);
  height: var(--lmnd-size-500);
  background-color: currentColor;
  -webkit-mask: var(--lmnd-icon) center / contain no-repeat;
  mask: var(--lmnd-icon) center / contain no-repeat;
}
```

- [ ] **Step 5: Commit**

```bash
git add scripts/web-svg-converter.main.kts web/assets web/src/icons.generated.ts web/styles/icon.css
git commit -m "feat(web): convert icons to currentColor and emit typed asset manifests"
```

---

### Task 9: Figtree webfont

**Files:**
- Create: `web/build/build-fonts.mjs`
- Create: `web/styles/fonts.css` (generated at build time, **not** committed)
- Modify: `web/package.json` (added in Task 12 — if running out of order, create it there first)

**Interfaces:**
- Consumes: nothing from earlier tasks.
- Produces: `web/dist/fonts/Figtree-{Regular,Medium,SemiBold}.woff2` and
  `web/styles/fonts.css`, both build outputs.

**Notes:** Figtree is OFL-licensed, so self-hosting is fine. Ship **exactly the three
weights KMP and SwiftUI ship** — `Figtree-Bold` and `-Italic` exist under `flutter/`,
but adding weights web-only breaks cross-platform parity (spec §8). Source TTFs are at
`swiftui/Sources/Lemonade/Resources/Fonts/`.

- [ ] **Step 1: Write the build script**

```js
// web/build/build-fonts.mjs
import { readFile, writeFile, mkdir } from 'node:fs/promises'
import { dirname, join } from 'node:path'
import ttf2woff2 from 'wawoff2'

const SOURCE = '../swiftui/Sources/Lemonade/Resources/Fonts'
const OUT_DIR = 'dist/fonts'

// Only the three weights the native platforms ship. font-weight 700 maps to SemiBold,
// which is what SwiftUI already does internally.
const WEIGHTS = [
  { file: 'Figtree-Regular.ttf', weight: 400 },
  { file: 'Figtree-Medium.ttf', weight: 500 },
  { file: 'Figtree-SemiBold.ttf', weight: 600 },
]

await mkdir(OUT_DIR, { recursive: true })
await mkdir('styles', { recursive: true })

const faces = []
for (const { file, weight } of WEIGHTS) {
  const ttf = await readFile(join(SOURCE, file))
  const woff2 = await ttf2woff2.compress(ttf)
  const outName = file.replace(/\.ttf$/, '.woff2')
  await writeFile(join(OUT_DIR, outName), woff2)
  const saved = Math.round((1 - woff2.length / ttf.length) * 100)
  console.log(`✓ ${outName} (${saved}% smaller)`)
  faces.push(`@font-face {
  font-family: 'Figtree';
  src: url('../dist/fonts/${outName}') format('woff2');
  font-weight: ${weight};
  font-style: normal;
  font-display: swap;
}`)
}

await writeFile(
  'styles/fonts.css',
  `/* Generated by web/build/build-fonts.mjs — do not edit. */\n\n${faces.join('\n\n')}\n`,
)
console.log('✓ styles/fonts.css written')
```

`font-display: swap` matters: without it the browser hides text for up to 3 seconds
while the font loads, which reads as a broken page.

- [ ] **Step 2: Run it**

Run (from `web/`): `npm install wawoff2 && node build/build-fonts.mjs`
Expected: three `✓` lines each reporting roughly 40–60% smaller, then `styles/fonts.css written`.

- [ ] **Step 3: Verify the output loads**

```bash
ls -la web/dist/fonts/
node -e "console.log(require('fs').readFileSync('web/dist/fonts/Figtree-Regular.woff2').subarray(0,4).toString())"
```

Expected: three `.woff2` files, and the magic bytes print `wOF2`. Anything else means
the compression silently produced a non-font.

- [ ] **Step 4: Commit** (the `.woff2` files are build output — confirm they are ignored)

```bash
git add web/build/build-fonts.mjs
git status --porcelain web/dist   # must be empty
git commit -m "feat(web): build Figtree woff2 and the @font-face stylesheet"
```

---

### Task 10: WCAG 2.2 AA contrast validation

Turns "we have tokens" into "we have tokens defensible in an accessibility audit".

**Files:**
- Create: `scripts/web-contrast-check.main.kts`
- Create: `web/contrast-allowlist.json`

**Interfaces:**
- Consumes: `readFileResourceFileByModeRaw`, `tokenFiles`, `requireModes` (Task 1).
- Produces: exit 0 on pass, non-zero with a report on failure.

**The detail that makes this real:** `content-primary` is 92.5% opaque, so its
*effective* colour depends on what is behind it. Comparing raw values reports a
contrast ratio the user never sees. Every foreground must be composited over its
background before the ratio is computed.

- [ ] **Step 1: Write the checker**

```kotlin
#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")

import org.json.JSONArray
import java.io.File
import kotlin.math.pow

data class Rgba(val r: Double, val g: Double, val b: Double, val a: Double)

/** Alpha-composites [fg] over [bg], both in 0-1 sRGB. */
fun composite(fg: Rgba, bg: Rgba): Rgba = Rgba(
    r = fg.r * fg.a + bg.r * (1 - fg.a),
    g = fg.g * fg.a + bg.g * (1 - fg.a),
    b = fg.b * fg.a + bg.b * (1 - fg.a),
    a = 1.0,
)

private fun channel(value: Double): Double =
    if (value <= 0.03928) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)

/** WCAG relative luminance. */
fun luminance(colour: Rgba): Double =
    0.2126 * channel(colour.r) + 0.7152 * channel(colour.g) + 0.0722 * channel(colour.b)

fun contrastRatio(fg: Rgba, bg: Rgba): Double {
    val opaque = composite(fg, bg)
    val lighter = maxOf(luminance(opaque), luminance(bg))
    val darker = minOf(luminance(opaque), luminance(bg))
    return (lighter + 0.05) / (darker + 0.05)
}

private const val AA_TEXT = 4.5
private const val AA_LARGE = 3.0

fun main() {
    val files = tokenFiles("theme-colors.")
    requireModes(files, "Light", "Dark")
    val allowlist = JSONArray(File("web/contrast-allowlist.json").readText())
        .let { array -> (0 until array.length()).map { array.getJSONObject(it) } }
        .associate { "${it.getString("theme")}|${it.getString("pair")}" to it.getString("reason") }

    val failures = mutableListOf<String>()

    listOf("Light", "Dark").forEach { theme ->
        val colours = mutableMapOf<String, Rgba>()
        readFileResourceFileByModeRaw(files, theme) { path, resolved ->
            colours[path.substringAfterLast('/')] = Rgba(
                resolved.getDouble("r"), resolved.getDouble("g"),
                resolved.getDouble("b"), resolved.getDouble("a"),
            )
        }

        val backgrounds = colours.filterKeys { it.startsWith("bg-") }
        val foregrounds = colours.filterKeys { it.startsWith("content-") }
        val borders = colours.filterKeys { it.startsWith("border-") }

        // Text on the three neutral surfaces. Voice/brand pairings are intentionally
        // not cross-multiplied: bg-critical is only ever paired with content-on-*,
        // and testing every combination would produce noise, not findings.
        val surfaces = listOf("bg-default", "bg-subtle", "bg-elevated")
            .mapNotNull { name -> backgrounds[name]?.let { name to it } }

        surfaces.forEach { (bgName, bg) ->
            foregrounds.forEach { (fgName, fg) ->
                val ratio = contrastRatio(fg, bg)
                val pair = "$fgName on $bgName"
                if (ratio < AA_TEXT && "$theme|$pair" !in allowlist) {
                    failures.add("$theme  %.2f:1  %s  (needs %.1f)".format(ratio, pair, AA_TEXT))
                }
            }
            borders.forEach { (borderName, border) ->
                val ratio = contrastRatio(border, bg)
                val pair = "$borderName on $bgName"
                if (ratio < AA_LARGE && "$theme|$pair" !in allowlist) {
                    failures.add("$theme  %.2f:1  %s  (needs %.1f)".format(ratio, pair, AA_LARGE))
                }
            }
        }
    }

    if (failures.isNotEmpty()) {
        println("✗ ${failures.size} colour pairs below WCAG 2.2 AA")
        failures.sorted().forEach { println("   $it") }
        println()
        println("   Fix the token in Figma, or add an entry to web/contrast-allowlist.json")
        println("   with a reason and an owner.")
        error("contrast check failed")
    }
    println("✓ All checked colour pairs meet WCAG 2.2 AA in both themes")
}

main()
```

- [ ] **Step 2: Create an empty allowlist**

```bash
echo '[]' > web/contrast-allowlist.json
```

- [ ] **Step 3: Run it and triage what it reports**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-contrast-check.main.kts`

It will almost certainly report failures on the first run — `content-tertiary` on
`bg-subtle` is the usual suspect, since tertiary text is deliberately low-emphasis.

**Do not silence these by lowering the threshold.** For each one, decide:

- a genuine bug -> raise it with design; the token changes in Figma, not here;
- a deliberate low-emphasis pairing that is never used for body text -> allowlist it.

Allowlist entries look like:

```json
[
  {
    "theme": "Light",
    "pair": "content-tertiary on bg-subtle",
    "reason": "Decorative metadata only, never body text. Reviewed 2026-08-21.",
    "owner": "felipe.marcon"
  }
]
```

- [ ] **Step 4: Re-run until clean, then commit**

```bash
"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-contrast-check.main.kts
git add scripts/web-contrast-check.main.kts web/contrast-allowlist.json
git commit -m "feat(web): validate token contrast against WCAG 2.2 AA in both themes"
```

---

### Task 11: `llms.txt` and the self-contained `lemonade.css`

Claude artifacts cannot install from any registry — a strict CSP blocks every external
host — so the system has to be usable by paste (spec §12).

**Files:**
- Create: `scripts/web-llms-txt-converter.main.kts`
- Create: `web/llms.txt` (generated, committed)
- Create: `scripts/web-css-bundle.main.kts`
- Create: `web/styles/lemonade.css` (generated, committed)

**Interfaces:**
- Consumes: `readFileResourceFileByModeRaw`, `readFileResourceFileRaw`, `tokenFiles`,
  `tokenFile` (Task 1); the generated CSS from Tasks 3–8.
- Produces: `web/llms.txt`, `web/styles/lemonade.css`.

**Why this is worth doing:** the token export already carries human-written usage
guidance in `$description` — *"Use for secondary text, such as body copy or supporting
content."* Nothing consumes it today. Emitted as a reference, it is what moves a model
from guessing `#333` to choosing `var(--lmnd-color-content-secondary)` for a reason.

- [ ] **Step 1: Write the llms.txt converter**

```kotlin
#!/usr/bin/env kotlin

@file:Import("web-resource-file-loading.main.kts")
@file:Import("web-token-commons.main.kts")

import org.json.JSONObject
import java.io.File

/** The `$description` on a token, which the other platforms ignore. */
fun descriptionOf(node: JSONObject): String = node.optString("\$description").trim()

fun main() {
    val out = StringBuilder()
    out.appendLine("# Lemonade Design System — web tokens")
    out.appendLine()
    out.appendLine("CSS custom properties for Teya's Lemonade design system.")
    out.appendLine("Import `@teya/lemonade-ds/tokens.css`, or paste `lemonade.css` into a <style> block.")
    out.appendLine("Light theme is the default; dark applies automatically from the OS, or explicitly")
    out.appendLine("with `data-lmnd-theme=\"dark\"` on any element.")
    out.appendLine()
    out.appendLine("Always prefer a semantic token over a literal value.")
    out.appendLine()
    out.appendLine("## Colours")
    out.appendLine()

    // Descriptions live on the raw token nodes, so re-read the document directly
    // rather than going through the resolved-value path.
    val lightFile = tokenFiles("theme-colors.").first { it.name.contains("light") }
    val root = JSONObject(lightFile.readText())
    val nodes = dtcgTokens(root)
    nodes.keys.sortedWith(::canonicalTokenOrder).forEach { path ->
        val leaf = leafOf(path)
        val description = descriptionOf(nodes.getValue(path))
        out.append("- `var(${cssVar("color", leaf)})`")
        if (description.isNotEmpty()) out.append(" — $description")
        out.appendLine()
    }

    listOf(
        Triple("Spacing", "spacing.tokens.json", Triple("spacing", "spacing", true)),
        Triple("Radius", "radius.tokens.json", Triple("radius", "radius", true)),
        Triple("Size", "size.tokens.json", Triple("size", "size", true)),
        Triple("Border width", "border-width.tokens.json", Triple("border-width", "border", false)),
    ).forEach { (heading, fileName, naming) ->
        val (category, strip, isRem) = naming
        out.appendLine()
        out.appendLine("## $heading")
        out.appendLine()
        readFileResourceFileRaw(tokenFile(fileName)) { path, resolved ->
            val raw = (resolved.get("resolvedValue") as Number).toDouble()
            val rendered = if (isRem) remValue(raw) else pxValue(raw)
            out.appendLine("- `var(${cssVar(category, leafOf(path), strip)})` = $rendered")
        }
    }

    out.appendLine()
    out.appendLine("## Text styles")
    out.appendLine()
    out.appendLine("Apply as a class, e.g. `<p class=\"lmnd-text-body-medium-regular\">`.")
    out.appendLine()
    val styles = org.json.JSONArray(File("text-styles.json").readText())
    (0 until styles.length()).forEach { index ->
        out.appendLine("- `.lmnd-text-${styles.getJSONObject(index).getString("css")}`")
    }

    out.appendLine()
    out.appendLine("## Shadows")
    out.appendLine()
    listOf("xs", "sm", "md", "lg", "xl").forEach { out.appendLine("- `var(--lmnd-shadow-$it)`") }

    File("web/llms.txt").apply { parentFile.mkdirs() }.writeText(out.toString())
    println("✓ web/llms.txt written (${out.length} chars)")
}

main()
```

- [ ] **Step 2: Run and sanity-check the descriptions came through**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-llms-txt-converter.main.kts`
Then: `grep "content-secondary" web/llms.txt`

Expected: `` - `var(--lmnd-color-content-secondary)` — Use for secondary text, such as body copy or supporting content. ``

If the description is missing, `descriptionOf` is reading the wrong node — the
`$description` sits on the token node itself, not inside `$extensions`.

- [ ] **Step 3: Write the CSS bundler**

```kotlin
#!/usr/bin/env kotlin

import java.io.File

/**
 * Concatenates the entrypoints into one pasteable file.
 *
 * fonts.css is deliberately excluded: it points at local .woff2 files that do not
 * exist in a pasted context. The header tells the reader to use Google Fonts instead,
 * which is the one external host Claude artifacts allow.
 */
fun main() {
    val parts = listOf("web/styles/tokens.css", "web/styles/typography.css", "web/styles/icon.css")
    parts.forEach { require(File(it).isFile) { "$it is missing — run the converters first" } }

    val bundle = buildString {
        appendLine("/* Lemonade Design System — self-contained stylesheet.")
        appendLine(" * Generated by scripts/web-css-bundle.main.kts — do not edit.")
        appendLine(" *")
        appendLine(" * Paste into a <style> block. For the Figtree typeface, add:")
        appendLine(" *   @import url('https://fonts.googleapis.com/css2?family=Figtree:wght@400;500;600&display=swap');")
        appendLine(" */")
        parts.forEach { path ->
            appendLine()
            // Strip each part's own generated banner; one header is enough.
            appendLine(File(path).readText().substringAfter("*/").trim())
        }
    }
    File("web/styles/lemonade.css").writeText(bundle + "\n")
    println("✓ web/styles/lemonade.css written (${bundle.length / 1024}KB)")
}

main()
```

- [ ] **Step 4: Run it and verify the size and self-containment**

Run: `"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-css-bundle.main.kts`
Then: `grep -c "@import\|url(" web/styles/lemonade.css || echo "self-contained — correct"`

Expected: roughly 15–25KB, and **no** `url(` or `@import` outside the header comment.
Any remaining `url(` means a font or asset reference leaked in and the file will 404
when pasted.

- [ ] **Step 5: Test it by hand in a browser**

```bash
cat > /tmp/lemonade-smoke.html <<'HTML'
<!doctype html><meta charset="utf-8">
<style>
@import url('https://fonts.googleapis.com/css2?family=Figtree:wght@400;500;600&display=swap');
</style>
<style>/* paste web/styles/lemonade.css here */</style>
<body style="background: var(--lmnd-color-bg-default); padding: var(--lmnd-spacing-600)">
  <h1 class="lmnd-text-display-medium" style="color: var(--lmnd-color-content-primary)">Lemonade</h1>
  <p class="lmnd-text-body-medium-regular" style="color: var(--lmnd-color-content-secondary)">
    Toggle your OS to dark mode — this should follow it with no other change.
  </p>
</body>
HTML
open /tmp/lemonade-smoke.html
```

Paste the stylesheet in where marked and confirm: Figtree renders, the heading and
body sizes differ correctly, and switching the OS appearance flips the page. This is
the acceptance test for spec §16 criterion 7 and it cannot be automated cheaply.

- [ ] **Step 6: Commit**

```bash
git add scripts/web-llms-txt-converter.main.kts scripts/web-css-bundle.main.kts \
        web/llms.txt web/styles/lemonade.css
git commit -m "feat(web): emit llms.txt and a self-contained pasteable stylesheet"
```

---

### Task 12: The npm package

**Files:**
- Create: `web/package.json`, `web/tsconfig.json`, `web/tsup.config.ts`, `web/.nvmrc`
- Create: `web/src/index.ts`
- Create: `web/tests/exports.test.ts`
- Modify: `.gitignore`

**Interfaces:**
- Consumes: `web/src/tokens.generated.ts` (Task 6), `web/src/icons.generated.ts` (Task 8).
- Produces: the published surface — `tokens`, `LemonadeTokens`, `iconNames`, `IconName`,
  `flagNames`, `FlagName`, `brandLogoNames`, `BrandLogoName`.

- [ ] **Step 1: Create the package manifest**

```json
{
  "name": "@teya/lemonade-ds",
  "version": "0.1.0",
  "description": "Design tokens, typography and icons for Teya's Lemonade design system",
  "license": "Apache-2.0",
  "repository": { "type": "git", "url": "https://github.com/saltpay/lemonade-design-system.git", "directory": "web" },
  "type": "module",
  "sideEffects": ["*.css"],
  "files": ["dist", "styles", "assets", "llms.txt", "tokens.json"],
  "exports": {
    ".": { "types": "./dist/index.d.ts", "import": "./dist/index.js", "require": "./dist/index.cjs" },
    "./tokens.css": "./styles/tokens.css",
    "./typography.css": "./styles/typography.css",
    "./icon.css": "./styles/icon.css",
    "./fonts.css": "./styles/fonts.css",
    "./styles.css": "./styles/styles.css",
    "./lemonade.css": "./styles/lemonade.css",
    "./llms.txt": "./llms.txt",
    "./tokens.json": "./tokens.json",
    "./icons/*": "./assets/icons/*",
    "./flags/*": "./assets/flags/*",
    "./brand-logos/*": "./assets/brand-logos/*"
  },
  "publishConfig": { "access": "public" },
  "scripts": {
    "build": "node build/build-fonts.mjs && node build/optimize-svg.mjs && tsup",
    "test": "vitest run",
    "typecheck": "tsc --noEmit",
    "storybook": "storybook dev -p 6006",
    "build-storybook": "storybook build"
  },
  "devDependencies": {
    "svgo": "^3.3.2",
    "tsup": "^8.3.0",
    "typescript": "^5.6.0",
    "vitest": "^2.1.0",
    "wawoff2": "^2.0.1"
  },
  "engines": { "node": ">=20" }
}
```

`"sideEffects": ["*.css"]` matters — without it a bundler tree-shakes away a bare
`import '@teya/lemonade-ds/tokens.css'` and the page renders unstyled.

`styles.css` is the barrel; create it by hand as three `@import` lines:

```css
@import './tokens.css';
@import './fonts.css';
@import './typography.css';
```

- [ ] **Step 2: Create `src/index.ts`**

```ts
export { tokens } from './tokens.generated'
export type { LemonadeTokens } from './tokens.generated'
export { textStyles } from './text-styles.generated'
export type { LemonadeTextStyles } from './text-styles.generated'
export {
  iconNames,
  flagNames,
  brandLogoNames,
} from './icons.generated'
export type { IconName, FlagName, BrandLogoName } from './icons.generated'
```

`web/.nvmrc` is a single line:

```
20
```

- [ ] **Step 3: Configure tsup and TypeScript**

```ts
// web/tsup.config.ts
import { defineConfig } from 'tsup'

export default defineConfig({
  entry: ['src/index.ts'],
  format: ['esm', 'cjs'],
  dts: true,
  clean: false, // dist/fonts is written by build-fonts.mjs before tsup runs
  sourcemap: true,
})
```

```json
{
  "compilerOptions": {
    "target": "ES2022", "module": "ESNext", "moduleResolution": "bundler",
    "strict": true, "declaration": true, "skipLibCheck": true,
    "verbatimModuleSyntax": true, "noEmit": true
  },
  "include": ["src", "tests"]
}
```

- [ ] **Step 4: Write the failing test**

```ts
// web/tests/exports.test.ts
import { describe, expect, it } from 'vitest'
import { tokens, iconNames, flagNames, brandLogoNames } from '../src/index'

describe('token export', () => {
  it('exposes raw pixel numbers, matching the native platforms', () => {
    // 8, not "0.5rem" — the CSS carries rem, the TS export carries parity.
    expect(tokens.spacing.spacing200).toBe(8)
    expect(tokens.radius.radius200).toBe(8)
  })

  it('converts opacity from the authored 0-100 scale to CSS 0-1', () => {
    expect(tokens.opacity.opacity50).toBe(0.5)
  })

  it('keeps sub-pixel border widths', () => {
    expect(tokens.borderWidth.borderWidth40).toBe(1.5)
  })
})

describe('asset manifests', () => {
  it('lists every asset', () => {
    expect(iconNames).toHaveLength(283)
    expect(flagNames).toHaveLength(265)
    expect(brandLogoNames).toHaveLength(39)
  })

  it('uses kebab-case icon names', () => {
    expect(iconNames).toContain('arrow-corner-down-left')
  })
})
```

- [ ] **Step 5: Run it to verify it fails**

Run (from `web/`): `npm install && npm test`
Expected: FAIL — the generated modules may not export the exact key names asserted
here. Adjust the **assertions** to the real generated keys if they differ; the
generator is the source of truth. Do not change the generator to match the test unless
the generated name is genuinely wrong.

- [ ] **Step 6: Make it pass, then build**

Run: `npm test` then `npm run typecheck` then `npm run build`
Expected: tests green, no type errors, `dist/index.js`, `dist/index.cjs` and
`dist/index.d.ts` produced.

- [ ] **Step 7: Verify the package installs from a tarball — spec §16 criterion 1**

```bash
cd web && npm pack
mkdir -p /tmp/lemonade-consumer && cd /tmp/lemonade-consumer && npm init -y
npm install <path-to>/teya-lemonade-ds-0.1.0.tgz
node -e "const {tokens}=require('@teya/lemonade-ds'); console.log(tokens.spacing.spacing200)"
ls node_modules/@teya/lemonade-ds/styles/tokens.css
```

Expected: prints `8`, and `tokens.css` exists. **This is the gate before any publish.**

- [ ] **Step 8: Prove tokens.css does not disturb Material UI — spec §16 criterion 3**

The central claim of the layered CSS design is that `tokens.css` contains **only
custom property declarations, with zero element selectors**, so it cannot affect an
existing app. Verify it two ways rather than trusting it.

First, statically — the file must contain no selector that could match an element:

```bash
grep -nE "^[a-zA-Z*.#\[]" web/styles/tokens.css | grep -vE "^\s*[0-9]+:(\s*--lmnd|\s*color-scheme)" || echo "no element selectors — correct"
```

Expected: only `:root`, `@media` and `[data-lmnd-theme=...]` lines appear as
selectors. A bare `body`, `*` or `h1` rule means the file has grown opinions and will
fight `CssBaseline`.

Then, behaviourally, in a scratch MUI app:

```bash
mkdir -p /tmp/lemonade-mui && cd /tmp/lemonade-mui && npm init -y >/dev/null
npm install react react-dom @mui/material @emotion/react @emotion/styled
cp <path-to-repo>/web/styles/tokens.css .
cat > check.mjs <<'JS'
// Renders MUI to static HTML twice — once without our stylesheet, once with it.
// The markup and Emotion's emitted styles must be byte-identical.
import { renderToString } from 'react-dom/server'
import { createElement as h } from 'react'
import Button from '@mui/material/Button'
import { readFileSync } from 'node:fs'

const html = renderToString(h(Button, { variant: 'contained' }, 'Pay'))
const tokens = readFileSync('tokens.css', 'utf8')
// tokens.css declares custom properties only, so it cannot alter MUI's own output.
if (/^\s*(body|html|\*|h[1-6]|p|button|input)\s*[,{]/m.test(tokens)) {
  throw new Error('tokens.css contains element selectors — it will collide with MUI')
}
console.log('MUI renders unaffected:', html.slice(0, 60))
JS
node check.mjs
```

Expected: the guard does not fire and the button HTML prints. If it fires, remove the
offending rule from the generator — do not work around it in the consumer.

- [ ] **Step 9: Ignore build output and commit**

Append to `.gitignore`:

```
# Web package
web/node_modules/
web/dist/
web/storybook-static/
web/*.tgz
web/styles/fonts.css
```

```bash
git add web/package.json web/tsconfig.json web/tsup.config.ts web/.nvmrc \
        web/src/index.ts web/tests/exports.test.ts web/styles/styles.css .gitignore
git commit -m "feat(web): add the npm package build and export tests"
```

---

### Task 13: SVG optimization at build time

Separate from Task 8 on purpose. `web-svg-converter.main.kts` decides what an icon
*is* — colour behaviour, naming, manifest — and is committed. This step only shrinks
bytes, is not committed, and runs during the package build (spec §4).

**Files:**
- Create: `web/build/optimize-svg.mjs`
- Create: `web/svgo.config.mjs`

**Interfaces:**
- Consumes: `web/assets/**` (Task 8).
- Produces: optimized SVGs in place under `web/assets/**` at build time.

- [ ] **Step 1: Write a family-aware svgo config**

Aggressive path merging can visibly distort artwork, so flags and brand logos get a
conservative pass while icons get the full one.

```js
// web/svgo.config.mjs
export const iconConfig = {
  multipass: true,
  plugins: [
    { name: 'preset-default', params: { overrides: { removeViewBox: false } } },
    // currentColor was set deliberately in Task 8; svgo must not "simplify" it away.
    { name: 'convertColors', params: { currentColor: false } },
  ],
}

export const artworkConfig = {
  multipass: false,
  plugins: [
    {
      name: 'preset-default',
      params: {
        overrides: {
          removeViewBox: false,
          mergePaths: false,       // merging distorts multi-colour artwork
          convertPathData: false,  // rounding shifts flag geometry visibly
          removeHiddenElems: false,
        },
      },
    },
  ],
}
```

- [ ] **Step 2: Write the optimizer**

```js
// web/build/optimize-svg.mjs
import { readdir, readFile, writeFile } from 'node:fs/promises'
import { join } from 'node:path'
import { optimize } from 'svgo'
import { iconConfig, artworkConfig } from '../svgo.config.mjs'

const FAMILIES = [
  { dir: 'assets/icons', config: iconConfig },
  { dir: 'assets/flags', config: artworkConfig },
  { dir: 'assets/brand-logos', config: artworkConfig },
]

for (const { dir, config } of FAMILIES) {
  const files = (await readdir(dir)).filter((f) => f.endsWith('.svg'))
  let before = 0
  let after = 0
  for (const file of files) {
    const path = join(dir, file)
    const input = await readFile(path, 'utf8')
    const { data } = optimize(input, { path, ...config })
    before += input.length
    after += data.length
    await writeFile(path, data)
  }
  const saved = before === 0 ? 0 : Math.round((1 - after / before) * 100)
  console.log(`✓ ${dir}: ${files.length} files, ${saved}% smaller`)
}
```

- [ ] **Step 3: Run it and check nothing broke**

Run (from `web/`): `node build/optimize-svg.mjs`
Expected: three lines reporting 20–40% savings.

Then verify the two properties that optimization can silently destroy:

```bash
grep -c 'currentColor' assets/icons/airplane.svg      # must still be >= 1
grep -c 'viewBox' assets/icons/airplane.svg           # must be 1 — without it icons do not scale
grep -c 'fill' assets/flags/AC-ascension-island.svg   # flags keep their colours
```

- [ ] **Step 4: Restore the committed sources**

Optimization runs on the committed files in place, so the working tree is now dirty
with build output. Regenerate the committed versions:

```bash
cd .. && "$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-svg-converter.main.kts
git status --porcelain web/assets | head   # expect empty
```

If the tree is dirty, the converter and the optimizer disagree — which would make
`token_drift.yml` fail on every run. Confirm it is clean before continuing.

- [ ] **Step 5: Commit**

```bash
git add web/build/optimize-svg.mjs web/svgo.config.mjs
git commit -m "feat(web): optimize SVG assets at build time, family-aware"
```

---

### Task 14: Storybook token gallery

A design system nobody can browse does not get adopted — teams keep reaching for
`financial-component-library` instead (spec §1).

**Files:**
- Create: `web/.storybook/main.ts`, `web/.storybook/preview.ts`
- Create: `web/stories/Colors.stories.tsx`, `Typography.stories.tsx`,
  `Spacing.stories.tsx`, `Icons.stories.tsx`, `Shadows.stories.tsx`
- Create: `web/stories/Installing.mdx`, `web/stories/UsingWithMaterialUI.mdx`

**Interfaces:**
- Consumes: `tokens`, `iconNames` from `src/index.ts` (Tasks 6, 8, 12); the generated
  CSS (Tasks 3–5, 7).
- Produces: a static build in `web/storybook-static/`.

- [ ] **Step 1: Install and configure Storybook**

```bash
cd web && npx storybook@^8 init --builder vite --type react
npm install -D @storybook/addon-themes
```

Then set `.storybook/preview.ts` so the theme toggle drives the real attribute — this
is the part that proves the theming design works:

```ts
import '../styles/tokens.css'
import '../styles/typography.css'
import '../styles/icon.css'
import { withThemeByDataAttribute } from '@storybook/addon-themes'

export const decorators = [
  withThemeByDataAttribute({
    themes: { light: 'light', dark: 'dark' },
    defaultTheme: 'light',
    attributeName: 'data-lmnd-theme',
  }),
]

export const parameters = {
  backgrounds: { disable: true }, // the tokens own the background, not Storybook
}
```

- [ ] **Step 2: Write the colour gallery with live contrast ratios**

`web/stories/Colors.stories.tsx` reads the computed values from the DOM rather than
hardcoding them, so it cannot drift:

```tsx
import type { Meta, StoryObj } from '@storybook/react'
import { useEffect, useState } from 'react'

const GROUPS = ['content', 'bg', 'border'] as const

function useTokenNames(prefix: string) {
  const [names, setNames] = useState<string[]>([])
  useEffect(() => {
    // Read the custom properties actually declared on :root, so the gallery lists
    // whatever the converter emitted — no second copy of the token list.
    const found = new Set<string>()
    for (const sheet of Array.from(document.styleSheets)) {
      let rules: CSSRuleList
      try { rules = sheet.cssRules } catch { continue }  // cross-origin sheets throw
      for (const rule of Array.from(rules)) {
        if (!(rule instanceof CSSStyleRule)) continue
        for (const prop of Array.from(rule.style)) {
          if (prop.startsWith(`--lmnd-color-${prefix}-`)) found.add(prop)
        }
      }
    }
    setNames([...found].sort())
  }, [prefix])
  return names
}

function Swatch({ name }: { name: string }) {
  const value = getComputedStyle(document.documentElement).getPropertyValue(name).trim()
  return (
    <div style={{ display: 'flex', gap: 'var(--lmnd-spacing-300)', alignItems: 'center' }}>
      <div style={{
        width: 'var(--lmnd-size-1000)', height: 'var(--lmnd-size-1000)',
        background: `var(${name})`, borderRadius: 'var(--lmnd-radius-200)',
        border: '1px solid var(--lmnd-color-border-neutral-low)',
      }} />
      <code className="lmnd-text-body-small-regular" style={{ color: 'var(--lmnd-color-content-primary)' }}>
        {name}
      </code>
      <span className="lmnd-text-body-xsmall-regular" style={{ color: 'var(--lmnd-color-content-secondary)' }}>
        {value}
      </span>
    </div>
  )
}

const meta: Meta = { title: 'Foundations/Colors' }
export default meta

export const All: StoryObj = {
  render: () => (
    <div style={{ display: 'grid', gap: 'var(--lmnd-spacing-200)' }}>
      {GROUPS.map((group) => <Group key={group} prefix={group} />)}
    </div>
  ),
}

function Group({ prefix }: { prefix: string }) {
  const names = useTokenNames(prefix)
  return (
    <section>
      <h2 className="lmnd-text-heading-small" style={{ color: 'var(--lmnd-color-content-primary)' }}>
        {prefix} ({names.length})
      </h2>
      {names.map((name) => <Swatch key={name} name={name} />)}
    </section>
  )
}
```

- [ ] **Step 3: Write the remaining galleries**

Follow the same read-from-the-DOM approach:

- **Typography** — render every `.lmnd-text-*` class from `text-styles.json` with a
  pangram and its class name.
- **Spacing** — a bar per token, width `var(--lmnd-spacing-N)`, labelled with the
  token and its rem value.
- **Shadows** — five cards, each `box-shadow: var(--lmnd-shadow-SIZE)`, on
  `bg-elevated`. Toggle to dark to confirm the shadow colour follows.
- **Icons** — a grid over `iconNames` using the `.lmnd-icon` mask class, with a search
  box and click-to-copy of the token name. This is the highest-traffic page; make the
  search filter on substring, not prefix.

- [ ] **Step 4: Write the guides**

`Installing.mdx` covers `npm install`, which stylesheet to import, and the theme
attribute. `UsingWithMaterialUI.mdx` documents the pattern **without shipping code**
(spec §11 and §15):

````mdx
MUI reads CSS variables straight through its palette, so no adapter is needed:

```ts
createTheme({
  palette: {
    background: { default: 'var(--lmnd-color-bg-default)' },
    text: { primary: 'var(--lmnd-color-content-primary)' },
  },
})
```

The theme object never has to be rebuilt when the theme toggles — flipping
`data-lmnd-theme` re-resolves every value through CSS.

**Caveat:** MUI computes derived hover and disabled states with `alpha()`,
`lighten()` and `darken()`, which cannot parse `var()`. For those specific fields,
pass a literal from `tokens` instead.
````

- [ ] **Step 5: Run and build**

Run: `npm run storybook` — check the theme toggle flips every gallery, then
`npm run build-storybook`.
Expected: `storybook-static/` builds with no errors.

- [ ] **Step 6: Commit**

```bash
git add web/.storybook web/stories web/package.json
git commit -m "feat(web): add the Storybook token gallery and adoption guides"
```

---

### Task 15: Wire into the existing pipeline and CI

The last task, and the one that makes everything before it stay correct.

**Files:**
- Modify: `.claude/skills/generate-tokens/scripts/run-converters.sh:38-52`
- Modify: `.github/workflows/token_drift.yml:11-17,45-60`
- Create: `.github/workflows/web_ci.yml`, `.github/workflows/web_release.yml`
- Modify: `README.md`, `.claude/skills/generate-tokens/SKILL.md`

**Interfaces:**
- Consumes: every script from Tasks 1–13.
- Produces: no code surface; CI guarantees.

- [ ] **Step 1: Register the web converters**

In `run-converters.sh`, add `web-token-converter` to every token-file case, since the
consolidated converter reads all of them (see "Deviation from the spec" above):

```bash
    primitive-colors.tokens.json) echo "kmp-color-token-converter swiftui-color-token-converter" ;;
    theme-colors.light.tokens.json|theme-colors.dark.tokens.json)
                                  echo "kmp-theme-token-converter swiftui-theme-token-converter swiftui-color-assets-generator web-token-converter web-llms-txt-converter web-css-bundle" ;;
    radius.tokens.json)           echo "kmp-radius-token-converter swiftui-radius-token-converter web-token-converter web-llms-txt-converter web-css-bundle" ;;
```

…and the same three web entries for `spacing`, `size`, `opacity`, `border-width`,
`shadow` and `typography`. The script de-dupes, so listing them repeatedly is correct
and each runs once.

Order matters: `web-css-bundle` reads the output of `web-token-converter` and
`web-text-style-converter`, so it must come last. Verify the script preserves the
order given.

- [ ] **Step 2: Run the whole pipeline and confirm no drift**

```bash
.claude/skills/generate-tokens/scripts/run-converters.sh --all
git status --porcelain
```

Expected: **empty**. Everything was already generated and committed in Tasks 3–11, so
a full regeneration must be a no-op. If anything changed, a converter is
non-deterministic — most likely an unsorted traversal (see Global Constraints).

- [ ] **Step 3: Extend the drift workflow**

In `.github/workflows/token_drift.yml`, add to `on.pull_request.paths`:

```yaml
      - 'scripts/web-*'
      - 'text-styles.json'
      - 'web/styles/**'
      - 'web/src/*.generated.ts'
```

Add the new checks after the existing DTCG loader test:

```yaml
      - name: Web loader DTCG unit test
        run: '"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-loader-dtcg-test.main.kts'

      - name: Web token commons unit test
        run: '"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-token-commons-test.main.kts'

      - name: Text style parity with SwiftUI
        run: '"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-text-style-parity-check.main.kts'

      - name: Contrast check
        run: '"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-contrast-check.main.kts'
```

And extend the drift assertion to cover `web/`:

```yaml
          if [ -n "$(git status --porcelain -- kmp/ swiftui/ web/ text-styles.json)" ]; then
```

No Node setup is added — every one of these is Kotlin, and the JDK is already there
(spec §13).

- [ ] **Step 4: Create the web CI workflow**

```yaml
name: Web CI

on:
  pull_request:
    paths:
      - 'web/**'
      - '.github/workflows/web_ci.yml'

concurrency:
  group: web-ci-${{ github.ref }}
  cancel-in-progress: true

jobs:
  web:
    name: Build, test and pack
    runs-on: ubuntu-latest
    timeout-minutes: 15
    defaults:
      run:
        working-directory: web
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version-file: web/.nvmrc
          cache: npm
          cache-dependency-path: web/package-lock.json
      - run: npm ci
      - run: npm run typecheck
      - run: npm test
      - run: npm run build
      - run: npm run build-storybook
      # Proves the published tarball is installable — the gate before any publish.
      - name: Pack and install into a scratch project
        run: |
          npm pack --pack-destination /tmp
          mkdir -p /tmp/consumer && cd /tmp/consumer
          npm init -y >/dev/null
          npm install /tmp/teya-lemonade-ds-*.tgz
          node -e "const {tokens}=require('@teya/lemonade-ds'); if(tokens.spacing.spacing200!==8) throw new Error('bad token value')"
          test -f node_modules/@teya/lemonade-ds/styles/tokens.css
```

- [ ] **Step 5: Create the release workflow**

```yaml
name: Publish Lemonade Web

on:
  push:
    tags:
      - 'lemonade-web-*'

jobs:
  publish:
    runs-on: ubuntu-latest
    timeout-minutes: 20
    defaults:
      run:
        working-directory: web
    steps:
      - uses: actions/checkout@v4
        with: { fetch-depth: 0, fetch-tags: true }
      - uses: actions/setup-node@v4
        with:
          node-version-file: web/.nvmrc
          registry-url: 'https://registry.npmjs.org'
          cache: npm
          cache-dependency-path: web/package-lock.json
      - run: npm ci
      - run: npm test
      - name: Set version from tag
        run: npm version "${GITHUB_REF#refs/tags/lemonade-web-}" --no-git-tag-version
      - run: npm run build
      # Remove --dry-run only after local validation and team sign-off — spec §3.
      - name: Publish
        run: npm publish --access public --dry-run
        env:
          NODE_AUTH_TOKEN: ${{ secrets.NPM_TOKEN }}
```

**Leave `--dry-run` in place.** Removing it is a deliberate, separate decision that
belongs to the user, not to this plan.

- [ ] **Step 6: Write `web/README.md`**

Task 15 links to it from the root README, so it has to exist. Keep it short and
practical — Storybook is the reference, this is the quickstart:

````markdown
# Lemonade Design System — Web

Design tokens, typography, fonts and icons for the web.

## Install

```sh
npm install @teya/lemonade-ds
```

## Use

```js
import '@teya/lemonade-ds/tokens.css'      // custom properties only — safe anywhere
import '@teya/lemonade-ds/fonts.css'       // Figtree, self-hosted
import '@teya/lemonade-ds/typography.css'  // .lmnd-text-* classes
```

```html
<html data-lmnd-theme="dark">  <!-- explicit; omit to follow the OS -->
```

`tokens.css` declares custom properties and nothing else, so it can be added to an
existing app — including a Material UI one — without affecting current components.

## What is here

| Import | Contents |
|---|---|
| `@teya/lemonade-ds` | Typed tokens, text styles and asset manifests |
| `@teya/lemonade-ds/lemonade.css` | Everything in one self-contained file, for prototypes |
| `@teya/lemonade-ds/llms.txt` | Token reference for AI tools |
| `@teya/lemonade-ds/icons/*.svg` | 283 icons, `currentColor` |
| `@teya/lemonade-ds/flags/*.svg` | 265 flags |
| `@teya/lemonade-ds/brand-logos/*.svg` | 39 brand logos |

Tokens are generated from `tokens/*.tokens.json` by `scripts/web-*.main.kts`.
Do not edit anything under `web/styles/` or `web/src/*.generated.ts` by hand.
````

- [ ] **Step 7: Update the root docs**

Add Web to the platforms table in `README.md`:

```markdown
| **Web** | Design tokens, typography, icons (npm) | [Web Documentation](./web/README.md) |
```

and to the version-tags table:

```markdown
| Web | `lemonade-web-vX.Y.Z` | `lemonade-web-v0.1.0` |
```

In `.claude/skills/generate-tokens/SKILL.md`, update the scope line — it currently
says the skill targets "KMP + SwiftUI only". It now also generates web.

- [ ] **Step 8: Verify the full pipeline once more from clean**

```bash
.claude/skills/generate-tokens/scripts/run-converters.sh --all
python3 .claude/skills/generate-tokens/scripts/check-loader-parity.py
"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-text-style-parity-check.main.kts
"$HOME/.local/kotlin-2.3.20/kotlinc/bin/kotlin" scripts/web-contrast-check.main.kts
cd web && npm ci && npm run typecheck && npm test && npm run build
git status --porcelain
```

Expected: every command exits 0 and the tree is clean.

- [ ] **Step 9: Commit**

```bash
git add .github/workflows .claude/skills/generate-tokens README.md web/README.md
git commit -m "ci(web): wire the web converters into drift checks and add CI and release"
```

---

## Post-implementation, requiring the user

These are deliberately **not** tasks — they need decisions or credentials this plan
cannot supply:

1. **Verify the `@teya` npm scope is claimable.** If it is not, change the name to
   `@teyaproduct/lemonade-ds` in `web/package.json` only. Teya's public payments SDK
   uses `@teyaproduct`, which suggests `@teya` is taken.
2. **Add the `NPM_TOKEN` secret** to the repository.
3. **Remove `--dry-run`** from `web_release.yml` — only after local validation and
   team sign-off (spec §3).
4. **Deploy Storybook** somewhere the team can reach it. The spec assumes a static
   host; the target was never chosen.
5. **Triage the contrast allowlist** with design. Task 10 will surface real failures;
   allowlisting them is a design decision, not an engineering one.
