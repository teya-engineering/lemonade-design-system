# Colour

## `LDS-COLOR-SEMANTIC` · Semantic first, themed where the meaning is not modelled, primitives never

**Rule.** Three layers, in this order.

**Semantic** — `colors.content.contentPrimary`, `colors.background.bgSubtle` — whenever one fits,
which is nearly always. A semantic token names what the colour means, so it follows the theme and it
survives a palette change.

**Themed** — `LemonadeThemedColors`, theme-aware hues each with a saturated and a subtle palette — for
colour the design system does not model: chart series, categories, per-role or per-product accents, a
`SymbolContainer` that carries no voice. The layer is experimental and needs
`@OptIn(ExperimentalLemonadeApi::class)`, so expect its call sites to be revisited.

**Primitive** — never in product code. The layer is marked `@InternalLemonadeApi`, and its own
documentation says using it directly is a mistake. A primitive is as wrong as a hex literal and for
the same reason: it cannot follow the theme.

**Why.** A primitive or a literal is a colour that looks right in the theme it was written in and
wrong in the other one.

**Exceptions.** None for primitives. Reach for the themed layer only after checking that no semantic
token carries the meaning.

**Do / don't.** `colors.content.contentCritical` for an error. `LemonadeThemedColors` for the fourth
series in a chart. Never a primitive palette entry, and never a hex literal.
