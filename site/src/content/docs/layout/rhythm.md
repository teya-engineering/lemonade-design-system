---
title: Layout & rhythm
description: The spacing scale, and why staying on it matters more than any individual value.
---

Lemonade has one spacing scale, and padding, gaps and margins all come from it. The
scale itself matters less than staying on it: a screen built entirely from scale values
reads as deliberate, and a screen with even one arbitrary number dropped in reads as
slightly off, even if nobody can say exactly why.

## One scale

There's no separate scale for padding versus gaps versus margins — it's the same
sequence of steps wherever you use it. See
[Space & shape](/lemonade-design-system/foundations/space-and-shape/) for the full set
of values.

The reason to care isn't aesthetic purism. Two screens built from the same scale sit
together without friction, because every rhythm on the page is a multiple of the same
unit. Mix in a value that isn't on the scale — even one that's only a couple of pixels
off — and the screen next to it starts to look misaligned, because it is.

## Choosing a step

Within a component — around an icon, between a label and its value — reach for
`spacing-200` or `spacing-300`. Between related blocks on the same screen, `spacing-400`
is the default. Between distinct sections, step up to `spacing-600` or beyond.

When a gap feels like it wants something between two steps, that's the scale telling you
to step up, not a reason to invent a value. A slightly more generous gap at the next
step almost always reads better than a precise one that happens to sit off the scale.

## Space belongs to the container

A component owns its internal padding — the gap between a field and its own support
text, the padding inside a button. That space is not something a consumer reaches in and
overrides from outside.

If the space inside a component looks wrong, that's a component bug worth reporting, not
a padding value to fight from the outside. Overriding it locally fixes the one screen
you're looking at and leaves every other use of that component with the same problem,
unfixed and now inconsistent with the one you patched.

## When the scale does not fit

There are real exceptions. Optical adjustments — nudging an icon a pixel to sit visually
centred rather than mathematically centred, a single-pixel correction to stop a hairline
border from looking blurry — exist, and they don't come from the spacing scale.

These are deliberate, made once by someone looking carefully at the result, not a general
licence to leave the scale whenever a value is inconvenient. If you're reaching for a
pixel-level fix because the scale doesn't have what you want, that's worth being sure
about before you make it — most of the time the answer is a different step, not a value
off the scale.
