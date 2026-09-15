---
title: Cards and containers
description: How to make a container read as a container — the three background approaches, and the radius to use on the page and nested inside it.
---

A card has to separate itself from the page behind it. There are three ways to do that,
and a rule for how round its corners should be.

## Making a container visible

### Lighter than the page

**Prefer this one.** Fill the container with a colour lighter than the page background —
`bg-subtle` for the page, `bg-default` for the container — so the content inside it comes
forward.

### Darker than the page

The reverse also works: `bg-default` for the page, `bg-subtle` or `bg-elevated` for the
container. It is a legitimate choice, but it crowds the interface more quickly, because
every container adds visual weight instead of lifting content out of the page. Reach for
it when you have a reason to.

### Outlined

Give the page and the container the same fill, and separate them with a border —
`border-neutral-low`. The lightest of the three: nothing changes tone, so a screen full
of outlined containers stays calm.

:::note
All three are valid. The choice is about how much weight a screen can carry, not about
which is correct — a dense screen usually wants outlines, a sparse one can afford fills.
:::

## Radius

A card or visible container of any considerable size, sitting **directly on the page**,
takes a **24px (`radius-600`)** radius.

:::note
The token export also carries `radius-container-default`, which resolves to the same
24px. If you are picking a radius for a container, that is the name that says what you
mean — see [Semantic tokens
first](/lemonade-design-system/standards/semantic-tokens/).
:::

### Containers inside containers

A rounded box nested inside another rounded box cannot reuse the outer radius — it would
sit inside the parent's curve and read as slightly wrong without it being obvious why.
Subtract the padding between them:

```
inner_radius = outer_radius − container_padding
```

So an outer radius of 16px with 4px of padding gives an inner radius of 12px:

```
16px − 4px = 12px
```

In tokens, nested containers usually land on `radius-400` (16px) or `radius-300` (12px),
depending on the parent.

## Related

- [Container margins](/lemonade-design-system/layout/container-margins/) — how far the
  content inside a container sits from its edge.
- [Block gaps](/lemonade-design-system/layout/block-gaps/) — how far apart to stack the
  containers themselves.
- [Space & shape](/lemonade-design-system/foundations/space-and-shape/) — every radius
  token with its value.
- [Elevation](/lemonade-design-system/foundations/elevation/) — shadows, for when a
  container needs to lift off the page rather than just separate from it.
