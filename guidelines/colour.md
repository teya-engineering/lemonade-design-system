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

Pick a token by what it means, even when another holds the same value today. `bgAlwaysLight` and
`bgDefault` match in the light theme and part in the dark one.

**Primitive** — never in product code. The layer is marked `@InternalLemonadeApi`, and its own
documentation says using it directly is a mistake. A primitive is as wrong as a hex literal and for
the same reason: it cannot follow the theme.

**Why.** A primitive or a literal is a colour that looks right in the theme it was written in and
wrong in the other one.

**Exceptions.** None for primitives. Reach for the themed layer only after checking that no semantic
token carries the meaning.

**Do / don't.** `colors.content.contentCritical` for an error. `LemonadeThemedColors` for the fourth
series in a chart. Never a primitive palette entry, and never a hex literal.

## `LDS-COLOR-SURFACE` · A container is lighter than the page

**Rule of thumb.** Fill a container with a colour lighter than the page under it: a `bgDefault` card on
a `bgSubtle` page. That is `LemonadeUi.Card`'s default `background`.

A darker container — `bgSubtle` or `bgElevated` on a `bgDefault` page — is allowed, but crowds the
screen.

`bgElevatedHigh` is one step above `bgElevated`, for a surface inside an elevated one.

**Why.** A lighter container lifts its content off the page. A container in its page's own colour reads
as no container at all.

**Exceptions.** A brand or voice surface — `bgBrandSubtle` behind a promotion, `bgCriticalSubtle`
behind an error — is chosen for its meaning, not its depth.

**Do / don't.** A `Default` card on a `bgSubtle` page. Not a `Default` card on a `bgDefault` page,
and not a `bgElevatedHigh` card straight on the page.

## `LDS-COLOR-ON-SURFACE` · Content on a filled surface uses that surface's pair

**Rule.** Text and icons on a filled background use the content token made for it:

| Background | Content |
| --- | --- |
| `bgBrand`, `bgBrandHigh`, `bgBrandElevated` | `contentOnBrandHigh`, and `contentOnBrandLow` for secondary content |
| `bgBrandSubtle` | `contentBrandHigh` |
| `bgCritical`, `bgCaution`, `bgInfo`, `bgPositive`, `bgNeutral`, `bgFeatured` | `content<Voice>OnColor` |
| `bgDefaultInverse`, `bgSubtleInverse`, `bgElevatedInverse` | `contentPrimaryInverse`, `contentSecondaryInverse`, `contentTertiaryInverse` |

On the neutral backgrounds and on a voice's subtle background, content is the ordinary
`contentPrimary` / `contentSecondary` set, or the voice's own content token (`contentCritical` on
`bgCriticalSubtle`).

**Why.** A pair is checked for contrast as a pair, in both themes. `contentPrimary` on `bgCritical`
can pass in one theme and fail in the other.

**Exceptions.** None.

**Do / don't.** `contentCriticalOnColor` on `bgCritical`. Not `contentPrimary`, and not
`contentAlwaysLight` because it happens to look right in light mode.

## `LDS-COLOR-INVERSE` · Inverse is an opposite surface, `Always` is a fixed one, neither is dark mode

**Rule.** An `Inverse` token is for a surface that contrasts with the theme — a dark toast or tooltip
in light mode, which turns light in dark mode. An `Always` token (`bgAlwaysDark`, `contentAlwaysLight`)
is for something that keeps one look in both themes — content over a photo or a camera feed, a
surface whose colour is part of the brand.

Dark mode is the theme's job. A screen reaches it by using semantic tokens, never by picking inverse or
`Always` tokens by hand.

**Why.** An `Always` token where an inverse one belongs stays dark on a dark screen and disappears. An
inverse token used to build dark mode flips back the moment the theme does.

**Exceptions.** None.

**Do / don't.** `bgDefaultInverse` with `contentPrimaryInverse` for a tooltip. `contentAlwaysLight`
over a photo. Not `bgAlwaysDark` for a tooltip, and not `contentPrimaryInverse` to make a screen dark.

## `LDS-COLOR-STATE` · Pressed and disabled come from tokens

**Rule.** A pressed element with a fill of its own takes that fill's `Interactive` token — `bgBrand`
becomes `bgBrandInteractive`. A pressed element with no fill of its own takes the `Pressed` token of
the background it sits on — a transparent row on `bgSubtle` shows `bgSubtlePressed`. A disabled
element is drawn whole at `opacities.state.opacityDisabled`, in its usual colours.

This is what the Lemonade controls do — a solid `Button` presses to its `Interactive` token, a
`Subtle` or `Ghost` one to `bgSubtlePressed` — so the rule is for anything a product draws itself.

**Why.** Each background has its own interaction tokens, tuned so the change shows on it in both
themes. A darker or greyer colour picked by hand is tuned for neither.

**Exceptions.** A component that animates its press as opacity, as `BoxSelection` and `Tabs` do with
`opacities.state.opacityPressed`.

**Deviations.** `Switch` presses its own track to a `Pressed` token (`bgBrandHighPressed`,
`bgElevatedPressed`) rather than an `Interactive` one.

**Do / don't.** `bgSubtlePressed` under a finger on a transparent row in a `bgSubtle` card. Not
`bgElevated` borrowed as a pressed colour, and not `contentTertiary` text for a disabled label.
