---
title: Container margins
description: How far content sits from the edge it lives inside — the device viewport, a card, or a container whose padding you cannot see.
---

Content is spaced **16px from the edge it sits in** — the device viewport, or the
container around it. That one number is most of what layout consistency comes down to
on a screen.

## Visually 16px, not structurally 16px

All content must be **visually** spaced 16px from the device's or the container's edge.
Readability, comfort and consistency across screens all follow from it.

:::note
"Visually" means the content *itself* respects the margin, not necessarily the outer
container. If a component includes an invisible container, wrapper, or interactive
layer, you have to account for that internal padding when you position it.
:::

That distinction is the whole subject. A frame whose edge sits at 16px is not the same
as content that *looks* 16px in — and the reader only ever sees the second one.

### At the device edge

| | |
|---|---|
| **Do** | Position content visually 16px from the edge of the device. |
| **Don't** | Position content visually more than 16px from the edge of the device. |

### At a container edge

For content inside a large visible container such as a card, use the same value for the
vertical padding as for the horizontal.

| | |
|---|---|
| **Do** | Keep the same visual padding on all sides. |
| **Don't** | Use different visual paddings on different sides. |

:::note
Smaller containers, and anything you are aligning optically, can take less. Optical
alignment sometimes needs a value that is not on the scale at all. The recommended
default is a starting point — what matters is that the layout looks right. Pay
attention to the details.
:::

## When the padding is invisible

Interactive elements usually carry padding you cannot see — a tap target, a hover or
pressed background, a safe area. When a container has inset padding and that padding is
not visible, its placement has to compensate, so that the **visible content still sits
16px from the screen edge**.

**Lemonade's interactive components already do this.** They are built with the 16px
visual margin in mind, so you do not need to wrap them in a container to compensate.
A list item that shows an interaction background when tapped already handles its own
safe area and interactive container: place it flush and the content inside will land in
the right place.

Wrapping one anyway is the common mistake. It pushes the visible content to 32px and
leaves the row misaligned with everything above and below it.

### Inside a container that already has padding

For content inside a visual container — a card, say — that already has its own padding,
do not add margin values on top.

| | |
|---|---|
| **Do** | Position content visually 16px from the parent container's edge, aligned with the other content in that container. |
| **Don't** | Push content further than 16px from a container with visible boundaries, or misalign it from the content beside it. |

## Related

- [Layout & rhythm](/lemonade-design-system/layout/rhythm/) — the spacing scale these
  values come from, and when to step off it.
- [Lists](/lemonade-design-system/patterns/lists/) — list rows are where inset padding
  and edge margins meet most often.
