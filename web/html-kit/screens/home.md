# Home

What the Teya app's Home tab is today, so a change can start from it. Taken from the app's code on 23 Oct
2026; where this file and the app disagree, the app is right.

Home is the first tab. It has no title: the company sits in the top bar, the balance on a brand-yellow
header under it, and widgets below. The company opens the **side menu**, the only way to Settings, Help and
the rest of the company's setup.

| Baseline | Shows |
| --- | --- |
| `home-restricted.html` | Home for a Cashier on a business still finishing registration: the role strip, the locked balance, Sell and "+", the registration card and the feedback card |
| `home.html` | Home for a merchant with a Business Account: balance, Sell, Pay and "+", and the default widgets except Savings Pots. The "+" menu is `#more-actions`, the side menu `#side-menu` |

The baseline shows the ready state with the balance visible. The variants and other states are described
below; build one from the baseline when a change needs it.

## Top bar

- **Company chip** (`lmnd-kit-identifier-button`): the company's logo or initials in a medium rounded
  symbol, its name in `body-medium-semibold`, and a `chevron-right`, on `bg-brand-elevated` with an
  `border-on-brand-medium` hairline. Tap opens the side menu; a long press opens the company picker when
  there's more than one company. With no company yet it shows a `user` symbol and the person's name.
