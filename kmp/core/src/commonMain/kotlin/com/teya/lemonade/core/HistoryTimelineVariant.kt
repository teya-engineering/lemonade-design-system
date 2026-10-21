package com.teya.lemonade.core

/**
 * How a `HistoryTimeline` styles the rows around its current step.
 */
public enum class HistoryTimelineVariant {
    /**
     * A record of updates as they happen, such as transactions or order status. Only the current
     * row takes its voice colour; every other row is muted.
     */
    Events,

    /**
     * A known path of steps, such as card activation. Rows before the current one show a check, the
     * current row shows an in-progress ring with an animated positive gradient line, and rows after
     * it are muted with a dashed line and tertiary labels. A reached row with a critical voice
     * shows an error icon instead.
     */
    Steps,
}
