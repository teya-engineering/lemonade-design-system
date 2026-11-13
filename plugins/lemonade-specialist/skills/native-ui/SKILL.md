---
name: native-ui
description: Use when building or changing any UI, with a Figma design or without one, styling any UI element, choosing a DS component (Tag, Button, Icon, ActionListItem, SymbolContainer, etc.), or before hardcoding a colour/spacing/typography/radius. Required when mapping Figma values to tokens, picking the right component (never build custom Row/HStack list items - use the DS list items: ContentListItem, ActionListItem, ResourceListItem, SelectListItem), or building a card or a list row. Covers guidelines/ (Lemonade's LDS- rules), tokens.md, components.md, layout-patterns.md for both Android Compose and iOS SwiftUI.
allowed-tools: Read, Write, Edit, Glob, Grep, Bash(git clone:*), Bash(git -C:*), Bash(find:*)
---

# Native UI with Lemonade

Complete token and component reference for building UI with Lemonade DS on Android (Compose) and iOS (SwiftUI).

## Before Writing UI

When implementing a screen (especially from a Figma design), go through this checklist BEFORE writing code:

1. **Read [`guidelines/index.md`](guidelines/index.md)** - Lemonade's rules for combining components: when rows need a card, what goes in a leading slot, which button is the secondary, which colour layer to reach for. Follow the Compose or SwiftUI example of each rule; the HTML one is for prototypes
2. **Read `components.md`** - Identify which DS components match the design elements (list items, buttons, tags, tiles, chips, icons, etc.)
3. **Read `layout-patterns.md`** - Check card patterns, status mapping patterns, and header layouts
4. **Map every design element** to either a DS component or a DS token. If a DS component exists for the element, you MUST use it - never build a custom version
5. **Extract all visual properties** from the design and map them to tokens:
   - Text styles → typography tokens (read `tokens.md` Typography section)
   - Colours → semantic colour tokens (read `tokens.md` Colours section)
   - Spacing/padding → spacing tokens
   - Corner radii → shape tokens wherever one carries the exact geometry (see tokens.md for what stays hand-built)

If Figma shows a value that doesn't exactly match a token, use the nearest semantic token - never hardcode the raw value.

## Quick Reference

| Category   | Android Access                            | iOS Access                                |
| ---------- | ----------------------------------------- | ----------------------------------------- |
| Typography | `LemonadeTheme.typography.{style}`        | `LemonadeTypography.shared.{style}.font`  |
| Colours     | `LemonadeTheme.colors.{category}.{token}` | `LemonadeTheme.colors.{category}.{token}` |
| Spacing    | `LemonadeTheme.spaces.spacing{N}`         | `LemonadeSpacing.spacing{N}.value`        |
| Radius     | `LemonadeTheme.shapes.radius{N}`          | `LemonadeRadius.radius{N}.shape`          |

## Reference Files

For detailed information, read the appropriate file:

- **guidelines/** - Lemonade's `LDS-` rules for using and combining components
- **tokens.md** - All design tokens (typography, colours, spacing, radius)
- **components.md** - Component catalog (Tag, Button, Icon, etc.)
- **layout-patterns.md** - Layout patterns and status mapping
- **mapping.md** - What each HTML kit (`lmnd-kit-`) block and modifier becomes in Compose and SwiftUI, copied from the design system

## Common Tasks

| Task                       | Reference File         |
| -------------------------- | ---------------------- |
| Style text                 | tokens.md (Typography) |
| Set colours/backgrounds     | tokens.md (Colours)     |
| Add spacing/padding        | tokens.md (Spacing)    |
| Use a component            | components.md          |
| Build a card layout        | guidelines/cards.md, layout-patterns.md |
| Build a list row           | guidelines/lists.md    |
| Choose a button variant    | guidelines/buttons.md  |
| Pick a colour layer        | guidelines/colour.md   |
| Map status to colours/icons | layout-patterns.md     |
| Port an HTML kit prototype | mapping.md             |

## Checking the DS source

The DS source is the Lemonade repository: https://github.com/teya-engineering/lemonade-design-system

Use it when this skill does not document an API (a component's exact parameters, a new token), or when the reference files look out of date.

1. **Find it.** Look for `../lemonade-design-system/` (a sibling of the project root).
2. **Clone it if it is missing.** Tell the user, then run from the project root:

   ```bash
   git clone --filter=blob:none https://github.com/teya-engineering/lemonade-design-system.git ../lemonade-design-system
   ```

3. **Refresh it if it is stale.** Skip this step right after a clone. Treat the checkout as stale when the last fetch is more than 7 days old:

   ```bash
   find ../lemonade-design-system/.git/FETCH_HEAD -mtime +7 2>/dev/null
   ```

   If the command prints a path, or `FETCH_HEAD` does not exist, update the checkout:

   ```bash
   git -C ../lemonade-design-system pull --ff-only
   ```

   If the pull fails or the checkout has local changes, do not reset it. Tell the user and read the files as they are.

4. **Check once per session.** Do not clone or pull again after the first check.

Browse `kmp/`, `swiftui/` and `tokens/` for the source. If the DS source disagrees with `tokens.md` or `components.md`, trust the DS source and tell the user the reference file needs an update.

## Best Practices

1. **Always use tokens** - Never hardcode colours, spacing, or typography
2. **Use semantic colours** - Choose colours by meaning (contentPrimary, bgCriticalSubtle), not appearance
3. **Consistent spacing** - Use spacing400 (16pt) as base unit
4. **Card patterns** - A card is the `Card` component on a `bgSubtle` screen (`LDS-CARD-NO-HANDROLL`, `LDS-CARD-SURFACE`)
5. **Typography hierarchy** - headingXSmall for titles, bodyMediumRegular for labels
6. **Icon accessibility** - Always provide contentDescription for meaningful icons
