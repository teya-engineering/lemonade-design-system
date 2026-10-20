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
     * A progress stepper. Rows before the current one show a check, the current row shows an
     * in-progress ring with a positive gradient line, and rows after it are muted with a dashed
     * line and tertiary labels. A reached row with a critical voice shows an error icon instead.
     */
    Progress,
}
