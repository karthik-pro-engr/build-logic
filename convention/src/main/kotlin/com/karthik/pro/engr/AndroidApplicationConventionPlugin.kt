// build-logic/convention/src/main/kotlin/com/karthik/pro/engr/buildlogic/AndroidApplicationConventionPlugin.kt
package com.karthik.pro.engr

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // apply AGP & Kotlin Android plugin to consumer module
        project.pluginManager.apply("com.android.application")
        project.pluginManager.apply("org.jetbrains.kotlin.android")

        // Configure android { } via the typed DSL (modern API)
        project.extensions.configure<ApplicationExtension>("android") {
            compileSdk = 36

            defaultConfig {
                minSdk = 21
                targetSdk = 36
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            // Optional: common build types / proguard, flavors etc. can be added here
        }

        // Resolve libs from version catalog available to included build
        val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

        // Add conservative, commonly-used dependencies into the consumer module
        project.dependencies.apply {
            // core + lifecycle
            add("implementation", libs.findLibrary("androidx-core-ktx").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())

            // Activity Compose (implementation-only)
            libs.findLibrary("androidx-activity-compose").ifPresent {
                add("implementation", it.get())
            }

            // Compose BOM (apply as platform) if present
            libs.findLibrary("androidx-compose-bom").ifPresent { bom ->
                val platformDep = project.dependencies.platform(bom.get())
                add("implementation", platformDep)
                // Also add platform to androidTestImplementation so test compose libs resolve versions
                add("androidTestImplementation", platformDep)
                // Optionally add to testImplementation if you have JVM compose tests
                add("testImplementation", platformDep)
            }

            // Compose UI libs (implementation)
            libs.findLibrary("androidx-ui").ifPresent { add("implementation", it.get()) }
            libs.findLibrary("androidx-ui-graphics").ifPresent { add("implementation", it.get()) }
            libs.findLibrary("androidx-ui-tooling-preview").ifPresent { add("implementation", it.get()) }
            libs.findLibrary("androidx-material3").ifPresent { add("implementation", it.get()) }

            // ViewModel helpers
            libs.findLibrary("androidx-lifecycle-viewmodel-ktx").ifPresent { add("implementation", it.get()) }
            libs.findLibrary("androidx-lifecycle-viewmodel-compose").ifPresent { add("implementation", it.get()) }

            // AppCompat + Material (safe defaults)
            libs.findLibrary("androidx-appcompat").ifPresent { add("implementation", it.get()) }
            libs.findLibrary("material").ifPresent { add("implementation", it.get()) }

            // Test deps
            libs.findLibrary("junit").ifPresent { add("testImplementation", it.get()) }
            libs.findLibrary("androidx-junit").ifPresent { add("androidTestImplementation", it.get()) }
            libs.findLibrary("androidx-espresso-core").ifPresent { add("androidTestImplementation", it.get()) }

            // Compose test helpers
            libs.findLibrary("androidx-ui-test-junit4").ifPresent { add("androidTestImplementation", it.get()) }
            libs.findLibrary("androidx-ui-test-manifest").ifPresent { add("debugImplementation", it.get()) }
            libs.findLibrary("androidx-ui-tooling").ifPresent { add("debugImplementation", it.get()) }
        }
    }
}
