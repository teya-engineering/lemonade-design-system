# Lemonade Design System

A multi-platform design system providing production-ready UI components, theming capabilities, and design tokens for building consistent user experiences across platforms.

---

## Table of Contents

- [Overview](#overview)
- [Platforms](#platforms)
- [Components](#components)
- [Design Tokens](#design-tokens)
- [Assets](#assets)
- [Philosophy](#philosophy)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

Lemonade ships the same design language to three platforms:

- **UI components** — a shared catalogue, implemented natively on each platform
- **Unified design tokens** — colors, typography, spacing, radius, shadows, opacity and border widths, generated from one Figma export
- **Theming** — every token group is overridable at the theme root
- **Platform-native implementations** — Compose Multiplatform, SwiftUI and Flutter, each idiomatic to its ecosystem

---

## Platforms

| Platform | Targets | Documentation |
|----------|---------|---------------|
| **Kotlin Multiplatform** | Android, iOS, JVM Desktop | [KMP Documentation](./kmp/README.md) |
| **Flutter** | Android, iOS, Web | [Flutter Documentation](./flutter/README.md) |
| **SwiftUI** | iOS 15+, macOS 12+ | [SwiftUI Documentation](./swiftui/README.md) |

### Version Tags

Each platform versions independently, and the version lives only in the tag.

| Platform | Tag Pattern | Example |
|----------|-------------|---------|
| KMP | `lemonade-kmp-X.Y.Z` | `lemonade-kmp-0.9.0` |
| SwiftUI | `lemonade-swiftui-X.Y.Z` | `lemonade-swiftui-0.9.1` |
| Flutter | `lemonade-flutter-vX.Y.Z` | `lemonade-flutter-v0.8.3` |

Each SwiftUI release also carries a plain `X.Y.Z` tag at the same commit — that is
the one Swift Package Manager consumers resolve.

---

## Components

| Category | Components |
|----------|------------|
| **Actions** | Button, IconButton, Link, Chip |
| **Form Controls** | TextField, SearchField, SelectField, PinCode, Switch, Checkbox, RadioButton, SegmentedControl, BoxSelection |
| **Display** | Text, Icon, Badge, Tag, Card, Tile, SymbolContainer, HorizontalDivider, VerticalDivider, BrandLogo, CountryFlag |
| **Lists & Navigation** | ListItem, ContentListItem, ActionListItem, ResourceListItem, SelectListItem, SwipeActionRow, Tabs, TopBar, BottomTabBar, HistoryTimeline |
| **Feedback & Overlays** | Toast, Tooltip, Notice, Spinner, Skeleton, Dialog, BottomSheet, Dropdown |
| **Date & Time** | DatePicker, DateRangePicker, InlineCalendar, TimePicker, TimeInput |

Coverage differs per platform — see each platform's documentation for the exact
catalogue.

---

## Design Tokens

All platforms share a consistent design foundation:

| Token | Description |
|-------|-------------|
| **Colors** | Primitive and semantic color tokens |
| **Typography** | Figtree font family with multiple text styles |
| **Spacing** | Consistent spacing scale (`spacing100`, `spacing200`, etc.) |
| **Radius** | Border radius tokens |
| **Shadows** | Pre-defined shadow sets for elevation |
| **Opacity** | Opacity levels for transparency |
| **Border Widths** | Border width configurations |

---

## Assets

Assets are authored as SVG under `svg/` and generated into each platform's native
format.

| Asset Type | Source |
|------------|--------|
| **Icons** | `svg/icons/` |
| **CountryFlag** | `svg/flags/` |
| **BrandLogo** | `svg/brandLogos/` — light and dark variants collapse into one asset |

---

## Philosophy

### Core Principles

- **Semantic First** — Prefer semantic tokens over raw values
- **Type Safety** — Leverage each platform's type system to prevent misuse
- **Consistency** — Use the design system to maintain visual harmony
- **Flexibility** — Lower-level primitives stay reachable when a design needs them

### Documentation Standards

Every public API is documented with:

- A description of purpose and usage
- Code examples showing common use cases
- Parameter documentation with types and defaults
- Preconditions and constraints stated explicitly

---

## Contributing

Platform-specific workflows live with each platform:

- [KMP Contributing Guide](./kmp/README.md#contributing)
- [Flutter Contributing Guide](./flutter/README.md#contributing)

### General Guidelines

1. **Design Validation** — Components must be validated by the design team before implementation
2. **Figma First** — Public components must exist in Figma with "Ready for Dev" status
3. **Documentation** — Every public API is documented
4. **Testing** — Include unit tests and widget/UI tests for components
5. **Code Review** — All changes require at least one approval

---

## License

See [LICENSE](LICENSE) for details.
