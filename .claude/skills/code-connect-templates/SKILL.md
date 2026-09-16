---
name: code-connect-templates
description: Write, fix or review a Figma Code Connect template under figma/connect/ (Kotlin) or figma/connect-swiftui/ (Swift), the shared helpers in figma/shared/, the asset manifests, or the scripts in figma/scripts/. Use when connecting a component to Figma, when a snippet comes out wrong, empty or in the other platform's language, when a Figma property is renamed, or when anyone says "connect this component", "the Dev Mode snippet is wrong" or "add a Code Connect mapping". Publishing the result is `publish-figma-connect`; prose in figma/README.md is `writing-docs`.
allowed-tools: Read, Edit, Write, Grep, Glob, Bash(node:*), Bash(npm:*), Bash(git diff:*)
---

# Code Connect templates

A template is a small program Figma runs on its servers every time someone selects an
instance in Dev Mode. It reads the design and prints the Kotlin or Swift a developer
should paste. The output is code a human will compile, so **the bar is that the snippet
compiles as pasted**, not that it renders.

Two labels publish from this repo — `Compose` from `figma/connect/`, `SwiftUI` from
`figma/connect-swiftui/` — and a node with no template under a label falls back to
another label's snippet. So every connected node needs a template in **both**
directories, even when only one platform has the component.

`figma/README.md` records the per-component mapping decisions; this skill is how a
template is built. Publishing and verifying against live Figma is `publish-figma-connect`.

## When to use

| Scenario | This skill? |
|---|---|
| Connecting a new component, or a sub-part a parent reads | Yes |
| A snippet shows `undefined`, JSX, or the other platform's code | Yes |
| A designer renamed a property or variant | Yes — fix the `getEnum`/`getString` key here, then republish |
| An icon, flag or brand logo was added | No — regenerate with `scripts/generate-asset-templates.mjs`, never hand-write one |
| Uploading templates to Figma, or a publish failure | No — `publish-figma-connect` |
| Rewriting a section of `figma/README.md` | No — `writing-docs` |

## Anatomy

```ts
// url=<LEMONADE_COMPONENTS>?node-id=15094-5516      // the file token, not a raw file key
// source=kmp/ui/src/commonMain/kotlin/com/teya/lemonade/ListItem.kt   // must exist
// component=ListItem
import figma from 'figma'
import { renderer } from '../shared/render'

const instance = figma.selectedInstance
const { slot, imports: slotImports } = renderer(instance, figma.kotlin)

export default {
  example: figma.kotlin`…`,   // figma.swift in connect-swiftui/
  imports: ['import com.teya.lemonade.ListItem', …, ...slotImports],
  id: 'list-item',            // kebab-case, unique across both directories
  metadata: { nestable: true },
}
```

`figma/connect/ListItem.figma.ts` is the canonical shape. The three URL tokens
(`<LEMONADE_COMPONENTS>`, `<LEMONADE_ICONS>`, `<LEMONADE_FLAGS>`) resolve through
`documentUrlSubstitutions` in both configs, so file keys live in one place.

Templates import helpers by relative path (`figma/shared/render.ts`); the CLI bundles
them at publish time. Helpers must sit outside the `connect*/**/*.figma.ts` globs or
they get published as templates themselves.

## The rules that keep snippets compiling

- **Slots.** Interpolating `getSlot()` prints React JSX. Render slot content with
  `renderer(instance, tag)` from `figma/shared/render.ts`; a slot with no Lemonade
  component in it keeps a `/* … */` placeholder. An enum-typed parameter takes the
  glyph of the Icon in the slot (`slotIcon`), never the slot itself.
- **Imports travel one level.** A child's result carries the child's own imports, not
  its children's. Re-export what the helper collected (`...slotImports`), and list the
  asset enum import in any template that prints `LemonadeIcons.X` itself.
- **Swift is order-sensitive.** Labelled arguments must appear in declaration order,
  optional arguments carry a **leading** comma, and a trailing comma in a call is a
  compile error. Kotlin named arguments may appear in any order.
- **A required Swift closure needs a value.** Pass `{ EmptyView() }` rather than
  omitting it, and check the overload exists: some take both content closures or
  neither.
- **Order of children is document order only without `traverseInstances`.** That option
  returns layers last-to-first. Search one level at a time instead, and build lists in a
  loop rather than a fixed `t1..t9`.
- **Hidden layers are skipped.** A lookup for a layer the variant hides returns nothing,
  which is why a design's `Show X` boolean gates the lookup rather than the reverse.
- **Say so when the code can't.** A design state neither platform implements emits a
  `// NOTE:` line; data the design cannot carry (locale formats, an image source) emits
  a `// TODO:`. Never invent a value to fill the gap.

## Sub-parts and parents

A parent reads a child through `findInstance(…)` and `executeTemplate()`. The child
template owns the child's code; the parent only places it. State that belongs to the
parent — which tab is selected, which row is current — travels back through
`metadata: { props: { … } }`, whose values are strings.

Connect an internal `_`- or `.`-prefixed Figma component only when it has a real code
representation the parent cannot otherwise get: `TabItem`, `HistoryTimelineItem`,
`CardHeaderConfig`. `figma/connect/HistoryItem.figma.ts` is the worked example — it
reads its own text, gets its voice from a nested indicator, and hands `current` up.

## Before you publish

```bash
cd figma
npm run check                                     # offline: does the snippet compile?
FIGMA_ACCESS_TOKEN=figd_... npm run validate      # does the template run?
```

`scripts/check-templates.mjs` reads the templates against the Kotlin and Swift sources
and fails on an import that does not resolve, a Lemonade name used without its import, a
named argument no overload takes, Swift labels out of declaration order, and a dead
`// source=` link. It reads source rather than running templates, so it cannot tell
which conditional arguments appear together.

Neither check sees Figma. A renamed property still yields `undefined` silently — only
reading the published snippet back catches that, which is step 3 of
`publish-figma-connect`.

## Anti-patterns

- **Hand-editing a file under `connect*/icons/`, `flags/` or `brand-logos/`.** The next
  generator run overwrites it. Fix `scripts/generate-asset-templates.mjs` or the
  manifest instead.
- **Copying a helper into a second template.** Put it in `figma/shared/` and import it;
  a copy is a fix applied in one place and missed in the other.
- **Adding a Compose-only component without a SwiftUI template.** The SwiftUI panel then
  shows Kotlin. Add a one-line `// NOTE:` template saying the component is Compose-only.
- **Mapping a deprecated Figma component.** Designers migrate off it and the snippet
  teaches the wrong API. Leave it unconnected and say so in `figma/README.md`.
- **Guessing a property name.** `getEnum('◇ Varient', …)` parses fine and yields
  `undefined`. Read the real names with `get_context_for_code_connect`.
- **Pasting a screenshot-derived layout into the snippet.** If the component has no
  parameter for it, the template is re-implementing component UI that will drift.

## References

- `references/template-api.md` — what each handle method returns and how it behaves on
  hidden layers, slots and other labels. Read it when a lookup comes back empty.
- `figma/README.md` — the per-component mapping decisions and the platform quirks
  behind them.
- `publish-figma-connect` — publishing, the two configs, and verifying the live snippet.
- `comment-review` — the comment bar these templates are held to, `.ts` included.
