---
name: kotlin-conventions
description: Kotlin code conventions for this project. Use when writing, editing, or reviewing Kotlin code (.kt files). Apply these rules for all Kotlin code generation.
---

# Code Conventions

## Explicit API Mode

All modules use Kotlin explicit API mode (`-Xexplicit-api=strict`). This requires:
- All public declarations must have explicit visibility modifiers (`public`, `internal`, `private`)
- All public declarations must have explicit return types

## Formatting Rules

**1. Named Parameters for Two or More Arguments**
A call passing two or more arguments names every one of them. This covers
functions, class constructors, and enum entries.

A single argument stays positional. Naming it adds nothing — the parameter it
binds to is unambiguous.

Invocations of a function-typed value take no names at all; `onClick()` has no
parameter name to use.

```kotlin
// Correct - two or more arguments, all named
Person(
    name = "Alice",
    age = 30,
)

// Correct - enum entry with one argument stays positional
enum class Color(val hex: String) {
    Red("#FF0000"),
    Blue("#0000FF"),
}

// Correct - single argument
Modifier.padding(LemonadeTheme.spaces.spacing100)

// Correct - invoking a function-typed value
onCheckedChange(true)

// Wrong - two arguments, neither named
Person("Alice", 30)
```

**2. One Parameter Per Line**
Each parameter must be on its own line for readability.

```kotlin
// Correct
Address(
    street = "Main St",
    city = "Springfield",
    zipCode = "12345",
)

// Wrong
Address(street = "Main St", city = "Springfield", zipCode = "12345")
```

**3. Expression Body for Single-Statement Functions**
A function whose body is a single statement uses expression-body form. ktlint
enforces this and fails the build on a block body wrapping one `return`.

Block bodies are for functions with two or more statements.

```kotlin
// Correct
public fun getData(): String = "data"

// Correct - two statements, so a block body
public fun getTrimmedData(): String {
    val raw = loadData()
    return raw.trim()
}

// Wrong
public fun getData(): String {
    return "data"
}
```

**4. Chain Calls on Separate Lines**
Two or more chained calls written inline on one line get broken, one call per
line.

A call attached to a closing paren or brace — `).method(…)` or `}.method(…)` —
is the continuation ktlint's `android_studio` code style produces for Compose
`Modifier` chains and multi-line builders. That is house style; leave it.

```kotlin
// Correct
listOf(1, 2, 3)
    .filter { value -> value > 1 }
    .map { value -> value * 2 }
    .toSet()

// Correct - continuation off a closing brace or paren
items
    .groupBy { item -> item.key }
    .mapValues { entry -> entry.value.size }
    .filterValues { count ->
        count > 1
    }.asSequence()

Modifier
    .background(
        color = LemonadeTheme.colors.background.bgDefault,
    ).clip(shape = LemonadeTheme.shapes.radiusFull)

// Wrong
listOf(1, 2, 3).filter { value -> value > 1 }.map { value -> value * 2 }.toSet()
```

**5. Elvis Operator on Separate Line**
The elvis operator (`?:`) must be on a new line.

```kotlin
// Correct
val name = map["key"]
    ?: return null

val value = getValue()
    ?: defaultValue

// Wrong
val name = map["key"] ?: return null
val value = getValue() ?: defaultValue
```

**6. No Implicit `it` in Lambdas**
Always use named parameters in lambdas instead of the implicit `it`.

```kotlin
// Correct
list.filter { item ->
    item.isValid
}
list.associateBy { entry ->
    entry.id
}

// Wrong
list.filter { it.isValid }
list.associateBy { it.id }
```

**7. Separate `when` Subject Declaration**
Always declare the `when` subject variable on a separate line before the `when` expression.

```kotlin
// Correct
val result = getData()
when (result) {
    is Success -> handleSuccess()
    is Error -> handleError()
}

// Wrong
when (val result = getData()) {
    is Success -> handleSuccess()
    is Error -> handleError()
}
```
