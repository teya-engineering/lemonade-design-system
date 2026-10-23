# Top bar

## `LDS-TOP-BAR-ACTIONS` · Top bar buttons are Solid or Subtle

**Rule.** A top bar's buttons say what each action is:

- **The primary action** — the one thing the screen is for, such as adding or creating — is filled with
  the brand colour. Compose: `LemonadeUi.IconButton(variant = Primary, type = Solid, size = Small,
  shape = Circular)`. SwiftUI: `ToolbarIconButton(prominence: .filled(tint:))` with the brand colour.
- **Every other action** is subtle. Compose: `IconButton(variant = Neutral, type = Subtle, size = Small,
  shape = Circular)`. SwiftUI: `ToolbarIconButton` with the default `.plain`, which the system bar draws as
  its own filled control.
- **Back and Close** are secondary actions, so they are subtle too. In Compose that is
  `NavigationAction(filled = true)`; the default `filled = false` draws a ghost button.

**Why.** A filled button reads as something to press. A ghost icon on the top bar's background looks like
a label rather than a control, and two treatments for the same weight of action make the reader work out
which one matters. Solid marks the one action the screen wants; subtle keeps every other action visible
without competing with it.

**Exceptions.** None. A ghost button never goes in a top bar.

**Do / don't.** A Primary · Solid add button next to a Neutral · Subtle settings button, under a filled
back button. Not `IconButton(type = Ghost)` in the trailing slot, and not a `NavigationAction` left at
`filled = false`.
