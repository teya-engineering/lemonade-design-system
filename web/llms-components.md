<!--
The hand-written half of llms.txt. Component markup is not token data, so
web-llms-txt-converter appends this file verbatim under "## Components" instead of
deriving it. Every directory under web/src/components/ needs a "### " heading here, or
the converter fails. A component that renders no markup of its own is listed instead:

no-markup: slot
-->

Every component is CSS plus markup. React is optional: the classes below are the API, so
any framework — or none — produces the same result.

With React: `import { Button, Text, Icon, Spinner } from '@teya/lemonade-mobile-ds/react'`. Without React, write
the markup and apply the same classes; the `textStyles` manifest on the root export maps
every style name to its class.

### Text

```html
<p class="lmnd-text-body-medium-regular">Account balance</p>
```

- One class per style, from `lmnd-text-display-xsmall` through `lmnd-text-body-xsmall-overline`.
  `llms.txt` lists all 29 under "Text styles".
- The element is yours. Lemonade text carries no semantics, so pick the tag the content
  needs — the class only sets family, size, line height, weight and letter spacing.
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

Hover and press come from the stylesheet; do not add classes for them.
