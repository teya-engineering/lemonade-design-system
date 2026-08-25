package com.teya.lemonade.docs.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.teya.lemonade.LemonadeDarkTheme
import com.teya.lemonade.LemonadeLightTheme
import com.teya.lemonade.LemonadeSemanticColors

internal sealed interface DocStyle {
    val label: String

    @Composable
    fun resolveColors(): LemonadeSemanticColors

    data object Light : DocStyle {
        override val label: String = "Light"

        @Composable
        override fun resolveColors(): LemonadeSemanticColors = LemonadeLightTheme
    }

    data object Dark : DocStyle {
        override val label: String = "Dark"

        @Composable
        override fun resolveColors(): LemonadeSemanticColors = LemonadeDarkTheme
    }

    data object System : DocStyle {
        override val label: String = "System"

        @Composable
        override fun resolveColors(): LemonadeSemanticColors =
            if (isSystemInDarkTheme()) {
                LemonadeDarkTheme
            } else {
                LemonadeLightTheme
            }
    }

    companion object {
        val entries: List<DocStyle> = listOf(Light, Dark, System)
        val Default: DocStyle = System
    }
}

internal sealed interface DocThemeVariant {
    val label: String

    data object Standard : DocThemeVariant {
        override val label: String = "Standard"
    }

    data object Expressive : DocThemeVariant {
        override val label: String = "Expressive"
    }

    companion object {
        val entries: List<DocThemeVariant> = listOf(Standard, Expressive)
        val Default: DocThemeVariant = Standard
    }
}
