---
title: Lists
description: Choosing a list item, handling selection, and when a divider helps.
---

Most screens in Teya's products are lists of something — transactions, resources,
settings, people. Lemonade gives you five list-item components, not one, because a row
that displays a balance and a row that toggles a setting are doing different jobs even
when they're the same height.

## Choosing a list item

Both platforms ship the same five:

| Component | What the row does |
|-----------|--------------------|
| `ListItem` | The general-purpose row — a label, optional support text, leading/trailing slots, an optional tap action and chevron. The default choice when nothing more specific fits. |
| `ActionListItem` | The same shape as `ListItem`, with an optional line *above* the label (`topLabel`) and a slot below the support text. Reach for it when a row needs more text stacked than label-plus-support-text. |
| `ResourceListItem` | A label-value pair built for a resource's info — a required leading slot, a value in the trailing position, and an optional tag or badge (`addonSlot`) underneath the value. |
| `ContentListItem` | Display-only label-value pairs, horizontal or vertical. It has no click handler at all — use it for read-only data like "Account holder — John Doe", not for anything tappable. |
| `SelectListItem` | Selection, and only selection. See below. |

Pick by what the row does, not by which one happens to render closest to the mock. A row
that shows a balance and lets you tap through to a statement is a `ResourceListItem`
with `onItemClicked` set, not a `ListItem` with the value crammed into the trailing
slot.

## Selection

`SelectListItem` is the only list item built for choosing. Its `type` — `Single`,
`Multiple`, or `Toggle` — decides which control renders (radio button, checkbox, or
switch) and how a tap behaves: a `Single` item that's already checked ignores the next
tap, so you can't uncheck a radio button by tapping it again.

The whole row is the click target, not just the control. The `onItemClicked` handler is
attached to the entire row's interactive background, so a user can tap the label, the
support text, or the leading icon and get the same result as tapping the switch itself.
Don't shrink the hit area back down to the control — that's undoing work the component
already did for you.

`SelectListItem` has two visual variants: `Plain`, a bare row meant to sit inside a
surrounding `Card` or list surface, and `Outlined`, which draws its own bordered
container with a brand-tinted background when selected, for items that need to stand
alone rather than stack inside a shared surface.

## Dividers

Every list item takes a `showDivider` flag, off by default. A divider is for separating
things that are actually different — a settings section from the one below it, a list
from a summary row that follows it. A uniform list of like items — ten transactions,
twenty contacts — almost always reads better with padding alone; a divider under every
row turns into visual noise at that density.

:::caution
Tabs are not symmetric here. On SwiftUI, `Tabs` takes its own `showDivider` flag
(defaulting to `true`) for the rule under the tab strip. On KMP, `Tabs` always draws
that rule — there's no parameter to turn it off. If you're building a tab strip that
needs to blend into the surface below it, that's a KMP gap to route around rather than
a prop you're missing.
:::

## Long content

Every list item's `label` and `supportText` default to no line limit and
`TextOverflow.Clip` — nothing truncates unless you opt in with `labelMaxLines` or
`supportTextMaxLines`. That default is deliberate: a row should wrap rather than cut off
content that's meant to be read.

Truncation is for identifiers — a long reference number, a UUID, a file name that's
mostly noise past the first twenty characters. It's the wrong tool for prose. If a
support line genuinely doesn't fit in the space you have, let the row grow instead of
lopping the sentence off with an ellipsis.
