package com.teya.lemonade

/**
 * Marks an API that is still taking shape and may change or be removed without the
 * usual deprecation cycle.
 *
 * Opting in means accepting that a later release can rename, restructure or drop the
 * declaration. Use it where the alternative is worse - reaching for
 * [LemonadePrimitiveColors], say - and expect to revisit the call site.
 *
 * Feedback on real usage is what moves an API out of this state, so tell the design
 * system team what you built with it and where it got in the way.
 */
@RequiresOptIn(
    message = "This API is experimental and may change without a deprecation cycle. " +
        "Opt in with @OptIn(ExperimentalLemonadeApi::class).",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.TYPEALIAS,
)
public annotation class ExperimentalLemonadeApi(val message: String = "")
