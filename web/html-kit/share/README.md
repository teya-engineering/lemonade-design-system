# Lemonade HTML kit

Plain HTML and CSS for prototyping Teya app screens. The screens look like the app because they use the
app's own design system, Lemonade: its colours, spacing, radii, type scale, Figtree, icons, flags and
logos, and components measured from the native ones. Every page works in light and dark mode.

Nothing to install. Open a page in a browser and it works.

## Start here

1. Open `gallery.html`. It shows every component next to the markup that draws it, every colour and
   every icon. The switch at the top flips between light and dark.
2. To change a screen the app already has, copy its baseline in `screens/` (`screens/index.md` lists
   them). For a new screen, copy the example in `examples/` closest to it. Rename the copy and edit it in
   any text editor. Refresh the browser to see the change.
3. Follow the rules in `guidelines/`. Start with `guidelines/index.md`.

Keep your pages in `examples/` or `screens/`, so they find `lemonade-kit.css`.

## With an AI assistant

Give the assistant this whole folder, or the zip, and ask for a screen:

> Using the Lemonade HTML kit, prototype a payout details screen: amount, status, a timeline of when it
> was sent and arrived, and the bank account it went to.

`SKILL.md` tells the assistant how to work: read the guidelines, look tokens up in `llms.txt` and assets
in `assets.txt`, start from an example, use only the kit's markup, and check the result in light and
dark. Tools that support skills, such as Claude, can load it as one.

## Sharing a screen

A page needs `lemonade-kit.css` beside it, so share the whole kit folder or the zip. To send one screen as a
single file that opens on its own, build it with Claude using the `lemonade-prototype` skill: it hands
the screen back as one file.

## What's here

| Path | What it is |
| --- | --- |
| `lemonade-kit.css` | Everything: tokens, type scale, components, Figtree, icons, flags and logos. Generated; don't edit it |
| `gallery.html` | Every component with its markup |
| `screens/` | The app's own screens as they are today, to change: Money › Settlements and Settlement details. `screens/index.md` has the site map |
| `examples/` | Screens to start from: Settings, Sales (with a filter sheet), Transactions (with a store picker), Card machines, Recipients, Transaction details, Add recipient |
| `guidelines/` | The rules a prototype follows |
| `llms.txt` | Every token and what it is for |
| `assets.txt` | Every icon, flag and logo name |
| `classes.txt` | Every kit class, by component |
| `SKILL.md` | Instructions for an AI assistant |

## Limits

- It reproduces the look, not native behaviour: no scroll physics, gestures or collapsing top bar.
- Components follow the Compose measurements. The phone frame draws an iOS status bar and home
  indicator, but iOS-only app chrome, such as the native navigation bar, isn't covered.
