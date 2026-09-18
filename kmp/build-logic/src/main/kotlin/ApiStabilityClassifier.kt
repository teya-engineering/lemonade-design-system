/**
 * Classifies a unified diff of BCV-generated `.api` / `.klib.api` files into:
 *
 *  - [Verdict.NoChanges]     — no public API delta. CI silently passes.
 *  - [Verdict.AdditionsOnly] — purely additive ABI changes. CI auto-passes.
 *  - [Verdict.Breaking]      — ABI-incompatible changes. CI requires a named
 *                              maintainer to approve the PR.
 *
 * Two ABI breakage rules are applied to the diff:
 *
 *  1. Any `-` line (excluding `---` file headers) — a declaration was removed,
 *     renamed, had its signature changed, or had its visibility narrowed.
 *
 *     Exception A: a `-` line that differs from a `+` line in the *same file* only
 *     by the JVM `synthetic` modifier is NOT a break. `synthetic` is the bytecode
 *     marker Kotlin emits for a `@Deprecated(level = HIDDEN)` member. The method
 *     keeps its exact descriptor (owner, name, parameters, return type), so every
 *     already-compiled consumer still links against it — only the source-level
 *     visibility changes. This is the standard "add a default parameter / rename
 *     a function while keeping the old binary symbol" pattern, and it auto-passes.
 *
 *     Exception B: a `-` line for a member carrying Kotlin's internal
 *     name-mangling suffix `$<module>` (Android variants append `_<variant>`, e.g.
 *     `$expressive_release`) is NOT a break. Kotlin mangles `internal` members
 *     that must stay public in bytecode with the module name so no other module
 *     can resolve them. They leak into the JVM `.api` dump as that artifact — the
 *     Kotlin-visibility-aware `.klib.api` dump omits them — and only the module's
 *     own compiled call sites reference them, always recompiling in lockstep. The
 *     usual case is a Compose `ComposableSingletons$<File>Kt.getLambda$<hash>` whose
 *     key re-hashes when a surrounding `@Composable` changes shape. See
 *     [String.isInternalMangled].
 *
 *     Exception C: the `ComposableSingletons$<File>Kt` class line and its
 *     `INSTANCE` field. The Compose compiler emits this holder as `internal`, and
 *     it renames with its source file. A bare `}` is not a declaration either.
 *
 *     Exception D: a `.klib.api` header `// Targets: [...]` line replaced by one
 *     that keeps every old target. Adding a target removes nothing; dropping one
 *     stays a break.
 *
 *  2. A `+` line declaring an abstract member (`abstract fun|val|var`) inside
 *     a class/interface block whose declaration line is **not** also a `+`
 *     line — i.e. an abstract member added to a type that already existed.
 *     This case shows up as purely additive in the diff but causes
 *     `AbstractMethodError` at runtime for any pre-existing implementer.
 *
 * Brand-new types containing abstract members produce only `+` lines (their
 * class header is also `+`) and are therefore correctly treated as additive
 * — there are no pre-existing implementers to break.
 *
 * Expected input: `git diff -U999999 …` so each hunk carries the full file's
 * structure and the enclosing class declaration is always visible to the
 * state machine.
 */
public object ApiStabilityClassifier {

    public fun classify(diff: String): Verdict {
        if (diff.isBlank()) return Verdict.NoChanges

        val reasons = mutableListOf<String>()

        val removed = collectRemovedDeclarations(diff)
        if (removed.isNotEmpty()) {
            reasons += "removed/changed declaration(s): ${removed.summarize()}"
        }

        val abstractAdded = collectAbstractMembersAddedToExistingTypes(diff)
        if (abstractAdded.isNotEmpty()) {
            reasons += "new abstract member(s) on existing type: ${abstractAdded.summarize()}"
        }

        return if (reasons.isEmpty()) Verdict.AdditionsOnly else Verdict.Breaking(reasons)
    }

