# Toasts

## `LDS-TOAST-DURATION` · A toast dismisses itself

**Rule.** A toast confirms something and goes away on its own, after the time its duration names:

| Duration | Time | For |
| --- | --- | --- |
| Short | 3 seconds | A confirmation, such as "Invitation sent". The default |
| Medium | 6 seconds | A toast with an action, such as Undo, or a longer message |
| Long | 9 seconds | An error the user has to read before acting on it |

A loading toast stays until another toast or a screen replaces it.

**Why.** A toast reports what just happened while the user carries on. One that stays covers the bottom
of the screen and reads as something still waiting on them.

**Exceptions.** None. Something the user must act on is a dialog, a sheet or a notice, not a toast that
stays up.

**Example.** A confirmation, then a toast with Undo.

Compose.

```kotlin
val toastState = LocalLemonadeToastState.current

toastState.show(label = "Role created", voice = ToastVoice.Success)
toastState.show(
    label = "Member removed",
    voice = ToastVoice.Success,
    duration = ToastDuration.Medium,
    actionLabel = "Undo",
    onAction = onUndo,
)
```

SwiftUI.

```swift
toastManager.show(label: "Role created", voice: .success)
toastManager.show(
    label: "Member removed",
    voice: .success,
    duration: .medium,
    actionLabel: "Undo",
    onAction: onUndo
)
```

HTML kit. The kit dismisses a toast in an `lmnd-kit-toast-host` after its duration: Short by default,
`lmnd-kit-toast--medium` or `lmnd-kit-toast--long` for the others.

```html
<div class="lmnd-kit-toast-host" id="member-removed">
  <div class="lmnd-kit-toast lmnd-kit-toast--success lmnd-kit-toast--medium" role="status"><span class="lmnd-icon" data-icon="circle-check" aria-hidden="true"></span><span class="lmnd-kit-toast__label lmnd-text-body-small-medium">Member removed</span><button type="button" class="lmnd-kit-toast__action lmnd-text-body-small-medium">Undo</button></div>
</div>
```

**Don't.** A toast that stays until the next tap, or Short on a toast with Undo.
