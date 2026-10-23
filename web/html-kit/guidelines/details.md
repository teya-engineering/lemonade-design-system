# Details screens

Rules for the Teya app's details screens: everything about one thing — a transaction, a settlement, a
recipient, a store, a terminal, a team member — reached by tapping its row in a list. They follow the
app's entity-detail archetype (`docs/design/archetypes/entity-detail.md` in the Teya app) and its
`DetailsListContainer`, so they carry the app's prefix, `APP-`. `examples/transaction-details.html`
follows every one of them. The gaps between the hero and the cards are the app's `APP-SPACING-RHYTHM`.

A screen that asks for values is a form. A screen that shows values back before they're committed is a
review. A details screen shows what the system holds, which the user mostly can't change.

```html
<div class="lmnd-kit-device">
  <div class="lmnd-kit-screen lmnd-kit-screen--details lmnd-kit-screen--wash-positive">
    <header class="lmnd-kit-top-bar lmnd-kit-top-bar--small">…</header>
    <main class="lmnd-kit-content">
      <div class="lmnd-kit-hero">…</div>
      <div class="lmnd-kit-card">…</div>
    </main>
  </div>
</div>
```

## `APP-DETAILS-TOP-BAR` · The bar names the kind of thing, and starts transparent

**Kind.** Rule.

**Rule.** A details screen is a `lmnd-kit-screen--details` with the small top bar, `lmnd-kit-top-bar--small`. Its
title names the kind of thing, never this one: "Sale transaction", "Payment link", "Store information".
The bar starts transparent and fills with `bg-default` as the screen scrolls; `lmnd-kit-screen--details` does
both. It leads with Back, because a details screen is pushed from a list; one opened as a sheet takes
Close.

**Why.** The instance is in the hero, a few points below, at four times the size. A bar repeating it
spends its one line on a word already on screen. Starting transparent lets the hero scroll up into a
solid bar rather than under a floating one.

**Exceptions.** None.

**Do / don't.** "Sale transaction" over a £24.00 hero. Not "£24.00 at Camden Market" in the bar.

## `APP-DETAILS-HERO` · One hero, four slots, in order

**Kind.** Rule.

**Rule.** The screen opens with one `lmnd-kit-hero`, the first item in `lmnd-kit-content`. It has up to four slots, in
this order, and only the main information is required:

| Slot | Markup | Filled when |
| --- | --- | --- |
| Artwork | `lmnd-kit-hero__artwork` on a `lmnd-kit-symbol lmnd-kit-symbol--xx-large lmnd-kit-symbol--rounded`, holding a logo, initials or a device image | The thing has a face worth showing |
| Main information | `lmnd-kit-hero__main`: an amount in `lmnd-text-display-large`, or a name in `lmnd-text-heading-large`, on one line | Always |
| Status | `lmnd-kit-hero__status` on a `lmnd-kit-tag` — Paid, In process, Declined | The thing has a status |
| Metadata | `lmnd-kit-hero__meta` in `lmnd-text-body-small-regular`: the facts that place it, joined by ` • ` | There is more worth knowing |

A transaction fills main information, status and metadata, with no artwork. A terminal fills artwork, its
name and status. A struck-through amount, `lmnd-kit-hero__main--struck`, means the record was undone; the tag
and metadata carry the reason ("Voided", "Voided on 7 Jun").

Metadata carries no labels and no punctuation but the bullet: "Today, 14:32 • Camden Market". It takes no
step up for sitting under a `display-large` amount; the size difference is the hierarchy, and it follows
`LDS-TEXT-INTRO`.

**Why.** Every details screen uses the same block. What differs between a transaction and a terminal is
which slots are filled, not the shape, so a reader always finds the amount, the status and the facts in
the same places.

**Exceptions.** None.

**Do / don't.** £24.00 in `display-large`, an Approved tag, and "Today, 14:32 • Camden Market". Not a
label line ("Card payment") between the amount and the status, not the amount at `display-xsmall`, and
not "Date: 23 Oct".

## `APP-DETAILS-WASH` · The wash comes from the status

**Kind.** Rule.

**Rule.** A details screen whose hero has a status takes the wash in that status's voice:
`lmnd-kit-screen--wash-positive`, `--info`, `--warning`, `--critical`, `--featured` or `--neutral`, matching
the status tag. A screen whose hero has no status takes no wash.

