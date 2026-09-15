---
title: Empty & loading states
description: What to show before there is anything to show.
---

Every screen that shows data has to answer the same question first: what does it show
before the data arrives, or when there isn't any? Getting this wrong is what makes an
app feel unfinished even when the happy path is polished.

## Skeletons vs. spinners

Lemonade gives you both `Skeleton` (as `LineSkeleton`, `CircleSkeleton`, and
`BlockSkeleton`) and `Spinner`, and they answer different questions.

Use `Skeleton` when you already know the shape of what's coming and the wait is short —
a list of transactions whose rows you can lay out before the data lands, a profile
card whose fields you know in advance. The skeleton's shimmer previews the layout so the
content doesn't jump when it arrives.

Use `Spinner` when neither of those holds — you don't know what the result will look
like, or the wait could run long enough that a static shape sitting on screen starts to
look stuck rather than loading.

Don't show both in the same region. A skeleton row with a spinner floating over it is
two components disagreeing about how long this is going to take.

## Do not flash

If the data usually arrives in a couple hundred milliseconds, don't show a loading state
at all. A skeleton that appears and vanishes almost immediately doesn't read as "fast" —
it reads as a flicker, and it's more disruptive than the brief blank moment it was meant
to smooth over. Debounce it: only switch to the loading state once the wait has run long
enough that showing nothing would look broken.

## Empty is not an error

An empty list is a normal, expected state — the first time someone opens a screen, after
they clear a filter down to nothing, right after they delete the last item. It is not a
failure and shouldn't be styled or worded like one.

An empty state needs two things: an explanation of why there's nothing here, and, where
one exists, the action that fills it. "No saved cards yet" paired with an "Add a card"
button gives someone somewhere to go. "No saved cards yet" on its own is half a
sentence.

## First-run vs. filtered-empty

"You haven't created anything yet" and "nothing matches this filter" are different
states, and the fix for conflating them is usually a support ticket. The first-run empty
state should point at the action that creates the first item. A filtered-empty state
should point at clearing or adjusting the filter — offering to "create" something when
the real problem is that a filter is too narrow sends someone down the wrong path
entirely.

If a screen can be empty for either reason, check which one you're in before you decide
what the empty state says. Same layout, different copy, different action.
