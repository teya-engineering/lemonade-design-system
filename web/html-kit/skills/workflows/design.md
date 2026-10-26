# Designing a screen

You build phone screens of the Teya app as HTML that look like the shipping app. Everything visual comes
from this skill's files; you never invent styles.

## Files

| Path | What it is |
| --- | --- |
| `assets/lemonade-kit.css` | The whole kit: tokens, type, components, fonts, icons, flags, logos. Link it; never read or grep it — it is mostly inline artwork |
| `assets/lemonade-kit.js` | The kit's optional behaviour: overlays that close on Esc, panels that switch, titles that fold. Link it after the stylesheet (`LHK-SCRIPT`); never write script of your own |
| `assets/gallery.html` | Every component with the markup that draws it. Your source for markup |
| `assets/examples/` | Finished screens to start from: `flow.html` (two screens in one file, `LHK-SCREENS`), `settings.html` (menu), `sales.html` (widgets, filter sheet), `transactions.html` (feed, multi-select picker), `card-machines.html` and `recipients.html` (resource lists), `transaction-details.html`, `add-recipient.html` (form) |
| `assets/screens/` | The app's own screens copied as they are in production, faults included: the baselines to change |
| `references/screens/` | How those screens work, and the app's site map. Start with `index.md` |
| `references/guidelines/` | The rules a screen follows. Start with `index.md` |
| `references/llms.txt` | Every token, and what each colour is for |
| `references/assets.txt` | Every icon, flag and logo name; anything else does not exist |
| `references/classes.txt` | Every `lmnd-kit-` class, by component; anything else does not exist |
| `scripts/build.py` | Turns a page into one self-contained file |

Paths inside the guidelines (`gallery.html`, `examples/`, `lemonade-kit.css`) are relative to `assets/`. A
page a screen description names, such as `money-settlements.html`, is in `assets/screens/`.

## Workflow

1. **Set up.** Copy `assets/` to a working folder. Work only in that copy.
2. **Read the rules that apply.** Always read `references/guidelines/index.md`, `app-principles.md`,
   `prototypes.md` and `cards.md`. Then read the file for each part of your screen: `lists.md` for rows, `buttons.md` for
   buttons, `top-bar.md` for the top bar, `sheets.md` for a sheet or dialog, `widgets.md` for dashboard
   widgets, `details.md` for a details screen, `list-screens.md` for a feed, a list of things or a picker sheet, `text.md` for intros, `colour.md` before any custom colour.
   Their exception lists are complete.
3. **Start from the app, or from the closest example.**
   - **A change to a screen the app already has:** read `references/screens/index.md`. If the screen is
     described there, read its description, copy its baseline from `assets/screens/` under a new name, and
     change only what was asked. Everything else stays as the baseline has it, byte for byte, including
     its "as in production" exemptions: those are the app's faults today, and fixing them unasked makes the
     prototype differ from production in ways nobody chose. If the screen isn't described, say so, and
     build it from the site map, the closest example and any screenshot you were given.
   - **A new screen:** copy the closest example into the working `examples/` folder under a new name and
     change its content. Keep its stylesheet link, `lmnd-kit-device` frame, `lmnd-kit-screen`, top bar and
     spacing.
   - **Either way,** use the site map in `references/screens/index.md` for where the screen sits: its
     tab, what leads to it and what it leads to.
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
   It writes `<page>.standalone.html` with the stylesheet and the kit's script inlined and only the assets the page uses, and
   it fails on an icon, flag or logo name that doesn't exist.
   A flow of several screens is one page with its screens side by side (`LHK-SCREENS`, `examples/flow.html`),
   so it hands over as one file that clicks through.
7. **Hand over** the `.standalone.html` file. Say which content is placeholder, list any exemptions, and
   name any role the kit had no component for. When it started from a baseline, list what changed against
   it, block by block, and say which baseline and which date it was copied from. End with the line "Built with lemonade-prototype
   {{VERSION}}", so a screen from an old copy of the skill is easy to spot.

## Copy

British English, plain and short, in Teya's voice. Realistic data — names, `£` amounts with pence, dates
like `23 Oct 2026` — never lorem ipsum. `•` between items, `-£12.00` for a negative amount.

## Limits

HTML reproduces the look, not native behaviour: no gestures or scroll physics. The kit's script adds a
little (`LHK-SCRIPT`), and the page must work without it.
Components follow the Compose measurements; the phone frame draws an iOS status bar.
