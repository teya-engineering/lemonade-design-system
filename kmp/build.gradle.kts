plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.binaryCompatibilityValidator)
    id("lemonade-api-stability")
}

apiValidation {
    // Validate only the publishable modules. composeApp is the sample app and
    // docs is the documentation site; neither is published, so their API
    // surface is irrelevant for consumers.
    ignoredProjects += listOf("composeApp", "docs")

    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        // Enables Kotlin/Native (and other non-JVM) ABI validation in addition
        // to the JVM/Android API dump. Without this, additions/removals on
        // iOS-only or common-only declarations would slip past CI.
        enabled = true
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/baseline.xml")
    parallel = true
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.compose.rules.detekt)
}
