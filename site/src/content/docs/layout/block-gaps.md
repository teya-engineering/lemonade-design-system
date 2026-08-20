---
title: Block gaps
description: How much space to leave between blocks — cards, sections, list items, chips and stacked text — and which spacing token each gap comes from.
---

Sections group related content, and the gap between them is what tells a reader where
one group ends and the next begins. Getting these four values right does most of the
work of making a screen legible.

| Gap between | Value | Token |
|---|---|---|
| Sections | 32px | `spacing-800` |
| Cards | 16px | `spacing-400` |
| Medium elements — list items | 12px | `spacing-300` |
| Small elements — tags, chips | 8px | `spacing-200` |
| Stacked text | none | — |

The pattern behind the numbers: **the more self-contained the things you are separating,
the larger the gap.** A section is a whole idea, a chip is a fragment of one.

## Between sections

A section that holds different kinds of element — a heading with a card under it, say —
is separated from the next by **32px (`spacing-800`)**.

This is the biggest gap on the page, and it should be. It is the only one doing
structural work: everything inside a section reads as one idea, and 32px is what says
the next idea has started.

## Between cards

When stacking cards, or any container whose boundaries are visible, the gap is
**16px (`spacing-400`)**.

The container's own edge is already doing some of the separating, so the gap does not
have to work as hard as it does between sections.

## Between medium elements

List items and similar mid-sized elements sit **12px (`spacing-300`)** apart.

## Between small elements

Tags, chips and similar small elements usually sit next to each other rather than
stacked, and take **8px (`spacing-200`)**. At that distance they read as members of one
group rather than as separate controls — which is the point. Pull them further apart and
the grouping falls away.

## Stacked text needs no gap

Labels stacked directly on top of each other need **no gap at all**. Lemonade's text
styles already carry enough line height to separate them, and adding space on top of
that breaks the relationship between a label and the value under it.

For paragraphs, use the text style's own line height as the gap — a blank line's worth,
not an arbitrary value. Same principle: the type is already spaced, so let it do the
work.

:::note
This is the one place where reaching for a spacing token is the wrong instinct. Space
between lines of text belongs to the type, not to the layout — see
[Typography](/lemonade-design-system/foundations/typography/) for the line heights each
style carries.
:::

## Related

- [Layout & rhythm](/lemonade-design-system/layout/rhythm/) — the spacing scale these
  values come from, and when to step off it.
- [Container margins](/lemonade-design-system/layout/container-margins/) — the other
  half of spacing: how far content sits from the edge around it.
- [Space & shape](/lemonade-design-system/foundations/space-and-shape/) — every spacing
  token with its value.
