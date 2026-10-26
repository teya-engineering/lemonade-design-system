---
name: lemonade-html-prototype
description: Build Teya app prototypes as HTML with the Lemonade HTML kit — real tokens, Figtree, icons and component markup, in light and dark. Use when asked to prototype, mock up or sketch a Teya app screen or flow in HTML, or to make a page that looks like the Teya app.
---

# Lemonade HTML prototype

You build phone screens of the Teya app as plain HTML that look like the shipping app. Everything visual
comes from this kit. Paths below are relative to the kit folder, the one holding `lemonade-kit.css`.

## Before you build

Read `guidelines/index.md` and every file it lists. The `LDS-` rules say how components are used and
combined; the `APP-` rules cover the Teya app's own components and screen kinds, such as dashboard widgets and
details screens; the `LHK-`
rules say how a prototype is put together. All of them apply, and their exception
lists are complete.

## Where to look things up

| Need | File |
| --- | --- |
| A token, and which one fits | `llms.txt`: every colour, spacing, radius, size, border width, text style and shadow, each colour with what it is for |
| An icon, flag or logo name | `assets.txt`: every name the kit carries; anything else does not exist |
| A kit class | `classes.txt`: every `lmnd-kit-` class, by component; anything else does not exist |
| A component's markup | `gallery.html`, then `examples/` |
| An app screen as it is today, and where screens sit | `screens/index.md`: the site map, and the screens described with baselines to change |

Never read or grep `lemonade-kit.css`: it is generated, and almost all of it is inline artwork. Link
`lemonade-kit.js` after it for the kit's optional behaviour, and write no script of your own (`LHK-SCRIPT`).

## Workflow

1. **Find the markup.** Read `gallery.html` for each component you need. Copy markup from there or from
   `examples/`, never from memory (`LHK-KIT-MARKUP`).
2. **Start from the app, or from the closest example.** For a change to a screen `screens/index.md`
   describes, copy its baseline from `screens/` to a new file there, read its description, and change only
   what was asked; its "as in production" exemptions stay. For a new screen, copy `examples/<screen>.html`
   to a new file in `examples/` and change the content. Either way keep the stylesheet link, phone frame,
   screen wrapper, top bar and spacing (`LHK-SCREEN`).
3. **Check.** Open the page in a window wider than 480px, where it shows in the phone frame, in light and
   dark (add `data-lmnd-theme="dark"` to `<html>` to force dark). Open every sheet and toast by its link. Fix anything that doesn't line up with the examples. If you can't render pages, say
   so in the hand-over.
4. **Review.** Read the page against the guidelines. Fix each violation, or keep it with an exemption
   comment that names the rule and the reason.
5. **Hand over** the page together with `lemonade-kit.css` and `lemonade-kit.js`, which it needs beside it, or the whole kit
   folder. List any exemptions, any role the kit had no component for, and which content is placeholder.

## Limits

- HTML reproduces the look, not native behaviour (scroll physics, gestures, keyboard).
- Components follow the Compose measurements. The phone frame draws an iOS status bar and home indicator,
  but iOS-only app chrome, such as the native navigation bar, isn't covered.
