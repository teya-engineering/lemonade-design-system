package com.teya.lemonade.core

public enum class LemonadeButtonVariant {
    Primary,
    Secondary,
    Neutral,
    Critical,

    /** Sits on top of brand-filled surfaces. Renders as a single Subtle treatment. */
    OnBrand,

    /** Sits on top of color-filled (voice) surfaces. Renders as a single Subtle treatment. */
    OnColor,
}

public enum class LemonadeButtonType {
    Solid,
    Subtle,
    Ghost,
}

public enum class LemonadeButtonSize {
    XSmall,
    Small,
    Medium,
    Large,
}
