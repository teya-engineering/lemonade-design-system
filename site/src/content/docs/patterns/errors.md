---
title: Errors & recovery
description: Where an error belongs, and how to write one.
---

An error is information the user needs at the moment they need it. Where you put it and
how you word it decide whether that's true.

## Put the error where the fix is

Lemonade gives you three places to surface a problem, and they aren't interchangeable:

- **Field-level messages** — a text field's own `errorMessage`, shown with
  `error = true` — for a problem with that specific field. See
  [Forms](/lemonade-design-system/patterns/forms/) for validation timing and how a field's
  error message replaces its support text.
- **`Notice`**, placed above the form, for a problem with the whole form or the
  request behind it — a failed submission, a server that timed out, a validation
  failure the server caught that the client didn't. Give it `voice = Critical` and, where
  there's something to do about it, an `actionLabel` and `onActionClick`.
- **`Toast`**, for a transient, background failure the user can't act on directly right
  now — a sync that failed quietly, a background refresh that didn't come through.
  Toasts auto-dismiss (the `Loading` voice is the one exception — it persists until
  you dismiss or replace it), so don't put anything in one that the user has to read
  before they can continue.

The test is simple: if fixing the problem means correcting one field, the message
belongs on that field. If it means retrying or going somewhere else, it belongs in a
`Notice`. If there's nothing to do but acknowledge it happened, it's a `Toast` — and even
then, only if missing it wouldn't block anything.

## Write the fix, not the fault

Say what to do, not what went wrong. This is the same rule
[Forms](/lemonade-design-system/patterns/forms/) sets for field-level messages, and it
applies just as directly to a `Notice`:

> **Do** — "Enter an email address in the format name@company.com"
>
> **Don't** — "Invalid input"

"Invalid input" is true and useless — it tells the user something is wrong without
telling them what wrong looks like or what right looks like. Every error message should
survive being read in isolation, out of context, by someone who doesn't know what the
system was trying to do when it failed.

## Always leave a way forward

Retry, go back, or a way to contact support — pick at least one. An error with no next
step is a dead end, and a dead end in a shipped product is a bug, not a UX nicety to
revisit later.

`Notice` has an `actionLabel` and `onActionClick` for exactly this. If the failure is
retryable, wire the action to retry. If it isn't, the action can still open a support
channel or take the user back to where they were before the request started — the
requirement is that tapping something does something, not that it fixes the problem
outright.

## Do not blame the user

Avoid "you failed to enter a valid email" or "you didn't complete this step." Describe
what happened and what to do next: "That email address doesn't look right. Enter it in
the format name@company.com." The distinction isn't cosmetic — accusatory copy reads as
the system deflecting responsibility for a mistake that, from the user's side, might not
even be theirs (a flaky connection, a server timeout, a stale session). Neutral,
instructive wording holds up regardless of whose fault it actually was.
