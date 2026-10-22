<!--
The hand-written half of llms.txt. Component markup is not token data, so
web-llms-txt-converter appends this file verbatim under "## Components" instead of
deriving it. Every directory under web/src/components/ needs a "### " heading here, or
the converter fails. A component that renders no markup of its own is listed instead:

no-markup: slot
-->

Every component is CSS plus markup. React is optional: the classes below are the API, so
any framework — or none — produces the same result.

With React: `import { Button, Text, Icon, Spinner } from '@teya/lemonade-mobile-ds/react'`,
plus the stylesheets — the components import none themselves, so `styles.css` carries the
tokens, the type scale and every component's classes. Without React, write
the markup and apply the same classes; the `textStyles` manifest on the root export maps
every style name to its class.

### Text

```html
<p class="lmnd-text-body-medium-regular">Account balance</p>
```

- One class per style, from `lmnd-text-display-xsmall` through `lmnd-text-body-xsmall-overline`.
  `llms.txt` lists all 29 under "Text styles".
- The element is yours. Lemonade text carries no semantics, so pick the tag the content
  needs — the class sets type, not layout or colour.
- The React component takes children, not a string prop, so a sentence can carry emphasis
  or a link. Its style prop is `textStyle`; `style` stays the DOM's own attribute.
- `lmnd-text-body-xsmall-overline` uppercases its text through CSS, so write the label in
  normal case and let the class transform it.

### Icon

```html
<span
  class="lmnd-icon lmnd-icon--medium"
  style="--lmnd-icon: url('/assets/icons/heart.svg')"
  role="img"
  aria-label="Favourite"
></span>
```

- The icon is a CSS mask, not an `<img>`, so it takes its colour from `currentColor` — set
  `color` on the element or anything containing it.
- Size: `lmnd-icon--x-small` through `lmnd-icon--xxxx-large`, matching the platforms'
  `LemonadeAssetSize`. The base class is already medium.
- The URL points at the package's `dist/assets/icons/`. Serve that directory and pass the
  name; the React prop for it is `basePath`, defaulting to `/assets/icons`.
- Decorative icons take `aria-hidden="true"` and no `role`. An icon that carries meaning
  takes `role="img"` and an `aria-label`. The React prop is `contentDescription`, named as
  the platforms name it, and `contentDescription={null}` is the decorative case — it is
  required, so the choice is always made. It never renders; visible text beside an icon is
  your own markup.
- `llms.txt` lists every icon name under "Icons".

### Spinner

```html
<span class="lmnd-spinner lmnd-spinner--medium" role="status" aria-label="Loading"></span>
```

- An empty element: the ring is a masked conic gradient, so there is nothing inside it and
  no SVG or image to load.
- Size: `lmnd-spinner--x-small` through `lmnd-spinner--xxxx-large`, the same
  `LemonadeAssetSize` scale as Icon. The base class is already medium.
- The tint defaults to content-secondary, as on the platforms. Override it by setting
  `color` on the element — `color: inherit` to take it from a filled surface.
- A spinner standing alone is the announcement: give it `role="status"` and an
  `aria-label`. One beside text that already says it is loading takes `aria-hidden="true"`
  and no role. The React prop is `contentDescription`, named as the platforms name it, and
  `contentDescription={null}` is that second case. It never renders: for visible text, put
  a decorative spinner and your own text inside one `role="status"`.
- Under `prefers-reduced-motion` the ring slows rather than stopping, because a still ring
  reads as broken rather than busy.

### Button

```html
<button
  type="button"
  class="lmnd-button lmnd-button--primary lmnd-button--solid lmnd-button--large lmnd-text-body-medium-semibold"
>
  <span class="lmnd-icon" style="--lmnd-icon: url('.../icons/plus.svg')"></span>
  <span class="lmnd-button__label">Add item</span>
</button>
```

- Block: `lmnd-button`. Always pair it with one variant, one size and the size's text style.
- Variant: `lmnd-button--primary`, `--secondary`, `--neutral`, `--critical`, `--on-brand`, `--on-color`.
- Fill treatment: `lmnd-button--solid`, `--subtle`, `--ghost`. Omit it for `--on-brand` and
  `--on-color`, which carry a single treatment. The React prop for this is `emphasis`, because
  `type` on a `<button>` is the DOM's own attribute.
- Size: `lmnd-button--x-small`, `--small`, `--medium`, `--large`.
- Text style: `lmnd-text-body-small-semibold` for x-small and small,
  `lmnd-text-body-medium-semibold` for medium and large.
- Label: wrap it in `lmnd-button__label`.
- Disabled: the DOM `disabled` attribute. Do not add a class.
- Loading: add `lmnd-button--loading`, replace the label with a spinner —
  `<span class="lmnd-spinner lmnd-spinner--medium" aria-hidden="true"></span>` — drop the
  icons, and set `disabled` and `aria-busy="true"` plus `aria-label` so the button keeps its
  name. The button tints the spinner with its own content colour.
- Full width: set the width on the element. Add `lmnd-button--expand` to stretch the label
  area and leave the icons at the edges.

