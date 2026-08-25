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

## Documentation

The guidance site — how to build with Lemonade, the standards behind it, and the token
reference generated from the Figma export — is at
<https://saltpay.github.io/lemonade-design-system/>. It is built with Lemonade itself,
compiled to the browser; the source is in [`kmp/docs`](./kmp/docs).

---

## Overview

Lemonade Design System is a comprehensive, multi-platform solution that enables teams to build consistent, high-quality user interfaces. It provides:

- **Production-ready components** — Battle-tested UI components ready for production use
- **Unified design tokens** — Colors, typography, spacing, and more shared across platforms
- **Theming support** — Easily customize the look and feel to match your brand
- **Platform-native implementations** — Each platform gets a native implementation optimized for its ecosystem

---

## Platforms

| Platform | Targets | Documentation |
|----------|---------|---------------|
| **Kotlin Multiplatform** | Android, iOS, JVM Desktop, Browser (wasm) | [KMP Documentation](./kmp/README.md) |
| **Flutter** | Android, iOS, Web | [Flutter Documentation](./flutter/README.md) |
| **SwiftUI** | iOS 15+, macOS 12+ | [SwiftUI Documentation](./swiftui/README.md) |

### Version Tags

Each platform has its own versioning scheme:

| Platform | Tag Pattern | Example |
|----------|-------------|---------|
| KMP | `lemonade-kmp-vX.Y.Z` | `lemonade-kmp-v1.0.0` |
| Flutter | `lemonade-flutter-vX.Y.Z` | `lemonade-flutter-v0.1.0` |
| SwiftUI | `lemonade-swiftui-vX.Y.Z` | `lemonade-swiftui-v1.0.0` |

---

## Components

The design system includes a comprehensive set of UI components:

| Category | Examples |
|----------|----------|
| **Form Controls** | Button, Switch, Input, Checkbox, Radio |
| **Display** | Text, Badge, Avatar, Card |
| **Selection & Lists** | List, Dropdown, Menu |
| **Feedback** | Toast, Dialog, Loading |

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

| Asset Type | Description |
|------------|-------------|
| **BrandLogo** | Brand logo assets |
| **CountryFlag** | Country flag icons |
| **SVG Icons** | Comprehensive icon library |

---

## Philosophy

The Lemonade Design System embraces **flexibility over rigidity**. We provide sensible defaults and semantic APIs, but acknowledge that design requirements vary.

### Core Principles

- **Semantic First** — Always prefer semantic tokens over raw values
- **Type Safety** — Leverage each platform's type system to prevent misuse
- **Consistency** — Use the design system to maintain visual harmony
- **Flexibility** — Access lower-level primitives when necessary, with clear warnings
- **Performance** — Theme changes animate smoothly via interpolation

### Documentation Standards

All public APIs must be thoroughly documented with:

- Clear description of purpose and usage
- Code examples showing common use cases
- Parameter documentation with types and defaults
- Preconditions and constraints clearly stated

---

## Contributing

We welcome contributions! Please see the platform-specific documentation for detailed contribution guidelines:

- [KMP Contributing Guide](./kmp/README.md#contributing)
- [Flutter Contributing Guide](./flutter/README.md#contributing)

### General Guidelines

1. **Design Validation** — Components must be validated by the design team before implementation
2. **Figma First** — Public components must exist in Figma with "Ready for Dev" status
3. **Documentation** — All public APIs require comprehensive documentation
4. **Testing** — Include unit tests and widget/UI tests for components
5. **Code Review** — All changes require at least one approval

---

## License

See [LICENSE](LICENSE) for details.
