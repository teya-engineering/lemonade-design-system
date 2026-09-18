---
name: binary-compatibility
description: >
  Diagnose and fix Kotlin Binary Compatibility Validator (BCV) failures in the
  Lemonade KMP modules. Use when `apiCheck` fails, when the "API Stability
  Review" CI job flags a PR as BREAKING, when an `api/*.api` or `*.klib.api`
  file shows up in a diff, or when adding a parameter/overload/enum entry to a
  published component and you need to keep ABI stable.
---

# Lemonade binary compatibility

The published KMP modules (`core`, `tokens`, `ui`, `expressive`, `calendar`) ship
to real consumers. The Binary Compatibility Validator dumps every public symbol
into `kmp/<module>/api/*.api` (JVM/Android), `kmp/<module>/api/*/*.api`, and
`*.klib.api` (Kotlin/Native). CI then does two separate things with those files,
and it's worth knowing which is which before you touch anything:

- **`apiCheck`** recomputes the API from your source and fails if it no longer
  matches the committed baseline. The fix is always the same: run `apiDump` and
  commit the regenerated files. This just keeps the baseline honest; it doesn't
  judge whether the change is safe.
- **API Stability Review** takes the diff of those baseline files between your
  branch and the base, and classifies it. The classifier lives at
  `kmp/build-logic/src/main/kotlin/ApiStabilityClassifier.kt` and the verdict is
  one of `NO_CHANGES`, `ADDITIONS_ONLY`, or `BREAKING`. Additions auto-pass.
  A `BREAKING` verdict blocks merge until one of @caiqueslp, @williankl, or
  @DevSrSouza approves the exact head commit.

Two rules decide `BREAKING`, and they are blunt on purpose:

1. **Any `-` line** in the diff (other than the `---` file header). A removal, a
   rename, a return-type change, a narrowed visibility — anything that deletes or
   alters an existing line counts. There are four carve-outs:
   - A `-`/`+` pair in the same file that differs *only* by the `synthetic`
     modifier is treated as additive, because that's a `@Deprecated(HIDDEN)` symbol
     keeping its exact descriptor (more below).
   - A `-` line for a name-mangled `internal` member is treated as additive,
     because it was never public ABI in the first place (more below).
   - The `ComposableSingletons$<File>Kt` class line, its `INSTANCE` field, and a
     bare `}` are treated as additive. The Compose compiler declares that holder
     `internal` (more below).
   - A `.klib.api` header `// Targets: [...]` line replaced by one that keeps
     every old target is treated as additive. A new KMP target removes nothing.
     Dropping a target stays `BREAKING`.
2. **A `+` abstract member added inside a type that already existed.** That looks
   additive in the diff but throws `AbstractMethodError` on consumers who already
   implement the type. A brand-new type with abstract members is fine — nobody
   implements it yet.

That's the whole model. The classifier reads text, not intent, so outside those
four carve-outs it stays deliberately strict.

## Step 1 — see what actually changed

Run the diagnosis script from anywhere in the repo:

```bash
.claude/skills/binary-compatibility/scripts/bcv-check.sh
```

It regenerates the baseline from your current source, diffs it against `HEAD`,
prints the `+`/`-` lines that moved, and runs the project's own classifier so you
get the same verdict CI will. Add `--ci` to mirror CI exactly (committed baseline
vs `origin/main`, no regeneration).

Read the verdict and the moved lines before deciding anything. A `BREAKING`
verdict with reasons like `removed/changed declaration(s): …` tells you which
symbol the classifier objected to.

## Step 2 — decide, per kind of change

### Adding a default parameter to a public function

Adding a default parameter looks source-compatible — existing call sites still
compile. But Kotlin changes the JVM method signature: `foo(a)` becomes `foo(a, b)`
plus a synthetic `foo$default(...)`. The old `foo(a)` line disappears from the
dump, so the classifier sees a `-` line and calls it `BREAKING`.

Keep the old binary symbol by leaving the previous signature in place as a
`@Deprecated(HIDDEN)` overload that delegates to the new one. The real example in
this repo is `LemonadeUi.CountryFlag` — read
`kmp/ui/src/commonMain/kotlin/com/teya/lemonade/CountryFlag.kt`:

