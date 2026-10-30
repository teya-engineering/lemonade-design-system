# Sales

What the Teya app's Sales tab is today, so a change can start from it. Taken from the app's code on 23
Oct 2026; where this file and the app disagree, the app is right.

Sales is the second tab: a large "Sales" title, a "+" and three inner tabs, Overview, Transactions and
Payment links, each shown only with its permission. A transaction opens its details.

| Baseline | Shows |
| --- | --- |
| `sales-overview.html` | Overview for a merchant with two stores and two currencies who has already sold: filters, currencies and the default widgets |
| `sales-transactions.html` | Transactions: search, filters and two days of sales |
| `sales-payment-links.html` | Payment links: search, filters and links in each status |
| `transaction-details.html` | Details of an approved card sale, with its "More options" menu (`#more-options`) |

The kit's `examples/sales.html`, `examples/transactions.html` and `examples/transaction-details.html` show
the rules, not the app; for a change to the app's Sales, start from these baselines.

## The Sales tab around them

- **Top bar:** the compact large title "Sales". Trailing, a Primary · Solid "+" ("Sales actions") on
  every inner tab, opening a dropdown: "New Sale" (`card-machine`) and "New payment link" (`link`). On
  Transactions only, a Download icon before it, with permission to download reports.
- **Inner tabs:** Overview, Transactions, Payment links. On iOS they sit under the bar rather than in it.
- **Tab bar:** Home, Sales (current), Money, Teya AI.
- **Edit mode** (Overview): the bar and tabs give way to a dark "Edit mode" / "Add, remove or reorder
  widgets" bar with "Add" and "Done".

## Overview (`sales-overview.html`)

- **Filters**, scrolling with the page: Period ("Today" by default, shown selected even then), Store (with
  more than one store), Device (with any devices), Payment method (with more than one for the country, or
  the method picked). "Reset filters" ends the bar once a filter differs from the default. Filters are
  remembered per company.
- **Currencies**, with sales in more than one: horizontal tiles, at least 132px wide, scrolling sideways:
  the flag, "GBP • £", "11,645 transactions". The first is selected.
- **Widgets**, in this order (with overline headings unless noted):

| Widget | Size | Content |
| --- | --- | --- |
| Start selling | full | Only before the first sale: "Start selling", "You're ready to start getting paid.", and "Sell" and "Create a payment link". Not in the baseline |
| Gross Sales | full | The change against the last period trailing the heading ("£120.00 vs Last Thursday", or "No comparison data"); the amount in `heading-medium`; "48 transactions • From 23 Oct, 00:00 to now"; Card, Cash and Meal cards cells (zero ones hidden), each opening that payment method's sales; a bar chart, previous period in neutral and current in brand, with its legend. Empty: "No sales in the selected period" / "Try to select a different period." |
| Top products | full | Rows: initials, the product, "42 sold", the amount, and its trend in brand (up) or critical (down). "See all products". Without the Catalogue: "No sales yet" / "Your best-selling items will show here once you start taking payments." |
| VAT summary | full | No heading once it has data: "VAT summary" inline, the VAT in `display-small`, "VAT collected • Today", a table of Rate, Net, VAT and Gross, and "Export for accountant". Empty: "No VAT to report" |
| Last transactions | full | Three transaction rows without the store ("23 Oct 2026, 14:32"), then "Go to Sales Transactions". Empty: "No transactions yet" / "Payments and transfers will appear here as they happen." |
| Sales per store | full | With more than one store: the store, "31 sales", a small trend line and the amount. Opens Transactions for that store |
| Refunds | half | The amount in `display-xsmall`, "3 transactions". Opens refunded transactions |
| Chargebacks | half | The amount, "1 chargeback". Not tappable |
| Tips | full | The amount in `heading-large`, "Avg tip is £2.10 • 8% of each sale", then "Transactions with tips" with its share as a brand progress bar. Empty: "Tips you collect will show up here" |
| Sales Breakdown | full | Chips "Sales Channel", "Card brands", "Meal cards". Channel rows: Card machine, Pay by Link, E-commerce, Tap, each with its amount and share; an unused Pay by Link or Tap shows "Create your first" or "Get started" instead |
| Busy times | full | "Hourly / Daily / Monthly" in the heading; the peak ("13:00") in `heading-medium`, "Average sales per hour • vs Last week", and a bar chart. Empty: "No info yet" / "Keep selling to see your busy hours here" |

- **Below the grid:** a "Customise page" pill; in edit mode, "Add widgets" and "Done".
- **A widget that fails** shows "Something went wrong" with a retry; loading skeletons each widget.

The baseline's charts, numbers and products are illustrative. The kit draws its charts with
`lmnd-kit-bars`, `lmnd-kit-stats`, `lmnd-kit-legend`, `lmnd-kit-delta`, `lmnd-kit-table` and
`lmnd-kit-progress`. The trend line in Sales per store rows isn't drawn.

## Transactions (`sales-transactions.html`)

- **Search:** "Search by last 4 digits or reference", which scrolls away. While a search narrows the period
  an info notice says "Search only covers the last 90 days. Change the date to search other periods."
- **Filters:** Period ("This year" by default), Store, Channel, Device, Card brand, Status, Payment method,
  Currency, Amount, then Reset filters. Each shows only when it can apply; Channel and Status show the
  value picked when there's one.
