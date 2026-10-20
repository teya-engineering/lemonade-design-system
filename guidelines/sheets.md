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
