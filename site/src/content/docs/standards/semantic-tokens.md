---
title: Semantic tokens first
description: Why Lemonade asks you to name the job a colour is doing rather than the colour itself.
---

Lemonade gives you two layers of colour. The **primitive** palette is the raw material —
`green-lime/500`, `blue/alpha/200`. The **semantic** layer sits on top and names the job:
`bg-critical-subtle`, `content-secondary`, `border-brand`.

Always reach for the semantic layer.

## Why it matters

A primitive says what a colour *is*. A semantic token says what it is *for*. That
difference is what lets one codebase render correctly in light and dark, survive a
palette change, and stay legible when someone turns contrast up.

When you write `green-lime/500` into a screen, you have made a promise that this
particular green is correct in every theme, forever. It isn't. `content-positive`
resolves to `#497d00` in light and `#7ccf00` in dark, because the same green that reads
well on white disappears on near-black.

## What good looks like

Name the role, and both themes come for free:

```kotlin
// Do
LemonadeUi.Text(
    text = "Payment received",
    color = LemonadeTheme.colors.content.contentPositive,
)

// Don't — this is a light-theme-only decision
LemonadeUi.Text(
    text = "Payment received",
    color = Color(0xFF497D00),
)
```

```swift
// Do
LemonadeUi.Text("Payment received", color: LemonadeTheme.colors.content.contentPositive)
```

## Choosing the right one

Work down the name. The first segment is the property you're setting:

| Prefix | Sets | Example |
|--------|------|---------|
| `bg-` | A surface fill | `bg-critical-subtle` |
| `content-` | Text and icons | `content-secondary` |
| `border-` | A stroke | `border-neutral-low` |

The middle is the *voice* — `positive`, `caution`, `critical`, `info`, `neutral`,
`brand` — and it should match the meaning of what you're building, not the colour you
had in mind. A destructive action is `critical` because it is destructive, not because
it happens to be red.

The suffix is the weight. `-subtle` is a tinted background you put content on top of;
the unsuffixed version is the full-strength colour. Pair them: `bg-critical-subtle` with
`content-critical`, never `bg-critical` with `content-critical`.

## When you may go lower

Rarely, and deliberately:

- **You are building a component for Lemonade itself.** New components define their own
  semantic tokens; that work happens with design, in Figma, and lands as a token export.
- **You are rendering brand artwork** — a partner logo, an illustration — where the
  colour is content rather than interface.

Outside those two cases, a primitive in product code is a bug that hasn't been noticed
yet. If nothing in the semantic layer fits what you're building, that's a gap worth
raising with the design systems team rather than routing around.

## Fixed tokens

A handful of tokens resolve to the same value in both themes, and their names say so:
`bg-always-dark`, `content-always-light`, `content-critical-always-on-color`. These are
for surfaces that must not flip — a photo overlay, a coloured banner that keeps its
colour in dark mode. Using one because you want to pin a colour is a misuse; using one
because the surface underneath is genuinely fixed is correct.
