---
name: publish-figma-connect
description: >
  Publish the Lemonade Figma Code Connect mappings so Figma Dev Mode and
  MCP-driven agents emit real LemonadeUi.* code. Use when a template under
  `figma/connect/` or `figma/connect-swiftui/` changes, when a Figma component is
  renamed, rebuilt or has its properties changed and the snippets have gone stale,
  or when the user asks to "publish Code Connect", "push the Figma mappings", or
  "sync Figma to code".
---

# Publish Lemonade Figma Code Connect

Uploads the templates in `figma/connect/` under the **`Compose`** label and those
in `figma/connect-swiftui/` under the **`SwiftUI`** label. Publishing writes to
the **shared team library**, so everyone in the org sees the result immediately.
There is no staging environment.

| Thing | Value |
|---|---|
| Configs | `figma/figma.compose.config.json` and `figma/figma.swiftui.config.json` |
| Labels | `Compose` and `SwiftUI`, each published from its own config. Neither touches the unrelated `React` label on the same file. |
| Components file | `91S16rhVrl5wivqV66fNjm` |
| Token env var | `FIGMA_CODE_CONNECT_TOKEN`, set up once per machine (below) |

## First run on a new machine

Nothing here is checked in. Each person sets it up once.

1. **Node 18+** (`node -v`). The CLI declares `engines.node >= 18`.
2. **Install**: `cd figma && npm ci`. `@figma/code-connect` comes from public
   npm. If your npm points at Teya's JFrog registry, it must proxy npmjs for this
   to resolve; `npm config get registry` shows where you point.
3. **Create a Figma personal access token** (Figma → Settings → Security →
   Personal access tokens) with exactly two scopes: **`file_code_connect:write`**
   and **`file_content:read`**. It needs a Dev or Full seat on an Organization or
   Enterprise plan; Teya is on `org`.
4. **Export it**, e.g. in `~/.zshrc`:
   ```bash
   export FIGMA_CODE_CONNECT_TOKEN=figd_...
   ```

Consuming Code Connect needs none of this. Snippets live on Figma's servers, so
Dev Mode and the Figma MCP work for everyone without local setup. These steps are
only for publishing.

---

## Procedure

Run everything from `figma/`. The `include` globs resolve against the working
directory, not the config file.

### 1. Validate

```bash
FIGMA_ACCESS_TOKEN="$FIGMA_CODE_CONNECT_TOKEN" npm run validate
```

`--dry-run` writes nothing but still needs a token and exits 1 without one, so
CI needs the token as a secret. A config whose glob matches zero templates is an
error, not a no-op.

Neither config is named `figma.config.json`, the CLI default, so a bare
`figma connect publish` finds no config and errors rather than publishing one
platform and reporting success. Always pass `--config`.

This is the check to wire into CI. It cannot catch a wrong Figma property name:
`getEnum('◇ Varient', …)` parses fine and yields `undefined`. Only step 3
catches that.

### 2. Publish

```bash
(
  set -o pipefail
  for cfg in figma.compose.config.json figma.swiftui.config.json; do
    FIGMA_ACCESS_TOKEN="$FIGMA_CODE_CONNECT_TOKEN" \
      ./node_modules/.bin/figma connect publish --config "$cfg" \
      2>&1 | grep -viE "^-> |\.figma\.ts$" | tail -8 || exit 1
  done
)
```

Publish both labels. Pipe the output: the command prints a line per template and
the success or error summary is the last line, so unfiltered a failure looks the
same as a success. `pipefail` keeps the publish's exit status through the pipe,
so the loop stops at the first failed label instead of publishing the other one
and leaving the two out of step.

Success ends with:

```
All Code Connect files are valid (4306ms)
Successfully uploaded to Figma, for Compose:
```

If it warns that nodes **already have UI-created Code Connect mappings**, those
were made by hand in the Figma UI and are skipped. `--force` replaces them with
the repo's templates. Confirm with the user first: `--force` destroys the
UI-created mapping and it can't be recovered from this repo.

### 3. Verify

A successful upload only means the upload succeeded. Check the rendered snippet
through the Figma MCP, once per label:

```
get_code_connect_map(fileKey, nodeId=<component set>, codeConnectLabel="Compose")
```

The response is keyed by **variant** node ids, not the component-set id you
published against, so the set's own id is absent even on a healthy publish. Read
the entries, not the key you passed.

Confirm each snippet is real Kotlin or Swift, and that two variants differ the way
they should:

```kotlin
8302:10572 → size = LemonadeButtonSize.Large
8302:10564 → size = LemonadeButtonSize.Medium
```

The response usually exceeds the tool's token cap and is written to a file; query
it with `python3` or `grep` rather than re-fetching.

When the correct output and a failure would look the same, force them apart. If
every library variant has the first tab selected, `selectedIndex = 0` is also
what a broken lookup would produce: change the fallback to something distinctive,
republish, check, then restore it.

---

## Trialling risky changes

To avoid disturbing the live snippets, change `label` in the config you are
testing (for example to `"Compose (test)"` in `figma.compose.config.json`),
publish with that config, verify in Dev Mode, then remove the trial mappings:

```bash
./node_modules/.bin/figma connect unpublish --config figma.compose.config.json
```

Restore the label afterwards. Worth doing when property names changed; not needed
to republish templates that already verified.

## Common failures

| Symptom | Cause |
|---|---|
| `Couldn't find a Figma access token` | `$FIGMA_CODE_CONNECT_TOKEN` is unset or empty. Either it was never set up (see First run), or the shell didn't source your profile: `source ~/.zshrc` first. Check with `echo ${FIGMA_CODE_CONNECT_TOKEN:+set}`. |
| `npm ci` cannot find `@figma/code-connect` | npm points at a registry that doesn't proxy public npm. Check `npm config get registry`. |
| `Framework-specific parsers are no longer supported` | Ran from the wrong directory. `cd figma` first. |
| Exit code 126 | `./node_modules/.bin/figma` resolved from the wrong cwd. Use an absolute path. |
| `node not found in file` | The Figma component was deleted or rebuilt under a new node. Find the new node and repoint the template's `// url=` line. |
| Publish succeeds, snippet shows `undefined` | A Figma property or variant value was renamed. Re-read it with `get_context_for_code_connect` and fix the `getEnum`/`getString` key; names are case- and emoji-sensitive (`✍️ Label` vs `↪ ✍️ Label`). |
| Nodes silently skipped | UI-created mappings exist. Re-run with `--force`. |

## Related

- `figma/README.md`: layout, and the mapping decisions worth knowing before
  editing a template.
- Templates are parserless `.figma.ts` files that emit Kotlin or Swift as strings,
  so publishing never touches `kmp/` or `swiftui/Sources`. A Code-Connect-only
  PR's API Dump section is "No public API changes".
