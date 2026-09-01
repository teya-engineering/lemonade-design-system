---
name: export-icons
description: >
  Export new or updated icons from the Lemonade DS Figma icons file into
  `svg/icons/`, then regenerate the KMP drawables, SwiftUI imagesets, the icon
  enums and the API baselines. Use when the user asks to "add an icon", "export
  icons from Figma", "sync the icons", or names icons that designers have added
  to the Figma icons page.
---

# Export Lemonade icons from Figma

Icons are drawn in Figma as 24×24 components on the **Icons** page of the
`🎲 Lemonade DS - Icons` file. Each one is committed to `svg/icons/<name>.svg`
exactly as Figma renders it, and a Kotlin converter turns that directory into
the per-platform assets. This skill covers both halves.

## TL;DR

```bash
# From the repo root
.claude/skills/export-icons/scripts/figma-icons.py diff      # what's new in Figma
.claude/skills/export-icons/scripts/figma-icons.py export    # write the missing ones
.claude/skills/export-icons/scripts/generate-assets.sh       # regenerate everything
```

Then review the diff and commit. A typical single-icon change touches ten files:
the SVG, `gen_<name>.xml`, the imageset (`Contents.json` + `.pdf`), both icon
enums, the KMP drawable extension, and the three `core` API baselines.

## Getting the icons out of Figma

`figma-icons.py` needs a Figma personal access token in `FIGMA_TOKEN` or
`FIGMA_CODE_CONNECT_TOKEN`, carrying a single scope: **`file_content:read`**.
That covers both calls the script makes — `/v1/files/:key/nodes` to list the
components and `/v1/images/:key` to render them. The token's user also needs
view access to the file; the scope alone is not enough.

- **`diff`** walks the Icons page and lists names present in Figma but not in
  `svg/icons/`, and the reverse. Repo-only names are not automatically wrong —
  an icon can be dropped from the Figma page while its enum entry has to stay
  for binary compatibility.
- **`export`** with no arguments writes every icon Figma has and the repo does
  not. Pass names (`export moon sun`) to re-export specific icons, which is how
  you pull in redrawn artwork.

Both accept `--file-key`, `--node` and `--dir` if you ever need a different
file, page or pack; the defaults point at the icons page.

Two things worth knowing about the Figma side:

- **The components live in two sibling frames**, one for the outline set and a
  smaller one for the `*-solid` icons. The script walks the whole page subtree
  and collects every `COMPONENT` node, so both are covered — a script that read
  one frame's children would silently miss the solid icons.
- **Don't switch the listing to `/v1/files/:key/components`.** It looks like the
  right endpoint for "list the components in this file", but it needs the
  `library_content:read` scope and returns 403 on a `file_content:read` token.
  Walking the tree keeps the skill to one scope.
- **The render is committed unmodified.** A `/v1/images?format=svg` render of an
  existing component comes back byte-identical to the committed file, trailing
  newline included. Do not tidy, reformat or re-minify the exported SVG: every
  icon already in the repo is a raw render, so any normalisation would show up
  as spurious churn the next time an untouched icon is re-exported.

## What the converter can express

`scripts/svg-asset-converter.main.kts` reads **only `<path>` elements** when
building the Android vector drawable. It fails the run if an icon paints a
`<rect>`, `<circle>`, `<text>` or similar outside a `<defs>`-like container, or
fills anything with a gradient, and it warns on strokes.

This matters because the failure is otherwise invisible: the SwiftUI side goes
through `rsvg-convert` and renders correctly, so the icon looks right on iOS
while silently losing artwork on Android. If the converter rejects a new icon,
the fix belongs in Figma — ask the designer to outline strokes and flatten
shapes to paths, then re-export. Do not hand-edit the SVG.

## Regenerating the assets

`generate-assets.sh` runs the pipeline in the order it has to happen, and
handles three traps that have each cost a bad commit before:

1. **Kotlin 2.3.20, by absolute path.** The `.main.kts` scripts crash on the
   2.4.0 that Homebrew installs. The script installs 2.3.20 under `~/.local` on
   first run and ignores whatever `kotlin` is on `PATH`.
2. **The country-flags companion.** The SVG converter rewrites
   `LemonadeCountryFlags.kt` from scratch, which deletes the public
   `companion object { getOrNull(alpha2) }` at the bottom — a removal that
   classifies as `BREAKING`. `kmp-country-flags-alpha2-generator.main.kts` puts
   it back, so it always runs immediately after. Never run the converter alone.
3. **PDF churn.** The converter skips unchanged files using a hash cache in
   `.cache/`, which is gitignored. In a fresh clone or worktree that cache is
   empty, so all ~560 imagesets are re-encoded and `rsvg-convert` stamps a new
   `CreationDate` inside each PDF — hundreds of binary diffs for pixel-identical
   artwork. The script reverts every imageset whose source SVG did not change.

It finishes with `apiDump` and the ABI classifier. Adding an icon appends enum
entries to `LemonadeIcons`, so the three `kmp/core/api/` baselines move; that is
additive, and the expected verdict is `ADDITIONS_ONLY`. Pass `--skip-api` to
stop after the converters.

`ANDROID_HOME` is not set in these worktrees and there is no
`kmp/local.properties`, so the script defaults it to `~/Library/Android/sdk`.

## Before opening the PR

The classifier reads the **committed** baseline, so it reports `NO_CHANGES`
until the regenerated `api/` files are committed. Commit first, then:

```bash
.claude/skills/binary-compatibility/scripts/bcv-check.sh --ci
```

Fill in the PR's **API Dump** section with the verdict and note that the only
change is new `LemonadeIcons` enum entries — an addition to a config enum the
components switch on internally. See the `binary-compatibility` skill if
anything else shows up in the diff.