```kotlin
@Composable
public fun LemonadeUi.CountryFlag(
    flag: LemonadeCountryFlags,
    contentDescription: String = flag.name,
    size: LemonadeAssetSize = LemonadeAssetSize.Medium,
    modifier: Modifier = Modifier,
    shape: CountryFlagShape = CountryFlagShape.Circular, // the new parameter
) { /* … */ }

@Deprecated(
    message = "Use the overload with a shape parameter.",
    replaceWith = ReplaceWith("CountryFlag(flag, contentDescription, size, modifier, CountryFlagShape.Circular)"),
    level = DeprecationLevel.HIDDEN,
)
@Composable
public fun LemonadeUi.CountryFlag(
    flag: LemonadeCountryFlags,
    contentDescription: String = flag.name,
    size: LemonadeAssetSize = LemonadeAssetSize.Medium,
    modifier: Modifier = Modifier,
) {
    CountryFlag(flag, contentDescription, size, modifier, CountryFlagShape.Circular)
}
```

`HIDDEN` keeps the old method in the bytecode (so already-compiled consumers keep
linking) while removing it from the Kotlin source API (so nobody writes new code
against it). That is the right move and you should make it.

`HIDDEN` adds the `synthetic` flag to that retained method, so its dump line flips
from `public static final fun …` to `public static final synthetic fun …` — a
`-`/`+` pair. The classifier recognises this case: a pair that differs only by
`synthetic` keeps an identical descriptor, so no consumer can break, and it's
counted as additive rather than a removal. Verdict `ADDITIONS_ONLY`, CI auto-passes,
no maintainer gate. Still mention the overload in the PR's API Dump section so a
reviewer can see what happened.

If you'd rather not hide the old overload at all, keep it **visible** (drop the
`@Deprecated`, or use `DeprecationLevel.WARNING`) and add the new behaviour as a
separate explicit overload. Both overloads then stay in the Kotlin API. Either way
the verdict is `ADDITIONS_ONLY` — the only difference is whether the old signature
is still visible to source. Pick per case and say which in the PR.

### Renaming a public function

Same shape as above. Keep the old name as a `@Deprecated(HIDDEN)` function that
forwards to the new one. The old binary symbol survives, source users move to the
new name, and the `synthetic`-only flip classifies as `ADDITIONS_ONLY`.

### Renaming a file with public top-level declarations

On the JVM, top-level declarations live in a facade class named after the file:
`TopBar.mobile.kt` compiles to `TopBar_mobileKt`. Rename the file (for example
when you move it from `mobileMain` to `commonMain/TopBar.kt`) and every top-level
symbol moves to `TopBarKt`, which is a real break on Android and desktop. Keep the
old class name with `@file:kotlin.jvm.JvmName("TopBar_mobileKt")` on the renamed
file. Use the fully qualified name, because `commonMain` does not resolve the
short `JvmName`. The `.klib.api` dump has no facade classes, so native and wasm
targets are not affected.

### Renaming or removing a public property — STOP

There is no clean overload trick for a property. Renaming or removing one is a
real ABI **and** API break: existing binaries fail to link and existing source
fails to compile. Do not do it silently to make a build pass.

If the rename is genuinely required, the change has to be deliberate: surface it
to the user, keep the old property where you can (e.g. a `@Deprecated(HIDDEN)`
backing accessor), and get sign-off from the required reviewers. This is exactly
the case the maintainer-approval gate exists for. Never reshape a property to
quiet `apiCheck`.

### Adding a property to a public `data class`

`data class` looks impossible to evolve and isn't — it just needs two shims. The
compiler regenerates `<init>`, `copy()`, `copy$default()`, and `componentN()` from
the primary constructor, so adding a property changes their signatures. Keep every
old binary symbol by **appending** the property and restoring the old constructor
and `copy` as `@Deprecated(HIDDEN)` overloads:

