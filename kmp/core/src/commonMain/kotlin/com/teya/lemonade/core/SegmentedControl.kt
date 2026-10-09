@file:Suppress("MatchingDeclarationName")

package com.teya.lemonade.core

public class TabButtonProperties private constructor(
    public val label: String?,
    public val icon: LemonadeIcons?,
    public val contentDescription: String?,
) {
    // The companion's calls compile to a public synthetic accessor for this signature, which the API baseline locks.
    private constructor(label: String?, icon: LemonadeIcons?) : this(
        label = label,
        icon = icon,
        contentDescription = null,
    )

    public companion object {
        public fun label(label: String): TabButtonProperties =
            TabButtonProperties(
                label = label,
                icon = null,
            )

        public fun labelAndIcon(
            label: String,
            icon: LemonadeIcons,
        ): TabButtonProperties =
            TabButtonProperties(
                label = label,
                icon = icon,
            )

        /**
         * Creates a tab that shows only [icon].
         *
         * @param contentDescription **localized** name screen readers announce for the tab. When null, the tab
         * has no accessible name, so pass one unless the tab is purely decorative.
         */
        public fun icon(
            icon: LemonadeIcons,
            contentDescription: String? = null,
        ): TabButtonProperties =
            TabButtonProperties(
                label = null,
                icon = icon,
                contentDescription = contentDescription,
            )

        @Deprecated(
            message = "Use the overload with a contentDescription parameter.",
            replaceWith = ReplaceWith("icon(icon, contentDescription = null)"),
            level = DeprecationLevel.HIDDEN,
        )
        public fun icon(icon: LemonadeIcons): TabButtonProperties = icon(icon = icon, contentDescription = null)
    }
}

public enum class LemonadeSegmentedControlSize {
    Small,
    Medium,
    Large,
}
