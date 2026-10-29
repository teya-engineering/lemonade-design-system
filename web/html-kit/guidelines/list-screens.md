# List screens

Rules for the Teya app's list screens: the feeds of money moving, the lists of things a merchant has, the
menus, and the sheets that pick from a list. They follow the app's `ResourceLazyListContainer` and its selector
template, so they carry the app's prefix, `APP-`. A list's filters are `APP-FILTER-BAR`, its empty state
`APP-SCREEN-EMPTY`, its first load `APP-LOADING`, and the gaps between its cards `APP-SPACING-RHYTHM`.

| Example | Kind | Shows |
| --- | --- | --- |
| `examples/transactions.html` | Feed | Search, filter bar, day groups with totals, the next page loading under a spinner, and a multi-select picker sheet behind the Store chip |
| `examples/card-machines.html` | Resource list | A segmented control, groups by state, the add action in the top bar |
| `examples/recipients.html` | Resource list | Search and the add action over one card sorted by name |
| `examples/settings.html` | Menu | The signed-in person, sections of plain-icon rows, Log out and the version footer |

```html
<div class="lmnd-kit-device">
  <div class="lmnd-kit-screen">
    <header class="lmnd-kit-top-bar">…</header>
    <main class="lmnd-kit-content">
      <label class="lmnd-kit-search">…</label>
      <div class="lmnd-kit-filter-bar">…</div>
      <section class="lmnd-kit-list-group">…</section>
      <section class="lmnd-kit-list-group">…</section>
    </main>
  </div>
</div>
```

## `APP-LIST-KIND` · A list is a feed, a list of resources or a menu

**Kind.** Rule.

**Rule.** What a list holds decides its kind, and the kind decides its rows, its groups and its actions:

| What it lists | Kind | Rules |
| --- | --- | --- |
| Records of money moving, newest first — sales, refunds, payment links, payouts, account activity | Feed | `APP-FEED-*` |
| Things the merchant has or works with — card machines, stores, team members, recipients | Resource list | `APP-RESOURCE-*` |
| Places to go — settings, a menu of tools | Menu | `APP-MENU-*` |
| Options to choose from, in a sheet | Picker sheet | `APP-PICKER-*` |

**Why.** Each kind answers a different question. A feed answers "what happened, and when"; a resource
list "what do I have, and does any of it need me"; a menu "where do I go". A screen that mixes them
answers none of them well.

**Exceptions.** None. A list that fits none of them stops and asks (`APP-SCREEN-ARCHETYPE`).

## `APP-FEED-CARD-APPEARANCE` · A feed renders as cards

**Kind.** Rule.

**Rule.** A feed's rows sit in cards on the subtle screen: one `lmnd-kit-card` per group, never rows
running edge to edge on a `lmnd-kit-screen--default`.

**Why.** It is the appearance every feed in the app already uses. A feed built as a raw list is the one
that looks different.

**Exceptions.** None.

## `APP-FEED-GROUPS` · One card per day, under the day and its total

**Kind.** Rule.

**Rule.** A feed runs newest first and groups its records by day. Each day is a `lmnd-kit-list-group`: a
header, then one card of that day's rows.

| The day | Title, `lmnd-text-body-small-semibold` | Label, `lmnd-text-body-small-regular` |
| --- | --- | --- |
| Today | "Today" | none |
| Yesterday | "Yesterday," | "22 Oct 2026" |
| Any day before | The weekday, "Monday," | "20 Oct 2026" |

The header ends with the day's total, `lmnd-kit-list-group__total` in `lmnd-text-body-small-medium`, on every
money feed: what that day's rows add up to, signed as the rows are. A declined or voided record adds
nothing, a refund subtracts, and a pending one counts.

```html
<section class="lmnd-kit-list-group">
  <h2 class="lmnd-kit-list-group__header"><span class="lmnd-kit-list-group__heading"><span class="lmnd-text-body-small-semibold">Yesterday,</span><span class="lmnd-text-body-small-regular">22 Oct 2026</span></span><span class="lmnd-kit-list-group__total lmnd-text-body-small-medium">£162.00</span></h2>
  <div class="lmnd-kit-card">…</div>
</section>
```

In the app the header sticks under the top bar while its day scrolls past. A prototype draws it in place.

