---
name: export-icons
description: >
  Export new or updated icons from the Lemonade DS Figma icons file into
  `svg/icons/`, then regenerate the KMP drawables, SwiftUI imagesets, the icon
  enums, the Figma Code Connect templates and the API baselines, and give new
  icons their alias descriptions in Figma. Use when the user asks to "add an icon", "export
  icons from Figma", "sync the icons", or names icons that designers have added
  to the Figma icons page.
---

# Export Lemonade icons from Figma

Icons are drawn in Figma as 24×24 components on the **Icons** page of the
`🎲 Lemonade DS - Icons` file. Each one is committed to `svg/icons/<name>.svg`
exactly as Figma renders it, a Kotlin converter turns that directory into the
per-platform assets, and a Code Connect template maps the Figma component back
to its enum entry. This skill covers all of it.

## TL;DR

```bash
# From the repo root
.claude/skills/export-icons/scripts/figma-icons.py diff      # what's new in Figma
.claude/skills/export-icons/scripts/figma-icons.py export    # write the missing ones + manifest
.claude/skills/export-icons/scripts/generate-assets.sh       # regenerate everything
```

Then give each new icon its alias description in Figma (below), review the diff
and commit. A typical single-icon change touches thirteen files: the SVG,
`gen_<name>.xml`, the imageset (`Contents.json` + `.pdf`), both icon enums, the
KMP drawable extension, the three `core` API baselines, the
`figma/icons.manifest.json` entry, and the two `<Name>.figma.ts` templates under
`figma/connect/icons/` and `figma/connect-swiftui/icons/`. Once the PR merges,
publish Code Connect.

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
  you pull in redrawn artwork. It also writes each exported icon's node id into
  `figma/icons.manifest.json`, kept sorted by name.

Both accept `--file-key`, `--node` and `--dir` if you ever need a different
file, page or pack; the defaults point at the icons page. `export` also takes
`--manifest`.

Worth knowing about the Figma side:

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

## Describing the icon in Figma

Every icon component carries a description of search aliases, which is what the
Figma asset panel and the icon search match against. Designers usually add the
component without one, so set it for each new icon with `use_figma` (load the
`figma-use` skill first) on the icons file, `f7zokCdnayXejxc2y7r1Qt`. The node
id is the one `export` wrote into the manifest.

Read a few neighbours first and match them:

| Icon | Description |
|---|---|
| `car` | `travel, transport, carriage, transit, vehicle` |
| `card-machine` | `pos, payment-terminal, card-reader, terminal, device` |
| `gear` | `settings, preferences, cog` |
| `moon` | `dark, dark mode, night` |
| `heart-solid` | `like, favorite, love` |

- Lowercase, comma-separated, three to eight terms.
- Synonyms and the things someone would search for when they need the icon, not
  words already in its name.
- Related icons share vocabulary: a new vehicle icon reuses `transport` and
  `vehicle` from `car`, `bus` and `airplane`.
- A `*-solid` icon takes the same aliases as its outline sibling.

```js
const c = await figma.getNodeByIdAsync('<node id>');
if (c.type !== 'COMPONENT') throw new Error(c.type);
c.description = 'vehicle, automobile, transport, travel, driving, parking';
return { mutatedNodeIds: [c.id], description: c.description };
```

The description reaches library consumers only once a designer publishes the
icons library from Figma.

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

It then regenerates the icon Code Connect templates with
`figma/scripts/generate-asset-templates.mjs icons` and compile-checks them with
`npm run check` in `figma/`. The generator fails if an enum entry has no node in
`figma/icons.manifest.json`, the same check the **Templates compile and assets
match** CI job runs, so an icon exported without `figma-icons.py export` stops
here rather than in CI.

It finishes with `apiDump` and the ABI classifier. Adding an icon appends enum
entries to `LemonadeIcons`, so the three `kmp/core/api/` baselines move; that is
additive, and the expected verdict is `ADDITIONS_ONLY`. Pass `--skip-api` to
stop after the templates.

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

## After the PR merges

Publish Code Connect from an up-to-date `main` with the `publish-figma-connect`
skill, both labels, then verify one new icon with `get_code_connect_map`. Until
then Dev Mode shows no snippet for the new icon. Publishing from `main`, not
from the branch, keeps any template change that merged meanwhile from being
rolled back in the shared library.
