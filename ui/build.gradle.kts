import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
    `maven-publish`
}

// for publishing
val uiVersion = providers.gradleProperty("projectVersion").get()

group = "com.thelightphone.lp3keyboard"
version = uiVersion

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        // Keeps the pre-KMP artifactId ("ui") for the artifact existing consumers actually
        // depend on; without this it defaults to "ui-android" and collides with the root
        // "kotlinMultiplatform" metadata publication renamed below.
        mavenPublication {
            artifactId = "ui"
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.jetbrains.lifecycle.viewmodel)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.material)
            implementation(libs.androidx.activity.compose)
            implementation("androidx.compose.ui:ui-tooling-preview:1.8.0")
            implementation("androidx.compose.ui:ui-tooling:1.8.0")
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(libs.mockk)
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.androidx.junit)
                implementation(libs.androidx.espresso.core)
            }
        }
    }
}

// Akkurat is license-restricted, so it's never committed — devs who have a license drop the
// real files in localFonts/ (gitignored). Compose Resources needs font files to physically
// exist at build time to generate their Res.font.* accessors, so we can't just skip a missing
// file at runtime like the Android target does. Instead this task always populates a fixed set
// of filenames from whichever source is available, preferring the real Akkurat files over the
// bundled Noto Sans fallback, so the build never breaks for devs without the license.
abstract class ResolvePrimaryLatinFontTask : DefaultTask() {
    @get:InputFiles
    @get:org.gradle.api.tasks.Optional
    abstract val akkuratLight: RegularFileProperty

    @get:InputFiles
    @get:org.gradle.api.tasks.Optional
    abstract val akkuratRegular: RegularFileProperty

    @get:InputFiles
    @get:org.gradle.api.tasks.Optional
    abstract val akkuratBold: RegularFileProperty

    @get:InputFile
    abstract val fallbackLight: RegularFileProperty

    @get:InputFile
    abstract val fallbackRegular: RegularFileProperty

    @get:InputFile
    abstract val fallbackBold: RegularFileProperty

    // Fonts with no license restriction, always copied through as-is (Compose Resources needs
    // every font in the *same* resolved directory as the conditional Akkurat/Noto ones below,
    // since customDirectory replaces rather than merges with a source set's own composeResources).
    @get:InputFile
    abstract val notoNaskhArabic: RegularFileProperty

    @get:InputFile
    abstract val notoEmojiMono: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun run() {
        val fontDir = outputDir.get().dir("font").asFile
        project.delete(fontDir)
        fontDir.mkdirs()

        fun resolve(akkurat: RegularFileProperty, fallback: RegularFileProperty, destName: String) {
            val source = akkurat.asFile.orNull?.takeIf { it.exists() } ?: fallback.get().asFile
            source.copyTo(fontDir.resolve(destName + "." + source.extension))
        }

        resolve(akkuratLight, fallbackLight, "primary_latin_light")
        resolve(akkuratRegular, fallbackRegular, "primary_latin_regular")
        resolve(akkuratBold, fallbackBold, "primary_latin_bold")
        notoNaskhArabic.get().asFile.copyTo(fontDir.resolve("noto_naskh_arabic.ttf"))
        notoEmojiMono.get().asFile.copyTo(fontDir.resolve("noto_emoji_mono.ttf"))
    }
}

val resolvePrimaryLatinFont = tasks.register<ResolvePrimaryLatinFontTask>("resolvePrimaryLatinFont") {
    akkuratLight.set(layout.projectDirectory.file("src/wasmJsMain/localFonts/akkuratll_light.ttf"))
    akkuratRegular.set(layout.projectDirectory.file("src/wasmJsMain/localFonts/akkuratll_regular.ttf"))
    akkuratBold.set(layout.projectDirectory.file("src/wasmJsMain/localFonts/akkuratpro_bold.otf"))
    fallbackLight.set(layout.projectDirectory.file("src/wasmJsMain/fallbackFont/NotoSans-Light.ttf"))
    fallbackRegular.set(layout.projectDirectory.file("src/wasmJsMain/fallbackFont/NotoSans-Regular.ttf"))
    fallbackBold.set(layout.projectDirectory.file("src/wasmJsMain/fallbackFont/NotoSans-Bold.ttf"))
    notoNaskhArabic.set(layout.projectDirectory.file("src/wasmJsMain/bundledFont/noto_naskh_arabic.ttf"))
    notoEmojiMono.set(layout.projectDirectory.file("src/wasmJsMain/bundledFont/noto_emoji_mono.ttf"))
    outputDir.set(layout.buildDirectory.dir("generated/primaryLatinFont"))
    outputs.upToDateWhen { false }
}

compose.resources {
    packageOfResClass = "com.thelightphone.lp3Keyboard.ui"
    publicResClass = true
    customDirectory(
        sourceSetName = "wasmJsMain",
        directoryProvider = resolvePrimaryLatinFont.map { it.outputDir.get() }
    )
}

android {
    namespace = "com.thelightphone.lp3Keyboard.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) }
}

publishing {
    // TODO signing config
    // The Kotlin Multiplatform + maven-publish combo auto-registers a root "kotlinMultiplatform"
    // metadata publication alongside the per-target ones; its default artifactId is also "ui",
    // which collides with the Android target's (pinned to "ui" above for existing consumers).
    publications.named<MavenPublication>("kotlinMultiplatform") {
        artifactId = "ui-common"
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/lightphone/light-keyboard")
            credentials {
                username = localProperties.getProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
                password = localProperties.getProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

// Suppress Gradle module metadata so consumers use the POM exclusively.
tasks.withType<GenerateModuleMetadata> {
    enabled = false
}
