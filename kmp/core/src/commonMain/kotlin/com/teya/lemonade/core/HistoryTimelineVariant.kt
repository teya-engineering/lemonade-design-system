package com.teya.lemonade.core

/**
 * How a `HistoryTimeline` styles the rows around its current step.
 */
public enum class HistoryTimelineVariant {
    /**
     * Only the current row takes its voice colour; every other row is muted.
     */
    Default,

    /**
     * A progress stepper. The current row and every row before it keep their voice colour and
     * join the next row with a selected-border line; rows after the current one are muted, with
     * tertiary labels.
     */
    Progress,
}
