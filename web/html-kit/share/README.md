# Lemonade HTML kit

Plain HTML and CSS for prototyping Teya app screens. The screens look like the app because they use the
app's own design system, Lemonade: its colours, spacing, radii, type scale, Figtree, icons, flags and
logos, and components measured from the native ones. Every page works in light and dark mode.

Nothing to install. Open a page in a browser and it works.

## Start here

1. Open `gallery.html`. It shows every component next to the markup that draws it, every colour and
   every icon. The switch at the top flips between light and dark.
2. Make a new page next to `gallery.html`, so it finds `lemonade-kit.css`, and copy the markup of each
   component you need from the gallery. Edit it in any text editor and refresh the browser to see the
   change.

## Sharing a screen

A page needs `lemonade-kit.css` beside it, so share the whole kit folder or the zip.

## What's here

| Path | What it is |
| --- | --- |
| `lemonade-kit.js` | Optional behaviour: sheets close on Esc, tabs switch panels, titles fold on scroll. Pages work without it |
| `lemonade-kit.css` | Everything: tokens, type scale, components, Figtree, icons, flags and logos. Generated; don't edit it |
| `gallery.html` | Every component with its markup |
| `llms.txt` | Every token and what it is for |
| `assets.txt` | Every icon, flag and logo name |
| `classes.txt` | Every kit class, by component |

## Limits

- It reproduces the look, not native behaviour: no scroll physics, gestures or collapsing top bar.
- Components follow the Compose measurements. The phone frame draws an iOS status bar and home
  indicator, but iOS-only app chrome, such as the native navigation bar, isn't covered.
