plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
}
group = "karthik.pro.engr"
version = "1.1.0"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Gradle API
    compileOnly(gradleApi())

    // AGP for compile-time types (compileOnly so AGP is not bundled)
    compileOnly(libs.gradle) // your libs alias for com.android.tools.build:gradle

    // Kotlin Gradle plugin types (compileOnly)
    compileOnly(libs.kotlin.gradle.plugin) // your libs alias for org.jetbrains.kotlin:kotlin-gradle-plugin

    // TestKit and test runtime
    testImplementation(gradleTestKit()) // Gradle TestKit
    testImplementation(libs.junit.jupiter.api) // JUnit API
    testRuntimeOnly(libs.junit.jupiter.engine) // JUnit engine to run tests
    testImplementation(libs.assertj)
    implementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        create("androidApplicationPlugin") {
            id = "karthik.pro.engr.android.application"
            implementationClass = "com.karthik.pro.engr.AndroidApplicationConventionPlugin"
        }
        create("androidLibraryPlugin") {
            id = "karthik.pro.engr.android.library"
            implementationClass = "com.karthik.pro.engr.AndroidLibraryConventionPlugin"
        }
    }
}

publishing {
    // minimal explicit publication for the plugin implementation jar
    publications {
        create<MavenPublication>("androidApplicationPlugin") {
            from(components["kotlin"])
            groupId = "karthik.pro.engr"
            artifactId = "android-application-plugin"    // explicit artifact for app plugin
            version = project.version.toString()
        }

        create<MavenPublication>("androidLibraryPlugin") {
            from(components["kotlin"])
            groupId = "karthik.pro.engr"
            artifactId = "android-library-plugin"        // explicit artifact for library plugin
            version = project.version.toString()
        }

        // optional: keep convention-plugins for a combined bundle
        create<MavenPublication>("conventionBundle") {
            from(components["java"])
            groupId = "karthik.pro.engr"
            artifactId = "convention-plugins"
            version = project.version.toString()
        }
    }

    publications.withType<MavenPublication>().configureEach {
        groupId = "karthik.pro.engr"
        artifactId = when (name) {
            "pluginMaven" -> "android.application.gradle.plugin"
            "androidApplicationPluginPluginMarkerMaven" -> "android-application-plugin-marker"
            "androidLibraryPluginPluginMarkerMaven" -> "android-library-plugin-marker"
            "mavenJava" -> "convention-plugins"
            else -> name.replace("Publication", "").lowercase().replace("[^a-z0-9.-]".toRegex(), "-")
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/karthik-pro-engr/build-logic")
            credentials {
                username = findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
                password = findProperty("gpr.token") as String? ?: System.getenv("GPR_TOKEN")
            }
        }
    }
}

// Use JUnit Platform for tests:
tasks.test {
    useJUnitPlatform()
}