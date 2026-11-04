# History timeline

## `LDS-TIMELINE-END` · The last step has no line after it

**Rule.** A history timeline ends at its last step. No line, solid or dashed, runs below the last
step's dot or indicator, even when more steps will follow later. A timeline that shows only some of
its steps ends at the last one it shows, and an action to see the rest sits below it, outside the
timeline.

**Why.** The line says that another step follows. Under the last step it points at nothing, and the
reader looks for a step that isn't there.

**Exceptions.** None.

**Example.** A card delivery, three steps with the second in progress. The timeline draws no line
after the last step it is given, so every step of the history goes into the one timeline.

Compose.

```kotlin
LemonadeUi.HistoryTimeline(
    items = listOf(
        HistoryTimelineItem(label = "Order placed"),
        HistoryTimelineItem(label = "Card on its way"),
        HistoryTimelineItem(label = "Activate card"),
    ),
    currentIndex = 1,
    variant = HistoryTimelineVariant.Steps,
)
```

SwiftUI.

```swift
LemonadeUi.HistoryTimeline(
    items: [
        LemonadeHistoryTimelineItem(label: "Order placed"),
        LemonadeHistoryTimelineItem(label: "Card on its way"),
        LemonadeHistoryTimelineItem(label: "Activate card"),
    ],
    currentIndex: 1,
    variant: .steps
)
```

HTML kit. The line is drawn under every item but the last child of the list, so nothing else goes
inside the `<ol>`.

```html
<ol class="lmnd-kit-timeline lmnd-kit-timeline--steps">
  <li class="lmnd-kit-timeline__item lmnd-kit-timeline__item--completed">
    <span class="lmnd-kit-timeline__indicator"><span class="lmnd-icon" data-icon="circle-check-solid" aria-hidden="true"></span></span>
    <div class="lmnd-kit-timeline__body"><span class="lmnd-kit-timeline__label lmnd-text-body-medium-medium">Order placed</span></div>
  </li>
  <li class="lmnd-kit-timeline__item lmnd-kit-timeline__item--active">
    <span class="lmnd-kit-timeline__indicator"></span>
    <div class="lmnd-kit-timeline__body"><span class="lmnd-kit-timeline__label lmnd-text-body-medium-medium">Card on its way</span></div>
  </li>
  <li class="lmnd-kit-timeline__item lmnd-kit-timeline__item--next">
    <span class="lmnd-kit-timeline__indicator"></span>
    <div class="lmnd-kit-timeline__body"><span class="lmnd-kit-timeline__label lmnd-text-body-medium-medium">Activate card</span></div>
  </li>
</ol>
```

**Don't.** A line drawn by hand under the timeline to show that more is coming, a step added after
the timeline to continue it, or a "See all" link or any other element inside `lmnd-kit-timeline`
after the last item, which moves the line onto that item.
