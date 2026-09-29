package com.teya.lemonade.core

public enum class SymbolContainerVoice {
    Neutral,
    Critical,
    Warning,
    Info,
    Positive,
    Brand,
    BrandSubtle,
}

public enum class SymbolContainerSize {
    XSmall,
    Small,
    Medium,
    Large,
    XLarge,
    XXLarge,
}

public enum class SymbolContainerShape {
    Circle,
    Rounded,
}

/**
 * The corner of a symbol container that its badge hangs off. Start and end follow the layout
 * direction, so [BottomEnd] sits bottom-right in LTR and bottom-left in RTL.
 */
public enum class SymbolContainerBadgePosition {
    TopStart,
    TopEnd,
    BottomStart,
    BottomEnd,
}
