import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Verifies that the declared dependencies in a module's build.gradle.kts exactly match
 * the entries listed in its dependencies.allowlist file. Fails the build with a detailed
 * message describing any added or removed dependencies.
 *
 * The check intentionally operates on the build script source (not Gradle's resolved
 * dependency graph) so contributors get a 1:1 correspondence between the lines they edit
 * and the allowlist they must update in the same PR.
 *
 * Limitation: only single-line dependency declarations are detected. Declarations split
 * across multiple lines (e.g. `implementation(\n    libs.foo\n)`) will not be matched and
 * will cause the check to fail, prompting the contributor to reformat onto one line.
 */
@CacheableTask
abstract class CheckDependencyAllowlistTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val buildScriptFile: RegularFileProperty

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val allowlistFile: RegularFileProperty

    @get:OutputFile
    abstract val markerFile: RegularFileProperty

    init {
        group = "verification"
        description =
            "Verifies declared dependencies in build.gradle.kts match dependencies.allowlist."
    }

    @TaskAction
    fun check() {
        val buildScript = buildScriptFile.get().asFile
        val allowlist = allowlistFile.get().asFile

        val declared = parseDeclaredDependencies(buildScript = buildScript)
        val allowed = parseAllowlist(allowlistFile = allowlist)

        val added = declared - allowed
        val removed = allowed - declared

        if (added.isEmpty() && removed.isEmpty()) {
            writeMarker()
            return
        }

        throw GradleException(
            buildErrorMessage(
                projectPath = project.path,
                allowlistPath = allowlist,
                added = added,
                removed = removed,
            ),
        )
    }

    private fun writeMarker() {
        val marker = markerFile.get().asFile
        marker.parentFile.mkdirs()
        marker.writeText("ok\n")
    }
}

private val CONFIG_KEYWORDS: List<String> = listOf(
    "debugImplementation",
    "releaseImplementation",
    "androidTestImplementation",
    "testImplementation",
    "debugApi",
    "releaseApi",
    "implementation",
    "compileOnly",
    "runtimeOnly",
    "api",
)

private fun parseDeclaredDependencies(buildScript: File): Set<String> {
    val results = mutableSetOf<String>()
    buildScript.forEachLine { rawLine ->
        val parsed = parseDependencyLine(rawLine = rawLine)
        if (parsed != null) {
            results.add(parsed)
        }
    }
    return results
}

private fun parseDependencyLine(rawLine: String): String? {
    val withoutComment = stripLineComment(line = rawLine)
    val trimmed = withoutComment.trim()
    if (trimmed.isEmpty()) {
        return null
    }
    val keyword = CONFIG_KEYWORDS.firstOrNull { candidate ->
        matchesKeyword(
            text = trimmed,
            keyword = candidate,
        )
    }
    if (keyword == null) {
        return null
    }
    val rest = trimmed
        .substring(startIndex = keyword.length)
        .trimStart()
    if (!rest.startsWith("(")) {
        return null
    }
    val expression = extractParenExpression(text = rest)
        ?: return null
    return "$keyword $expression"
}

private fun stripLineComment(line: String): String {
    val commentIdx = line.indexOf("//")
    if (commentIdx < 0) {
        return line
    }
    return line.take(commentIdx)
}

private fun matchesKeyword(
    text: String,
    keyword: String,
): Boolean {
    if (!text.startsWith(keyword)) {
        return false
    }
    if (text.length == keyword.length) {
        return false
    }
    val next = text[keyword.length]
    return !next.isLetterOrDigit() && next != '_'
}

private fun extractParenExpression(text: String): String? {
    var depth = 0
    var endIdx = -1
    for (i in text.indices) {
        val c = text[i]
        if (c == '(') {
            depth++
        } else if (c == ')') {
            depth--
            if (depth == 0) {
                endIdx = i
                break
            }
        }
    }
    if (endIdx == -1) {
        return null
    }
    return text
        .substring(
            startIndex = 1,
            endIndex = endIdx,
        )
        .trim()
}

private fun parseAllowlist(allowlistFile: File): Set<String> {
    val entries = mutableSetOf<String>()
    allowlistFile.forEachLine { rawLine ->
        val withoutComment = stripHashComment(line = rawLine)
        val trimmed = withoutComment.trim()
        if (trimmed.isNotEmpty()) {
            val normalized = trimmed
                .split(Regex(pattern = "\\s+"))
                .joinToString(separator = " ")
            entries.add(normalized)
        }
    }
    return entries
}

private fun stripHashComment(line: String): String {
    val commentIdx = line.indexOf('#')
    if (commentIdx < 0) {
        return line
    }
    return line.substring(
        startIndex = 0,
        endIndex = commentIdx,
    )
}

private fun buildErrorMessage(
    projectPath: String,
    allowlistPath: File,
    added: Set<String>,
    removed: Set<String>,
): String {
    val builder = StringBuilder()
    builder.appendLine()
    builder.appendLine("Dependencies in $projectPath no longer match the allowlist.")
    builder.appendLine()
    if (added.isNotEmpty()) {
        builder.appendLine("  Added (need approval):")
        added
            .sorted()
            .forEach { entry ->
                builder.appendLine("    + $entry")
            }
        builder.appendLine()
    }
    if (removed.isNotEmpty()) {
        builder.appendLine("  Removed (orphaned in allowlist):")
        removed
            .sorted()
            .forEach { entry ->
                builder.appendLine("    - $entry")
            }
        builder.appendLine()
    }
    builder.appendLine(
        value = ":core, :tokens, and :ui are kept lean — every dependency added to these",
    )
    builder.appendLine("modules ships to consumers and inflates the public API surface.")
    builder.appendLine()
    builder.appendLine("If this change is intentional, edit:")
    builder.appendLine("  $allowlistPath")
    builder.appendLine()
    builder.append("Then commit the file in the same PR. Reviewers will see the diff.")
    return builder.toString()
}