- **Eye** (`lmnd-kit-identifier-button--circle`, `eye-open` / `eye-closed`, "Toggle balance
  visibility"), only while the balance shows.
- The bar is clear over the yellow, and fills with `bg-subtle` once the page scrolls. Scrolled past 16px,
  the chip folds down to its symbol.

## Balance header (`lmnd-kit-balance`)

- "Available balance" in `body-small-regular` on-brand low, then the amount in `display-medium` on-brand
  high, "£12,345.67". Tapping it opens Money › Overview.
- **Hidden:** the digits become "••••••"; the currency symbol stays.
- **No permission to see it** (`home-restricted.html`): "£ • • • • • •" and, at the far end, a Primary ·
  Solid circular `padlock` button, which reads as a bare lock on the yellow. The row opens the "You
  don't have access" sheet.
- **Balance Boost**, between the balance and the buttons when offered: "Balance Boost • Earn 4.5% AER" /
  "Daily interest on your full balance" as a switch row; once on, "+£12.40 interest earned this month ›".
- **Buttons**, `spacing-200` apart, each shown only with its permission:
  - **Sell:** a Lemonade Button, Secondary · Solid · Medium, `card-machine`, at least 96px wide.
  - **Pay:** `lmnd-kit-identifier-button--text`, `arrow-up-right`, "Pay".
  - **"+":** `lmnd-kit-identifier-button--icon`, "More actions". It opens a dropdown: under "Account", New
    Savings Pot (`pig`), Account details (`money-check`), Cash deposit (`money-arrow-down`), Bank accounts
    (`bank`); under "Sell", New payment link (`link`).
- The yellow fades into `bg-subtle` about 100px below the buttons, and scrolls with the page.

## Between the header and the widgets, when they apply

1. **Registration card**, until the merchant is set up. One of: an invitation ("You've been invited to
   join {company} as {role}", "Accept invitation"), no offer yet ("Let's find the perfect offer for you",
   "Choose offer"), registration in progress ("Finish activating your account", "Continue
   registration"), payment failed ("We couldn't complete your payment", "Review payment details"),
   under review ("We're reviewing your application", "Check your order") or declined ("We're unable to
   register your account", "Learn more").
2. **Notifications:** a stacked deck of cards, headed "Notifications" with "Show more" / "Show less".
   Each card is a list item with a voiced symbol, a one-line title, up to three lines of description,
   and a chevron when it leads somewhere.
3. **Orders:** the same deck, for card and card-machine orders, with a "Processing / Dispatched /
   Delivered" stepper.
4. **Role strip**, for a Manager, Accountant or Cashier (`home-restricted.html`). It isn't a block in the
   page: it changes the whole screen (`lmnd-kit-screen--restricted`). A dark strip on `bg-always-dark`
   takes the status bar, which turns light, and holds a `padlock` at 80% and "Some features aren't
   available for your current role." in `body-small-regular`, light and centred, with `spacing-400`
   around. It stays put; Home scrolls under it in a `bg-subtle` panel with `radius-900` top corners.
   The same line serves all three roles. What the role can't use is gone from the screen rather than
   disabled: Pay and the eye are missing, and a balance the role can't see is locked.

## Widgets

A grid (`lmnd-kit-widgets`), `spacing-300` across and `spacing-400` down. Half-width widgets keep the
173 × 172 mini shape. The set depends on the merchant:

| With a Business Account (the baseline) | Without |
| --- | --- |
| Gross sales today (half), Today's settlements (half), Offers (full), Savings Pots (full), Teya Account Activity (full) | Today's sales, Offers, Last settlements, Savings Pots (all full) |

- **Gross sales today:** the label (`lmnd-kit-widget__label`, `body-small-regular`) in the figure with
  the amount, 4px above it, not as an overline heading; the amount in `display-xsmall`; "To be paid
  tomorrow" (or "To be paid on 15 Jun", "+ 2 currencies"); then the day's chart, 140 × 76, 4px under the
  figure and cut by the widget's edge (`lmnd-kit-sparkline`, the Figma Mini Widget's chart; its curve
  doesn't follow the amount). Empty: "Ready for your first sale of the day?" in
  positive, over an empty chart.
- **Today's settlements:** the label in the figure, as above; the amount, the day's status in its voice ("Paid"), and a
  "More details ›" bar at its foot (`lmnd-kit-widget__footer`). Opens Money › Settlements. Empty: "No settlements
  yet". With stores on instant: "2 stores on instant" / "Total tomorrow".
- **Offers:** a carousel of cards with no heading, turning every 4 seconds, with dots under it
  (`lmnd-kit-content-card`, `lmnd-kit-page-indicator`). Each card: artwork, a title, a line, a call to
  action in brand ("Find out more ›") and a dismiss. The copy comes from the CMS; the baseline's is
  illustrative.
- **Savings Pots:** overline "Savings Pots · 4.5% AER" with the rate in brand, and a "New" button. Empty:
  a piggy bank, "Instant access, daily payout and FSCS protected", "Create your first Pot". One pot
  shows its balance in `heading-small`; several page sideways.
- **Teya Account Activity:** overline heading, account rows (initials with a `money-in` or `money-out`
  badge, "Today, 11:05 · INV-2231", positive amounts for money in), and "See account activity".
- **Today's sales** (without a Business Account): a neutral gradient card with "Today's sales" and an
  info tooltip, the amount in `display-small`, then "This week so far" and "This month so far" against
  last week and last month.
- **Last settlements** (without a Business Account): overline heading, settlement rows as in the
  Settlements List, and "See all settlements". Empty: "No settlements" / "You'll see your settlements
  here once there's something to pay out."
- **A widget that fails** shows a small illustration, "Something went wrong" and "Try again".

**Editing:** a long press on a widget opens a dropdown with "Edit page" (`grid`) and "Remove widget"
(`trash`). In edit mode the widgets jiggle, everything above them dims, and the top bar becomes a dark
"Edit mode" / "Add, remove or reorder widgets" bar with "Add" and "Done"; "Add widgets" and "Done"
also sit under the grid. "Add widgets" opens a sheet of live previews with a checkbox each.

## Side menu (`#side-menu`, `lmnd-kit-drawer`)

From the left, at most 360px and 90% of the screen, on `bg-subtle` with `radius-800` on its right corners,
over a scrim.

1. **Close** (`times`), top left.
2. **The company:** its logo or initials, extra large; the name in `body-xlarge-semibold`, with a
   `chevron-down` when there's more than one company; then "Joe Doe • Admin" (Super Admin, Admin,
   Manager, Accountant or Employee).
3. **Two cards side by side:** "Pricing" / "From 1.2%" with the Teya symbol, and "Card machines" (or
   "Payment devices") / "2 units". Each needs its permission.
4. **Refer a business:** a brand-yellow card with a `gift` symbol and a line from the CMS.
5. **The company's setup:** Stores, Bank accounts, Team & Permissions, Integrations, Statements and
   Billing (or Billing), each with a chevron and its own permission.
6. **Settings, Help, Log out**, with no chevrons. Log out asks first.
7. **Footer:** "Version 2.51.8 (456)", "Teya Services Ltd.".

The company picker, from the chip or the name: a sheet, "Select Company", one card of companies (avatar,
name, "GB123456789 • 3 stores"), search past 10, and "Confirm".

## Other states

- **Loading:** the chip is a skeleton and the eye is hidden; widgets skeleton in place. It pulls to
  refresh.
- **No company yet:** no balance and no widgets. The registration card sits on the yellow, then orders,
  then a carousel of account benefits from the CMS.
- **Errors:** there is no page-level error; a widget fails on its own, or a toast says so.

## As in production: known faults

The baseline copies these on purpose. Leave them unless the change is about them.

- Sell is a Lemonade Button but Pay and "+" are the app's own buttons, so the three side by side don't
  match (`LDS-BUTTON-PAIR`, marked in the baseline).
- Account activity separates with "·", not "•" (`LHK-COPY`, marked).
- Log out shares a card with Settings and Help, rather than having its own (`APP-MENU-LOG-OUT`, marked).
- The half-width widgets have no overline heading, unlike the same widgets on Sales.
- The chip always shows `chevron-right`, even with one company; the side menu uses `chevron-down`, and
  only with several.
- There are two pairs of edit-mode controls: Add and Done in the top bar and again under the grid.
- While the merchant's subscriptions load, Home assumes a Business Account and can switch widget sets
  when they arrive.
- Title case in "New Savings Pot", "Teya Account Activity" and "Select Company".
- In the restricted Home the "+" menu's items for this role are unverified; the baseline keeps Home's.
- The restricted Home's registration artwork is an image in the app; the baseline stands in a symbol.
- The yellow fade is an image in the app. The baseline draws it with `bg-brand`; how it looks in dark mode
  is unverified.

## Android and iOS

- The eye: a 40px brand circle with no border on Android (the baseline gives it the chip's hairline); glass at 48px on iOS 26; a Primary · Solid icon button before.
- The chip folds on scroll on Android and on iOS 18 and later only.
- The side menu has no width cap on iOS, and its info cards are drawn by hand.
- iOS shows a remove badge on each widget in edit mode; Android removes through the long-press menu only.
