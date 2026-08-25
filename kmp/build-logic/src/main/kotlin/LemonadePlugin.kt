import com.android.build.gradle.LibraryExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishBasePlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class LemonadePlugin : Plugin<Project> {

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
                apply("com.android.library")
            }

            configureKotlinTargets()
            configureAndroid()
            configureDependencyAllowlist()

            pluginManager.apply(MavenPublishBasePlugin::class.java)

            val extension = extensions
                .create("lemonadePublishing", LemonadePublishingPluginExtension::class.java)

            afterEvaluate {
                val artifactId = extension.artifactId.orNull ?: return@afterEvaluate
                configurePublishing(artifactId)
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)
    private fun Project.configureKotlinTargets() {
        extensions.configure<KotlinMultiplatformExtension> {
            jvmToolchain(17)
            explicitApi()

            androidTarget {
                publishLibraryVariants("release")
            }

            iosArm64()
            iosSimulatorArm64()

            jvm("desktop")

            wasmJs {
                browser {
                    testTask {
                        enabled = false
                    }
                }
            }

            applyDefaultHierarchyTemplate {
                common {
                    group("mobile") {
                        withAndroidTarget()
                        withIosArm64()
                        withIosSimulatorArm64()
                    }
                }
            }
        }
    }

    private fun Project.configureAndroid() {
        extensions.configure<LibraryExtension> {
            compileSdk = findVersionInt("android-compileSdk")

            defaultConfig {
                testOptions.targetSdk = findVersionInt("android-targetSdk")
                minSdk = findVersionInt("android-minLibSdk")
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
    }

    private fun Project.configureDependencyAllowlist() {
        val allowlist = file("dependencies.allowlist")
        if (!allowlist.exists()) {
            return
        }
        val task = tasks.register<CheckDependencyAllowlistTask>("checkDependencyAllowlist") {
            buildScriptFile.set(file("build.gradle.kts"))
            allowlistFile.set(allowlist)
            markerFile.set(layout.buildDirectory.file("dep-allowlist/ok"))
        }
        tasks.named("check").configure {
            dependsOn(task)
        }
    }

    private fun Project.configurePublishing(artifactId: String) {
        extensions.configure<MavenPublishBaseExtension> {
            @Suppress("UnstableApiUsage")
            configureBasedOnAppliedPlugins(sourcesJar = true, javadocJar = true)
        }

        extensions.configure<MavenPublishBaseExtension> {
            val version: String = findEnvironmentVariable("PUBLICATION_VERSION")
            coordinates(
                groupId = "com.teya.foundation",
                artifactId = artifactId,
                version = version
            )

            publishToMavenCentral(true)
            if (hasSigningKey()) {
                signAllPublications()
            }

            pom()
        }
    }

    private fun MavenPublishBaseExtension.pom() {
        pom {
            name.set("Teya Lemonade Design System")
            description.set("Compose Multiplatform implementation of Teya's lemonade design system")
            inceptionYear.set("2026")
            url.set("https://teya.com")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("teya")
                    name.set("Teya")
                    url.set("https://teya.com")
                    email.set("terminal-team@teya.com")
                }
            }
            scm {
                url.set("https://github.com/saltpay/lemonade-design-system/")
                connection.set("scm:git:git://github.com/saltpay/lemonade-design-system.git")
                developerConnection.set("scm:git:ssh://git@github.com/saltpay/lemonade-design-system.git")
            }
        }
    }
}

private fun hasSigningKey(): Boolean {
    val hasSigningKey = !System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey").isNullOrEmpty()
    return hasSigningKey
}

private inline fun <reified T> Project.findEnvironmentVariable(variable: String): T {
    val envValue = System.getenv(variable)
    if (envValue != null) {
        return envValue as T
    }

    val gradleProperty = findProperty(variable)
    if (gradleProperty != null) {
        return gradleProperty as T
    }

    error("$variable not found in environment variables or Gradle properties")
}

private fun Project.findVersionInt(name: String): Int {
    val catalog = extensions.getByType(
        org.gradle.api.artifacts.VersionCatalogsExtension::class.java
    ).named("libs")
    return catalog.findVersion(name).get().requiredVersion.toInt()
}

abstract class LemonadePublishingPluginExtension {
    abstract val artifactId: Property<String>
}
