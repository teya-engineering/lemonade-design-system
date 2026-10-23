# The app's screens

The Teya app as it is, for changes to a screen that already exists. Taken from the app's code on 23 Oct
2026; the app may have moved on since, and where this folder and the app disagree, the app is right.

## Described screens

Each has a description of how it works and baseline pages that copy it as it is in production, faults
included. Iterate on a copy of the baseline (`workflows/design.md`), never on a screen rebuilt from an
archetype.

| Screen | Description | Baselines |
| --- | --- | --- |
| Home, its "+" menu and the side menu | `home.md` | `home.html` |
| Money › Settlements, and Settlement details | `settlements.md` | `money-settlements.html` (Daily report), `money-settlements-list.html` (List, with its filter sheets), `settlement-details.html` |

A screen not listed here has no baseline. Build it from the closest example and the rules, and say in the
hand-over that it wasn't copied from the app. A screenshot of the real screen, if there is one, is the
best guide.

## Site map

Four tabs, always in this order and always shown: **Home**, **Sales**, **Money**, **Teya AI**. On a wide
screen the tab bar becomes a rail on the left. What changes between merchants is what's inside each tab.

"Push" is a full screen on the stack, with Back; "modal" a full screen presented over another, with
Close; "sheet" a bottom sheet; "menu" a dropdown from a button.

```
Home   (no title: the company chip and an eye that hides the balance)
├─ Company chip → Side menu; long press → company picker, or Business information
├─ Balance, with Sell, Pay and "+"
│  ├─ Balance → Money › Overview
│  ├─ Sell → Sell flow (push)
│  ├─ Pay → Bill payments (push)
│  └─ "+" menu: New Savings Pot · Account details · Cash deposit · Bank accounts · New payment link
└─ Widgets (long press to edit): Today's sales → Sales › Overview · Today's settlements → Money ›
   Settlements · Savings Pots · Teya Account Activity → Money › Activity · Offers

Sales   ("+" menu: New Sale · New payment link)
├─ Overview: filter chips (period, stores, devices, payment method) over widgets
├─ Transactions: search and filters over the feed; Export (modal)
│  └─ Transaction details (push) → Issue refund · Download receipt · Chat · Payment link details
└─ Payment links: filters over the list
   └─ Payment link details (push) → its transactions → Transaction details

Money   ("+" Add money menu: Pay · Quick Transfer (sheet) · Share account details · Deposit cash;
         hidden on Settlements)
├─ Overview: Money in, Money in & out, Savings Pots, Upcoming payments, My cards, Funding, Last
│  settlements (a row → Settlement details)
├─ Activity ("My activity" for someone who only sees their own): account transactions
│  └─ Transaction details (push), or Grouped transactions (sheet)
├─ Settlements: Daily report or List                                   → settlements.md
│  └─ Settlement details (sheet) → Sales transactions · Transactions held
└─ Funding: financing → its details (push)

Teya AI
└─ Chat, with New chat and Chat history (sheet); an early-access teaser where it isn't on

Side menu   (from Home's company chip; a dialog on tablets)
├─ The company: logo, name, the user and their role
├─ Pricing → Subscriptions (push) · Card machines (or "Payment devices") → Card machines (push)
├─ Referral card
├─ Stores · Bank accounts · Team & Permissions · Integrations · Statements and Billing (or Billing)
├─ Settings (push) · Help (sheet) · Log out (confirmation)
└─ Version · "Teya Services Ltd."

Settings (push): the signed-in person → Account · Security and privacy · Preferences · Business
information · Card machines · Team and permissions · Settlements → Settlement preferences · Log out
Help (sheet): Ask Teya AI (or Chat) · WhatsApp · Email · Call us · Help centre
```

There is no notification inbox and no Settings gear: Settings, Help and the company picker are all in the
side menu.

**What changes the map:**

- **Sales:** Overview, Transactions and Payment links each need their own permission; Payment links also
  needs the product.
- **Money:** Activity needs a Business Account and permission to see transactions. Settlements needs
  permission to view settlements. Funding needs an active financing product.
- **Home:** with a Business Account it shows the balance and account widgets; without, sales and
  settlements widgets.
- **No company yet:** Home shows registration, and Sales, Money and Teya AI show upsells.
- **Side menu:** Bank accounts, Team & Permissions, Integrations, Statements and Billing and Pricing each
  need their own permission.
- **Roles:** a Manager, Accountant or Cashier sees a banner on Home saying what they can't do.
