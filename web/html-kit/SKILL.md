---
name: lemonade-html-prototype
description: Build app prototypes as HTML with the Lemonade HTML kit — real tokens, Figtree, icons and component markup, in light and dark. Use when asked to prototype, mock up or sketch a Lemonade app screen or flow in HTML, or to make a page that looks like an app built with Lemonade.
---

# Lemonade HTML prototype

You build phone screens as plain HTML that look like an app built with Lemonade. Everything visual
comes from this kit. Paths below are relative to the kit folder, the one holding `lemonade-kit.css`.

## Before you build

Read `guidelines/index.md` and every file it lists. The `LDS-` rules say how components are used and
combined, with an example for Compose, SwiftUI and the kit, and a prototype follows the kit's; the `LHK-`
rules say how a prototype is put together. All of them apply, and their exception
lists are complete.

## Where to look things up

| Need | File |
| --- | --- |
| A token, and which one fits | `llms.txt`: every colour, spacing, radius, size, border width, text style and shadow, each colour with what it is for |
| An icon, flag or logo name | `assets.txt`: every name the kit carries; anything else does not exist |
| A kit class | `classes.txt`: every `lmnd-kit-` class, by component; anything else does not exist |
| A component's markup | `gallery.html` |
| Several screens in one page | `examples/flow.html` |

Never read or grep `lemonade-kit.css`: it is generated, and almost all of it is inline artwork. Link
`lemonade-kit.js` after it for the kit's optional behaviour. A script of your own may drive state the markup
can't reach simply, on kit markup only (`LHK-SCRIPT`).

## Workflow

1. **Find the markup.** Read `gallery.html` for each component you need. Copy markup from there or from
   `examples/flow.html`, never from memory (`LHK-KIT-MARKUP`).
2. **Start from `examples/flow.html`.** Copy it to a new file in `examples/` and change its content. Keep
   the stylesheet link, phone frame, screen wrapper, top bar and spacing (`LHK-SCREEN`); a single screen
   keeps one of its screens.
3. **Check.** Open the page in a window wider than 480px, where it shows in the phone frame, in light and
   dark (add `data-lmnd-theme="dark"` to `<html>` to force dark). Open every sheet and toast by its link. Fix anything that doesn't line up with the gallery. If you can't render pages, say
   so in the hand-over.
4. **Review.** Read the page against the guidelines. Fix each violation, or keep it with an exemption
   comment that names the rule and the reason.
5. **Hand over** the page together with `lemonade-kit.css` and `lemonade-kit.js`, which it needs beside it, or the whole kit
   folder. List any exemptions, any role the kit had no component for, and which content is placeholder.

## Limits

- HTML reproduces the look, not native behaviour (scroll physics, gestures, keyboard).
- Components follow the Compose measurements. The phone frame draws an iOS status bar and home indicator,
  but iOS-only app chrome, such as the native navigation bar, isn't covered.
