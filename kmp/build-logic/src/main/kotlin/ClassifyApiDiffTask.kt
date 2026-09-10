import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Reads a unified diff of `.api` / `.klib.api` files, runs [ApiStabilityClassifier],
 * and writes a machine-readable verdict to [verdictFile]. First line is one of:
 * `NO_CHANGES`, `ADDITIONS_ONLY`, `BREAKING`. For `BREAKING`, subsequent indented
 * lines list the reasons.
 *
 * Designed to be called from CI after running `git diff -U999999` against
 * `origin/<base>...HEAD` over the BCV-managed `api` directories and piping the
 * result into a file.
 *
 * Enforcement — failing the build on `BREAKING`, requiring a named maintainer
 * approval — is intentionally *not* here. It belongs in the CI workflow where
 * the GitHub Pull Request context (approvers, head SHA) is available.
 */
public abstract class ClassifyApiDiffTask : DefaultTask() {

    @get:InputFile
    public abstract val diffFile: RegularFileProperty

    @get:OutputFile
    public abstract val verdictFile: RegularFileProperty

    init {
        group = "verification"
        description = "Classifies a BCV .api/.klib.api diff as NoChanges/AdditionsOnly/Breaking."
    }

    @TaskAction
    public fun run() {
        val diffText = diffFile.get().asFile.readText()
        val verdict = ApiStabilityClassifier.classify(diffText)

        val token = when (verdict) {
            Verdict.NoChanges -> "NO_CHANGES"
            Verdict.AdditionsOnly -> "ADDITIONS_ONLY"
            is Verdict.Breaking -> "BREAKING"
        }

        val body = buildString {
            appendLine(token)
            if (verdict is Verdict.Breaking) {
                verdict.reasons.forEach { reason -> appendLine("  - $reason") }
            }
        }
        verdictFile.get().asFile
            .apply { parentFile.mkdirs() }
            .writeText(body)

        when (verdict) {
            Verdict.NoChanges ->
                logger.lifecycle("API stability: no public API changes detected.")
            Verdict.AdditionsOnly ->
                logger.lifecycle("API stability: additions-only — no maintainer approval required.")
            is Verdict.Breaking -> {
                logger.warn("API stability: BREAKING change detected:")
                verdict.reasons.forEach { reason -> logger.warn("  - $reason") }
            }
        }
    }
}
