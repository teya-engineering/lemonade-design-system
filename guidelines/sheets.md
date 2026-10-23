# Sheets

## `LDS-SHEET-CHROME` · The scaffold owns the chrome

**Rule.** A sheet's title and close button belong to the scaffold. A sheet closes with `Close`, never
`Back`.

**Why.** Hand-built chrome gets the title's typography, its collapse behaviour and the close button's
position slightly wrong, and every sheet gets them wrong differently. `Back` on a sheet promises a
screen underneath that the gesture does not go to.

**Exceptions.** A sheet that is genuinely a step inside a sheet-hosted flow may carry `Back` to reach
the previous step, as long as the flow's root still closes with `Close`.

**Do / don't.** The scaffold's title and close slots. Not a `Text` and an icon button laid out at the
top of the sheet's content, and not a drag handle on top of the scaffold's own spacing.

## `LDS-SHEET-BAR-ACTIONS` · A sheet's bar follows the top bar's button rules

**Rule.** The buttons in a sheet's bar — the scaffold's close slot and any actions beside it — follow
`LDS-TOP-BAR-ACTIONS`, exactly as in a screen's top bar. In Compose each is
`LemonadeUi.IconButton(size = Small, shape = Circular)`: the sheet's primary action, if the bar has one, is
`variant = Primary, type = Solid`, and every other action, Close included, is `variant = Neutral,
type = Subtle`. In SwiftUI a sheet that uses a navigation bar follows the top bar's `ToolbarIconButton`
prominence: `.filled(tint:)` for the primary action, `.plain` for the rest.

**Why.** A sheet's bar sits where a top bar sits and does the same job. Different buttons there make the
same action look different depending on whether it opened as a screen or as a sheet.

**Exceptions.** None. A text action such as "Reset" or "Done" is an icon button with that label as its
content description, or a button in the sheet's action block at the bottom.

**Do / don't.** A Neutral · Subtle reset `IconButton` beside the Subtle close button. Not a `Text` link in
the bar, and not `IconButton(type = Ghost)`.