**Why.** A day is what a merchant reconciles against: the till, the bank statement, the shift. The total
lets them check a day without adding up its rows, and the weekday tells them which day it was without
counting back from today.

**Exceptions.** A feed whose records arrive on a longer schedule groups by that period instead: payouts
by week, titled with the range, "13 – 19 Oct 2026", and still carrying the period's total.

## `APP-FEED-ROWS` · A money row: how it moved, when, and how much

**Kind.** Rule.

**Rule.** Every row in a feed is a `lmnd-kit-list-item` with the same four slots:

| Slot | Holds |
| --- | --- |
| Leading | A `lmnd-kit-symbol--rounded` showing how the money moved — the card brand's logo, `money` for cash, `link` for a payment link, the other party's initials on an account — with the record's status as the app's badge on it |
| Label | What it is: "Visa •••• 4242", "Cash", the other party's name |
| Support | When and where: "14:32 • Camden". The time alone, because the header already gives the day |
| Value | The amount, styled by the record's state |

The badge is a `<span class="lmnd-kit-symbol__status" data-badge="…" aria-hidden="true">` last in the
symbol, drawing the app's own artwork, which swaps to its dark version in dark mode:

| State | `data-badge` | Amount, on `lmnd-kit-list-item__value` |
| --- | --- | --- |
| Sale approved | `sale-transaction-approved` | Default |
| Sale pending | `sale-transaction-pending` | `--dimmed` |
| Sale declined | `sale-transaction-declined` | `--strikethrough` |
| Sale voided | `sale-transaction-void` | `--strikethrough` |
| Refund, approved or pending | `sale-transaction-refund` | Default, negative: "-£12.00"; `--dimmed` while pending |
| Refund declined | `sale-transaction-refund-declined` | `--strikethrough` |
| Pre-authorisation, or its expiry | `sale-transaction-pre-auth`, `sale-transaction-pre-auth-declined` | Default; `--strikethrough` once expired |
| Payout on hold, or paid | `sale-transaction-processing-on-hold`, `sale-transaction-settlement-paid` | Default |
| Account: money in, money out | `money-in`, `money-out` | `--positive` with a plus, "+£310.00"; default with a minus |
| Account: processing, declined | `transaction-processing`, `transaction-declined` | `--dimmed`; `--strikethrough` |

```html
<span class="lmnd-kit-list-item__leading"><span class="lmnd-kit-symbol lmnd-kit-symbol--rounded"><span class="lmnd-kit-logo" data-logo="visa" aria-hidden="true"></span><span class="lmnd-kit-symbol__status" data-badge="sale-transaction-declined" aria-hidden="true"></span></span></span>
…
<span class="lmnd-kit-list-item__value lmnd-kit-list-item__value--strikethrough"><span class="lmnd-text-body-medium-medium">£56.20</span></span>
```

The whole row opens the record's details, so it carries no chevron. The status is on the symbol, so the
value holds no tag.

**Why.** With the status on the symbol, the right-hand column holds amounts and nothing else, and reads
down the screen as one column of figures. A struck-through amount says the money never moved while
keeping what was attempted, which is what the merchant is looking for when a customer says they paid.

**Exceptions.** A feed whose records are mostly told apart by their status — payment links, Active or
Expired; payouts, Paid or Scheduled — names the status in the label and gives the symbol the status's
voice (`lmnd-kit-symbol--positive`) instead of a badge.

## `APP-FEED-PAGING` · The next page loads under a spinner

**Kind.** Rule.

**Rule.** A long feed loads more as it scrolls. While the next page loads, a spinner sits centred under
the last group, in a `lmnd-kit-load-more`. There is no Load more button and no page number.

```html
<div class="lmnd-kit-load-more" role="status" aria-label="Loading more transactions"><span class="lmnd-spinner lmnd-spinner--medium" aria-hidden="true"></span></div>
```

A feed's first load is still the skeleton of its rows (`APP-LOADING`): the spinner is only for more of
a list that is already on screen.

**Why.** The merchant never has to ask for more. The rows already loaded are what they are reading, so
the wait for the next page stays small and out of the way below them.

**Exceptions.** None.

## `APP-LIST-SEARCH` · Search says what it matches