    /**
     * Collects declarations the diff removed or altered, minus Exceptions A to D.
     *
     * Lines are bucketed per file (a `---` header starts a new file in both the
     * `git diff` and plain `diff -u` formats) so a genuine removal on one target
     * can never be cancelled out by a synthetic addition on a different target.
     */
    private fun collectRemovedDeclarations(diff: String): List<String> {
        val realRemovals = mutableListOf<String>()
        var added = mutableListOf<String>()
        var removed = mutableListOf<String>()
        var module = ""
        var inDumpHeader = false
        var removedTargetsHeader: String? = null
        var addedTargetsHeader: String? = null

        fun flushFile() {
            removedTargetsHeader?.let { header ->
                if (!header.isWidenedBy(addedTargetsHeader)) realRemovals += header
            }
            for (removedLine in removed) {
                if (removedLine.isInvisibleToConsumers(module)) continue
                val removedSignature = removedLine.stripSyntheticModifier()
                val syntheticOnly = added.any { addedLine ->
                    addedLine != removedLine && addedLine.stripSyntheticModifier() == removedSignature
                }
                if (!syntheticOnly) realRemovals += removedLine
            }
            added = mutableListOf()
            removed = mutableListOf()
            removedTargetsHeader = null
            addedTargetsHeader = null
        }

        for (line in diff.lineSequence()) {
            if (line.startsWith("---")) {
                flushFile()
                inDumpHeader = true
                moduleFromHeader(line)?.let { moduleName -> module = moduleName }
                continue
            }
            if (line.startsWith("+++")) {
                moduleFromHeader(line)?.let { moduleName -> module = moduleName }
                continue
            }
            val body = line.drop(n = 1)
                .trim()
            if (body.startsWith(KLIB_LIBRARY_NAME_PREFIX)) inDumpHeader = false
            val isTargetsHeader = inDumpHeader && body.startsWith(KLIB_TARGETS_PREFIX)
            when {
                line.startsWith("+") && isTargetsHeader -> addedTargetsHeader = body
                line.startsWith("-") && isTargetsHeader -> removedTargetsHeader = body
                line.startsWith("+") && body.isNotEmpty() -> added += body
                line.startsWith("-") && body.isNotEmpty() -> removed += body
            }
        }
        flushFile()
        return realRemovals
    }

    private fun String.isWidenedBy(addedHeader: String?): Boolean =
        addedHeader != null && addedHeader.klibTargets()
            .containsAll(elements = klibTargets())

    private fun String.klibTargets(): Set<String> =
        substringAfter(delimiter = '[')
            .substringBefore(delimiter = ']')
            .split(',')
            .map { target -> target.trim() }
            .toSet()

    private fun String.isInvisibleToConsumers(module: String): Boolean =
        this == "}" || isInternalMangled(module) || isComposableSingletonsHolder()

    private fun String.isComposableSingletonsHolder(): Boolean =
        composableSingletonsHolderRegex.matches(input = trim())

    /**
     * Pulls the module name out of a diff file-header line such as
     * `--- a/kmp/expressive/api/desktop/expressive.api` (→ `expressive`). The
     * regex keys on the `<module>/api/` segment, so it works whether the path is
     * repo-relative (`a/kmp/expressive/api/…`) or module-relative
     * (`a/expressive/api/…`). Returns null for headers with no such path (e.g.
     * `/dev/null`), leaving the previously seen module in place.
     */
    private fun moduleFromHeader(headerLine: String): String? =
        modulePathRegex.find(headerLine)?.groupValues?.get(1)

    /**
     * True when this `.api` line declares a member carrying Kotlin's internal
     * name-mangling suffix `$<module>` (Android variants append `_<variant>`, e.g.
     * `$expressive_release`). Such a member is `internal`: bytecode-public only so
     * the same module's own call sites can link, name-mangled so no other module
     * can resolve it, and absent from the `.klib.api` dump. Renaming or removing
     * one — e.g. a Compose `getLambda$<hash>$<module>` singleton re-hashing when a
     * surrounding `@Composable` gains a parameter — can never break an external
     * consumer, so it is not an ABI break.
     *
     * Only the trailing `$`-segment is compared to the module name, so the real
     * default-argument symbols (`copy$default`, `show$default`, …) are left
     * flagged: their suffix is `default`, never the module name.
     */
    private fun String.isInternalMangled(module: String): Boolean {
        if (module.isEmpty()) return false
        val name = jvmMemberNameRegex.find(trim())?.groupValues?.get(1)
            ?: return false
        val mangleSuffix = name.substringAfterLast(
            delimiter = '$',
            missingDelimiterValue = "",
        )
        return mangleSuffix == module || mangleSuffix.startsWith("${module}_")
    }

    /**
     * Removes the JVM `synthetic` modifier (only when it sits in front of `fun`
     * or `field`, so a declaration literally named `synthetic` is left alone) and
     * normalises whitespace. Two lines with the same result describe the same
     * binary descriptor and differ only in whether the symbol is hidden.
     */
    private fun String.stripSyntheticModifier(): String =
        replace(
            regex = syntheticModifierRegex,
            replacement = "",
        ).replace(
            regex = whitespaceRegex,
            replacement = " ",
        ).trim()

