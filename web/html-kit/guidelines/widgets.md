# Widgets

Rules for the Teya app's mini widgets: the half-width dashboard tiles on Home and Sales. They are Teya
app components rather than Lemonade ones — the templates live in the Teya App Figma file and the widget
grid in the Teya app — so these rules carry the app's own prefix, `APP-`. They sit on top of the `LDS-`
rules: a widget is still a card on a subtle surface (`LDS-CARD-SURFACE`) in semantic colour
(`LDS-COLOR-SEMANTIC`).

There are two templates:

- **Generic** carries a figure: an optional heading or top label, one value, an optional support line,
  and an optional bottom slot. Markup: `lmnd-kit-widget` with `lmnd-kit-widget__heading`, `lmnd-kit-widget__content`,
  `lmnd-kit-widget__figure` and `lmnd-kit-widget__bottom`.
- **Box** carries one free slot under a heading, for content that isn't a figure. Markup: `lmnd-kit-widget` with
  `lmnd-kit-widget__heading` and `lmnd-kit-widget__slot`.

The gallery's Mini widget section shows both, with every pattern below.

## `APP-WIDGET-GRID` · Mini widgets sit two to a row

**Kind.** Rule.

**Rule.** Mini widgets go in a `lmnd-kit-widgets` grid: two columns, `spacing-300` between columns and
`spacing-400` between rows. A widget is one column wide and keeps its 173 × 172 shape, so its height
follows its width; never give it a fixed height. Widgets are white cards on the screen's subtle background.

**Why.** This is the app's own grid: `MiniWidgetDimensions` keeps the Figma aspect ratio and the widget
grid sets those gaps. A widget at a fixed height turns into a strip or a tower on a wider or narrower
phone, and its bottom slot stops lining up with its neighbour's.

A full widget, `lmnd-kit-widget--full`, is the app's full-row span: it takes both columns and sizes to its
content. Use it for the one figure a screen leads with, such as Gross sales on Sales, at the top of the
grid.

**Exceptions.** A row with an odd widget out leaves the second column empty.

**Do / don't.** `<div class="lmnd-kit-widgets">` around `<a class="lmnd-kit-widget">`s. Not widgets in a `lmnd-kit-grid` or
a `lmnd-kit-row`, and not `height: 172px`.

## `APP-WIDGET-ONE-NAME` · A widget names itself once

**Kind.** Rule.

**Rule.** A widget is named by an overline heading or by a top label, never both.

- An overline heading, `lmnd-kit-widget__heading` with `lmnd-text-body-xsmall-overline`, names a category:
  Transactions, Refunds, Upcoming.
- A top label, `lmnd-kit-widget__label` with `lmnd-text-body-small-regular`, names a figure in a phrase, usually
  one that says when: Gross sales today, Today's settlements.

A Box widget always has an overline heading.

**Why.** Two names stack two lines of secondary text above the value and push it down, and the reader has
to work out which name is the widget's.

**Exceptions.** None.

**Do / don't.** An overline "Refunds" over the value, or a top label "Today's settlements" over it. Not an
overline "Settlements" with a top label "Today" under it.

## `APP-WIDGET-ONE-VALUE` · One value per widget

**Kind.** Rule.

**Rule.** A Generic widget shows one value, `lmnd-kit-widget__value`, on one line: `lmnd-text-display-xsmall` on
a mini widget, `lmnd-text-display-small` on a full one. No other text on the widget uses that size. A negative amount is `-£` (`LHK-COPY`).

**Why.** The value is what the widget is for. Two figures at the same size compete, and a value that wraps
breaks the line every widget's support text and bottom slot align to.

**Exceptions.** None. A second figure is a support line or a label–value pair in the bottom slot.

## `APP-WIDGET-SUPPORT` · At most one support line

**Kind.** Rule.

**Rule.** Under the value there is at most one support line, `lmnd-kit-widget__support`, of one of two kinds:

- **Context** — when, how many, compared with what: "To be paid tomorrow", "3 due this week",
  "12% more than September". `lmnd-text-body-xsmall-regular`, in `content-secondary`.
- **Status** — "Paid today", "Postponed", "Failed". `lmnd-text-body-xsmall-medium` on a widget with a
  status modifier, which colours it (`APP-WIDGET-STATUS-TINT`).

**Why.** The support line qualifies the value; a second one turns the figure area into a paragraph. A
status reads as a status only when it looks different from context.

**Exceptions.** None.

## `APP-WIDGET-STATUS-TINT` · A tint goes with a status

**Kind.** Rule.

**Rule.** A widget whose support line is a status takes the matching modifier — `lmnd-kit-widget--positive`,
`lmnd-kit-widget--caution` or `lmnd-kit-widget--critical` — which tints the top of the widget and colours the support
line in the same voice. A widget with no status line has no tint.

