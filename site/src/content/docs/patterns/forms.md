---
title: Forms
description: How to assemble a form from Lemonade components — labels, support text, validation timing, and errors.
---

A form is the most common screen in Teya's products and the easiest to get subtly wrong.
This is how Lemonade expects one to be built.

## The shape of a field

Every text input in Lemonade is one component with four slots of copy around it, and
each slot has one job:

| Slot | Job | When to use it |
|------|-----|----------------|
| `label` | Names the field | Always. A placeholder is not a label. |
| `optionalIndicator` | Marks a field as not required | When most fields on the form *are* required |
| `placeholderText` | Shows the expected format | When format is genuinely ambiguous |
| `supportText` | Explains why you're asking | When the reason isn't obvious |
| `errorMessage` | Says what to fix | Only with `error = true` |

```kotlin
LemonadeUi.TextField(
    input = email,
    onInputChanged = { email = it },
    label = "Email address",
    supportText = "We'll send your receipt here",
    placeholderText = "name@company.com",
    errorMessage = "Enter an email address in the format name@company.com",
    error = emailTouched && !email.isValidEmail,
)
```

```swift
LemonadeUi.TextField(
    input: $email,
    onInputChanged: { email = $0 },
    label: "Email address",
    supportText: "We'll send your receipt here",
    placeholderText: "name@company.com",
    errorMessage: "Enter an email address in the format name@company.com",
    error: emailTouched && !email.isValidEmail
)
```

The two platforms take the same arguments in the same order. A form written on one
should be readable by someone who works on the other.

## Placeholders are not labels

Putting the label in the placeholder looks tidy on an empty form and fails the moment
someone types — the label vanishes exactly when it becomes useful for checking your
work. It also breaks screen reader navigation, because there is nothing left to
announce.

If a field is so obvious it seems not to need a label, it still needs a label.

## When to validate

Validate on blur, not on keystroke. A field that turns red while you are still typing
the second character of your email address is telling you that you are wrong before you
have finished being right.

- **On blur** — the field has lost focus and the user has finished their thought
- **On submit** — everything gets re-checked, and focus moves to the first field in error
- **While typing** — only to *clear* an error that has already been shown

That last one matters. Once a field is in error, correcting it should return it to
normal immediately; making the user blur the field again to be told they've fixed it is
needless.

## Writing the error

An error message replaces support text, so it has to carry the whole explanation. Say
what to do, not what went wrong:

> **Do** — "Enter an email address in the format name@company.com"
>
> **Don't** — "Invalid input"

Keep the field's own error message specific to the field. Errors that apply to the whole
form — a failed submission, a server that timed out — belong in a `Notice` above the
form, not attached to a field the user cannot fix.

## Spacing a form

Use `spacing400` between fields and `spacing600` between groups of fields. The gap
between a field and its own support text is part of the component and is not yours to
adjust — if it looks wrong, that's a component bug worth reporting rather than a padding
to override.

## Submit

One primary button, at the end, doing the thing the form is named after. Keep it enabled
even when the form is incomplete: disabled submit buttons give no explanation for why
nothing happens, whereas a form that validates on press can point at the field that
needs attention.
