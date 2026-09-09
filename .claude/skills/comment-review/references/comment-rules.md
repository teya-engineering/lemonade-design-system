# Comment Rules

Almost no code needs a comment. The target is close to zero. A comment is the rare exception you reach for only after you have failed to make the code say the thing itself. Most of the time the real fix is to split the code into functions whose names say it, not to write prose explaining it. That is Clean Code's central lesson: a comment is usually an apology for code that wasn't clear enough.

A comment also rots. The code changes, the comment doesn't, and the next reader trusts a line that now lies. A wrong comment costs more than no comment. So every comment that survives has to clear two bars at once: it can't be replaced by clearer code, and it has to stay true as the code moves. Most can't clear the first bar, so they never reach the second.

These rules apply to Kotlin in every source set under `kmp/` and to Swift under `swiftui/`. They cover implementation comments, KDoc, and Swift doc comments. They are enforced as blocking findings. See [Enforcement](#enforcement).

## A comment is almost always a missing function

This is the rule behind the rule, and the one most PRs get wrong. When you feel the urge to write a comment that explains *what* a block of code does, stop. That urge is the signal that the block wants to be a function with a name. Extract it. The function name carries the meaning the comment would have, and a name can't drift out of sync with its body the way a comment can.

```kotlin
// Before: comments narrate each step, and they'll drift the moment the body changes.
internal fun buildMonthGrid(month: CalendarMonth, selection: DateRange?): List<DayCell> {
    // pad the first week with the tail of the previous month
    val leading = month.firstDay.startOfWeek().until(month.firstDay).map { day ->
        DayCell.Padding(date = day)
    }
    // mark every day inside the selected range
    val days = month.days.map { day ->
        DayCell.Day(date = day, selected = selection?.contains(day) == true)
    }
    // disable anything outside the selectable bounds
    return (leading + days).map { cell ->
        if (cell.date in month.bounds) cell else cell.disabled()
    }
}

// After: the step names live in the code, so they can't lie.
internal fun buildMonthGrid(month: CalendarMonth, selection: DateRange?): List<DayCell> {
    val leading = month.previousMonthPadding()
    val days = month.daysMarkedBy(selection)
    return (leading + days).disabledOutside(month.bounds)
}
```

Same readable steps, none of the maintenance debt.

A section-header comment inside a function (`// validate`, `// build the list`, `// then charge`) is the loudest version of this signal. It is you drawing the boundaries between the steps of a function that is doing too many things. Each section is a function waiting to be named. Split it; don't label it.

This is why a cluster of comments is a code-quality problem, not a documentation problem. A function carrying three or four explanatory comments is a function that should have been three or four functions. The fix is decomposition, and a reviewer treats it as one: the comments are the symptom, the missing functions are the defect.

## Write code that doesn't need the comment

Extraction is the main tool, and the stack makes it cheap. Reach for these before you reach for a comment:

- **Extract a named private function** until the body reads like the comments would have. This is the strongest move and the answer to most "I should explain this" moments.
- **Rename the variable or function** so the line explains itself. `val grouped = items.groupByDay()` needs no `// group transactions by day` above it.
- **Pick a type that makes intent obvious** — a sealed class over a boolean flag, `TextFieldSize` over a raw `Boolean`.
- **Extract a private `@Composable`.** A section comment inside a composable is a child composable waiting for a name: `TrailingIconSlot()` beats `// draw the trailing icon`.
- **Flow operators with named lambdas.** `.filter { it.isSettled }` or `.map(::toListItem)` says what it does in the name. A comment above it just repeats the name.
- **Kotlin itself.** `when`, sealed classes, and named arguments carry intent. Use them before you reach for prose.

## The delete tests

The burden of proof is on keeping a comment, never on deleting it. A comment goes unless it survives every one of these:

1. Does it restate or narrate what the code already says? Delete it.

   ```kotlin
   // map the dto to the domain model
   return dto.toDomain()
   ```

2. Would a rename or an extracted function remove it? Do that instead: extract the function, then drop the comment.

   ```kotlin
   // group transactions by day
   val grouped = items.groupBy { it.date.dayStart() }
   // becomes:
   val grouped = items.groupByDay()
   ```

3. Is it a section header inside a function? Split the function; each section becomes a named function.

4. Will it rot if the code below changes and someone forgets to update it? Then it stays only if it carries something the code physically cannot express (see below). Otherwise the risk outweighs the value, and it goes.

5. Does it explain why something is absent, empty, or not done (`// no-op`, `// nothing to dispose`, `// intentionally left blank`)? Delete it; leave empty blocks empty. If the absence guards a real invariant, state the invariant a future editor would break — never the absence itself.

6. Does it narrate the change that produced this code ("now uses X", "switched to Y", why you did it)? Delete it. That rationale belongs in the commit message and PR description, where it's pinned to the diff it explains.

When you are unsure, delete. A missing comment sends the reader to the code, which is the source of truth. A wrong comment sends them off a cliff.

## The only comments that survive

A comment earns its place only by saying something the code itself cannot. These are the only categories. Anything outside them falls under one of the delete tests above.

- **The why behind a non-obvious choice.** A design decision, or the reason we deliberately skip the approach a reader would expect. Never the *what*. The code is the what.

  ```kotlin
  // The motion spec pins toast entrances at 200ms; Material's 300ms default reads sluggish here.
  val toastEnterDuration = 200.milliseconds
  ```

- **A real footgun.** An invariant a future editor would break by accident, or a platform quirk you only learn the hard way. The same class of warning the binary-compatibility rules encode (append-only `data class` constructors on published API, `@Deprecated(HIDDEN)` binary-compat shims, files a generator overwrites).

- **Public API documentation.** KDoc on the published API surface (`core`, `tokens`, `ui`, `expressive`, `calendar` — anything the BCV baseline tracks) earns its place when it adds what the signature can't: units, ranges, nullability meaning, which errors come back. Skip it when it only echoes the name. Hold internal declarations to the strict "only if non-obvious" bar: internal code is read with its body in view, so it almost never qualifies. In this repo, component KDoc is the contract: downstream repos read each component's `## Usage` block as the canonical reference instead of the source. A public component's KDoc with its `## Usage` example always earns its place — keep it accurate, and never strip it for brevity.

If a comment you want to keep doesn't fit one of these three, it doesn't survive. Make the code clearer instead.

## Stale comments

When you change code, fix or delete every comment that touched it in the same edit. A comment and the code it describes move together or the comment dies.

```kotlin
// retries the request up to 3 times    <-- now it's 5; the comment lies
repeat(5) { attempt -> ... }
```

If you can't keep a comment honest as the code evolves, it shouldn't exist. Facts that drift (counts, timings, "currently") are the first to rot, so lean on the code to state them.

## Keep comments local

A comment can only stay honest about code in the same file. Point it at something this file doesn't control (another module, a design file, anything behind an API) and it's pinned to state that changes without anyone touching this code. It rots silently, and nobody finds out until it has been lying for months.

So when a comment is warranted, describe the **local invariant**: what has to be true *here* for this code to be correct. Skip the external reason that happens to make it true today.

```kotlin
// Bad — pinned to a design file that can change without touching this code:
// matches the shadow blur on the elevation page in Figma
val focusRingBlur = 24.dp

// Good — states the rule this code enforces on its own:
// the largest blur the elevation scale defines; anything above it is a token error
val focusRingBlur = 24.dp
```

Reading another module or the design file to learn how to build this code is normal. It doesn't earn a comment. If an external detail doesn't explain the local logic, leave it out: it adds nothing, and it's the first thing to go stale.

## TODO and FIXME

A `TODO` or `FIXME` left in committed code must point at a GitHub issue. A bare `// TODO: clean this up` is a comment with no owner and no end date.

```kotlin
// TODO(#123): drop the hidden overload when the next major release breaks ABI anyway.
```

No issue, no TODO. Either file one or finish the work.

## Formatting

- **Standalone comment** (its own line): a full sentence. Capital letter, ending period.
- **Inline comment** (end of a code line): a lowercase fragment, no period. If it needs a second line, promote it to a standalone full sentence.
- **KDoc / Swift doc**: start by describing the symbol (`Returns the resolved semantic color…`, `True when the field shows its error state.`). Document the contract, not the implementation.

Comments are prose. Write them the way a tired teammate would leave a note: plain and direct. No inflated stakes ("this crucial step ensures…"), no hedging ("it could perhaps be argued"), no rule-of-three padding, no filler ("in order to"). Say the thing.

## Enforcement

Comment hygiene is a blocking gate, not a nicety. In the `comment-review` skill, every comment issue is a **blocking** finding that must be fixed before the PR goes up:

- a comment that restates or narrates what the code does,
- a section-header comment that should be a function split,
- a stale comment that no longer matches the code,
- a non-local comment pinned to code this file doesn't control,
- a comment explaining why something is absent, empty, or not done,
- a comment narrating the change that produced the code,
- an AI-flavored, inflated, or padded comment,
- a `TODO`/`FIXME` with no issue.

The fix for a narrating or section-header comment is to extract the function and delete the comment, not to reword it. The fix for a stale one is to correct or delete it. "It's only a comment" is not a reason to ship it: a comment cluster is a decomposition defect, and that's what the gate is catching.

## Leave these alone

- Generated files — anything whose header carries the `DO NOT MODIFY THIS FILE MANUALLY` banner, and the `kmp/*/api/` baselines. Never hand-edit them; fix the generator.
- License and copyright headers.