**Why.** The tint is how a status reads from across the screen. A tint without a status line means
nothing, and a tint in a different voice from the line contradicts it.

**Exceptions.** None.

**Do / don't.** `<a class="lmnd-kit-widget lmnd-kit-widget--positive">` with "Paid today" as its support line. Not a
tint on a widget with a context line, and not `lmnd-kit-positive` on the line of a widget with no modifier.

## `APP-WIDGET-BOTTOM` · The bottom slot holds one thing

**Kind.** Rule.

**Rule.** A Generic widget's `lmnd-kit-widget__bottom` holds at most one of these, pinned to the bottom:

- a label–value pair: `lmnd-text-body-xsmall-regular` label in `lmnd-kit-widget__bottom-label`, over a
  `lmnd-text-body-small-medium` value — "Sales from" over "14–15 Apr";
- a count: `lmnd-text-body-small-regular` in `lmnd-kit-widget__bottom-label` — "8 transactions";
- a chart of the value over time;
- avatars: circular `lmnd-kit-symbol`s in a `lmnd-kit-avatars` group — the people or businesses the figure is about;
- a "More details" button: `lmnd-button--neutral lmnd-button--subtle lmnd-button--x-small` with
  `lmnd-button--expand` and a trailing `chevron-right`.

**Why.** The bottom slot is where widgets on one row line up. Two things in it either overflow the widget
or squeeze each other, and the row stops reading as a row.

**Exceptions.** None. Avatars stay circular: `LDS-LIST-SYMBOL-SHAPE` covers list rows, not widgets.

## `APP-WIDGET-BOX` · Content that isn't a figure goes in a Box

**Kind.** Rule.

**Rule.** A widget whose content isn't a figure — an empty state, card art, an illustration — is a Box:
`lmnd-kit-widget__heading` over one `lmnd-kit-widget__slot`. An empty state is an illustration over exactly one button,
Neutral · Subtle · Small, full width, at the bottom of the slot.

**Why.** Forcing card art or an empty state into the Generic layout leaves an empty value and a label with
nothing to label. The Box keeps the heading in the same place as every other widget, so the row still
lines up.

**Exceptions.** None.

**Do / don't.** "No cards": a Box with a card illustration and a Create button. Not a Generic widget with
the value "0" and a button in its bottom slot.

## `APP-WIDGET-LIST-TITLE` · A widget of rows has a header title and a footer action

**Kind.** Rule.

**Rule.** A widget that is a list of rows — Last transactions, Upcoming payments — is a full-width
`lmnd-kit-card` whose title is the card's own heading: `lmnd-kit-card__header` with a `lmnd-kit-card__title` in
`lmnd-text-body-xsmall-overline`, the same style as a mini widget's heading. It sits in the `lmnd-kit-widgets` grid,
spanning both columns, and never takes a section title above it. It ends with a footer action,
`lmnd-kit-card__footer`, that opens the full list: "Go to Sales Transactions".

**Why.** The title belongs to the widget, so it moves, hides and reorders with it on the widget grid. A
title outside the card is a separate element that the grid doesn't know about, and it reads as a heading
for everything below it rather than for one card. The widget shows only the latest few rows, so it needs a
way to the rest; the footer action is that way, and it is the same on every list widget.

**Exceptions.** None.

**Do / don't.**

```html
<div class="lmnd-kit-card">
  <div class="lmnd-kit-card__header">
    <div class="lmnd-kit-card__heading"><h2 class="lmnd-kit-card__title lmnd-text-body-xsmall-overline">Last transactions</h2></div>
  </div>
  <a href="…" class="lmnd-kit-list-item lmnd-kit-list-item--divider">…</a>
  <a href="…" class="lmnd-kit-list-item">…</a>
  <a href="transactions.html" class="lmnd-kit-card__footer lmnd-text-body-small-semibold">Go to Sales Transactions</a>
</div>
```

Not a `lmnd-kit-section` with a `lmnd-kit-section__title` "Last transactions" over the card, and not a list widget
whose only way to the full list is a chevron on a row.

## `APP-WIDGET-TAP` · The whole widget opens its detail

**Kind.** Rule.

**Rule.** A Generic or Box widget is tappable as a whole: the widget is the `<a>`, linking to its detail
screen. Nothing inside it is a separate link or button; a "More details" button in the bottom slot is a
visible hint that the widget opens, written as a `<span>` so it isn't a control inside a control.

**Why.** A widget is a small target already. Splitting it into a widget tap and a button tap makes one of
them easy to miss, and nested controls are invalid HTML that a screen reader announces twice.

**Exceptions.** A widget of rows (`APP-WIDGET-LIST-TITLE`) is a card, not a link: each of its rows is its
own link.

**Do / don't.** `<a href="settlements.html" class="lmnd-kit-widget">` around everything. Not a `<div>` widget with
a `<button>` inside it.