```kotlin
data class Foo(
    val a: String,
    val b: Int = 0,        // new — append it (never insert in the middle), give it a default
) {
    // Restores the old constructor symbol: <init>(String).
    @Deprecated("kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public constructor(a: String) : this(a, 0)

    // Restores the old copy symbols: copy(String) AND copy$default(Foo, String, int, Object).
    // The default on `a` is required — it's what regenerates the old copy$default.
    @Deprecated("kept for binary compatibility", level = DeprecationLevel.HIDDEN)
    public fun copy(a: String = this.a): Foo = copy(a = a, b = this.b)
}
```

Verified against a real `apiDump`: this compiles with no warnings, every old symbol
(`<init>(String)`, `copy(String)`, `copy$default(Foo, String, int, Object)`) is kept
as `synthetic`, the new symbols are pure additions, and the verdict is
`ADDITIONS_ONLY`. An old compiled consumer keeps linking whether it calls the
constructor, `copy(a = …)`, or `copy()`.

What makes or breaks it:

- **Append, never insert.** Add the new property last. Inserting in the middle
  renumbers `componentN()` (destructuring is positional) and the shim signatures stop
  lining up with the old ones.
- **The `copy` shim must take a default** (`a: String = this.a`). Without it the old
  `copy$default(...)` isn't regenerated, and any consumer that called `copy()` with
  defaulted arguments breaks.
- **One shim pair per released shape.** If the class grows again (`a, b` → `a, b, c`),
  keep a `@Deprecated(HIDDEN)` constructor and `copy` for the `(a)` shape *and* the
  `(a, b)` shape. The BCV baseline is the source of truth: if `apiCheck` is green and
  the verdict is additions-only, every symbol the baseline knows about is covered.
- `equals`/`hashCode`/`toString` regenerate to include the new property — that's what
  keeps the class stable for Compose, so leave them be.

One caveat: Kotlin doesn't officially bless hand-declaring `copy` in a data class. It
compiles cleanly on the current toolchain and BCV guards the symbols, so if a future
compiler ever drops them `apiCheck` fails loudly rather than silently breaking a
consumer. If carrying shims gets old, `@Poko` is the longer-term route — it generates
`equals`/`hashCode`/`toString` for Compose stability but deliberately omits
`copy`/`componentN`, so there's no compiler-owned surface to break and you write the
constructors yourself. That's a migration, not a quick fix; raise it separately.

Reordering or removing a constructor parameter is still a hard break with no shim.
STOP and escalate.

### Adding to an `interface`

Adding a property or function to an interface can be an API and ABI change in the
general case. In Lemonade it's almost always fine: the interfaces here are meant
to be implemented *inside* the design system, not by consumers. If something
downstream implements one, that's already a misuse and we don't design around it.

So: **additions to an interface are acceptable**, but they're not free. The
classifier flags a new *abstract* member on an existing type as `BREAKING` (rule
2), because it would `AbstractMethodError` any external implementer. Two ways
through:

- Give the new member a default implementation (`fun foo(): T = …` on the
  interface). It dumps as `open`, not `abstract`, so the verdict stays additive.
- Or keep it abstract if a default makes no sense, accept the `BREAKING` verdict,
  and have a maintainer approve it after you state in the PR that the interface is
  local-only.

Renaming an interface member is a different story — that's a hard break with no
workaround. Treat it like a property rename: STOP and escalate.

### Adding an `enum` entry

Most enums here configure a component, and the component is the only thing that
does `when (value)` over them. Adding an entry is an ABI/API change in theory
(an exhaustive `when` in consumer code could now miss a branch), but for a
config enum the component owns the `when`, so in practice it's safe. The
classifier treats a pure entry addition as `ADDITIONS_ONLY` — no `-` line — so it
auto-passes.

Two caveats: make sure every `when` *inside the design system* handles the new
entry, and call the addition out in the PR so a reviewer can confirm the enum is
really config-only and not something a consumer branches on. Renaming or removing
an entry is a `-` line and a real break — STOP and escalate.

### A `getLambda$<hash>` line moved and you never touched it

You'll see this when a `@Composable` you edited gains a parameter, and a `-`/`+`
pair like this shows up in a file you didn't think you changed:

