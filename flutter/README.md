# Lemonade Design System - Flutter

[![style: very good analysis][very_good_analysis_badge]][very_good_analysis_link]
[![Powered by Mason][mason_badge]][mason_link]

A Flutter package carrying the Lemonade Design System's UI components, theming, and design tokens.

---

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Basic Setup](#basic-setup)
  - [Wrap Your App](#wrap-your-app)
  - [Accessing Theme Values](#accessing-theme-values)
- [Theme Customization](#theme-customization)
- [Foundation Tokens](#foundation-tokens)
- [Best Practices](#best-practices)
  - [Package Imports](#package-imports)
  - [Theme Usage](#theme-usage)
  - [Color Usage](#color-usage)
  - [Typography](#typography)
  - [Spacing](#spacing)
  - [Border Radius](#border-radius)
  - [Shadows & Elevation](#shadows--elevation)
- [Creating Components](#creating-components)
- [Contributing](#contributing)
- [Troubleshooting](#troubleshooting)
- [Running Tests](#running-tests)

---

## Prerequisites

- Flutter SDK 3.32.8 or higher
- Git access to the Teya Lemonade repository

---

## Installation

The Lemonade Design System is distributed as a Git-based Flutter package. Add it to your project's `pubspec.yaml`:

```yaml
dependencies:
  lemonade_design_system:
    git:
      url: git@github.com:saltpay/lemonade-design-system.git
      ref: lemonade-flutter-v0.8.3  # Use the latest version tag
      path: flutter
```

### Version Selection

Always use a specific version tag (e.g., `lemonade-flutter-v0.8.3`) rather than a branch to ensure stability. Version tags follow the pattern `lemonade-flutter-vX.Y.Z`; `git tag -l 'lemonade-flutter-*'` lists them.

After adding the dependency, run:

```sh
flutter pub get
```

If you're using Melos for monorepo management:

```sh
melos bootstrap
```

---

## Basic Setup

### Wrap Your App

Use `LemonadeUi` to provide the Lemonade theme throughout your application:

```dart
import 'package:flutter/material.dart';
import 'package:lemonade_design_system/lemonade_design_system.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return LemonadeUi(
      builder: (context) {
        return MaterialApp(
          home: MyHomePage(),
        );
      },
    );
  }
}
```

### Accessing Theme Values

Access design tokens from anywhere in your widget tree:

```dart
class MyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    final theme = LemonadeTheme.of(context);

    return Container(
      padding: EdgeInsets.all(theme.spaces.spacing400),
      decoration: BoxDecoration(
        color: theme.colors.background.bgDefault,
        borderRadius: BorderRadius.circular(theme.radius.radius200),
      ),
      child: Text(
        'Hello Lemonade',
        style: theme.typography.bodyMediumRegular,
      ),
    );
  }
}
```

---

## Theme Customization

### Light and Dark Modes

Lemonade supports automatic theme switching based on system brightness:

```dart
LemonadeUi(
  lightTheme: LemonadeThemeData(),
  darkTheme: LemonadeThemeData(
    brightness: Brightness.dark,
    colors: const LemonadeDarkColors(),
  ),
  builder: (context) => MaterialApp(...),
)
```

### Custom Theme

Override specific theme properties:

```dart
LemonadeThemeData(
  colors: CustomLemonadeColors(),
  typography: const LemonadeTypography().apply(
    fontFamily: 'CustomFont',
  ),
)
```

---

## Foundation Tokens

### Colors

Semantic colors organized by purpose:

```dart
theme.colors.content.contentPrimary      // Text colors
theme.colors.background.bgDefault        // Background colors
theme.colors.border.borderNeutralMedium  // Border colors
theme.colors.interaction.bgBrandInteractive // Interactive states
```

### Typography

Predefined text styles with consistent sizing and weights:

```dart
theme.typography.displayLarge       // Large display text
theme.typography.headingMedium      // Section headings
theme.typography.bodyMediumRegular  // Body text
```

### Spacing

Consistent spacing scale using numeric tokens:

```dart
theme.spaces.spacing200   // 8.0
theme.spaces.spacing400   // 16.0
theme.spaces.spacing600   // 24.0
```

### Radius

Border radius values using numeric tokens:

```dart
theme.radius.radius100   // 4.0
theme.radius.radius200   // 8.0
theme.radius.radiusFull  // 999.0 - for pills/circles
```

### Shadows

Elevation shadows for depth:

```dart
theme.shadows.small   // Subtle elevation
theme.shadows.medium  // Standard elevation
```

### Opacity

Transparency levels:

```dart
theme.opacity.base.opacity50   // 50% transparency
theme.opacity.base.opacity100  // Fully opaque
theme.opacity.state.opacityDisabled  // Disabled state
```

### Borders

Border width configurations:

```dart
theme.border.base.border25  // 1.0 - thin border
theme.border.base.border50  // 2.0 - standard border
theme.border.state.focusRing  // Focus indicator
```

---

## Best Practices

### Package Imports

Always import from the main package entry point:

```dart
// Good
import 'package:lemonade_design_system/lemonade_design_system.dart';

// Bad - Don't import internal files directly
import 'package:lemonade_design_system/src/components/button.dart';
```

### Theme Usage

Cache theme reference in the build method:

```dart
@override
Widget build(BuildContext context) {
  final theme = LemonadeTheme.of(context);

  return Column(
    children: [
      Text('Title', style: theme.typography.headingMedium),
      Text('Body', style: theme.typography.bodyMediumRegular),
    ],
  );
}
```

### Color Usage

Use semantic color categories:

```dart
// Good - Semantic colors
Container(color: theme.colors.background.bgDefault)
Text('Label', style: TextStyle(color: theme.colors.content.contentPrimary))

// Bad - Hardcoded values
Container(color: const Color(0xFF0066CC))
```

### Typography

Use predefined typography styles:

```dart
// Good
Text('Heading', style: theme.typography.headingLarge)

// Bad - Creating styles from scratch
Text('Heading', style: const TextStyle(fontSize: 28, fontWeight: FontWeight.w600))
```

### Spacing

Use semantic spacing tokens:

```dart
// Good
Padding(padding: EdgeInsets.all(theme.spaces.spacing400))

// Bad - Magic numbers
Padding(padding: const EdgeInsets.all(17.0))
```

### Border Radius

Use semantic radius tokens:

```dart
// Good
BorderRadius.circular(theme.radius.radius200)

// Bad - Hardcoded values
BorderRadius.circular(13.5)
```

### Shadows & Elevation

Use semantic shadow tokens:

```dart
// Good
Container(decoration: BoxDecoration(boxShadow: theme.shadows.medium))

// Bad - Custom shadow values
Container(decoration: BoxDecoration(boxShadow: [BoxShadow(...)]))
```

---

## Creating Components

### Component Types

**Public Components** (`src/components/`):
- User-facing UI components validated by design team
- Named with `Lemonade` prefix (e.g., `LemonadeButton`, `LemonadeCard`)
- Must exist in Figma with "Ready for Dev" status

**Raw Components** (`src/raw_components/`):
- Internal/auxiliary components for implementation
- Not exported publicly, marked with `@internal`
- No `Lemonade` prefix required

### Implementation Checklist

For public components:
- Component exists in Figma with "Ready for Dev" status
- Design team approval obtained
- Uses foundation tokens (colors, spacing, typography)
- Has documentation with examples
- Includes unit and widget tests
- Exported from `lemonade_design_system.dart`

---

## Contributing

### Development Setup

```sh
git clone git@github.com:saltpay/lemonade-design-system.git
cd lemonade-design-system/flutter
flutter pub get
```

### Development Workflow

1. Create a feature branch from `master`
2. Make changes in `flutter/lib/src/`
3. Run tests: `flutter test --coverage`
4. Format code: `dart format .`
5. Analyze code: `flutter analyze`
6. Create PR against `master`

### Documentation Standards

Use `///` for public APIs:

```dart
/// Creates a [LemonadeTheme] with the specified [data].
///
/// The [child] widget will have access to the theme via
/// [LemonadeTheme.of(context)].
///
/// Example:
/// ```dart
/// LemonadeTheme(
///   data: LemonadeThemeData(),
///   child: MyApp(),
/// )
/// ```
const LemonadeTheme({
  required this.data,
  required super.child,
  super.key,
});
```

---

## Troubleshooting

### Package Not Found

Ensure you have SSH access to the GitHub repository:

```sh
ssh -T git@github.com
```

### Version Conflicts

Ensure your Flutter SDK version matches the requirement (3.32.8):

```sh
flutter --version
```

### Theme Not Applied

Verify your widget tree is wrapped with `LemonadeUi`:

```dart
// Correct
LemonadeUi(
  builder: (context) {
    final theme = LemonadeTheme.of(context); // Works
    return WidgetsApp(...);
  },
)

// Incorrect - theme accessed outside builder
final theme = LemonadeTheme.of(context); // Theme not available here
LemonadeUi(builder: (context) => WidgetsApp(...))
```

---

## Running Tests

Install the [very_good_cli][very_good_cli_link]:

```sh
dart pub global activate very_good_cli
```

Run all unit tests:

```sh
very_good test --coverage
```

View coverage report:

```sh
# Generate Coverage Report
genhtml coverage/lcov.info -o coverage/

# Open Coverage Report
open coverage/index.html
```

---

[license_badge]: https://img.shields.io/badge/license-MIT-blue.svg
[license_link]: https://opensource.org/licenses/MIT
[mason_badge]: https://img.shields.io/endpoint?url=https%3A%2F%2Ftinyurl.com%2Fmason-badge
[mason_link]: https://github.com/felangel/mason
[very_good_analysis_badge]: https://img.shields.io/badge/style-very_good_analysis-B22C89.svg
[very_good_analysis_link]: https://pub.dev/packages/very_good_analysis
[very_good_cli_link]: https://pub.dev/packages/very_good_cli
