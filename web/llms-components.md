<!--
The hand-written half of llms.txt. Component markup is not token data, so
web-llms-txt-converter appends this file verbatim under "## Components" instead of
deriving it. Every directory under web/src/components/ needs a "### " heading here, or
the converter fails.
-->

Every component is CSS plus markup. React is optional: the classes below are the API, so
any framework — or none — produces the same result.

With React: `import { Button } from '@teya/lemonade-mobile-ds/react'` and
`import '@teya/lemonade-mobile-ds/components.css'`.
Without React, write the markup and import the same stylesheet. `buttonClasses` from the
root export builds the class list for you in any TypeScript stack.

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
- Type: `lmnd-button--solid`, `--subtle`, `--ghost`. Omit it for `--on-brand` and `--on-color`,
  which carry a single treatment.
- Size: `lmnd-button--x-small`, `--small`, `--medium`, `--large`.
- Text style: `lmnd-text-body-small-semibold` for x-small and small,
  `lmnd-text-body-medium-semibold` for medium and large.
- Label: wrap it in `lmnd-button__label`.
- Disabled: the DOM `disabled` attribute. Do not add a class.
- Loading: add `lmnd-button--loading`, replace the label with
  `<span class="lmnd-button__spinner" aria-hidden="true"></span>`, drop the icons, and set
  `disabled` and `aria-busy="true"` plus `aria-label` so the button keeps its name.
- Full width: set the width on the element. Add `lmnd-button--expand` to stretch the label
  area and leave the icons at the edges.

Hover and press come from the stylesheet; do not add classes for them.