```
- public final fun getLambda$-968066332$expressive ()Lkotlin/jvm/functions/Function2;
+ public final fun getLambda$1980115960$expressive ()Lkotlin/jvm/functions/Function2;
```

There is no `getLambda` in your source. It's a Compose-compiler-generated
singleton in `ComposableSingletons$<File>Kt` that holds a composable lambda which
captures nothing, for example the `{ BottomSheetDefaults.DragHandle() }` passed
as `dragHandle`. The `$<hash>` part is the compiler's lambda key, derived from the
shape of the enclosing composable, so adding a parameter to that composable
re-hashes it even though the lambda body is identical.

The trailing `$expressive` (or `$expressive_release` on Android) is Kotlin's
name-mangling for an `internal` symbol. It's bytecode-public only so the module's
own call sites can link, the module name in the suffix stops any other module from
resolving it, and it never appears in the `.klib.api` dump (which honours Kotlin
visibility). A consumer calls `LemonadeUi.BottomSheet(...)` and passes its own
content; it never names `getLambda$<hash>`. So the hash flip cannot
`NoSuchMethodError` anyone downstream.

This is not really a Compose rule. The carve-out keys on the `$<module>` mangling,
which is how Kotlin marks any `internal` member that has to stay public in
bytecode, so in principle it covers every mangled-internal symbol, not just
Compose's. In practice Compose is the only case you actually see. BCV already drops
hand-written `internal` declarations from the dump using Kotlin metadata, so the
`internal fun CoreBottomSheet`, `internal fun CoreTextField`, and the dozens like
them never produce a `-`/`+` line no matter how you change them. The
`ComposableSingletons` lambdas slip past that filter because the compiler emits
them as public, and they re-hash on their own, so they are the one internal symbol
you watch move without touching its source.

The classifier knows this: a `-` line whose member name ends in `$<module>` (the
module is read from the `…/<module>/api/…` path in the diff header) is treated as
additive, so a lone hash flip lands as `ADDITIONS_ONLY`. You don't need a shim or a
maintainer gate for it. Leave it. Moving the lambda to another file only moves the
singleton to a different holder.

The holder class takes its name from the source file, even when the file has
`@file:JvmName`. Rename the file or move it to another source set and
`ComposableSingletons$<Old>Kt` becomes `ComposableSingletons$<New>Kt`. The
compiler declares the holder `internal`, so the classifier also counts its class
line and `INSTANCE` field as additive.

This is scoped tightly: only the trailing `$`-segment is matched against the
module name, so the genuine default-argument symbols (`copy$default`,
`show$default`, and the like, whose suffix is `default`) stay flagged as the real
ABI they are. So does an `internal` member you deliberately publish with
`@PublishedApi`: that one is emitted unmangled, with no `$<module>` suffix, so the
carve-out leaves it flagged. That's correct, because a consumer's inlined code can
call it. Still note the moved line in the PR's API Dump section so a reviewer sees
it was the mangled singleton and not something load-bearing.

## Step 3 — regenerate, re-check, then write the PR section

After any of the above:

```bash
cd kmp && ./gradlew apiDump      # rewrite the baseline from your new source
.claude/skills/binary-compatibility/scripts/bcv-check.sh   # confirm the verdict
```

Commit the regenerated `api/` files alongside the source. Then fill in the
**API Dump** section of the PR description (the template has it). The section
exists so a reviewer can read the API delta without re-deriving it. State:

- the verdict (`ADDITIONS_ONLY` or `BREAKING`);
- any `@Deprecated(HIDDEN)` overload you added — note that the `-`/`+` pair is just
  the `synthetic` flag with an unchanged descriptor, which the classifier counts as
  additive;
- any interface or enum addition, with a line confirming the type is local-only /
  config-only;
- any `ComposableSingletons` line that moved (a `getLambda$<hash>` re-hash or a
  holder rename), which the classifier counts as additive;
- any `// Targets:` header that gained a target, which the classifier counts as
  additive;
- if the verdict is `BREAKING` for a real reason, who needs to approve and why the
  break is acceptable.

A reviewer should be able to approve from that section alone. If you can't write a
clean justification for a `-` line, that's the signal the change needs a real
conversation, not a workaround.
