---
title: Lists
description: Stacking list items — why interactive rows need no spacing from you, why static ones do, and when a list should carry dividers.
---

Lists come in two kinds, and they want opposite things from you. Interactive rows bring
their own spacing; static rows bring none. Most list layout problems come from treating
one like the other.

## Interactive items stack on their own

Lemonade's interactive list items — **Action List Item**, **Resource List Item** and
**Selection List Item** — already carry the padding, margin and divider they need,
because they have to reserve room for an interaction feedback area.

So stack them and stop. No extra margins, no gaps, no dividers drawn by hand. To show a
divider between rows, turn on the item's **Show Divider** property (`showDivider` in
code) rather than adding one yourself.

This holds whether the list sits **directly on the page** or **inside a container**. The
item does not change; only what is behind it does.

:::note
Wrapping an interactive row to add spacing is the common mistake. The row already
accounts for the 16px visual margin — see [Container
margins](/lemonade-design-system/layout/container-margins/) — so wrapping it pushes the
content in twice and breaks alignment with everything around it.
:::

## Static items need spacing from you

Static items — **Content Item** — carry no extra padding, margin or divider. They have
no interaction feedback area, so nothing reserves space on their behalf and the spacing
is yours to set.

Wrap them in a container with **16px (`spacing-400`) on all sides**, and put **16px**
between items.

|           |                                                                                                 |
| --------- | ----------------------------------------------------------------------------------------------- |
| **Do**    | Wrap static items in a container with an even 16px margin on all sides, and 16px between items. |
| **Don't** | Leave the spacings looking uneven or unbalanced across the four directions.                     |

:::note
You will sometimes need to compensate optically — a value that is mathematically equal
can still look wrong. That is fine. What matters is that the finished result reads as
even, not that every number matches.
:::

## When to use dividers

As a rule of thumb, use dividers in **interactive** lists, especially dense ones.

### Interactive lists

Good use cases:

- Dense or text-heavy lists where the items look alike
- Data lists — transactions, settlements, settings
- Long lists, where a visual rhythm helps someone keep their place

:::note
In these cases dividers are structure, not decoration. They exist to help someone track
across a row and find the boundary between one item and the next.
:::

### Static lists

Good use case: separating groups of content inside cards and containers — a details
panel where a few related fields belong together and the next few belong apart.

## Related

- [Container margins](/lemonade-design-system/layout/container-margins/) — why an
  interactive row already sits correctly against the edge.
- [Block gaps](/lemonade-design-system/layout/block-gaps/) — the 12px gap for medium
  elements, and the rest of the gap scale.
- [Lists pattern](/lemonade-design-system/patterns/lists/) — assembling a whole list
  screen, including empty and loading states.
