plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gradle.agp)
    implementation(libs.gradle.vanniktech.publish)
    implementation(libs.gradle.kmp)
    implementation(libs.detekt.gradlePlugin)
    implementation(libs.ktlint.gradlePlugin)
    implementation(libs.gradle.roborazzi)

    testImplementation(kotlin("test"))
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        register("LemonadePlugin") {
            id = "lemonade"
            implementationClass = "LemonadePlugin"
        }
        register("LemonadeLintPlugin") {
            id = "lemonade-lint"
            implementationClass = "LemonadeLintPlugin"
        }
        register("LemonadeApiStabilityPlugin") {
            id = "lemonade-api-stability"
            implementationClass = "LemonadeApiStabilityPlugin"
        }
        register("LemonadeScreenshotPlugin") {
            id = "lemonade-screenshot"
            implementationClass = "LemonadeScreenshotPlugin"
        }
    }
}