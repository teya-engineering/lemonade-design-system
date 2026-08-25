import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("lemonade-lint")
}

kotlin {
    jvmToolchain(17)

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("docs")
        browser {
            commonWebpackConfig {
                outputFileName = "docs.js"
            }
        }
        binaries.executable()
    }

    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(projects.ui)
            implementation(projects.expressive)
        }

        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }

        val desktopTest by getting
        desktopTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.teya.lemonade.docs.MainKt"
    }
}
