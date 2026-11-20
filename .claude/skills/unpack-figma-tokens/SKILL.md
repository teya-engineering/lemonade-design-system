---
name: unpack-figma-tokens
description: >
  Unzip the Figma variable exports from the Lemonade Foundations file into `tokens/`: read each
  per-collection zip, write every mode file under its `tokens/*.tokens.json` name, and flag theme
  colours whose stored hex no longer matches their primitive. Use when the user has exported
  variables from Figma, drops a `Theme.zip` or `Primitive Colors.zip` in the repo, or asks to
  "unzip", "copy" or "rename" the Figma tokens, or to "update tokens from Figma". Not for regenerating the platform code from
  `tokens/`; that is `generate-tokens`.
---

# Unpack Figma tokens

Figma exports each variable collection as its own zip, named after the collection, holding one
`<Mode>.tokens.json` per mode. The file names say only the mode, so most collections arrive as a
lone `Default.tokens.json` and the zip name is the only thing identifying it. The unpack script maps
each one to its `tokens/` name and refuses anything it does not recognise.

## TL;DR

```bash
.claude/skills/unpack-figma-tokens/scripts/unpack-tokens.py ~/Downloads/Theme.zip ~/Downloads/"Primitive Colors.zip"
.claude/skills/generate-tokens/scripts/run-converters.sh --changed
```

Pass every zip from the same export in one call. Unpacking and generating stay separate steps:
check the `tokens/` diff before running the converters.

## Exporting from Figma

In the Foundations file's variables panel, right-click each collection that changed and choose
**Export modes**. That native export is the only supported source; plugin exports have a different
shape. The mode-to-file table is `COLLECTIONS` in [`scripts/unpack-tokens.py`](scripts/unpack-tokens.py); it
is the only copy.

When **Primitive Colors** changes, export **Theme** in the same sitting and unpack both together.
Every theme colour stores the resolved hex of the primitive it aliases. KMP follows the alias, but
the SwiftUI colour assets and the web output are built from that stored hex, so a theme file from an
older export ships the old colour on those platforms.

## What the script does

- Reads the zips in memory and never extracts into `tokens/`. `__MACOSX/`, `.DS_Store` and `._*`
  entries are skipped. A second download named `Theme (1).zip` counts as `Theme`.
- Validates everything before writing. An unknown collection, an unknown mode, a missing mode, a
  collection given twice, invalid JSON (`NaN` and `Infinity` included), a file whose
  `com.figma.modeName` disagrees with its name, or a non-token file already in `tokens/` stops the run
  with nothing written.
- Writes the bytes Figma exported plus the trailing newline Figma omits. It never re-serialises, so
  key order and `com.figma.hiddenFromPublishing` on the primitives survive as exported.
- Deletes `tokens/.DS_Store` and any input zip that sat in `tokens/`, so `tokens/` holds only the
  ten token files.
- Warns when Primitive Colors comes without Theme, and lists every theme colour whose stored hex or
  alpha differs from the primitive it aliases.
- Prints `git status --short tokens/` and the converter command to run next.

## When the script refuses

| Error | Cause | Fix |
|---|---|---|
| unknown collection | A collection was added or renamed in Figma | Add or rename its entry in `COLLECTIONS`, plus the converter wiring in `generate-tokens` |
| unknown mode | A mode was renamed, such as `Light` to `Day` | Rename the mode in `COLLECTIONS` and delete the superseded `tokens/` file in the same change, or the converters generate a second theme class beside the stale one |
| missing mode | The zip lacks one of the collection's modes | Re-export the whole collection |
| not token files in `tokens/` | Something other than the ten files and the input zips sits there | Move it out and rerun |

## Anti-patterns

- **Renaming files by hand.** `Sizing` and `Spacing` both hold plain numbers, so a swap parses and
  generates plausible values in the wrong place. Run the script; it maps by collection name, never by
  content.
- **Silencing the stale-hex warning.** It means the theme and primitive files come from different
  Figma states. Export both and unpack them in one run rather than editing hex values in the JSON.
- **Stripping `com.figma.hiddenFromPublishing`.** It is expected on every primitive; the primitive
  converters read hidden tokens on purpose (see `generate-tokens`).
