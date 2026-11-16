package com.teya.lemonade

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.teya.lemonade.core.LemonadeCardPadding

private data class ProgressStepperSample(
    val title: String,
    val items: List<ProgressStepperItem>,
    val completedSteps: Int,
    val isFailed: Boolean = false,
)

private val orderSteps = listOf(
    "Ordered" to "12 Jun",
    "Dispatched" to "14 Jun",
    "Delivered" to "18 Jun",
)

private val progressStepperSamples: List<ProgressStepperSample> = buildList {
    for (completedSteps in 0..3) {
        add(
            element = ProgressStepperSample(
                title = "With titles — $completedSteps/3",
                items = orderSteps.mapIndexed { index, (label, date) ->
                    ProgressStepperItem(
                        label = label,
                        subheading = date.takeIf { index < completedSteps },
                    )
                },
                completedSteps = completedSteps,
            ),
        )
    }
    for (completedSteps in 0..3) {
        add(
            element = ProgressStepperSample(
                title = "Bars only — $completedSteps/3",
                items = List(size = 3) { ProgressStepperItem() },
                completedSteps = completedSteps,
            ),
        )
    }
    add(
        element = ProgressStepperSample(
            title = "Failed — with titles",
            items = listOf(
                ProgressStepperItem(label = "Ordered", subheading = "12 Jun"),
                ProgressStepperItem(label = "Delivery failed", subheading = "14 Jun"),
                ProgressStepperItem(label = "Delivered"),
            ),
            completedSteps = 1,
            isFailed = true,
        ),
    )
    add(
        element = ProgressStepperSample(
            title = "Failed — bars only",
            items = List(size = 3) { ProgressStepperItem() },
            completedSteps = 2,
            isFailed = true,
        ),
    )
    add(
        element = ProgressStepperSample(
            title = "Four steps, wrapping titles",
            items = listOf(
                ProgressStepperItem(label = "Application sent"),
                ProgressStepperItem(label = "Identity checked"),
                ProgressStepperItem(label = "Under review"),
                ProgressStepperItem(label = "Account ready"),
            ),
            completedSteps = 2,
        ),
    )
}

@Composable
internal fun ProgressStepperDisplay() {
    SampleScreenDisplayLazyColumn(title = "ProgressStepper") {
        progressStepperSamples.forEach { sample ->
            item(key = sample.title) {
                LemonadeUi.Card(
                    modifier = Modifier.padding(bottom = LemonadeTheme.spaces.spacing600),
                    contentPadding = LemonadeCardPadding.Medium,
                    header = CardHeaderConfig(title = sample.title),
                ) {
                    LemonadeUi.ProgressStepper(
                        items = sample.items,
                        completedSteps = sample.completedSteps,
                        isFailed = sample.isFailed,
                    )
                }
            }
        }
    }
}
