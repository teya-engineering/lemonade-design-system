@file:Suppress("MatchingDeclarationName")

package com.teya.lemonade.core

/**
 * Layout arrangement for `LemonadeUi.ContentListItem`.
 *
 * [Horizontal] places the label on the left and value on the right.
 * [Vertical] stacks the label above the value.
 */
public enum class LemonadeContentListItemLayout {
    Horizontal,
    Vertical,
}

/**
 * Vertical density for `LemonadeUi.ContentListItem`.
 *
 * [Comfortable] applies a larger vertical padding (spacing400).
 * [Compact] applies a reduced vertical padding (spacing200).
 */
public enum class LemonadeContentListItemDensity {
    Comfortable,
    Compact,
}