    private fun collectAbstractMembersAddedToExistingTypes(diff: String): List<String> {
        val results = mutableListOf<String>()
        var enclosingTypeIsNew: Boolean? = null
        var sawFileHeader = false

        for (rawLine in diff.lineSequence()) {
            when {
                rawLine.startsWith("diff --git") || rawLine.startsWith("index ") -> {
                    enclosingTypeIsNew = null
                    sawFileHeader = false
                }
                rawLine.startsWith("---") || rawLine.startsWith("+++") -> {
                    sawFileHeader = true
                }
                rawLine.startsWith("@@") -> {
                    enclosingTypeIsNew = null
                }
                else -> {
                    if (!sawFileHeader) continue
                    val (prefix, body) = rawLine.takeIfDiffLine()
                        ?: continue

                    val typeAddition = body.matchTypeDeclaration()
                    if (typeAddition != null) {
                        enclosingTypeIsNew = (prefix == '+')
                        continue
                    }

                    if (prefix == '+') {
                        val abstractName = body.matchAbstractMember()
                        if (abstractName != null && enclosingTypeIsNew == false) {
                            results += abstractName
                        }
                    }
                }
            }
        }
        return results
    }

    private fun String.takeIfDiffLine(): Pair<Char, String>? {
        if (isEmpty()) return null
        val c = this[0]
        return if (c == '+' || c == '-' || c == ' ') c to substring(1) else null
    }

    /**
     * Returns a non-null token if the body looks like a class/interface declaration in either
     * the JVM `.api` format (`public abstract interface class com/foo/Bar {`) or the klib format
     * (`abstract interface com.foo/Bar { …`).
     */
    private fun String.matchTypeDeclaration(): String? {
        val trimmed = trim()
        return jvmTypeDeclRegex.find(trimmed)?.value
            ?: klibTypeDeclRegex.find(trimmed)?.value
    }

    /**
     * Returns the declared member name if the body is an abstract `fun` / `val` / `var`
     * declaration in either format. Returns null otherwise (concrete methods, fields, etc).
     *
     * The klib format also uses `open fun` for default interface methods — those are NOT
     * abstract and must not match.
     */
    private fun String.matchAbstractMember(): String? {
        val trimmed = trim()
        return jvmAbstractMemberRegex.find(trimmed)?.groupValues?.get(1)
            ?: klibAbstractMemberRegex.find(trimmed)?.groupValues?.get(1)
    }

    private val jvmTypeDeclRegex = Regex(
        """^(?:public|protected|private)\s+(?:final\s+|abstract\s+|static\s+)*(?:interface|class|enum)\s+"""
    )

    private val klibTypeDeclRegex = Regex(
        """^(?:final\s+|abstract\s+|sealed\s+|open\s+|enum\s+)*(?:interface|class|object)\s+"""
    )

    private val syntheticModifierRegex = Regex("""synthetic (?=fun |field )""")

    private val whitespaceRegex = Regex("""\s+""")

    private val modulePathRegex = Regex("""(?:^|/)([\w.\-]+)/api/""")

    private val composableSingletonsHolderRegex = Regex(
        """^public (?:final class (?:\S+/)?ComposableSingletons\$\S+ \{|static final field INSTANCE L(?:\S+/)?ComposableSingletons\$\S+;)$"""
    )

    private const val KLIB_TARGETS_PREFIX = "// Targets:"

    private const val KLIB_LIBRARY_NAME_PREFIX = "// Library unique name:"

    private val jvmMemberNameRegex = Regex("""\b(?:fun|field)\s+([^\s(]+)""")

    private val jvmAbstractMemberRegex = Regex(
        """^public\s+abstract\s+(?:fun|field)\s+(\w+)"""
    )

    private val klibAbstractMemberRegex = Regex(
        """^abstract\s+(?:fun|val|var)\s+(\w+)"""
    )

    private fun List<String>.summarize(limit: Int = 5): String {
        if (size <= limit) return joinToString("; ")
        val head = take(limit)
            .joinToString("; ")
        return "$head; … (${size - limit} more)"
    }
}

public sealed interface Verdict {
    public object NoChanges : Verdict
    public object AdditionsOnly : Verdict
    public data class Breaking(val reasons: List<String>) : Verdict
}
