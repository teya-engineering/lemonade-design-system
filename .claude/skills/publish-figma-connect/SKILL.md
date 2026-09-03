---
name: publish-figma-connect
description: >
  Publish the Lemonade Figma Code Connect mappings so Figma Dev Mode and
  MCP-driven agents emit real LemonadeUi.* code. Use when a `figma/connect/*.figma.ts`
  template changes, when icons are added and the templates need regenerating, when
  a Figma component's properties are renamed and the snippets have gone stale, or
  when the user asks to "publish Code Connect", "push the Figma mappings", or
  "sync Figma to code".
---

# Publish Lemonade Figma Code Connect

Uploads the templates in `figma/connect/` to Figma under the **`Compose`** label.
Publishing writes to the **shared team library** — everyone in the org sees the
result immediately. There is no staging environment.

| Thing | Value |
|---|---|
| Configs | `figma/figma.config.json` (Compose) and `figma/figma.swiftui.config.json` (SwiftUI) |
| Labels | `Compose`, `SwiftUI` — each published separately from its own config. Neither touches the unrelated `React` label on the same file. |
| Components file | `91S16rhVrl5wivqV66fNjm` |
| Icons file | `f7zokCdnayXejxc2y7r1Qt` |
| Token env var | `FIGMA_CODE_CONNECT_TOKEN` (exported in `~/.zshrc`) |

The token needs exactly two scopes: **`file_code_connect:write`** and
**`file_content:read`**. Nothing else. Code Connect also requires an
Organization or Enterprise plan; Teya is on `org`.

---

## Procedure

### 1. Regenerate icon templates, if icons changed

`figma/connect/icons/*.figma.ts` is **generated — never hand-edit it**:

```bash
cd figma && node scripts/generate-icon-templates.mjs
```

It cross-checks `icons.manifest.json` against the `LemonadeIcons` enum and
**fails** if they have drifted, rather than emitting a template for an icon the
enum lacks. If it fails, either run the `svg-asset-converter` first or refresh
`icons.manifest.json` from Figma (`list_file_components_for_code_connect` on the
icons file → `{name: nodeId}` for every property-less `COMPONENT`).

### 2. Validate — no token, no side effects

```bash
cd figma && npm ci
./node_modules/.bin/figma connect publish --config figma.config.json \
  --dry-run --exit-on-unreadable-files
```

**Run this from `figma/`.** The `include` globs resolve against the working
directory, not the config file. From anywhere else the run either finds nothing
or fails with the misleading `Framework-specific parsers are no longer supported
in Code Connect CLI v2`, which is about the glob matching non-template files —
not about the `parser` setting being wrong.

This is the check to wire into CI. **It cannot catch a wrong Figma property
name**: `getEnum('◇ Varient', …)` parses fine and silently yields `undefined`.
Only step 4 catches that.

### 3. Publish

```bash
cd figma
for cfg in figma.config.json figma.swiftui.config.json; do
  FIGMA_ACCESS_TOKEN="$FIGMA_CODE_CONNECT_TOKEN" \
    ./node_modules/.bin/figma connect publish --config "$cfg" \
    2>&1 | grep -viE "^-> |\.figma\.ts$" | tail -8
done
```

Publish **both** labels, and never a platform's components without its icons.
Figma resolves a nested icon by node; a label with no template for that node
falls back to another label's, so a missing SwiftUI icon renders the *Kotlin*
snippet inside a Swift call rather than rendering nothing.

**Pipe it.** The command prints one line per template — ~300 of them — and the
success or error summary is the *last* line. Unfiltered it scrolls off and a
failure looks identical to a success.

Success ends with:

```
All Code Connect files are valid (4306ms)
Successfully uploaded to Figma, for Compose:
```

If it warns that nodes **already have UI-created Code Connect mappings**, those
were made by hand in the Figma UI and are skipped. Re-run with `--force` to
replace them with the repo's templates. Confirm with the user first — `--force`
destroys someone's UI-created mapping, and it is not recoverable from this repo.

### 4. Verify — the step that actually proves anything

The CLI reporting success only means the upload succeeded. Check the rendered
snippet through the Figma MCP:

```
get_code_connect_map(fileKey, nodeId=<component set>, codeConnectLabel="Compose")
```

The response is keyed by **variant** node ids, not the component-set id you
published against — so the set's own id will be absent even on a healthy
publish. That is expected; look at the entries, not the key you passed.

Confirm the snippet names real Kotlin, and that two different variants differ in
the way they should:

```kotlin
8302:10572 → size = LemonadeButtonSize.Large
8302:10564 → size = LemonadeButtonSize.Medium
```

The response usually exceeds the tool's token cap and gets written to a file —
query it with `python3`/`grep` rather than re-fetching.

For a nested icon, check a `Tag` variant renders `icon = LemonadeIcons.Heart`
and not an opaque instance; that is the cross-file resolution working.

---

## Trialling risky changes

To avoid disturbing the live `Compose` snippets, set `label` to
`"Compose (test)"` in `figma.config.json`, publish, verify in Dev Mode, then:

```bash
./node_modules/.bin/figma connect unpublish --config figma.config.json
```

and restore the label. Worth doing when property names changed; unnecessary for
a re-publish of templates that already verified.

## Common failures

| Symptom | Cause |
|---|---|
| `Couldn't find a Figma access token` | `$FIGMA_CODE_CONNECT_TOKEN` empty in this shell. Non-interactive shells don't source `~/.zshrc` — `source ~/.zshrc` first. |
| `Framework-specific parsers are no longer supported` | Ran from the wrong directory. `cd figma` first. |
| Exit code 126 | `./node_modules/.bin/figma` resolved from the wrong cwd. Use an absolute path. |
| Publish succeeds, snippet shows `undefined` | A Figma property was renamed. Re-read it with `get_context_for_code_connect` and fix the `getEnum`/`getString` key — names are case- and emoji-sensitive (`✍️ Label` vs `↪ ✍️ Label`). |
| Nodes silently skipped | UI-created mappings exist. Re-run with `--force`. |

## Related

- `figma/README.md` — layout, the icon generator, and the deliberate mapping gaps.
- Templates are parserless `.figma.ts` emitting Kotlin strings, so publishing
  **never** touches `kmp/ui` or the BCV baseline. A Code-Connect-only PR's API
  Dump section is always "No public API changes".
