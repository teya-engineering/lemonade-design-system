<!--
The hand-written half of llms.txt. Component markup is not token data, so
web-llms-txt-converter appends this file verbatim under "## Components" instead of
deriving it. Every directory under web/src/components/ needs a "### " heading here, or
the converter fails. A component that renders no markup of its own is listed instead:

no-markup: slot
-->

Every component is CSS plus markup. React is optional: the classes below are the API, so
any framework — or none — produces the same result.

With React: `import { Text, Icon, Spinner } from '@teya/lemonade-mobile-ds/react'`. Without React, write
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
