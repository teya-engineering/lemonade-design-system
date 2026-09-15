import com.android.build.gradle.LibraryExtension
import io.github.takahirom.roborazzi.GenerateComposePreviewRobolectricTestsTask
import io.github.takahirom.roborazzi.RoborazziExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

/**
 * Wires Roborazzi Compose `@Preview` screenshot testing onto a KMP `:ui`-style
 * module. Apply *after* the `lemonade` plugin so the `androidTarget` /
 * `androidUnitTest` source set already exists.
 *
 * Screenshot test dependencies are added here (not in the module's
 * build.gradle.kts) so the module's `dependencies.allowlist` stays limited to
 * shipped dependencies.
 */
class LemonadeScreenshotPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.github.takahirom.roborazzi")

            dependencies {
                add("androidUnitTestImplementation", lib("roborazzi"))
                add("androidUnitTestImplementation", lib("roborazzi-compose"))
                add(
                    "androidUnitTestImplementation",
                    lib("roborazzi-compose-preview-scanner-support"),
                )
                add("androidUnitTestImplementation", lib("composable-preview-scanner"))
                add("androidUnitTestImplementation", lib("robolectric"))
                add("androidUnitTestImplementation", lib("junit"))
                // Roborazzi's generated tests and JUnit4 lifecycle use
                // AndroidComposeTestRule (+ transitively ComponentActivity /
                // ActivityScenarioRule); roborazzi-compose declares these as
                // compileOnly so the consumer must provide them.
                add("androidUnitTestImplementation", lib("compose-ui-test-junit4"))
                // The tester paints the themed surface (Box + Modifier.background)
                // behind each preview; :ui exposes foundation only as
                // implementation, so the test source set needs it directly.
                add("androidUnitTestImplementation", lib("compose-foundation"))
            }

            extensions.configure<LibraryExtension> {
                testOptions {
                    unitTests.isIncludeAndroidResources = true
                    unitTests.isReturnDefaultValues = true
                    // Rendering all previews in one parameterized JVM is
                    // memory-heavy; without a larger heap the run OOMs.
                    unitTests.all { test ->
                        test.maxHeapSize = "4g"
                    }
                }
            }

            configureRoborazziPreviewGeneration()
            registerGeneratedTestSources()
        }
    }

    /**
     * Turns on Roborazzi's `@Preview` test generation. The generated parameterized
     * Robolectric test scans [PREVIEW_PACKAGE] (including private previews, since
     * Lemonade's previews are private) and routes each preview through our
     * [TESTER_CLASS], which injects [com.teya.lemonade.LemonadeTheme].
     *
     * Goldens are written to the in-tree `screenshots/` folder (not under
     * `build/`) so CI can commit them. Roborazzi propagates [outputDir] to the
     * test JVM's `roborazzi.output.dir` system property as a project-relative
     * path, which our [TESTER_CLASS] reads back when reconstructing golden paths
     * under the default `RelativePathFromCurrentDirectory` record strategy.
     */
    private fun Project.configureRoborazziPreviewGeneration() {
        extensions.configure<RoborazziExtension> {
            outputDir.set(layout.projectDirectory.dir(SCREENSHOTS_DIR))
            generateComposePreviewRobolectricTests.apply {
                enable.set(true)
                packages.set(listOf(PREVIEW_PACKAGE))
                includePrivatePreviews.set(true)
                testerQualifiedClassName.set(TESTER_CLASS)
                // Required alongside a custom tester: the generated tests hand these
                // scan options to the tester, whose delegate applies them.
                useScanOptionParametersInTester.set(true)
                robolectricConfig.set(mapOf("sdk" to ROBOLECTRIC_SDK))
            }
        }
    }

    /**
     * On a KMP `com.android.library` module Roborazzi registers its generated
     * sources on AGP's `sources.java`, which the Kotlin compilation does not pick
     * up, so generation is effectively a no-op. Register the generate task's
     * output on the `androidUnitTest` Kotlin source set instead, and wire the
     * task as a dependency so it runs before that source set is compiled.
     */
    private fun Project.registerGeneratedTestSources() {
        val generateTasks = tasks.withType<GenerateComposePreviewRobolectricTestsTask>()
        extensions.configure<KotlinProjectExtension> {
            sourceSets
                .getByName("androidUnitTest")
                .kotlin
                .srcDir(generateTasks)
        }
    }
}

private const val PREVIEW_PACKAGE = "com.teya.lemonade"
private const val TESTER_CLASS =
    "com.teya.lemonade.screenshot.LemonadeComposePreviewTester"

// Module-relative directory the goldens are recorded into and verified from.
// Matches teya's convention: `<module>/src/androidMain/screenshots/`.
private const val SCREENSHOTS_DIR = "src/androidMain/screenshots"

// The generated `@Config(sdk = ...)` annotation takes this literal; pinning it
// keeps goldens deterministic when Robolectric's default SDK moves.
private const val ROBOLECTRIC_SDK = "[36]"

private fun Project.lib(alias: String): Provider<MinimalExternalModuleDependency> {
    val catalog = extensions
        .getByType(VersionCatalogsExtension::class.java)
        .named("libs")
    return catalog.findLibrary(alias).get()
}
