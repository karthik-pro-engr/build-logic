// build-logic/convention/src/main/kotlin/com/karthik/pro/engr/buildlogic/AndroidLibraryConventionPlugin.kt
package com.karthik.pro.engr

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply("com.android.library")
        project.pluginManager.apply("org.jetbrains.kotlin.android")

        project.extensions.configure<LibraryExtension>("android") {
            compileSdk = 36

            defaultConfig {
                minSdk = 21
                targetSdk = 36
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }

        val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
        project.dependencies.apply {
            // conservative defaults for libraries
            add("implementation", libs.findLibrary("androidx-core-ktx").get())
            libs.findLibrary("androidx-lifecycle-runtime-ktx").ifPresent { add("implementation", it.get()) }

            // Expose material/appcompat only if library needs them (we prefer implementation here)
            libs.findLibrary("androidx-appcompat").ifPresent { add("implementation", it.get()) }
            libs.findLibrary("material").ifPresent { add("implementation", it.get()) }

            // Compose support for libraries that are UI modules (optional)
            libs.findLibrary("androidx-activity-compose").ifPresent { add("implementation", it.get()) }
            libs.findLibrary("androidx-compose-bom").ifPresent { bom ->
                val platformDep = project.dependencies.platform(bom.get())
                add("implementation", platformDep)
                // Also add platform to androidTestImplementation so test compose libs resolve versions
                add("androidTestImplementation", platformDep)
                // Optionally add to testImplementation if you have JVM compose tests
                add("testImplementation", platformDep)
            }
            libs.findLibrary("androidx-ui").ifPresent { add("implementation", it.get()) }
            libs.findLibrary("androidx-ui-graphics").ifPresent { add("implementation", it.get()) }
            libs.findLibrary("androidx-ui-tooling-preview").ifPresent { add("implementation", it.get()) }
            libs.findLibrary("androidx-material3").ifPresent { add("implementation", it.get()) }

            // Test deps
            libs.findLibrary("junit").ifPresent { add("testImplementation", it.get()) }
        }
    }
}