**Why.** The wash behind the bar and the hero reinforces what the tag says. A thing that isn't in a state
isn't coloured as though it were: a terminal with no status isn't neutral-coloured, it's uncoloured.

**Exceptions.** None.

**Do / don't.** An Approved positive tag on a `lmnd-kit-screen--wash-positive` screen. Not a positive wash
behind a Declined tag, and not a neutral wash on a screen with no status.

## `APP-DETAILS-CARD-HEADING` · Every card has an overline heading

**Kind.** Rule.

**Rule.** Every card on a details screen has an overline heading: a `lmnd-kit-card__header` whose
`lmnd-kit-card__title` is `lmnd-text-body-xsmall-overline` — Origin, Card, Record. The actions card is the one
exception.

**Why.** A details screen is a dozen rows of facts in groups, and a group of facts with no name is a list
the reader has to sort out themselves.

**Exceptions.** The actions card at the end (`APP-DETAILS-ACTIONS`), whose rows name themselves.

## `APP-DETAILS-ROWS` · Facts are label–value rows, laid out per card

**Kind.** Rule.

**Rule.** A card's facts are `lmnd-kit-content-item` rows, with a `lmnd-kit-divider` between rows and none after the
last. Each card chooses one layout for all its rows, by its longest value:

| The card's values | Layout |
| --- | --- |
| All short — a channel, a brand, four digits, a date | Label beside value, the default |
| Any one of them long — an address, an IBAN, a reference, a full device id | `lmnd-kit-content-item--vertical` on every row: label above value |

A value the user will want elsewhere — an account number, a reference, a device id — gets a copy button
in its value: `lmnd-kit-icon-button--primary lmnd-kit-icon-button--ghost lmnd-kit-icon-button--x-small
lmnd-kit-icon-button--circular` with the `copy` icon. A value the user can change gets the `pencil-line` icon the
same way, and edits in place or in a sheet. A row that points at a related entity — the sale behind a
refund — is the only row that may navigate: a `lmnd-kit-list-item` with that entity's own symbol.

**Why.** Choosing per card rather than per row keeps the rows in a card aligned with each other. A long
value beside its label wraps into two short lines that read as two values.

**Exceptions.** None.

**Do / don't.** A Record card with a long reference, every row vertical. Not one vertical row among
horizontal ones, and not a reference that wraps beside its label.

## `APP-DETAILS-ACTIONS` · Actions scroll with the content, in one place

**Kind.** Rule.

**Rule.** A details screen pins nothing to the bottom; its actions scroll with the content. Where they go
depends on what they are:

| How many, and what kind | Where |
| --- | --- |
| Several peer actions on the record | One card, last on the screen, each action a `lmnd-kit-list-item` with a leading icon |
| One action the entity exists to enable — Pay a recipient | A primary `lmnd-button`, full width, as the last block |
| Destructive, administrative or rare — remove, deactivate, close | The top bar's trailing menu |

An action appears in one place only. A disabled action says why, in its row's support text; an action
that can't be explained is removed, not greyed.

**Why.** The screen exists to be read, and a button fixed over it claims the opposite. The same action in
two places is two places to find it and two to keep in step.

**Exceptions.** None.

**Do / don't.** Download receipt and Issue refund as rows in the last card. Not a `lmnd-kit-actions` block
fixed to the bottom, and not Download receipt in both the actions card and the top bar.

## `APP-DETAILS-STATES` · The bar and the hero survive every state

**Kind.** Rule.

**Rule.** A details screen's other states keep its top bar and hero, which come from the row the user
tapped, and change only what's below them:

- **Loading:** skeletons in the real layout — the cards and rows at the sizes they'll have — never a
  spinner.
- **Error:** the error, with a retry, sits where the cards would be.
- **Empty:** there is none. The screen was reached by tapping a thing that exists; a card with nothing in
  it is removed.

Each state is its own prototype file (`LHK-FLOWS`).

**Why.** The screen opens from a row the user just tapped, so its shape and its headline are already
known. A spinner throws that away, and nothing about the record being unreachable makes its bar or its
amount wrong.

**Exceptions.** None.
