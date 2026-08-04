import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

val appVersion = providers.gradleProperty("projectVersion").get()

val generateVersionInfo = tasks.register("generateVersionInfo") {
    val outputDir = layout.buildDirectory.dir("generated/versionInfo/kotlin")
    inputs.property("appVersion", appVersion)
    outputs.dir(outputDir)
    doLast {
        val packageDir = outputDir.get().dir("com/thelightphone/lp3keyboard/web")
        packageDir.asFile.mkdirs()
        packageDir.file("Version.kt").asFile.writeText(
            """
            package com.thelightphone.lp3keyboard.web

            const val APP_VERSION = "$appVersion"
            """.trimIndent()
        )
    }
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        wasmJsMain {
            kotlin.srcDir(generateVersionInfo.map { it.outputs.files.singleFile })
            dependencies {
                implementation(project(":ui"))
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
            }
        }
    }
}
