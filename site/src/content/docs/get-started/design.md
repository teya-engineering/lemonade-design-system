---
title: Design
description: How Lemonade is set up in Figma — the libraries, what each one holds, and how to enable them in a file.
---
Lemonade is designed in **Figma**. The libraries there are not a picture of the system;
they are the system's other half. The values in them are exported and generated into
the platform code, so a token you pick in Figma is the same token an engineer writes.

This page gets you set up. Everything else — the values, the reasoning, the assembled
solutions — is linked at the bottom.

## How the libraries are organised

Four published libraries, split by what they hold rather than by who uses them.


| Library           | What it holds                                                                                                          |
| ----------------- | ---------------------------------------------------------------------------------------------------------------------- |
| **Components**    | The components — buttons, inputs, cards, list items, top bars and the rest, with their variants and states.            |
| **Foundations**   | The semantic tokens and the scales behind them: colour, typography, spacing, radius, elevation, opacity, border width. |
| **Icons**         | The icon set.                                                                                                          |
| **Global Colors** | The raw colour ramps — `green-lime/500`, `blue/alpha/200` and their neighbours.                                        |


The split between the last two is the one worth understanding.
**Global Colors is the primitive layer** — every ramp, unopinionated about meaning.
**Foundations sits on top and names the job**: `bg-critical-subtle`, `content-secondary`.
Design against Foundations, the same way engineers are asked to. A primitive picked
directly is a light-mode-only decision that will not survive a theme change or a palette
update — the reasoning is in
[Semantic tokens first](/lemonade-design-system/standards/semantic-tokens/).

## Enabling them in a file

**New files usually have them already.** The core libraries are set as team defaults, so
a file created in the team inherits them and there is nothing to do.

If a file is missing one — an older file, or one that came from elsewhere:

1. Open the **Assets** panel in the left sidebar.
2. Click the **Libraries** button (the book icon).
3. Enable the library you need.

For ordinary product work, enable **Components**, **Foundations** and **Icons**. Global
Colors is there for the rarer job of defining a new semantic token, and can stay off the
rest of the time.

## Where to go next

- **The values** — [Foundations](/lemonade-design-system/foundations/colour/) has every
colour, type style, space and radius, with the name to use on each platform.
- **The reasoning** — [Standards](/lemonade-design-system/standards/semantic-tokens/)
covers semantic tokens, theming and dark mode, layout rhythm and the accessibility
baseline.
- **Whole solutions** — [Patterns](/lemonade-design-system/patterns/forms/) shows forms,
lists, empty states and errors already assembled, with the decisions written down.
- **Web prototypes** — [Prototyping with Lemonade](/lemonade-design-system/prototyping/)
explains what to do until official web support lands.

