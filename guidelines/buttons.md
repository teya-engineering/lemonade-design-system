# Buttons

## `LDS-BUTTON-PAIR` · At most two actions, primary above secondary

**Rule.** A screen offers at most two actions, the primary above the secondary.

The primary is the component default on both platforms — `variant = Primary`, `type = Solid`,
`size = Large` — so it needs no arguments. The secondary in the hierarchy is always `Neutral` /
`Subtle`, written out in full. A destructive primary is `Critical` / `Solid` over the same secondary.

**Why.** Two buttons of equal weight ask the user to decide which one the screen wants. A third action
means the screen is doing two jobs.

**`Secondary` is a branded treatment, not a position in the hierarchy.** It never means "the second
button".

**Exceptions.** A third action belongs in the top bar or in the content, not in the action block.

**Do / don't.** A bare `LemonadeUi.Button(label = …, onClick = …)` above a `Neutral` / `Subtle` one.
Not two bare buttons stacked — both of those are primaries.

## `LDS-BUTTON-EXPLICIT-TYPE` · Only a primary relies on the defaults

**Rule.** Any variant other than `Primary` states its `type`.

**Why.** `variant` and `type` are independent enums and `type` defaults to `Solid`. So
`variant = Neutral` with no `type` renders a solid neutral button — a filled button where a subtle one
was meant.

**Exceptions.** None. A primary may omit both, which is what makes two stacked primaries detectable:
a call with no arguments *is* a primary solid.

**Do / don't.** `variant = Neutral, type = Subtle`. Not `variant = Neutral` alone.
