---
title: Accessibility
description: The baseline every Lemonade screen is expected to meet.
---

Accessibility isn't a pass you make at the end. Most of it is decided by which token you
reach for and whether you fill in a label — decisions you're already making, so getting
them right costs nothing extra.

## Contrast

Content tokens and background tokens are designed together, in pairs.
`content-critical` is checked against `bg-critical-subtle`, not against whatever
background happens to be nearby. Pair a `content-*` token with its matching `bg-*`
token and contrast is correct by construction — see
[Semantic tokens first](/lemonade-design-system/standards/semantic-tokens/) for how the
naming maps a content token to the background it's meant for.

Contrast failures come from crossing families: putting `content-critical` on
`bg-caution-subtle` because both looked fine in isolation, or laying content over a
background that was never the pair it was designed against. If a token combination looks
wrong, check whether the two are actually a matched pair before assuming the colours
themselves are at fault.

## Touch targets

Apple's and Android's platform guidelines both set a 44×44pt minimum for anything a
user taps, and that's the standard Lemonade expects every interactive element to meet —
the visual size of the control is not the same as its hit area, and the hit area is what
the guideline is about.

Lemonade's own components don't all sit at 44pt by default: the smallest icon button
sizes render well below it. Where a control's visible size is smaller than 44×44pt,
either choose a larger size or give it a comfortably large hit area so the tap target
still clears the minimum. A control that looks small but taps like it's 44×44pt is doing
its job; one that's small in every dimension, including the invisible one, is not.

## Labels

Every interactive control needs an accessible label — something a screen reader can
announce that says what the control does. A placeholder is not a label: it disappears
the moment there's content in the field, which is exactly when a screen reader user
needs it most. See [Forms](/lemonade-design-system/patterns/forms/) for how this plays
out on a text field specifically.

Icon-only controls — an icon button with no visible text — always need an explicit label
describing the action, not the icon. "Favorite", not "heart icon." Without one, a screen
reader has nothing to announce and the control is effectively invisible to anyone using
one.

## Text scaling

Type tokens carry a size that maps to `sp` on Android and points on iOS, which is what
lets a user's system text-size setting actually change the text on screen. Overriding a
type token's size with a raw pixel value opts that text out of the user's setting, which
is the whole point of asking for a larger size in the first place.

The corollary is on your layout, not the token: a layout that assumes text stays at one
height will clip or overlap the moment someone scales it up. Don't put fixed heights
around text — let the container grow with it.

## Focus

Focus needs to be visible. The `state/focus-ring` token exists for exactly this — see
[Opacity & borders](/lemonade-design-system/foundations/opacity-and-borders/) for how it
sits alongside the other state tokens. Reach for it rather than inventing a focus
treatment, for the same reason any state token beats a raw value: components and screens
that use the token pick up any future change together, and the ones that didn't, won't.

A control that a keyboard or switch-access user can reach but can't see focused is no
better, for them, than a control they can't reach at all.
