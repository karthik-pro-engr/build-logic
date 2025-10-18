plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.kotlin.jvm)
}
group = "karthik.pro.engr"
version = "1.3.0"

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

    }

    afterEvaluate {
        // names we want to normalize
        val mapping = mapOf(
            // implementation publications you created:
            "androidApplicationPlugin" to Pair("karthik.pro.engr", "android-application-plugin"),
            "androidLibraryPlugin" to Pair("karthik.pro.engr", "android-library-plugin"),
            // auto-generated plugin marker publications (override defaults)
            "androidApplicationPluginPluginMarkerMaven" to Pair("karthik.pro.engr", "android-application-plugin-marker"),
            "androidLibraryPluginPluginMarkerMaven" to Pair("karthik.pro.engr", "android-library-plugin-marker"),
            "pluginMaven" to Pair("karthik.pro.engr", "convention-plugins-marker") // pick a safe artifactId
        )

        mapping.forEach { (pubName, coords) ->
            publications.findByName(pubName)?.let { pub ->
                (pub as MavenPublication).apply {
                    groupId = coords.first
                    artifactId = coords.second
                    version = project.version.toString()
                    // set a minimal, safe pom so registries accept it:
                    pom {
                        name.set(artifactId)
                        description.set("Convention plugin artifact")
                        url.set("https://github.com/karthik-pro-engr/build-logic")
                        licenses {
                            license {
                                name.set("MIT")
                                url.set("https://opensource.org/licenses/MIT")
                            }
                        }
                        developers {
                            developer {
                                id.set("karthik-pro-engr")
                                name.set("Karthik")
                            }
                        }
                    }
                }
            }
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

tasks.register("printPublications") {
    doLast {
        publishing.publications.forEach { p ->
            println("publication: ${p.name} -> ${(p as? MavenPublication)?.artifactId} (group=${(p as? MavenPublication)?.groupId})")
        }
    }
}

// Use JUnit Platform for tests:
tasks.test {
    useJUnitPlatform()
}