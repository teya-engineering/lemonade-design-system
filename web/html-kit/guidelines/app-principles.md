# App principles

The Teya app's rules that hold across every screen, from its `docs/design/principles.md`, written here in
kit markup. The app owns them: their IDs, meaning and exceptions are the app's, and only the examples are
the kit's. `APP-TOPBAR-COMPONENT` and `APP-SHEET-OVER-SHEET` are about app code and navigation, so the kit
doesn't carry them. `APP-FEED-CARD-APPEARANCE` is with the other list rules, in `list-screens.md`.

## `APP-SCREEN-ARCHETYPE` · Every screen names its archetype

**Kind.** Rule.

**Rule.** Every screen names its archetype and follows that anatomy: explainer sheet, picker sheet,
confirmation, intro screen, form, review, entity detail, feed, resource list or menu. The kit's examples
cover six of them — `add-recipient.html` is a form, `transaction-details.html` an entity detail
(`details.md`), `transactions.html` a feed with a picker sheet, `card-machines.html` and `recipients.html`
resource lists, and `settings.html` a menu (`list-screens.md`) — and a dashboard of widgets needs none. A screen that fits
none of them stops and asks rather than inventing one.

**Why.** An invented anatomy is the drift. Most of the inconsistency in the app comes from screens
building their own structure instead of reaching for a shared one.

**Exceptions.** None. "Stop and ask" is the exception path.

## `APP-TOPBAR-TITLE` · Large titles belong to section roots

**Kind.** Rule of thumb.

