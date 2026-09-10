import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

class LemonadeLintPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("io.gitlab.arturbosch.detekt")
                apply("org.jlleitschuh.gradle.ktlint")
            }

            configureDetekt()
            configureKtlint()
        }
    }
}

private fun Project.configureDetekt() {
    extensions.configure<DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(files("${rootDir}/config/detekt/detekt.yml"))
        baseline = file("${rootDir}/config/detekt/baseline.xml")
        parallel = true
    }

    tasks.withType<Detekt>()
        .configureEach {
            exclude { element -> element.file.absolutePath.contains("/build/") }
        }
}

private fun Project.configureKtlint() {
    extensions.configure<KtlintExtension> {
        android.set(true)
        verbose.set(true)
        outputToConsole.set(true)
    }

    tasks.withType<BaseKtLintCheckTask>()
        .configureEach {
            exclude { element -> element.file.absolutePath.contains("/build/") }
        }
}
