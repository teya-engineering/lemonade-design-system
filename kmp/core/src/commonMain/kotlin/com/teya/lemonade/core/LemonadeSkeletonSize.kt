package com.teya.lemonade.core

/**
 * Size scale for skeleton placeholders, ordered from smallest to largest.
 *
 * Each size maps to a dimension in the design system sizing scale. The pixel value depends on the
 * skeleton variant and resolves from the design system composition locals.
 *
 * Note: concrete dp values come from the sizing tokens (for example [LemonadeSizes]) and may change
 * over time. Rely on these semantic sizes rather than specific dp values.
 *
 * ## Usage
 * ```kotlin
 * LemonadeUi.LineSkeleton(size = LemonadeSkeletonSize.Large)
 * LemonadeUi.CircleSkeleton(size = LemonadeSkeletonSize.Small)
 * ```
 */
public enum class LemonadeSkeletonSize {
    XSmall,
    Small,
    Medium,
    Large,
    XLarge,
    XXLarge,
    XXXLarge,
}
