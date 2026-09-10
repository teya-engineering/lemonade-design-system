# Lemonade Design System — Web

Teya's Lemonade design system for the web, published as `@teya/lemonade-ds`.

## Status

This is the package scaffold. It builds, tests and packs, but exports nothing
yet — the design tokens, SVG assets and the Storybook gallery arrive in the
stacked PRs on top of this one.

## Install

```sh
npm install @teya/lemonade-ds
```

## Develop

```sh
npm ci          # install from the committed lockfile, as CI does
npm test        # vitest
npm run typecheck
npm run build   # tsup -> dist/
```

`dist/` is build output and is gitignored. Nothing here is published: the
release workflow retains `npm publish --dry-run` until the registry and scope
are settled.
