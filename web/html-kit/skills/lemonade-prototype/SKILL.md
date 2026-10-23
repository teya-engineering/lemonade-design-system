---
name: lemonade-prototype
description: Prototype Teya app screens and flows as HTML that looks like the app, using Lemonade's tokens, components and icons. Use for any Teya app mock-up, screen, flow or prototype.
---

# Lemonade prototype

You build phone screens of the Teya app as HTML that look like the shipping app. Everything visual comes
from this skill's files; you never invent styles.

## Files

| Path | What it is |
| --- | --- |
| `assets/lemonade-kit.css` | The whole kit: tokens, type, components, fonts, icons, flags, logos. Link it; never read or grep it — it is mostly inline artwork |
| `assets/gallery.html` | Every component with the markup that draws it. Your source for markup |
| `assets/examples/` | Finished screens to start from: `settings.html`, `sales.html` (widgets, filter sheet), `transaction-details.html`, `add-recipient.html` (form) |
| `references/guidelines/` | The rules a screen follows. Start with `index.md` |
| `references/llms.txt` | Every token, and what each colour is for |
| `references/assets.txt` | Every icon, flag and logo name; anything else does not exist |
| `references/classes.txt` | Every `lmnd-kit-` class, by component; anything else does not exist |
| `scripts/build.py` | Turns a page into one self-contained file |

Paths inside the guidelines (`gallery.html`, `examples/`, `lemonade-kit.css`) are relative to `assets/`.

## Workflow

1. **Set up.** Copy `assets/` to a working folder. Work only in that copy.
2. **Read the rules that apply.** Always read `references/guidelines/index.md`, `app-principles.md`,
   `prototypes.md` and `cards.md`. Then read the file for each part of your screen: `lists.md` for rows, `buttons.md` for
   buttons, `top-bar.md` for the top bar, `sheets.md` for a sheet or dialog, `widgets.md` for dashboard
   widgets, `details.md` for a details screen, `text.md` for intros, `colour.md` before any custom colour.
   Their exception lists are complete.
3. **Start from the closest example.** Copy it into the working `examples/` folder under a new name and
   change its content. Keep its stylesheet link, `lmnd-kit-device` frame, `lmnd-kit-screen`, top bar and spacing.
4. **Compose with kit markup only.** Copy each component's markup from `gallery.html`. Look tokens up in
   `references/llms.txt`, asset names in `references/assets.txt` and classes in `references/classes.txt`. Screen-specific layout goes in a
   page `<style>` using `var(--lmnd-*)` tokens only.
5. **Check.** Render the page if you can, in a window at least 520px wide so it shows in its phone frame,
   and keep every render in a `shots/` folder in your working copy. Render copies rather than editing the
   page: one with `data-lmnd-theme="dark"` on `<html>` for dark mode, one with
   `<style>.lmnd-kit-device{height:2400px}</style>` in `<head>` to see everything below the fold, and one per
   sheet or toast with its `#id` at the end of the URL to open it. Then read the page against the rules
   you loaded and fix each violation, or keep it with an exemption comment that names the rule and why.
6. **Make one file.** Run `python3 scripts/build.py <page.html>`.
   It writes `<page>.standalone.html` with the stylesheet inlined and only the assets the page uses, and
   it fails on an icon, flag or logo name that doesn't exist.
7. **Hand over** the `.standalone.html` file. Say which content is placeholder, list any exemptions, and
   name any role the kit had no component for.

## Copy

British English, plain and short, in Teya's voice. Realistic data — names, `£` amounts with pence, dates
like `23 Oct 2026` — never lorem ipsum. `•` between items, `-£12.00` for a negative amount.

## Limits

HTML reproduces the look, not native behaviour: no gestures, scroll physics or collapsing top bar.
Components follow the Compose measurements; the phone frame draws an iOS status bar.
