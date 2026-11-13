---
name: publish-version
description: >
  Publish a new version of the Lemonade SDK — KMP and/or SwiftUI — and of the
  lemonade-specialist Claude Code plugin. Use when the user wants to cut a release,
  ship a new version, tag a release, or "publish the SDK". Syncs to latest
  origin/main, analyzes what changed per platform since the last release, suggests
  a version bump (confirmed with the user), then cuts and pushes the tags, creates
  the KMP GitHub release, and bumps or tags the plugin.
---

# Publish a Lemonade SDK version

Cuts a new release for the two published SDKs and the `lemonade-specialist` plugin.
All three version **independently**. The SDK versions are **purely tag-driven** — there
are no version files to bump on either side. The plugin's version lives in
`plugins/lemonade-specialist/.claude-plugin/plugin.json`: Claude Code updates an
installed plugin only when that number changes.

| SDK | Release tag | What the tag does | GitHub Release |
|-----|-------------|-------------------|----------------|
| **KMP** | `lemonade-kmp-X.Y.Z` | `kmp_release.yml` publishes to Maven Central | **You create it** (KMP CI does *not*) — holds the **Latest** badge |
| **Plugin** | `lemonade-specialist--vX.Y.Z` | Marks the released version; nothing runs on it. The `version` in `plugin.json` is what reaches users | None |
| **SwiftUI** | `lemonade-swiftui-X.Y.Z` **and** plain `X.Y.Z` | `swiftui_release.yml` (on the prefixed tag) builds the XCFramework + creates the GH release. The plain `X.Y.Z` tag is the **SPM resolution tag** consumers actually pin to (root `Package.swift` builds from source). | **CI creates it** — do *not* create it yourself |

Both SwiftUI tags point at the same commit. The prefixed tag drives the GitHub
release and the Slack notification; the plain tag is what SPM consumers resolve,
building from source. Push both.

**Version bump rule (suggestion only, always confirmed):**
- changes on **one** side only → **patch** bump for that SDK
- changes on **both** sides → **minor** bump for both

A "new icons" / token PR generates into *both* `kmp/` and `swiftui/Sources/`, so it
lands on both sides and warrants a release on both.

---

## Procedure

### 1. Pre-flight

Confirm tooling and remote, from anywhere in the repo:

```bash
gh auth status            # gh must be authenticated
git remote get-url origin # expect …saltpay/lemonade-design-system
```

### 2. Gather release status (syncs to latest origin/main)

```bash
.claude/skills/publish-version/scripts/release-status.sh
```

