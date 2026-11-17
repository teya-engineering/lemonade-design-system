# Adding assets

Icons, country flags and brand logos are authored in Figma, committed here as SVG, and generated
into every platform's native format. Nothing outside `svg/` is edited by hand.

| Asset | Folder | Figma file | Code enum |
|---|---|---|---|
| Icon | `svg/icons/` | Lemonade DS - Icons, **Icons** page | `LemonadeIcons` |
| Country flag | `svg/flags/` | Lemonade DS - Icons | `LemonadeCountryFlags` |
| Brand logo | `svg/brandLogos/` | Lemonade DS - Components | `LemonadeBrandLogos` |

Each SVG becomes an Android vector drawable, a SwiftUI imageset (PDF), an enum entry on both
platforms, a web asset, a `web/llms.txt` line and a Figma Code Connect template.

## Let Claude Code do it

For icons, run `/export-icons` in Claude Code, or ask it to "add the new icons from Figma". The
`export-icons` skill (`.claude/skills/export-icons/`) runs the whole icon flow below. After the PR
merges, the `publish-figma-connect` skill publishes the Code Connect templates.

Flags and brand logos have no export script and no skill of their own, so follow the manual steps
for them.

## Before you start

- `rsvg-convert` (`brew install librsvg`), Node, and a JDK for Gradle.
- `ANDROID_HOME` pointing at an Android SDK. `generate-assets.sh` defaults it to
  `~/Library/Android/sdk`.
- Kotlin 2.3.20. `generate-assets.sh` installs it under `~/.local` on first run. Newer Kotlin
  crashes the `.main.kts` converters.

## Add an icon

1. **Draw it in Figma,** following the icon guidelines in the Icons file. A 24×24 component on
   the Icons page, named in kebab-case (`scan-barcode`). Outline strokes and flatten shapes to
   paths: the Android converter reads `<path>` elements only and fails on `<rect>`, `<circle>`,
   `<text>` or gradients. Fix this in Figma, never by editing the SVG.
2. **Run `/export-icons` in Claude Code.** The `export-icons` skill
   (`.claude/skills/export-icons/`) does the rest of the work:
   - exports the new icons from Figma into `svg/icons/`, exactly as Figma renders them;
   - generates the Android drawables, SwiftUI imagesets, enum entries, web assets,
     `web/llms.txt` and Code Connect templates, and refreshes the API baselines;
   - sets the icon's search aliases in Figma;
   - runs the API check.

   It needs a Figma personal access token with the `file_content:read` scope in `FIGMA_TOKEN`.
3. **Update the web test.** Raise the `iconNames` length in `web/tests/exports.test.ts` by the
   number of icons you added.
4. **Commit and open the PR** with the API check's verdict in the **API Dump** section. New enum
   entries are additive, so the expected verdict is `ADDITIONS_ONLY`.
5. **After it merges,** run the `publish-figma-connect` skill from an up-to-date `main`. Until
   then Dev Mode shows no snippet for the new icon.

A typical single-icon PR touches about seventeen files: the SVG, the drawable, the imageset, both
enums, the KMP extension, three API baselines, the manifest entry, two Code Connect templates, the
web asset, `web/llms.txt`, `web/src/icons.generated.ts` and the web test.

## Add a country flag or brand logo

The Figma export script covers icons only, so the SVG and its manifest entry are added by hand.

1. **Draw it in Figma** with the same path-only rule as icons.
   - Flags are named `<ISO alpha-2>-<country-name>`, for example `AD-andorra`. Sub-regions add
     the region code after the country code (`GB-ENG-england`).
   - Brand logos are named in kebab-case (`apple-pay`). For a dark-theme version, add a second
     component named `<name>-dark`. A logo that reads fine on both surfaces needs no dark file:
     the converter drops a `-dark` file that renders the same as its light one.
2. **Export the SVG** from Figma into `svg/flags/` or `svg/brandLogos/`, named exactly like the
   component. Commit it as exported.
3. **Add the Figma node ids** to `figma/flags.manifest.json` or `figma/brand-logos.manifest.json`
   (right-click the component in Figma, *Copy link*, and take `node-id`, with `:` for `-`). Add
   the dark component too, as `<name>-dark`. Keep the entries sorted.
4. **Generate the platform assets:**

   ```bash
   .claude/skills/export-icons/scripts/generate-assets.sh --skip-api
   ```

   The script generates all three asset types, but its Code Connect step only covers icons.
5. **Generate the flag and logo Code Connect templates:**

   ```bash
   cd figma
   npm ci
   node scripts/generate-asset-templates.mjs flags brandLogos
   npm run check
   ```

   The generator fails when an enum entry has no manifest entry, and when a manifest entry has
   no enum entry, so a missing step shows up here.
6. **Update the web test.** Run `npm test` in `web/` and set the `flagNames` or `brandLogoNames`
   length in `web/tests/exports.test.ts` to what it reports.
7. **Run the API check and open the PR,** as for icons, then publish Code Connect after it merges.

## If something looks wrong

- **`apiCheck` or the classifier reports `BREAKING`.** Stop. Adding an asset only appends enum
  entries. A removal or rename means an SVG was deleted or renamed, which breaks consumers. See the
  `binary-compatibility` skill.
- **Hundreds of changed PDFs.** The converter's hash cache is gitignored, so a fresh worktree
  re-encodes everything. `generate-assets.sh` reverts these; if you ran the converter directly,
  `git checkout --` the imagesets whose SVG you did not change.
- **`LemonadeCountryFlags.getOrNull` disappeared.** The converter was run without
  `kmp-country-flags-alpha2-generator.main.kts`. Run that script after it.
- **The converter rejects an SVG.** The artwork contains something other than paths. Fix it in
  Figma and re-export.
- **Dropping an asset from Figma.** Keep its SVG and enum entry. Removing a published enum entry
  is a breaking change; deprecate it instead (see `deprecations` in
  `scripts/svg-asset-converter.main.kts`).
