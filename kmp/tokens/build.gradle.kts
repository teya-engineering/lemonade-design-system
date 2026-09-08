plugins {
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("lemonade")
    id("lemonade-lint")
}

lemonadePublishing {
    artifactId = "lemonade-tokens"
}

android {
    namespace = "com.teya.lemonade.tokens"
}

kotlin {
    sourceSets.all {
        // The themed colour layer is experimental for consumers; the module declares it.
        languageSettings.optIn("com.teya.lemonade.ExperimentalLemonadeApi")
    }
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            api(projects.core)
        }
    }
}
