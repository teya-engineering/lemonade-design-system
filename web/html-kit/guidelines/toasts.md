# Toasts

## `LDS-TOAST-DURATION` · A toast dismisses itself

**Kind.** Rule.

**Rule.** A toast confirms something and goes away on its own, as Lemonade's `ToastDuration` does in the
app. In an `lmnd-kit-toast-host` the kit dismisses it after the time its class names:

| Duration | Time | Class | For |
| --- | --- | --- | --- |
| Short | 3 seconds | `lmnd-kit-toast`, the default | A confirmation: "Invitation sent", "Role created" |
| Medium | 6 seconds | `lmnd-kit-toast--medium` | A toast with an action, such as Undo, or a longer message |
| Long | 9 seconds | `lmnd-kit-toast--long` | An error the user has to read before acting on it |

A loading toast, one with an `lmnd-spinner`, stays until another toast or a screen replaces it.

```html
<div class="lmnd-kit-toast-host" id="role-created">
  <div class="lmnd-kit-toast lmnd-kit-toast--success" role="status"><span class="lmnd-icon" data-icon="circle-check" aria-hidden="true"></span><span class="lmnd-kit-toast__label lmnd-text-body-small-medium">Role created</span></div>
</div>
```

**Why.** A toast reports what just happened while the user carries on. One that stays covers the bottom
of the screen and reads as something still waiting on them.

**Exceptions.** None. Something the user must act on is a dialog, a sheet or a notice, not a toast that
stays up.

**Do / don't.** "Invitation sent" fading after 3 seconds. Not a toast that stays until the next tap, and
not Short on a toast with Undo.