- **Days:** "Today" with "23 Oct 2026", "Yesterday" with "22 Oct 2026", older days relative ("2 days
  ago"); no total.
- **Rows:**
  - The label is the card type, "Debit •••• 4242" or "Credit •••• 8817", or "Card", "PayPal", "Payment
    link", "Cash". The brand shows only as the logo.
  - The badge is the app's status artwork (`data-badge`); payment links have none.
  - The support line is "14:32 • Camden Market", with the merchant's reference on a second line when
    there is one.
  - Pending and on-hold amounts are dimmed. Declined, void, expired and failed amounts are struck
    through. Refunds read "-£12.00".
- **States:** more loads on scroll as three skeleton rows. Empty and errors: nothing on Android; "There's
  nothing here, yet" and a full-screen error on iOS.

## Payment links (`sales-payment-links.html`)

- **Search:** "Search by email, phone or reference", with the same 90-day notice.
- **Filters:** Period ("This year"), Link type (Single-use / Multi-use), Store, Status, Amount, Reset
  filters.
- **Rows**, by the day each link was created: the status as the label, with its symbol in its voice —
  Active (`link`, info), Completed (`circle-check`, positive), Expired (`clock-cross`, neutral), Disabled
  (`circle-x`, critical) — then "customer • reference • Expires 30 Oct, 14:32" and the amount.
- **Details** (not in a baseline): "Payment link", with Share while active. The hero has the amount, a tag
  ("Active", "Paid on {date}", "Expired", "Disabled") and "Created on 23 Oct 2026, 14:32". Cards: the
  link with a copy icon; Creation, Destination, Link type and "Expires at"; Reference; Basket items;
  Transactions; Breakdown; History. Actions: "Share link" in a card of its own, then "Change expiry",
  "Issue refund", "Report an issue" and "Expire link" (critical, which asks first).

## Transaction details (`transaction-details.html`)

- **Top bar:** "Sale transaction" for every kind of transaction. Back, or Close in a sheet. Trailing: a
  "More options" menu with "Download Receipt" and "Issue refund" when a refund is possible; otherwise a
  Download icon.
- **Hero:** the amount in `display-large` (struck through when it failed or was voided), the status tag,
  then "23 Oct 2026, 14:32 • Camden Market".

| Status | Tag | Voice | Icon |
| --- | --- | --- | --- |
| Payment approved | "Payment approved" | Positive | `check` |
| Payment declined | "Payment declined" | Critical | `times` |
| Void | "Payment void" | Neutral | `times` |
| Pending | "Payment pending" | Info | `clock` |
| Refund approved / declined | "Refund approved" / "Refund declined" | Neutral / Critical | `arrow-corner-down-left` |
| Pre-authorisation | "Pre-authorisation approved" / "expired" | Neutral / Critical | `clock` / `clock-cross` |
| On hold | "Settlement on hold" | Warning | `clock` |
| Settled | "Settlement paid" | Positive | `check-double` |
| Partially refunded | "Partially refunded" | Warning | `undo` |

- **Cards, in order, each when it applies,** with compact rows:
  1. **Reason for failure**, such as "Insufficient funds".
  2. **Under review**, when on hold: "As part of our routine checks, we are currently reviewing your
     transfer…", then "Chat with us".
  3. **Original transaction**, for a refund: the sale as a transaction row.
  4. **Link**, for a payment link: "Payment link" with "Expires on 23 Oct 2026 at 14:32", and Destination.
  5. **Refunds** made on it: "Refund sent", the date and "-£5.00".
  6. **Unreferenced refund:** "This refund was entered manually and it is not linked to an original
     payment.", with Refund amount, Entered on and Refunded to.
  7. **Origin:** Channel, Device ID (with copy; "Device ID copied"), Store, Reference.
  8. **Payment method:** Card brand, Card type, Last 4 card digits, Issuing country, Entry mode,
     Authorisation code.
  9. **Breakdown:** Transaction amount, Tips, Cash Advance, Fees ("- £0.34").
  10. **Items**, the basket.
  11. **History:** newest first, "Settlement paid: £23.66", "Payment approved: £24.00", "Refund approved:
      £5.00 was refunded to the customer."
  12. **Actions:** "Download Receipt" and "Issue refund".
- **Toasts:** "Refund sent"; "There was an issue with your refund request. Please try again."

## As in production: known faults

The baselines copy these on purpose, each marked with an exemption comment where it shows. Leave them
unless the change is about them.

- Details' title is "Sale transaction" even for a refund or a pre-authorisation.
- Details' metadata is `body-medium`, not `body-small` (`APP-DETAILS-HERO`).
- Details' actions are in the top bar's menu and in a card (`APP-DETAILS-ACTIONS`).
- Details' Breakdown is the app's label-and-value rows, both sides secondary (`LDS-LIST-NO-HANDROLL`).
- Overview's Period chip shows as selected at its default, Today (`APP-FILTER-BAR`).
- Overview opens on Today; Transactions and Payment links open on This year.
- Days have no total (`APP-FEED-GROUPS`).
- The VAT summary has an overline heading only while empty; with data its title is inline.
- Title case: "New Sale", "Gross Sales", "Sales Breakdown", "Sales Channel", "Download Receipt", "Never
  Expires".
- "Go to Sales Transactions" leads from inside Sales to its own tab.
- The currency tile counts "transactions" where the tooltips say "sales".
- The Under review text says "transfer".
- An expired link reads "Expires {past date}", and an unknown store reads "• -".

## Android and iOS

- The inner tabs sit in the top bar on Android, and under it on iOS.
- Transactions' empty and error states, and details' loading and error, exist on iOS only.
- "Issue refund" on a payment link's details is Android only.
