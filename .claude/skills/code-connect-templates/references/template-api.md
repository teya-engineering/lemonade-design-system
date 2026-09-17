# Template API

What a template can read from the selected instance, and how each call behaves at the
edges. Types live in `figma/node_modules/@figma/code-connect/figma-types.d.ts`; this
file records the behaviour that the types don't state and that cost time to find.

`figma.selectedInstance` is the instance the developer selected. Every lookup below is a
method on it, or on another instance handle reached from it.

## Properties

| Call | Returns | Notes |
|---|---|---|
| `getString('✍️ Label')` | the text | Names are case- and emoji-sensitive. `✍️ Label` and `↪ ✍️ Label` are different properties. |
| `getBoolean('◉ Show Leading')` | `true`/`false` | Second argument maps the two cases to values. |
| `getEnum('◇ Size', { Large: 'Large' })` | the mapped value | **`undefined` when the key is missing** — a renamed variant degrades silently, it does not fail. |
| `getInstanceSwap('🧩 Icon')` | instance handle | The swapped-in component. Inlines correctly, unlike a slot. |
| `getSlot('🧩 Content')` | slot result | Interpolating it prints React JSX. Use `connectedInstances` (below). |

A Figma boolean modelled as a two-option variant is read with `getEnum`, not
`getBoolean` — `◉ Is Loading` is a variant on most Lemonade sets.

## Layers

| Call | Returns | Notes |
|---|---|---|
| `findText('Label')` | text handle | Check `.type === 'TEXT'`, then read `.textContent`. |
| `findInstance('Card Heading')` | instance handle | Check `.type === 'INSTANCE'`. |
| `findLayers(fn)` | array | Document order. |
| `findLayers(fn, { traverseInstances: true })` | array | **Last-to-first.** Search level by level when order matters. |

**Hidden layers are skipped by every lookup.** A `Show X` boolean that hides a layer
makes the layer unreachable, so gate the lookup on the boolean rather than expecting an
empty result to mean the same thing. This is also why a component's own library variants
often can't exercise a branch: if every variant hides the slot, only a real instance
elsewhere in the file will.

## Slot content

```ts
const found = instance.getSlot('↪ 🧩 Leading')
const children = found?.connectedInstances ?? []
```

`connectedInstances` lists the instances in the slot that have a Code Connect mapping —
**under any label**. A component mapped only under the unrelated `React` label appears
here and renders as JSX. Filter on `child.codeConnectId() !== null`, which is `null` for
a template that isn't ours. `figma/shared/render.ts` does this.

## Children

`child.executeTemplate()` runs the child's template for the current label and returns:

- `example` — an array of sections. A `CODE` section's `code` is a string; re-indent by
  replacing its newlines before interpolating, or the nested snippet keeps its own
  indentation.
- `metadata` — what the child's template declared, including `props`. Values are
  strings: `props: { selected: 'true' }`.

Each section carries `nestedImports`: the child's own imports, **not** its children's.
Collect them for the parent to re-export, which is what `renderer` does.

An instance handle reached with `findInstance` supports the same property calls as the
selected instance, so a parent can read a child's properties directly rather than going
through `executeTemplate()` when it only needs a value.

## Output

`example` takes a tagged template — `figma.kotlin` or `figma.swift`. Interpolating a
plain string works in either; interpolating a section array (from `executeTemplate()` or
another tagged template) **requires** the tagged form, otherwise it prints
`[object Object]`.

`imports` is a plain array of strings, evaluated after `example`, so it can depend on
what the example rendered.

A tag result carries its own `language` — `figma.kotlin``.language` is `'kotlin'`,
`figma.swift``.language` is `'swift'` (both checked in Dev Mode). `shared/render.ts`
reads it to escape per language, and throws on anything else rather than guessing,
because guessing wrong drops Kotlin's `$` escape silently.

`id` is a stable kebab-case identifier, unique across both directories.
`metadata.nestable` says the snippet is safe to inline in a parent.

## Reading a design before writing a template

`get_context_for_code_connect` (Figma MCP) on the component set gives the real property
names, variant options and the descendant tree — the names to paste into `getEnum` and
`findInstance`. `get_code_connect_map` reads back what is published; it is keyed by
**variant** node ids, so the set's own id is absent even when the publish worked.
