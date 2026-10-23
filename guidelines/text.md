# Text

## `LDS-TEXT-INTRO` · An intro is short and secondary

**Rule.** An intro sits below the title, runs to at most two lines, and uses `bodySmallRegular` in
`contentSecondary`. The same holds for any description or subheading under a heading.

**Why.** An intro is read on the way to the content. Past two lines it becomes the content, pushing
what the screen is actually for below the fold. In `contentPrimary`, or at a larger body size, it
competes with the title it sits under.

**Exceptions.** None. Longer explanation belongs in the content — a card of list items, or its own
section — not above it.

**Do / don't.** One or two lines of `bodySmallRegular` in `contentSecondary` under the title. Not
`bodyMediumRegular` or `bodyLargeRegular`, not a four-line paragraph, and not a second paragraph
beneath the first.

## `LDS-TEXT-STYLE` · Text uses a typography token

**Rule.** Every piece of text takes one of `LemonadeTheme.typography`'s styles, in a semantic content
colour. No font size, weight, line height or letter spacing is set at the call site.

**Why.** A style carries the family, size, weight, line height and tracking as one decision. Changing
one of them by hand makes a style the system doesn't have, which drifts when the scale changes.

**Exceptions.** None. A size the scale lacks is a request for the design system, not a local value.

**Do / don't.** `typography.bodyMediumSemiBold`. Not `bodyMediumRegular.copy(fontWeight = Bold)`,
and not `fontSize = 15.sp`.

## `LDS-TEXT-HEADING-DISPLAY` · Headings structure, display makes a statement

**Rule of thumb.** A `heading` style titles a screen, a section or a component — it says where you
are. A `display` style is for a figure that is the point of the screen: a balance, a total, a big
statistic. A `body` style is everything that is read: copy, labels, values in a row.
`bodyXSmallOverline` labels a group from above.

**Why.** A heading orders the screen and display stops the eye. A section title in a display style
outranks the content it introduces, and an amount in a heading style reads as a title.

**Exceptions.** An amount that is one value among many — a transaction amount in a row — is body, not
display.

**Do / don't.** `displayMedium` for the account balance at the top of a screen, `headingSmall` for the
section below it. Not `displayLarge` for a screen title, and not `headingLarge` for a balance.
