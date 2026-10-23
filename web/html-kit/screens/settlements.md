# Settlements

What the Teya app's Settlements screens are today, so a change can start from them rather than from a
blank archetype. Taken from the app's code on 23 Oct 2026. The app may have moved on since; where this
file and the app disagree, the app is right.

Settlements is not a screen of its own. It is the **Settlements tab of Money** (`index.md`), shown only to
someone allowed to view settlements. It has two view modes, picked from a menu in the top bar and
remembered: **Daily report**, the default, and **List**. Tapping a settlement in either opens
**Settlement details**, a large sheet.

| Baseline | Shows |
| --- | --- |
| `money-settlements.html` | Money › Settlements in Daily report mode: a paid day with two stores |
| `money-settlements-list.html` | Money › Settlements in List mode, with the Period, Status and Store sheets (`#period-sheet`, `#status-sheet`, `#store-sheet`) |
| `settlement-details.html` | Settlement details for one store's paid settlement, drawn as a full screen |

The baselines show the ready state. The other states are described below; build one from its baseline
when a change needs it.

## The Money screen around it

- **Top bar:** the compact large title "Money", no back button.
- **Tabs:** Overview, Activity ("My activity" when the user only sees their own), Settlements, Funding.
- **Trailing actions on the Settlements tab:**
  1. **View mode menu.** Its glyph shows the current mode: `calendar-day-timeline` for Daily report, `list`
     for List. Its label is "More options". It opens a menu with one section, "View mode", holding "Daily
     report" and "List", the current one ticked (`#view-mode-menu` in both baselines).
  2. **Export** (`download`, "Export settlements"), only with permission to view reports. It opens the
     "Export report" picker: "Generate report" ("Download a PDF including a breakdown of your
     settlement") or "Settlement List CSV" ("Download all transactions included in the settlement").
- Money's "+" (Add money) is hidden on this tab.
- **Tab bar:** Home, Sales, Money (current), Teya AI.

## Daily report (`money-settlements.html`)

One day's settlements, picked from a strip of days. Top to bottom:

| Block | Kit markup | Content |
| --- | --- | --- |
| Month | `lmnd-kit-month-nav` | Ghost circular chevrons, "Previous month" and "Next month", around "October 2026". Next is disabled on the current month |
| Days | `lmnd-kit-day-strip` of `lmnd-kit-day-chip` | Every day of the month up to today, 64px chips: the short weekday ("Thu") over the date ("22") over status dots. It opens scrolled so the selected day is fourth from the left; earlier days sit off to the left |
| Upsell, optional | `lmnd-kit-notice` | "Sell and get paid in seconds" / "Receive your money seconds after a sale with Instant Settlements.", "Set up now" and Dismiss. Not shown in Hungary or Czechia |
| Currencies, optional | `lmnd-kit-chips` | Only on a day with more than one currency: "GBP • £", "EUR • €" |
| Settlement amount | `lmnd-kit-card lmnd-kit-gradient-card--<voice>` | Overline "Settlement amount" with the day's status tag ("Paid", "Partial payment", …), the amount in `display-small`, then "2 stores". Below, as rows: "Sending to" (`bank`, the account or "Multiple accounts") and "Settlement Frequency" (`calendar`, "Business days", "Everyday", "Instant", "Scheduled" or "Multiple") |
| Per store | `lmnd-kit-card` | Overline "Per store", and "3 of 8" trailing when cut short (`lmnd-kit-card__trailing`). A row per store: initials or logo with its frequency (`lmnd-kit-symbol__frequency--<frequency>`), the store's name, "1 settlement · 48 transactions · Business days" on one line, the amount in semibold over the status tag. A last row "View all 8 stores" when cut short |
| Breakdown overview | `lmnd-kit-card` | Overline "Breakdown overview". Rows with a plain icon and the amount trailing, each shown only when not zero: Initial balance (`coins-double-stack`), Card sales (`card`, "69 transactions"), Refunds (`arrow-corner-down-left`), Chargeback, Balance transfers (`arrows-exchange`), Fees (`coins-stack`, shown negative), Meal cards (`cookie`), On hold (`clock`), Released from hold, Cash Advance (`hand-coins`), Ending balance (`coins-double-stack`). Card sales, Refunds, Fees and On hold have a chevron and open that day's sheet |
| Actions | `lmnd-kit-card` | "Download report" (`download`, with permission), "Settlement preferences" (`gear`, with permission), "Get help" (`support-chat`), each with a chevron |

**Day chips.** A day with no settlement has one hollow dot. A settled day has a dot per status in its
voice; a second overlaps the first. The selected day is inverse and raised, with a glow from below in its
status's colour (`lmnd-kit-day-chip--glow-<voice>`), or red when it has more than one status.

**Statuses and voices:** Paid, positive. Partial payment, Postponed and Pending, warning. Scheduled,
info here (but neutral in the list and in details).

**Frequencies:** Instant (`calendar-lightning`), Business days (`calendar-arrow-right`), Everyday
(`calendar-repeat`), Scheduled, "N days after sale" (`calendar-day`). Each has its own colour, the
`bg-settlement-*` tokens.

**An empty day** replaces the amount, per-store and breakdown cards with a card titled "No settlement for
this day", "No settlements have been scheduled for this day", or "Day out of the payment frequency" with
a call to action to Everyday or Instant, followed by the Sending to and Settlement Frequency rows in a
card of their own.

**Some stores on instant:** under the amount, "2 stores on instant" (`lightning`, brand) with "Detailed
breakdown will be available tomorrow". **All stores on instant:** the amount is replaced by "You're
getting paid instantly" / "Today's breakdown available tomorrow".

**Where it goes:** a store row opens Settlement details, or a "Settlements" sheet listing that store's
payouts when it has several. Card sales, Refunds and Fees open sheets for the day; On hold opens
"Transactions held"; Sending to opens Bank accounts; Settlement Frequency opens Change frequency (with
permission to edit; otherwise the row has no chevron and does nothing).

**States:** loading skeletons the month, the days and the cards in place. It pulls to refresh.

## List (`money-settlements-list.html`)

Every settlement, newest first, grouped by week.

**Filters**, in a filter bar that scrolls with the list. All three open picker sheets with "Reset" in the
bar and a fixed "Confirm":

| Chip | Sheet | Options |
| --- | --- | --- |
| Period (the preset's name once picked, e.g. "This week") | "Filter by period", single choice | Today, Yesterday, This week, Last week, This month, Last month, Last 90 days, This year, Custom. Each shows its range, "1 Oct 2026 - 31 Oct 2026"; Custom reads "Choose a custom period" and opens a range calendar in the sheet, with Confirm disabled until a start date is picked |
| Status (with a counter) | "Filter by status", several | Paid, Partial payment, Postponed, Scheduled |
| Store (with a counter; absent with one store) | "Filter by store", several | Every store, with its logo or initials and its address; an inactive store has a neutral "Inactive" tag. Search appears past 10 stores |

"Clear filters" (Neutral · Ghost · X-Small) ends the bar once a filter is set. By default nothing is
picked, and the list shows paid, postponed, scheduled and pending settlements.

**Groups:** by week, Monday to Sunday, headed by the range: "19–25 Oct 2026", "26 Oct – 1 Nov 2026". No
total. A settlement's week is its payout date, so scheduled settlements sit in weeks ahead of today, at
the top.

**Rows:** one per settlement, with no chevron, tag or badge:

| Status | Symbol | Support |
| --- | --- | --- |
| Paid | `check-double`, positive | "Paid on 22 Oct" |
| Scheduled | `calendar-day`, neutral | "Scheduled" |
| Postponed | `clock`, warning | "Postponed" |
| Pending, partially paid | `clock`, warning | "To be paid tomorrow, 24 Oct", or "today", "in 3 days" |

The label is the store's name, or the payout date when the store has none. The amount is in the default
value style.

**States:** more loads on scroll, as three skeleton rows under a blank header. It does not pull to
refresh. An empty list shows nothing under the filter bar on Android, and "There's nothing here, yet" on
iOS.

## Settlement details (`settlement-details.html`)

A large sheet over Money, with the details container behind it: a wash in the status's colour.

- **Top bar:** Close, "Settlement Details", and a Download menu: "Download report" (`file-pdf`, only once
  the PDF exists) and "Download transactions CSV" (`file-chart`). When the hero scrolls away the title
  becomes "£1,640.20 • Camden Market".
- **Hero:** the amount (`display-large`), the status tag, the store's name.

| Status | Tag | Voice |
| --- | --- | --- |
| Paid | `check`, "Paid on 22 Oct 2026" | Positive |
| Partially paid | `clock`, "Partial payment" | Warning |
| Postponed | `clock-arrow-up`, "Postponed" | Warning |
| Pending | `clock`, "Pending" | Warning |
| Scheduled | `clock`, "Scheduled to 24 Oct 2026" | Neutral |

- **Cards, in order:**
  1. **A reason**, for postponed, partially paid or scheduled settlements only: "Reason for postponed",
     "Reason for partial payment" (ending in a "View transactions" link to "Transactions held") or
     "Reason for scheduled", with its explanation, such as "This settlement is scheduled. Based on your
     settlement plan, it's due to be paid on 24 Oct 2026."
  2. **Breakdown** (`lmnd-kit-row-data`): Initial balance, Card sales (with Meal cards under it), Balance
     activity (with Refunds and Chargeback under it), Fees (with Teya fees, Interchange fees, Scheme
     fees, Chargeback fees, Monthly Fees, DCC revenue sharing, ISO fees and Minimum volume fees under
     it), Released from hold, Amount on hold, Cash Advance, and Ending balance in semibold. A row shows
     only when it has a value.
  3. **Transactions:** up to five, as sales rows, then "Go to Sales Transactions".
  4. **Sales by scheme** (`lmnd-kit-row-data`): each card scheme's logo, name and amount.
  5. **Actions:** "Download report" and "Download transactions CSV" ("Preparing CSV…" while it runs).
  6. **Help:** "Report an issue" (`circle-info`), which opens the chat.
- **States:** loading is a centred spinner under the top bar; an error replaces the content with a retry.

## As in production: known faults

The baselines copy these on purpose, each marked with an exemption comment. Leave them unless the change
is about them.

- The Daily report's cards sit `spacing-200` apart, not `APP-SPACING-RHYTHM`'s `spacing-400`.
- The per-store support line separates with "·", not "•".
- The List's filter order is Period, Status, Store: an attribute before the scope (`APP-FILTER-BAR`).
- A List week has no total (`APP-FEED-GROUPS`).
- Partially paid and unknown settlements are only listed once someone picks "Partial payment".
- A pending settlement whose date has passed reads "To be paid 2 days ago, 21 Oct".
- The Breakdown is the app's own label-and-value rows, not content list items (`LDS-LIST-NO-HANDROLL`).
- Details spreads its actions over the top bar's menu and two cards (`APP-DETAILS-ACTIONS`).
- Scheduled is info in the Daily report and neutral everywhere else.
- Details' tag comes from the server and its reason from the list, so the two can disagree.
- On Android the List ignores errors; on iOS an error replaces the whole tab, filters included.

## Android and iOS

They match except where noted above, and: Details' collapsed title is one line on Android, "£1,640.20 •
Camden Market", and two on iOS, amount over store; a failed download is a snackbar on Android and a toast
on iOS.
