plugins {
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("lemonade")
    id("lemonade-lint")
}

lemonadePublishing {
    artifactId = "lemonade-calendar"
}

android {
    namespace = "com.teya.lemonade.extensions.calendar"
}

kotlin {
    sourceSets.all {
        languageSettings.optIn("com.teya.lemonade.InternalLemonadeApi")
    }
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            api(libs.kotlinx.datetime)
            api(projects.ui)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core)
        }
    }
}