**Kind.** Rule.

**Rule.** A list gets a `lmnd-kit-search` when it can run past a screen and people come to it for one item
they can name: a card's last four digits, a reference, a person. The field goes first in
`lmnd-kit-content`, above the filter bar, and scrolls with the list; only the top bar and a section's tabs
stay pinned. Its placeholder says what it matches — "Search by
last 4 digits or reference", "Search by name" — never "Search" alone.

A search that finds nothing shows the "Nothing for this search" empty state (`APP-SCREEN-EMPTY`).

**Why.** A bare "Search" leaves the merchant to guess whether a name, an amount or a reference will
work, and to find out by typing.

**Exceptions.** A picker sheet's search follows `APP-PICKER-OPTIONS`.

## `APP-RESOURCE-ROWS` · A resource row: its face, its name, one fact

**Kind.** Rule.

**Rule.** Every row in a resource list is a `lmnd-kit-list-item`:

| Slot | Holds |
| --- | --- |
| Leading | A `lmnd-kit-symbol--rounded` with the thing's face — a device photo, a store's logo, a person's initials — or its kind's icon where it has none, `card-machine` |
| Label | Its name, as the merchant named it |
| Support | The one fact that tells two of them apart: the model, the address, the email, the bank and last digits |
| Trailing | A chevron: the row opens the thing's details |

A tag sits before the chevron only for what sets the row apart from the rest of its group — "Not
activated", a role, "Invited". A resource row carries no amount.

**Why.** A merchant finds a thing by its face and its name. The support line is there for the day two
card machines are both called "Front counter".

**Exceptions.** A row that opens nothing, such as an invite still waiting to be accepted, drops the
chevron and is a `<div>`.

## `APP-RESOURCE-GROUPS` · Grouped by what needs doing

**Kind.** Rule.

**Rule.** Where the things have states that ask something of the merchant, each state is one card with an
overline heading (`lmnd-kit-card__title lmnd-text-body-xsmall-overline`), in this order: what needs action
first, then active, then inactive. A state with nothing in it is left out. Where they don't, the list is
one card. Either way, the rows in a card are sorted by name.

A control that switches what is listed — Card machines or Tap to Pay, Members or Roles — is a Large
`lmnd-kit-segmented` first in the content (`APP-SEGMENTED-SIZE`).

**Why.** A merchant mostly opens a list of things to deal with one of them. Putting what needs action
first answers that before they scroll.

**Exceptions.** Team members group by role, which is what a team is organised by, with an "Invited" card
first: invitations still waiting to be accepted are what the merchant checks on after sending one.

## `APP-RESOURCE-ADD` · Adding is the top bar's primary action

**Kind.** Rule.

**Rule.** The action that adds to a resource list is the top bar's trailing button: circular, Small,
`lmnd-kit-icon-button--primary lmnd-kit-icon-button--solid`, with `plus`, or with the glyph of how the thing
is added (`qr-code` to scan a card machine). Its `aria-label` names the action: "Add recipient". Where
adding has more than one route — invite a member, create a role — the button opens a menu of them.

It is never a row at the top or end of the list, a footer button, or a button beside the search field.

**Why.** Add sits in the same place on every list, and the top bar stays put while the list scrolls, so
it is always one tap away.

**Exceptions.** None.

## `APP-MENU-SECTIONS` · A menu is sections of one card each

**Kind.** Rule.

**Rule.** A menu groups its rows by who or what they are about — Personal, Company — and each group is a
`lmnd-kit-section`: a `lmnd-kit-section__title` in `lmnd-text-heading-xsmall`, then one `lmnd-kit-card` of its rows.
The sections are spaced by `APP-SPACING-RHYTHM`, which `lmnd-kit-content` draws. A menu has no search, no
filters and no add action.

```html
<section class="lmnd-kit-section">
  <h2 class="lmnd-kit-section__title lmnd-text-heading-xsmall">Company</h2>
  <div class="lmnd-kit-card">…</div>
</section>
```

**Why.** A heading per group tells the merchant where to look before they read a row, and one card per
group keeps the rows that belong together inside one edge.

**Exceptions.** The signed-in person's card and the Log out card stand outside the sections
(`APP-MENU-PROFILE`, `APP-MENU-LOG-OUT`).

