## Development

When starting the dev server, use background mode:

```
astro dev --background
```

Manage the background server with `astro dev stop`, `astro dev status`, and `astro dev logs`.

## What this is

A Starlight (Astro) documentation site for the Lemonade design system — no React or
other framework components, no Tailwind, no i18n. See `README.md` for commands.

Foundations pages under `src/content/docs/foundations/` are generated at build time
from `../tokens/*.json` via `src/lib/tokens.ts` — don't hand-edit token values or
counts into those pages; change the export instead.

Internal content links must be base-absolute (`/lemonade-design-system/...`).
`npm run build` runs `starlight-links-validator` and fails on any dead link.

## Documentation

Full documentation: https://docs.astro.build

Consult these guides before working on related tasks:

- [Adding pages, dynamic routes, or middleware](https://docs.astro.build/en/guides/routing/)
- [Working with Astro components](https://docs.astro.build/en/basics/astro-components/)
- [Adding or managing content](https://docs.astro.build/en/guides/content-collections/)
