# Forms

Rules for the Teya app's screens that ask for values. They follow the app's form archetype, so they carry
the app's prefix, `APP-`.

## `APP-FORM-FOCUS` · A screen made for typing opens with its first field focused

**Kind.** Rule.

**Rule.** When the main or only job of a screen or sheet is to fill in a field, the first enabled field is
focused when the user arrives to type, and the keyboard comes up with it. The user arrives to type on first
arrival, and when a later screen sends them back to change the field's value, such as an Edit action on a
review or a later step rejecting the value. A field that already has a value is focused too, with the
cursor at the end.

Going back with Back leaves the field unfocused and the keyboard down, including on a single-field step.

Mark the field with `autofocus`. The kit's script focuses it as its screen is pushed in or its sheet opens,
and leaves it alone on Back, or on the way back to the sheet from a picker.

**Why.** On arrival, typing is what comes next, and a tap on the field first is a step that does nothing. On
Back, the user may be leaving the flow, or looking at the screen, and a keyboard opening only to close again
on the next Back is noise; the value is still there, one tap from being edited.

**Exceptions.** A search field that only narrows a list, such as a picker sheet's: the job is to pick, so the
options stay in view and the keyboard stays down.

**Do / don't.** `<input class="lmnd-kit-field__input" autofocus>` on a form's first field. Not `autofocus` on a
disabled field, and not on a picker sheet's `lmnd-kit-search`.