## `APP-MENU-ROWS` · A menu row: a plain icon, where it goes, a chevron

**Kind.** Rule.

**Rule.** Every row in a menu is a `lmnd-kit-list-item`:

| Slot | Holds |
| --- | --- |
| Leading | A plain `lmnd-icon` for the place, never a `lmnd-kit-symbol` (`LDS-LIST-LEADING-SLOT`) |
| Label | The place's name: "Card machines" |
| Support | What is inside it: "View your card machines and terminal IDs". Optional, but every row in a card has one or none does |
| Trailing | A chevron for a row that opens a screen, as an `<a>`. A switch for a setting that changes in place, as a `<label>`, with no chevron |

**Why.** A menu is read for its labels. Plain icons help the eye down the list without competing with
them, and the trailing slot says before the tap whether the row goes somewhere or changes something.

**Exceptions.** None.

## `APP-MENU-PROFILE` · The signed-in person comes first

**Kind.** Rule.

**Rule.** A settings menu opens with one card holding the signed-in person: a `lmnd-kit-symbol--large
lmnd-kit-symbol--rounded` with their initials, their name as the label, their email as the support, and a
chevron to their profile.

**Why.** It answers "whose account is this" before anything is changed, which matters to a merchant who
shares a device or runs more than one business.

**Exceptions.** A menu that isn't about the account, such as a menu of tools, has no profile card.

## `APP-MENU-LOG-OUT` · Log out is last, in a card of its own

**Kind.** Rule.

**Rule.** Log out is the last card: one row with the `log-out` icon and the label "Log out", in the
default colours rather than critical, and with no chevron. Tapping it asks first, as a confirmation sheet.

**Why.** On its own at the end it can't be hit while reaching for a neighbour. It ends a session, which
the merchant can start again, so it is not drawn as destructive.

**Exceptions.** None.

## `APP-MENU-FOOTER` · The version closes the menu

**Kind.** Rule.

**Rule.** Under the last card, a `lmnd-kit-menu-footer` gives the app's version and the legal entity, each a
`lmnd-text-body-small-regular` line, centred, in `content-secondary`:

```html
<footer class="lmnd-kit-menu-footer">
  <p class="lmnd-text-body-small-regular">Version 2.51.8 (456)</p>
  <p class="lmnd-text-body-small-regular">Teya Services Ltd.</p>
</footer>
```

**Why.** Support asks for the version, and the legal entity is who the merchant has the contract with.
Both are found by scrolling to the end, out of the way of everything else.

**Exceptions.** A menu that isn't the settings menu has no footer.

## `APP-PICKER-OPTIONS` · Up to ten options in one card, more get search

**Kind.** Rule.

**Rule.** A picker sheet is a `lmnd-kit-sheet--subtle` with Close leading its bar, a large title naming
what is picked ("Filter by store"), and its options as `<label class="lmnd-kit-list-item">` rows with the
control trailing: `lmnd-kit-radio` for one choice, `lmnd-kit-checkbox` for several.

| Options | Body |
| --- | --- |
| Up to 10 | One card of every option. No search; the sheet fits its content |
| More than 10 | A `lmnd-kit-search` first in `lmnd-kit-sheet__body`. With nothing picked, one card of every option. With something picked, a "Selected" card then an "Available" card, each with an overline heading |

Reset sits in the bar's trailing slot, and only while there is something to reset. The confirming action
is in `lmnd-kit-sheet__actions`, which stays fixed while the options scroll.

**Why.** Ten rows fit on a phone, so scanning them is quicker than typing. Past that, the picked options
float to the top, so the merchant can see what they chose without scrolling through the rest.

**Exceptions.** A sheet whose context reads better with other words than "Selected" and "Available"
uses its own.

## `APP-PICKER-LEADING` · Every option has a leading slot, or none does

**Kind.** Rule.

**Rule.** An option shows what it offers in its leading slot: a flag for a country or a currency, a logo
or an icon for anything that has one. An option with no image takes its label's two initials in the same
`lmnd-kit-symbol--rounded`, so every row keeps the same shape. A list whose options can't be illustrated at
all goes without leading slots.

**Why.** Half a list with leading slots starts its labels at two indents down one card.

**Exceptions.** None.
