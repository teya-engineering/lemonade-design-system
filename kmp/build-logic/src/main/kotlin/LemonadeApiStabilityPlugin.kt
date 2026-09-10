import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

/**
 * Adds the `classifyApiDiff` Gradle task. Apply this once on the root project.
 *
 * CI usage:
 *   git diff -U999999 ... > diff.patch
 *   ./gradlew classifyApiDiff -PapiDiffFile=diff.patch -PapiVerdictFile=verdict.txt
 *
 * The task writes the verdict (`NO_CHANGES` / `ADDITIONS_ONLY` / `BREAKING`) to
 * the verdict file. Enforcing maintainer approval on a `BREAKING` verdict is
 * the CI workflow's job — see `.github/workflows/kmp_ci.yml`.
 */
public class LemonadeApiStabilityPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        // Capture only configuration-cache-safe inputs outside the task action.
        val diffPath = target.providers.gradleProperty("apiDiffFile")
        val verdictPath = target.providers.gradleProperty("apiVerdictFile")
            .orElse(
                target.layout.buildDirectory.file("api-verdict.txt")
                    .map { verdictLocation -> verdictLocation.asFile.absolutePath },
            )
        val rootDir = target.layout.projectDirectory

        target.tasks.register<ClassifyApiDiffTask>("classifyApiDiff") {
            diffFile.set(diffPath.map { path -> rootDir.file(path) })
            verdictFile.set(verdictPath.map { path -> rootDir.file(path) })
        }
    }
}
