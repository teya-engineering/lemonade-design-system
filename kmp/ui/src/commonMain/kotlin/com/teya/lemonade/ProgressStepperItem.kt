package com.teya.lemonade

/**
 * Describes a single segment inside a [LemonadeUi.ProgressStepper].
 *
 * @param label optional step title shown below the segment's bar
 * @param subheading optional secondary text shown below [label]
 */
public class ProgressStepperItem(
    public val label: String? = null,
    public val subheading: String? = null,
)