Hover and press come from the stylesheet; do not add classes for them. Hover is inside
`@media (hover: hover)`, so a tap on a touch screen does not leave the button stuck in
the hover colour.

### IconButton

```html
<button
  type="button"
  aria-label="Favourite"
  class="lmnd-icon-button lmnd-icon-button--neutral lmnd-icon-button--medium lmnd-icon-button--subtle"
>
  <span class="lmnd-icon" style="--lmnd-icon: url('.../icons/heart.svg')"></span>
</button>
```

- Block: `lmnd-icon-button`. A fixed square, so there is no label and no text style. Pair it
  with one variant *and* one fill treatment — the colours come from the two together, so a
  variant on its own leaves the button transparent. The exception is `--on-brand` and
  `--on-color`, which carry their own colours: give those no fill treatment.
- Variant, fill treatment and size are Button's, and resolve to the same colours:
  `lmnd-icon-button--primary` through `--on-color`, `--solid` / `--subtle` / `--ghost`, and
  `--x-small` through `--large`. The defaults are not Button's — the platforms default an
  icon button to neutral, subtle, medium.
- Shape: rounded unless you say otherwise, and each size carries its own radius, so there is
  no class to write for it. `lmnd-icon-button--circular` makes it a pill at every size.
- The glyph takes no size modifier: the button sizes it, as the platforms do, so
  `lmnd-icon` on its own is right inside one.
- Name it with `aria-label` on the button and leave the glyph decorative. There is no text
  for a glyph name to repeat, so the React prop `contentDescription` is required and cannot
  be null — a button without it announces only "button".
- Disabled: the DOM `disabled` attribute. Do not add a class.
- Loading: add `lmnd-icon-button--loading`, swap the glyph for
  `<span class="lmnd-spinner" aria-hidden="true"></span>`, and set `disabled` and
  `aria-busy="true"`. The button sizes and tints the ring.

### Card

```html
<div class="lmnd-card lmnd-card--default lmnd-card--padding-medium">
  <div class="lmnd-card__header">
    <div class="lmnd-card__header-text">
      <span class="lmnd-card__title lmnd-text-heading-xxsmall">Recent activity</span>
      <span class="lmnd-card__subtitle lmnd-text-body-small-regular">Last 30 days</span>
    </div>
  </div>
  <div class="lmnd-card__content">Three payments today</div>
  <button type="button" class="lmnd-card__footer-action lmnd-text-body-small-semibold">See all</button>
</div>
```

- Block: `lmnd-card`. Full width, with the semantic container radius.
- Background: `lmnd-card--default`, `--subtle`, `--elevated`.
- Content padding: `lmnd-card--padding-none`, `--padding-x-small`, `--padding-small`,
  `--padding-medium`. The modifier goes on the card and the padding lands on
  `lmnd-card__content`, so a header and a footer keep their own spacing.
- Wrap the body in `lmnd-card__content`. It is always present, even with no padding. Its
  children are start-aligned, as they are on the platforms, so a row meant to span the card
  needs its own `width: 100%`.
- Header: `lmnd-card__header`, holding anything leading, then `lmnd-card__header-text` with
  `lmnd-card__title` and an optional `lmnd-card__subtitle`, then anything trailing. Both
  lines are one line and ellipsised.
- Heading style: the title takes `lmnd-text-heading-xxsmall` by default, or
  `lmnd-text-body-xsmall-overline` with `lmnd-card__header--overline` on the header — that
  modifier also carries the overline's colour and its taller text box.
- Navigation indicator: a decorative chevron, last in the header. Like any icon it needs the
  mask URL — the class only sets its colour, so without one it is a solid square.

  ```html
  <span
    class="lmnd-icon lmnd-card__navigation-indicator"
    style="--lmnd-icon: url('.../icons/chevron-right.svg')"
    aria-hidden="true"
  ></span>
  ```
- Footer action: a `<button class="lmnd-card__footer-action">` across the bottom. It fades to
  half opacity while pressed rather than changing fill, as on the platforms.

### Divider

```html
<hr class="lmnd-divider" />
```

- Block: `lmnd-divider`. The base class is already a solid horizontal rule, and neither of
  those two defaults has a modifier of its own — only the changes below do.
- Variant: `lmnd-divider--dashed`. The dash and the gap come from `size-100` and
  `spacing-100`, drawn as a repeating gradient — `border-style: dashed` would leave the
  lengths to the browser. Dashed is also a step up in contrast.
- Orientation: `lmnd-divider--vertical` takes its height from the row it sits in, so give
  that row `align-items: stretch`.
- Labelled: `lmnd-divider--labelled` turns the divider into a row. An `<hr>` cannot hold a
  label, so use a `<div role="separator">` with an `aria-label`, containing
  `lmnd-divider__line`, `lmnd-divider__label` and a second `lmnd-divider__line`. With no
  label to show, write the plain `<hr>` instead: a labelled divider with nothing in it
  leaves a gap in the rule and an unnamed separator.
- A vertical divider also needs `aria-orientation="vertical"`. An `<hr>` is a separator
  already, but ARIA reads one as horizontal unless it is told otherwise, so the orientation
  would exist only in the stylesheet.
