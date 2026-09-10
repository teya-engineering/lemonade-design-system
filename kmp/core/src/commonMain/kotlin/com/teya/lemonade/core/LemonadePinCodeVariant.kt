package com.teya.lemonade.core

/**
 * Input modes for the PIN code component. Selects which system keyboard is requested.
 */
public enum class LemonadePinCodeVariant {
    /** Requests a numeric keyboard. */
    Numeric,

    /** Requests the full keyboard. */
    Alphanumeric,
}