This fetches `origin/main` + tags, resolves `RELEASE_SHA` (the exact origin/main
HEAD all tags will point at — the worktree-safe equivalent of "checkout
origin/main"), finds the latest released KMP and SwiftUI versions, lists what
changed in each SDK's *shipped* source since its last release, and prints
patch/minor candidates plus a suggested bump. **Capture `RELEASE_SHA` and each
`*_PREV_TAG` / `*_PREV_VERSION` from this output — you need them below.** For the
plugin it prints `PLUGIN_ACTION`:

- `tag` — `plugin.json` is ahead of the latest plugin tag (a bump PR merged, or the
  first release). Tag `PLUGIN_TAG_SHA`, the commit that set the version, with
  `PLUGIN_TAG`. When `PLUGIN_CHANGED_AFTER_TAG` is above 0, the plugin changed after
  that commit, so also open a bump PR in the same run.
- `bump` — the plugin folder changed since its tag but the version did not. Open a
  bump PR; the next release tags it.
- `none` — nothing to do.

The script's path filters (`kmp/` minus the `composeApp` sample; `swiftui/Sources/`)
decide whether each side *has* changes. They are a first pass — the next step makes
the real call.

### 3. Decide "is there code to release?" — one analysis subagent per SDK

For each SDK the script reports as `HAS_CHANGES=true` (and, if the user is unsure,
even for `false`), spawn an **Explore** subagent — run the two in parallel. Hand
each the range from the script and have it read the actual diff and judge whether
the change is **consumer-facing** and worth a release, vs. noise that is not
(only the sample app, tests, docs, CI, or a pure `api/*.api` baseline reformat).

Prompt template (fill in the range from the script):

> Read the diff `git diff <PREV_TAG>..origin/main -- <paths>` and the commit list
> `git log --no-merges <PREV_TAG>..origin/main -- <paths>`.
> - For **KMP** use paths: `kmp/ ':(exclude)kmp/composeApp/'`
> - For **SwiftUI** use paths: `swiftui/Sources/`
> Decide: does this contain a real, consumer-facing change to the published SDK
> (new/changed component, new icon or token, bug fix in shipped code, public API
> change)? Answer `RELEASE` or `SKIP`, then 2–4 bullets summarizing what changed
> (call out new icons/tokens, new components, and any public API additions — an
> `api/*.api` diff means the public surface changed). Read only; do not edit.

The plugin needs no subagent: everything under `plugins/lemonade-specialist/` is what
agents read, including the copy of `guidelines/`, so any change there is worth a
version.

Use both verdicts to settle which SDKs to release. A reliable signal of a real KMP
release is a changed `kmp/<module>/api/*.api` or `*.klib.api` baseline.

### 4. Confirm versions with the user — **always** via AskUserQuestion

Apply the bump rule to form the suggestion, then confirm with `AskUserQuestion`.
**Never tag without this confirmation.** Ask one question per SDK that has changes,
plus one for the plugin when `PLUGIN_ACTION` is not `none` (at most three questions). Put the rule-suggested version first, marked
"(Recommended)", and always include a "Don't release this SDK" option.

Example, when KMP has changes and SwiftUI doesn't (KMP-only → patch):

- **Question** "KMP version to release? (currently `0.20.0`)"
  - `0.20.1 — patch (Recommended)` — KMP-only change
  - `0.21.0 — minor` — if these are significant/breaking-ish additions
  - `Don't release KMP`

For the plugin, `bump` suggests a **patch** (`PLUGIN_NEXT_PATCH`); offer
`PLUGIN_NEXT_MINOR` for a new skill or a new guideline file. `tag` asks only whether
to tag `PLUGIN_TAG` now.

When **both** sides changed, suggest **minor** for both (e.g. KMP `0.20.0 → 0.21.0`,
SwiftUI `0.16.2 → 0.17.0`) and ask one question each. Respect the user's choice
even if it differs from the rule.

### 5. Execute — only after confirmation

Use `RELEASE_SHA` from step 2 so tags point at the exact origin/main commit.
Before tagging, make sure the chosen tag does not already exist
(`git tag -l <tag>`). Run the SDKs the user confirmed.

**KMP** — tag → push (triggers Maven Central publish) → create the GitHub release
(KMP CI does *not* create it). Mark it `--latest`. Build the notes with
`release-notes.sh`, never `--generate-notes`: GitHub's generated notes list every
PR merged in the range, so web, docs and SwiftUI-only work would show up as KMP
changes. The script lists only PRs that touched shipped KMP source, in the same
"What's Changed" + "Full Changelog" format:

```bash
NEW=0.20.1                       # confirmed version
PREV=lemonade-kmp-0.20.0         # KMP_PREV_TAG from the script
SHA=<RELEASE_SHA>

git tag "lemonade-kmp-${NEW}" "$SHA"
git push origin "lemonade-kmp-${NEW}"

.claude/skills/publish-version/scripts/release-notes.sh \
  "$PREV" "lemonade-kmp-${NEW}" "$SHA" kmp/ ':(exclude)kmp/composeApp/' \
  | gh release create "lemonade-kmp-${NEW}" \
      --title "Lemonade Release ${NEW}" \
      --notes-file - \
      --latest
```

**SwiftUI** — push **both** tags at the same commit. The prefixed tag triggers
`swiftui_release.yml` (XCFramework build + GH release + Slack); the plain tag is
what SPM consumers resolve. **Do not** create a GitHub release yourself — CI does.

```bash
NEW=0.17.0                       # confirmed version
SHA=<RELEASE_SHA>

git tag "${NEW}" "$SHA"                      # SPM resolution tag
git tag "lemonade-swiftui-${NEW}" "$SHA"     # triggers CI (XCFramework + GH release)
git push origin "${NEW}" "lemonade-swiftui-${NEW}"
```

**Plugin, `PLUGIN_ACTION=tag`** — tag the commit that set the version, not
`RELEASE_SHA`: users who installed that version have exactly that commit, and later
plugin changes belong to the next version. Nothing is published:

```bash
TAG=lemonade-specialist--v1.1.1  # PLUGIN_TAG from the script
SHA=<PLUGIN_TAG_SHA>

git tag "$TAG" "$SHA"
git push origin "$TAG"
```

If `PLUGIN_CHANGED_AFTER_TAG` is above 0, continue with the bump PR below, using the
next patch after `PLUGIN_TAG`'s version.

**Plugin, `PLUGIN_ACTION=bump`** — open a PR that bumps `version` in `plugin.json`,
branched from `RELEASE_SHA` in a throwaway worktree, so the user's checkout is not
touched. Don't tag; the next release finds the merged bump and tags it. First check
that no bump PR is already open (`gh pr list --search "lemonade-specialist in:title"
--state open`); if one is, report it instead of opening another:

```bash
NEW=1.1.1                        # confirmed version
SHA=<RELEASE_SHA>
BRANCH="chore/lemonade-specialist-${NEW}"
DIR="$(mktemp -d)/lemonade-specialist-${NEW}"
FILE=plugins/lemonade-specialist/.claude-plugin/plugin.json

git worktree add -b "$BRANCH" "$DIR" "$SHA"
sed -i.bak "s/\"version\": \"[^\"]*\"/\"version\": \"${NEW}\"/" "$DIR/$FILE" && rm "$DIR/$FILE.bak"
git -C "$DIR" commit -am "chore(plugins): release lemonade-specialist ${NEW}"
git -C "$DIR" push -u origin "$BRANCH"
git worktree remove "$DIR"
gh pr create --head "$BRANCH" --base main --title "chore(plugins): release lemonade-specialist ${NEW}" \
  --body "Bumps lemonade-specialist to ${NEW}, so installed copies pick up the changes since the last release."
```

### 6. Report back

Summarize what shipped, with links:
- KMP: the GH release URL (`gh release view lemonade-kmp-${NEW} --web` to confirm)
  and the Maven publish run (`gh run list --workflow kmp_release.yml --limit 1`).
- SwiftUI: the CI run that will build + publish the release
  (`gh run list --workflow swiftui_release.yml --limit 1`), and remind the user the
  SPM consumer version is the plain `${NEW}` tag.
- Both post to the Slack release channel from CI.
- Plugin: the pushed `lemonade-specialist--v…` tag, or the bump PR URL with a
  reminder that the next release tags it once it merges.

---

## Guardrails

- **Never tag without the AskUserQuestion confirmation** of versions (step 4).
- Tag the resolved `RELEASE_SHA` (origin/main HEAD), never the local worktree HEAD —
  this skill releases what is on origin/main, not local work in progress. The plugin
  tag is the one exception: it goes on `PLUGIN_TAG_SHA`, which is on origin/main too.
- Pushing a tag is effectively irreversible (it triggers a public Maven/CI publish).
  Only push the tags for the SDKs the user explicitly confirmed.
- Never push a plugin version bump straight to `main`; it goes through a PR.
- Don't create the SwiftUI GitHub release — `swiftui_release.yml` already does, and
  doubling it up fights the CI-managed release.
- These are public, outward-facing actions. Do the analysis and version
  confirmation, then execute the push/release for the confirmed SDKs in the same run.
