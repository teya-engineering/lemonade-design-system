# History timeline

## `LDS-TIMELINE-END` · The last step has no line after it

**Kind.** Rule.

**Rule.** A history timeline ends at its last step. No line, solid or dashed, runs below the last step's dot
or indicator, even when more steps will follow later. A timeline that shows only some of its steps ends at
the last one it shows, and an action to see the rest sits below it, outside the timeline.

**Why.** The line says that another step follows. Under the last step it points at nothing, and the reader
looks for a step that isn't there.

**Exceptions.** None.

**Do / don't.** A card delivery as one `<ol class="lmnd-kit-timeline lmnd-kit-timeline--steps">`. The line is
drawn under every item but the last child of the list, so nothing else goes inside the `<ol>`. Not a line drawn
by hand under the timeline to show that more is coming, not a step added after the timeline to continue it,
and not a "See all" link inside `lmnd-kit-timeline` after the last item, which moves the line onto that item.
