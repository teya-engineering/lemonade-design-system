---
title: Prototyping with Lemonade
description: How to build a web prototype that reads as Teya, including a block to paste into an AI session.
---

For designers, product managers and anyone validating an idea in a web prototype — usually
with an AI tool such as v0, Lovable or Claude.

## Three things to know first

**There is no Lemonade web library.** Lemonade ships Kotlin Multiplatform and SwiftUI.
Nothing on this site can be installed from npm. An AI tool will happily invent a
`<LemonadeButton>` if you let it — that component does not exist, and a prototype built on
it cannot be handed to anyone.

**The values here are reference, not a contract.** Copy them into your prototype as raw
values. Official web support for Lemonade — tokens and components — is planned, and when it
arrives the names and structure may differ from anything you paste today.

**Approximate is the goal.** A prototype should read as Teya at a glance: right colours,
right typeface, right rhythm. It should not look production-shippable, because it is not.
Chasing pixel fidelity builds a second, unmaintained implementation of the design system.

## Load the typeface

Lemonade is set in **Figtree**, which is on Google Fonts. Without it a prototype falls back
to something generic and stops looking like Teya, so load it first:

```html
<link
  href="https://fonts.googleapis.com/css2?family=Figtree:wght@400;500;600;700&display=swap"
  rel="stylesheet"
/>
```

## Set up your AI session

Paste this at the start of a prototyping session. It carries the visual foundations and the
behaviour rules that matter most.

````text
We're building a WEB PROTOTYPE for Teya, styled with the Lemonade design system.

IMPORTANT CONSTRAINT
Lemonade ships only Kotlin Multiplatform and SwiftUI — there is NO web component
library. Do not import, install, or invent any Lemonade package or component.
Build plain HTML/CSS (or React, if I ask) and style it with the raw values below.
Treat these values as reference only: they are not a public API.

TYPEFACE
Figtree throughout, loaded from Google Fonts.
Weights: Regular 400, Medium 500, SemiBold 600, Bold 700.
Sizes (px): 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 40, 48, 56, 64, 72.
Body copy is 16. Never invent sizes between these steps.

COLOUR — light / dark. 8-digit hex is #RRGGBBAA.
Surfaces      bg-default        #ffffff    / #201f1d
              bg-subtle         #f6f5f3    / #151513
Text/icons    content-primary   #090806ec  / #ffffffe5
              content-secondary #16140e8c  / #ffffff99
              content-tertiary  #211c1266  / #ffffff80
Brand         bg-brand          #e1e51a    / #e1e51a   (fixed in both themes)
              content-on-brand  #29320c    / #29320c   (text ON brand)
Status text   positive          #497d00    / #7ccf00
              caution           #bb4d00    / #fe9a00
              critical          #e41e2b    / #f73c48
              info              #1447e6    / #51a2ff
Status fills  positive-subtle   #7ccf001a  / #7ccf001a
              caution-subtle    #fe9a001a  / #fe9a001a
              critical-subtle   #f73c481a  / #f73c481a
              info-subtle       #2b7fff1a  / #2b7fff1a
Borders       neutral-low       #756b571a  / #ffffff1a
              neutral-medium    #8f867633  / #ffffff33

Pair a status TEXT colour with its matching SUBTLE fill — never a status text
colour on a full-strength status background.

SPACING (px) — use only these: 2, 4, 8, 12, 16, 20, 24, 32, 40, 48, 56, 64, 72, 80.
16 between related fields, 24 between groups, 32+ between sections.
Never use a value between two steps.

RADIUS (px) — use only these: 2, 4, 6, 8, 10, 12, 14, 16, 20, 24, 32, 40.
Use 999 for pill shapes. Cards and containers default to 24.

BEHAVIOUR RULES — these hold regardless of platform:
- Every input has a visible label. A placeholder is NOT a label.
- Validate on blur, not on keystroke. Clear an error as soon as it's corrected.
- Error messages say what to fix ("Enter an email in the format name@company.com"),
  not what went wrong ("Invalid input").
- Field-level errors go on the field. Whole-form or server errors go in a notice
  above the form. Toasts are only for transient things the user can't act on.
- Empty states are normal, not errors — explain them and offer the action that
  fills them. "Nothing created yet" and "no results match this filter" are
  different states with different actions.
- Minimum touch target 44x44pt (iOS) / 48x48dp (Android), even when the visible
  control is smaller.
- Support light and dark using the pairs above.

Ask me before inventing any colour, size, spacing or radius not listed here.
````

That last line is deliberate: it makes the tool stop and check rather than confidently
improvise a value that is nearly right. Drop it if you find it slows you down.

## Where the full values live

The block above is a working subset, chosen because colour, type and spacing are what make a
prototype read as Teya. The complete set — every token, its light and dark value, and the
usage note design wrote in Figma — is in Foundations:

- [Colour](/lemonade-design-system/foundations/colour/)
- [Typography](/lemonade-design-system/foundations/typography/)
- [Space & shape](/lemonade-design-system/foundations/space-and-shape/)
- [Elevation](/lemonade-design-system/foundations/elevation/)
- [Opacity & borders](/lemonade-design-system/foundations/opacity-and-borders/)

Elevation and opacity are left out of the paste block on purpose — they matter less to a
prototype's credibility than the three above, and every line in that block costs attention.

## The thinking behind the rules

The behaviour rules are compressed to fit. When a prototype needs to get one right in
detail, the reasoning is written up in full, and it is platform-agnostic — it applies to a
web prototype exactly as it applies to production:

- [Semantic tokens first](/lemonade-design-system/standards/semantic-tokens/)
- [Layout & rhythm](/lemonade-design-system/standards/layout-rhythm/)
- [Accessibility](/lemonade-design-system/standards/accessibility/)
- [Forms](/lemonade-design-system/patterns/forms/)
- [Empty & loading states](/lemonade-design-system/patterns/empty-and-loading/)
- [Errors & recovery](/lemonade-design-system/patterns/errors/)

## When web support lands

Official Lemonade web tokens and components are planned. When they ship, this page will be
rewritten to point at them, and new prototypes should use the real thing. Prototypes built
against this page will not break — they hold self-contained raw values — but they will not
inherit any improvements either.
