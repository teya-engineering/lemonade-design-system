<!--
The hand-written half of llms.txt. Component markup is not token data, so
web-llms-txt-converter appends this file verbatim under "## Components" instead of
deriving it. Every directory under web/src/components/ needs a "### " heading here, or
the converter fails.
-->

Every component is CSS plus markup. React is optional: the classes below are the API, so
any framework — or none — produces the same result.

With React: `import { Text } from '@teya/lemonade-mobile-ds/react'`. Without React, write
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
