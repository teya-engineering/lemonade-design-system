# Colour

## `LDS-COLOR-SEMANTIC` · Semantic first, themed where the meaning is not modelled, primitives never

**Kind.** Rule.

**Rule.** Three layers, in this order.

**Semantic** — `--lmnd-color-content-primary`, `--lmnd-color-bg-subtle`, `--lmnd-color-border-neutral-low`
— whenever one fits, which is nearly always. A semantic token names what the colour means, so it follows
the theme and it survives a palette change. The kit's components and the `lmnd-kit-secondary`-style text
helpers already use them.

**Themed** — `--lmnd-color-themed-<hue>-*`, theme-aware hues — for colour the design system does not
model: chart series, categories, per-role or per-product accents. The layer is experimental in the app,
so expect a prototype that uses it to be revisited.

**Primitive** — never. A hex value, `rgb()` or a named colour is as wrong as a primitive, for the same
reason: it cannot follow the theme.

**Why.** A primitive or a literal is a colour that looks right in the theme it was written in and wrong
in the other one. Every kit page renders in light and dark, so the mistake shows the moment someone
switches.

**Exceptions.** None for primitives and literals. Reach for the themed layer only after checking that no
semantic token carries the meaning.

**Do / don't.** `color: var(--lmnd-color-content-critical)` for an error. A themed hue for the fourth
series in a chart. Never `#e5262a`, and never `color: red`.
