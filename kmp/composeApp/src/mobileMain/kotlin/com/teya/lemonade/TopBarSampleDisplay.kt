package com.teya.lemonade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.teya.lemonade.core.LemonadeButtonSize
import com.teya.lemonade.core.LemonadeButtonType
import com.teya.lemonade.core.LemonadeIcons
import com.teya.lemonade.core.TopBarAction
import kotlinx.coroutines.launch

@Composable
internal fun TopBarSampleDisplay() {
    val topBarState = rememberTopBarState(
        startCollapsed = false,
        lockGestureAnimation = false,
    )
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LemonadeTheme.colors.background.bgSubtle)
            .navigationBarsPadding(),
    ) {
        LemonadeUi.TopBar(
            label = "Top Bar",
            collapsedLabel = "Collapsed Top Bar",
            subtitle = "Subheading preview",
            state = topBarState,
            navigationAction = NavigationAction(
                navigationAction = TopBarAction.Close,
                onNavigationActionClicked = { },
            ),
            trailingSlot = {
                LemonadeUi.IconButton(
                    icon = LemonadeIcons.ChevronDown,
                    type = LemonadeButtonType.Ghost,
                    contentDescription = "Expand",
                    size = LemonadeButtonSize.Medium,
                    onClick = {
                        coroutineScope.launch {
                            topBarState.expand()
                        }
                    },
                )
                LemonadeUi.IconButton(
                    icon = LemonadeIcons.ChevronTop,
                    type = LemonadeButtonType.Ghost,
                    contentDescription = "Collapse",
                    size = LemonadeButtonSize.Medium,
                    onClick = {
                        coroutineScope.launch {
                            topBarState.collapse()
                        }
                    },
                )
            },
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .nestedScroll(topBarState.nestedScrollConnection)
                .background(LemonadeTheme.colors.background.bgSubtle),
        ) {
            item {
                LemonadeUi.Text(
                    text = SAMPLE_TEXT,
                    textStyle = LemonadeTheme.typography.bodyMediumRegular,
                    modifier = Modifier.padding(horizontal = LemonadeTheme.spaces.spacing300),
                )
            }
        }
    }
}

private const val SAMPLE_TEXT = """
The top bar is a fundamental component in mobile applications that provides users with context about their current location within the app and offers navigation controls. This component demonstrates the collapsing behavior of the top bar as you scroll through the content.

When the user scrolls down, the large title smoothly collapses into a compact inline title in the center of the top bar. This behavior is commonly seen in iOS applications and provides a clean, modern user experience that maximizes screen real estate while maintaining context.

The top bar supports several customization options including leading and trailing slots for action buttons, a bottom slot for additional content like search fields or segmented controls, and the title text itself.

As you continue scrolling through this content, notice how the transition between the expanded and collapsed states is smooth and responsive. The animation uses a fade transition to create a polished look that feels native to the platform.

This pattern is particularly useful for screens with long-form content, lists, or any interface where maximizing the content area is important. The collapsing behavior allows users to see more content while still having quick access to navigation controls when needed.

The implementation uses Compose's ScrollState to track the scroll position and calculate the collapse percentage. This approach ensures that the animation is perfectly synchronized with the user's scroll gesture, providing a fluid and intuitive experience.

One of the key benefits of this design pattern is that it reduces visual clutter when the user is focused on content consumption, while still providing full navigation context at the top of the screen before scrolling begins.

The top bar also includes a subtle divider that appears when the title is fully collapsed. This helps visually separate the top bar from the content below and provides a clear boundary between the fixed navigation area and the scrollable content.

In terms of accessibility, the top bar maintains proper contrast ratios and touch targets for all interactive elements. The leading and trailing slots are designed to accommodate standard icon buttons that meet accessibility guidelines for minimum touch target size.

This component is part of the Lemonade Design System and follows the established patterns and conventions used throughout the system. It integrates seamlessly with other components like IconButton, Text, and the theming system to provide a consistent user experience.

The flexibility of the slot-based API allows developers to customize the top bar for various use cases without modifying the core component. Whether you need a simple back button, multiple action items, or additional UI elements in the bottom slot, the top bar can accommodate these requirements while maintaining its core collapsing behavior.

Thank you for exploring this top bar sample. Feel free to scroll up and down to see the collapsing animation in action, and notice how the divider appears and disappears based on the collapse state.
"""
