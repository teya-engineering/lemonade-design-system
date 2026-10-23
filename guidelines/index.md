# Guidelines

How Lemonade components are used and combined. These rules apply to every product built on
Lemonade, so they name components and tokens and never a particular app's screens.

A component's own KDoc or doc comment says what it does and what its parameters mean. These rules
say what to do with it next to another one: when rows need a card, what belongs in a leading slot,
which of two buttons is the secondary, which colour layer to reach for.

| File | Rules |
| --- | --- |
| [cards.md](cards.md) | `LDS-CARD-GROUPED-ITEMS`, `LDS-CARD-NO-HANDROLL`, `LDS-CARD-SURFACE`, `LDS-CARD-PADDING` |
| [lists.md](lists.md) | `LDS-LIST-NO-HANDROLL`, `LDS-LIST-LEADING-SLOT`, `LDS-LIST-SYMBOL-SHAPE` |
| [buttons.md](buttons.md) | `LDS-BUTTON-PAIR`, `LDS-BUTTON-EXPLICIT-TYPE` |
| [colour.md](colour.md) | `LDS-COLOR-SEMANTIC` |
| [sheets.md](sheets.md) | `LDS-SHEET-CHROME` |
| [top-bar.md](top-bar.md) | `LDS-TOP-BAR-ACTIONS` |
| [text.md](text.md) | `LDS-TEXT-INTRO` |

## How to read a rule

Each rule has an ID, the rule itself, why it exists, and the full list of exceptions. Anything absent
from the exception list is a violation.

An ID is a slug: `<book>-<topic>-<name>`. `LDS` is this book. A consuming product with rules of its
own uses its own prefix, so an ID says which repository owns the rule and a reader can tell at a
glance whether a finding is arguable here or there.

Slugs are stable. A slug never changes once published, and a change to what a rule requires gets a
new slug rather than new wording under the old one — otherwise an exemption written against the old
rule silently starts exempting the new one.

An exemption is written at the site, in the consuming code, and states its reason:

```kotlin
// LDS-CARD-GROUPED-ITEMS exempt: the outline is this row's own container
```

## Rules and rules of thumb

A rule holds unless its exception list says otherwise. A rule of thumb says what to reach for and
leaves the judgment with a person, because no check can make the call. Each rule says which it is.

A rule of thumb can become a rule. Every exception writes its reason at the site, so the exemptions
can be read back: a few, each its own story, means the handful folds into the exception list and the
rule turns firm. Many that cluster mean the cluster is a rule nobody wrote. Many that scatter mean
the rule of thumb is wrong, or it asks a machine for a judgment.

## For consumers

A product following these rules keeps a copy of this directory in its own repository, synced at the
Lemonade version it pins, because an agent follows what it can read and Lemonade is a dependency
rather than a checkout. The Teya app does this with `scripts/sync-lemonade-guidelines.sh`.

Rules about a product's own screens — which archetype a screen is, its anatomy, its states — belong
in that product's repository, not here.
