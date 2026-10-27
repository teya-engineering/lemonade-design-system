# Guidelines

How the kit's components are used and combined. The kit draws the Lemonade components in HTML, so the
Lemonade rules apply to a prototype exactly as they apply to the app: a screen that breaks one here would
break it on Android and iOS too.

Three books of rules live here:

| Prefix | Owner | What it covers |
| --- | --- | --- |
| `LDS-` | Lemonade | How components are used and combined. The same rules, under the same IDs, as the Lemonade guidelines for KMP and SwiftUI, with the examples written in kit markup. |
| `LHK-` | This kit | How a prototype is built: tokens, markup, screens, assets, copy and flows. |
| `APP-` | The Teya app | How the app's own components and screen kinds are used, such as its dashboard widgets and details screens. The same book as the app's `docs/design/` rules, under the same prefix. The widget rules here are not in the app's docs yet; the details and list rules follow its entity detail, feed, resource list and picker sheet archetypes. |

| File | Rules |
| --- | --- |
| [cards.md](cards.md) | `LDS-CARD-GROUPED-ITEMS`, `LDS-CARD-NO-HANDROLL`, `LDS-CARD-SURFACE`, `LDS-CARD-PADDING` |
| [lists.md](lists.md) | `LDS-LIST-NO-HANDROLL`, `LDS-LIST-LEADING-SLOT`, `LDS-LIST-SYMBOL-SHAPE` |
| [buttons.md](buttons.md) | `LDS-BUTTON-PAIR`, `LDS-BUTTON-EXPLICIT-TYPE` |
| [colour.md](colour.md) | `LDS-COLOR-SEMANTIC` |
| [sheets.md](sheets.md) | `LDS-SHEET-CHROME`, `LDS-SHEET-BAR-ACTIONS` |
| [top-bar.md](top-bar.md) | `LDS-TOP-BAR-ACTIONS`, `LDS-TOP-BAR-DESCRIPTION` |
| [text.md](text.md) | `LDS-TEXT-INTRO` |
| [app-principles.md](app-principles.md) | `APP-SCREEN-ARCHETYPE`, `APP-TOPBAR-TITLE`, `APP-TOPBAR-ACTION`, `APP-TOPBAR-TRAILING`, `APP-TOPBAR-ACTION-STYLE`, `APP-SCREEN-STATES`, `APP-LOADING`, `APP-ROLE-STRIP`, `APP-ROLE-ABSENT`, `APP-ROLE-LOCKED`, `APP-WIDGET-EMPTY`, `APP-SCREEN-EMPTY`, `APP-FILTER-BAR`, `APP-SEGMENTED-SIZE`, `APP-FOOTER-PLACEMENT`, `APP-SPACING-RHYTHM` |
| [widgets.md](widgets.md) | `APP-WIDGET-GRID`, `APP-WIDGET-ONE-NAME`, `APP-WIDGET-ONE-VALUE`, `APP-WIDGET-SUPPORT`, `APP-WIDGET-STATUS-TINT`, `APP-WIDGET-BOTTOM`, `APP-WIDGET-BOX`, `APP-WIDGET-LIST-TITLE`, `APP-WIDGET-TAP` |
| [list-screens.md](list-screens.md) | `APP-LIST-KIND`, `APP-FEED-CARD-APPEARANCE`, `APP-FEED-GROUPS`, `APP-FEED-ROWS`, `APP-FEED-PAGING`, `APP-LIST-SEARCH`, `APP-RESOURCE-ROWS`, `APP-RESOURCE-GROUPS`, `APP-RESOURCE-ADD`, `APP-MENU-SECTIONS`, `APP-MENU-ROWS`, `APP-MENU-PROFILE`, `APP-MENU-LOG-OUT`, `APP-MENU-FOOTER`, `APP-PICKER-OPTIONS`, `APP-PICKER-LEADING` |
| [details.md](details.md) | `APP-DETAILS-TOP-BAR`, `APP-DETAILS-HERO`, `APP-DETAILS-WASH`, `APP-DETAILS-CARD-HEADING`, `APP-DETAILS-ROWS`, `APP-DETAILS-ACTIONS`, `APP-DETAILS-STATES` |
| [prototypes.md](prototypes.md) | `LHK-TOKENS-ONLY`, `LHK-KIT-MARKUP`, `LHK-SCREEN`, `LHK-SAFE-AREAS`, `LHK-ASSETS`, `LHK-COPY`, `LHK-FLOWS`, `LHK-SELECT-OPENS`, `LHK-SCREENS`, `LHK-SCRIPT` |

## How to read a rule

Each rule has an ID, the rule itself, why it exists, and the full list of exceptions. Anything absent
from the exception list is a violation.

An ID is a slug: `<book>-<topic>-<name>`. The book says which repository owns the rule, so a reader can
tell at a glance whether a finding is arguable here or upstream in Lemonade. Slugs never change: a change
to what a rule requires gets a new slug, so an exemption written against the old rule never silently
covers the new one.

An exemption is written at the site, in the prototype, and states its reason:

```html
<!-- LDS-CARD-GROUPED-ITEMS exempt: the outline is this row's own container -->
```

## Rules and rules of thumb

A rule holds unless its exception list says otherwise. A rule of thumb says what to reach for and leaves
the judgment with a person. Each rule says which it is.

## Reviewing a prototype

Read the prototype's HTML against these files before it goes to anyone. A finding names the rule
ID. A prototype that needs to break a rule says so with an exemption comment, and the hand-over mentions
it.