**Rule.** A large title (`lmnd-kit-top-bar__large`, or the compact bar's) belongs to a section root — Sales,
Activity, Cards — and to a sheet that is its own root, where the title names what the sheet is ("Filter by
payment method"). Everything reached from inside a section, and every step of a flow, has an inline
title: `lmnd-kit-top-bar--small`.

**Why.** A large title says "you have arrived somewhere". Inside a flow it says it four times.

This is a rule of thumb: a screen that reads better against it is an exception a person decides.

## `APP-TOPBAR-ACTION` · Back is for pushed screens, Close for sheets

**Kind.** Rule.

**Rule.** A pushed screen leads with Back (`arrow-left`). A presented sheet, or a screen presented over
another, leads with Close (`times`), which dismisses.

**Why.** `Back` promises a screen underneath that the gesture doesn't go to. A sheet carrying `Back` is a
violation.

**Exceptions.** A step inside a sheet-hosted flow may carry `Back` to reach the previous step, as long as
the flow's root closes with `Close`.

## `APP-TOPBAR-TRAILING` · At most two icon actions, or one text action

**Kind.** Rule.

**Rule.** A top bar's trailing slot carries at most two icon actions, or one text action such as Reset or
Done, never both kinds at once.

Past two, one action may stay as a button and the rest collapse into a menu behind an
`ellipsis-horizontal` icon button. A menu is a `lmnd-kit-dropdown` (`gallery.html`). The one that stays out is the action the screen exists to support;
where two could each claim that, neither does and all of them go in the menu. A single trailing action is
always a button, never a menu of one. A destructive action stays out of the menu only when removing
something is what the screen is for.

**Why.** Three touch targets in a 44pt strip is a row of guesses.

**Exceptions.** None.

## `APP-TOPBAR-ACTION-STYLE` · The bar's buttons follow the top bar rule

**Kind.** Rule.

**Rule.** A top bar's and a sheet bar's buttons are styled by `LDS-TOP-BAR-ACTIONS`: circular Small icon
buttons, or a Small text button in its pill shape; the screen's primary action is Primary · Solid, and every other action is
Neutral · Subtle. A destructive action — remove, delete, cancel an invite — that stays in the bar by
`APP-TOPBAR-TRAILING` is always Critical · Subtle (`LDS-TOP-BAR-CRITICAL`), and in the menu it is a
`lmnd-kit-dropdown__item--critical`, on every screen and in every bar. A disabled action uses the
button's `disabled`; one that is working shows a spinner in place of its glyph.

**Why.** One treatment per weight of action, on every screen and every sheet, so the same action always
looks the same.

**Exceptions.** None. The bar never holds a ghost button.

## `APP-SCREEN-STATES` · All three states are decided first

**Kind.** Rule.

**Rule.** A screen declares its loading, empty and error states before it is built. In a prototype that
needs them, each is its own file (`LHK-FLOWS`): `payouts.html`, `payouts-loading.html`,
`payouts-empty.html`, `payouts-error.html`. The kit's examples show the ready state only.

**Why.** A state nobody decided becomes a blank screen in production.

**Exceptions.** None.

## `APP-LOADING` · The wait looks like what is being waited on

**Kind.** Rule.

**Rule.**

| Waiting on | Shows |
| --- | --- |
| A screen of repeating content — lists, feeds, cards of rows | `lmnd-kit-skeleton`s in the real layout: the same cards, rows and indents |
| A single-purpose screen — one object, one action | A centred `lmnd-spinner` |
| One element inside a loaded screen — a widget, a card | That element skeletons in place at its own size |
| An action in a control | The button's loading state: `lmnd-button--loading` with a spinner |
| Background or optimistic work | Nothing |

The chrome doesn't wait: the top bar keeps its real title and its Back or Close, and a footer action is
present but disabled. An entity detail skeletons its cards under a hero it already knows (`details.md`).

**Why.** A skeleton in the real layout means nothing shifts when the data lands, and the user can always
leave.

**Exceptions.** None.

## `APP-ROLE-STRIP` · A restricted role is told once, on Home

**Kind.** Rule.

**Rule.** When someone's role hides features — a Manager, Accountant or Cashier — Home says so once, with
the role strip: Home becomes a `lmnd-kit-screen--restricted`, and a `lmnd-kit-role-strip` under the status
bar carries a `padlock` and "Some features aren't available for your current role." No other screen
repeats it, and no screen names the features it hides.

```html
<div class="lmnd-kit-screen lmnd-kit-screen--restricted">
  <div class="lmnd-kit-role-strip">
    <span class="lmnd-icon" data-icon="padlock" aria-hidden="true"></span>
    <p class="lmnd-text-body-small-regular">Some features aren't available for your current role.</p>
  </div>
  <div class="lmnd-kit-screen lmnd-kit-screen--home">…</div>
</div>
```

**Why.** The merchant needs to know why their app looks thinner than a colleague's, once. A strip on
every screen turns a fact into a reproach, and a list of what's missing is a list of things they can't
have.

**Exceptions.** None. `screens/home-restricted.html` is the baseline.

## `APP-ROLE-ABSENT` · What a role can't do isn't there

**Kind.** Rule.

**Rule.** An action the role can't take is absent: no button, no menu item, no row, no chip, no tab. It is
never shown disabled. A card or widget the role can't use is removed, and the screen closes the gap.

**Why.** A disabled control promises something the merchant can't get, and makes them look for a way to
turn it on. Absent, the screen is simply the screen for their job.

**Exceptions.** Data the role can't see but whose place matters follows `APP-ROLE-LOCKED`.

**Do / don't.** A Cashier's Home with Sell and "+" only. Not a greyed-out Pay button.

## `APP-ROLE-LOCKED` · Data a role can't see is masked, with a lock

**Kind.** Rule.

**Rule.** A figure the role can't see keeps its place, masked with six dots after its currency symbol
("£ • • • • • •"), and ends with a Primary · Solid circular `padlock` button that opens a sheet saying
why. It is never shown as zero, empty or a dash.

```html
<a href="#no-access" class="lmnd-kit-balance__locked" aria-label="You don't have access">
  <span class="lmnd-kit-balance__amount lmnd-text-display-medium">£ • • • • • •</span>
  <span class="lmnd-kit-icon-button lmnd-kit-icon-button--primary lmnd-kit-icon-button--solid lmnd-kit-icon-button--circular" aria-hidden="true"><span class="lmnd-icon" data-icon="padlock"></span></span>
</a>
```

**Why.** A zero is a false fact, and an empty space looks like a fault. The dots say the figure exists
and is held back, and the lock says why when asked.

**Exceptions.** A figure that only matters to people who can see it, such as a widget of it, is removed
instead (`APP-ROLE-ABSENT`).

## `APP-WIDGET-EMPTY` · A widget's empty state follows what it draws

**Kind.** Rule.

**Rule.** An empty widget shows one of three bodies, picked by what it draws when it has data: a chart's
empty body is decorative bars at reduced opacity under one line; a list's is the stacked-rows placeholder
over one line; anything else is an illustration with a title and subtitle, or a caption with one
Neutral · Subtle · Small action (`APP-WIDGET-BOX`).

The copy says why it's empty: "No sales for the selected filters" where a filter narrows the window, "No
transactions yet" where nothing has happened. The widget keeps its heading in every state, values it
doesn't have are removed rather than greyed, and controls that could change the outcome stay.

**Why.** A merchant who picked a quiet week isn't told to start taking payments.

**Exceptions.** A fixed set of rows carrying zero isn't empty: a zero there is data.

## `APP-SCREEN-EMPTY` · A list's empty state fills the list, not the screen

**Kind.** Rule.

**Rule.** The screen keeps its chrome — top bar, search field, filter bar — and a `lmnd-kit-empty` fills the
space under it:

```html
<div class="lmnd-kit-empty">
  <span class="lmnd-icon" data-icon="search" aria-hidden="true"></span>
  <div class="lmnd-kit-empty__text">
    <p class="lmnd-text-body-medium-semibold">No transactions for the selected filters</p>
    <p class="lmnd-kit-empty__subtitle lmnd-text-body-small-regular">Try changing or removing a filter</p>
  </div>
  <button type="button" class="lmnd-button lmnd-button--neutral lmnd-button--subtle lmnd-button--small lmnd-text-body-small-semibold"><span class="lmnd-button__label">Clear filters</span></button>
</div>
```

| Why it's empty | Icon | Title | Subtitle | Action |
| --- | --- | --- | --- | --- |
| Nothing yet | the subject's own, `receipt` for transactions | "No transactions yet" | what will appear here, and when | none |
| Nothing for these filters | `search` | "No transactions for the selected filters" | "Try changing or removing a filter" | Clear or Reset filters |
| Nothing for this search | `search` | names the term searched for | what the field accepts | none |

**Why.** Replacing the screen takes away the control that caused the emptiness. Clearing a filter is what
the user needs to do next, so the bar that holds it stays where it was.

**Exceptions.** None for a list.

## `APP-FILTER-BAR` · Filters are chips above the list

**Kind.** Rule.

**Rule.** Filters sit in a `lmnd-kit-filter-bar` first in `lmnd-kit-content`, under the search field when the
list has one (`APP-LIST-SEARCH`), and scroll away with the list. Only the top bar, with a section's tabs,
stays pinned. Each filter is a `lmnd-kit-chip` carrying the filter's name and a
`chevron-down`, `lmnd-kit-chip--selected` while it has a value, and a `lmnd-kit-chip__counter` for how many values are
picked. A chip shows its name and counter, never the chosen value, so it keeps its width. Tapping it opens
that filter's picker sheet.

Order runs period, then scope — stores, devices — then attributes of the thing listed. A chip that can't
apply is absent, not disabled. The bar ends with a Neutral · Ghost · X-Small button, only while a filter
differs from the state the screen opened in: "Clear filters" on a list that opens unfiltered, "Reset
filters" on one that opens filtered.

```html
<main class="lmnd-kit-content">
  <div class="lmnd-kit-filter-bar">
    <a href="#period-sheet" class="lmnd-kit-chip lmnd-kit-chip--selected lmnd-text-body-small-medium"><span class="lmnd-kit-chip__label">Period</span><span class="lmnd-icon" data-icon="chevron-down" aria-hidden="true"></span></a>
    <a href="#store-sheet" class="lmnd-kit-chip lmnd-kit-chip--selected lmnd-text-body-small-medium"><span class="lmnd-kit-chip__label">Store</span><span class="lmnd-kit-chip__counter lmnd-text-body-xsmall-semibold">1</span><span class="lmnd-icon" data-icon="chevron-down" aria-hidden="true"></span></a>
    <button type="button" class="lmnd-button lmnd-button--neutral lmnd-button--ghost lmnd-button--x-small lmnd-text-body-small-semibold"><span class="lmnd-button__label">Reset filters</span></button>
  </div>
  …
</main>
```

**Why.** Once the merchant starts reading the list, the list gets the screen, and the filters are one
flick back up. A chip that keeps its width doesn't shuffle the row every time someone picks.

**Exceptions.** None. "Status" is a chip; "Card payments" against "Bank transfers" is a tab.

## `APP-SEGMENTED-SIZE` · A screen's segmented control is Large

**Kind.** Rule.

**Rule.** A segmented control that switches what a whole screen shows — Members or Roles on Team and
permissions, Card machines or Tap to Pay — is Large, the default `lmnd-kit-segmented` with no size
modifier, first in the content. `lmnd-kit-segmented--medium` and `--small` are for a control inside the
content: in a card, a widget, a sheet or a form.

```html
<main class="lmnd-kit-content">
  <div class="lmnd-kit-segmented" role="radiogroup" aria-label="Team">
    <label class="lmnd-kit-segmented__item lmnd-text-body-small-medium"><input type="radio" name="team" checked><span>Members</span></label>
    <label class="lmnd-kit-segmented__item lmnd-text-body-small-medium"><input type="radio" name="team"><span>Roles</span></label>
  </div>
  …
</main>
```

**Why.** The control that switches the screen is the screen's own navigation, so it takes the full size,
as the native `SegmentedControl` does by default. A smaller one reads as a filter on part of the screen.

**Exceptions.** None.

## `APP-FOOTER-PLACEMENT` · A fixed action sits in the faded footer

**Kind.** Rule.

**Rule.** An action fixed to the bottom while the content scrolls sits in `lmnd-kit-actions`, which fades the
content where it meets the action; in a sheet, in `lmnd-kit-sheet__actions`. Never a button as the last thing
in `lmnd-kit-content` held in place some other way.

**Why.** The fade gives the scroll a visible end. A button over a hard edge doesn't.

**Exceptions.** None. An entity detail fixes no action at all (`APP-DETAILS-ACTIONS`).

## `APP-SPACING-RHYTHM` · The gaps

**Kind.** Rule.

**Rule.** A section is a heading and its card, `lmnd-kit-section` with a `lmnd-kit-section__title`. The gaps are:

| Gap | Value | In the kit |
| --- | --- | --- |
| Screen side padding | `spacing-400` | `lmnd-kit-content` |
| Between large elements — cards, blocks | `spacing-400` | `lmnd-kit-content` |
| Between small elements — tiles, tags, chips | `spacing-200` | `lmnd-kit-row`, `lmnd-kit-chips`, `lmnd-kit-filter-bar` |
| Between sections | `spacing-800` | `lmnd-kit-content` around a `lmnd-kit-section` |
| Between a section's heading and its card | `spacing-200` | `lmnd-kit-section` |

`lmnd-kit-content` draws all of these, so a screen puts its cards and sections straight in it and adds no gaps
of its own.

A hero is not a section, so the first section after a `lmnd-kit-hero` takes the gap between large elements,
`spacing-400`, not the gap between sections.

**Why.** One rhythm on every screen, so a section looks like a section wherever it sits.

**Exceptions.** None.
