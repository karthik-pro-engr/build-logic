# Build Logic

Reusable Gradle Convention Plugins for standardizing Android application and Android library build configuration across projects.

---

## Why This Exists

Android projects often repeat the same Gradle configuration across multiple modules.

Over time, duplicated configuration can lead to:

- Repetitive `build.gradle.kts` files
- Configuration drift between modules
- Repeated dependency declarations
- More effort when shared build configuration changes

This repository centralizes common build conventions into reusable Gradle plugins.

The goal is:

```text
Define common build configuration once
              ↓
Reuse it across Android modules
              ↓
Keep configuration consistent
```

---

## Convention Plugins

The repository currently provides two convention plugins.

### Android Application

```text
karthik.pro.engr.android.application
```

The application convention plugin centralizes common Android application configuration such as:

- Android application plugin
- Kotlin Android plugin
- SDK configuration
- Java 17 compatibility
- Common AndroidX dependencies
- Lifecycle dependencies
- Compose support
- ViewModel dependencies
- Compose testing dependencies
- All Variants Preview dependency

---

### Android Library

```text
karthik.pro.engr.android.library
```

The library convention plugin provides shared configuration for Android library modules, including:

- Android library plugin
- Kotlin Android plugin
- SDK configuration
- Java 17 compatibility
- AndroidX dependencies
- Compose support
- ViewModel dependencies
- Common test dependencies

---

## Consumer Model

A consuming Android project can apply a convention plugin instead of repeating common configuration in every module.

Example:

```kotlin
plugins {
    id("karthik.pro.engr.android.application")
}
```

The consuming project can then inherit the shared build conventions defined by this repository.

---

## Example Architecture

Without convention plugins:

```text
app/build.gradle.kts
    ├── Android configuration
    ├── Kotlin configuration
    ├── Compose configuration
    ├── Common dependencies
    └── Test configuration

feature-a/build.gradle.kts
    ├── Android configuration
    ├── Kotlin configuration
    ├── Compose configuration
    ├── Common dependencies
    └── Test configuration

feature-b/build.gradle.kts
    ├── Android configuration
    ├── Kotlin configuration
    ├── Compose configuration
    ├── Common dependencies
    └── Test configuration
```

With convention plugins:

```text
                    ┌─────────────────────────┐
                    │    Convention Plugins   │
                    │                         │
                    │ Android Application     │
                    │ Android Library         │
                    └────────────┬────────────┘
                                 │
                ┌────────────────┼────────────────┐
                │                │                │
                ▼                ▼                ▼
              app            feature-a        feature-b
```

---

## Repository Structure

```text
build-logic/

├── convention/
│   ├── build.gradle.kts
│   │
│   └── src/main/kotlin/com/karthik/pro/engr/
│       ├── AndroidApplicationConventionPlugin.kt
│       └── AndroidLibraryConventionPlugin.kt
│
├── .github/
│   └── workflows/
│       ├── build-logic-ci.yml
│       └── publish-plugin.yml
│
├── gradle/
│   └── libs.versions.toml
│
└── settings.gradle.kts
```

---

## Publishing

The convention plugins are configured for Maven publication.

The repository publishes the convention artifacts and plugin marker artifacts so that consuming projects can resolve the plugins through a configured Maven repository.

GitHub Packages is used as the configured package repository.

---

## CI/CD

GitHub Actions validates the build logic repository.

### Pull Request Validation

The CI workflow runs:

```text
:convention:build
:convention:check
:convention:test
```

Pull requests also validate Maven publication using:

```text
:convention:publishToMavenLocal
```

This validates the local publication configuration without requiring a remote package publication.

### Protected Push / Tag

For protected pushes or tags, the workflow can publish the convention artifacts to GitHub Packages.

Build reports and generated artifacts are uploaded as CI artifacts.

---

## Testing

The convention module is included in the automated `build`, `check`, and `test` workflow.

The repository is structured so that the build logic can be validated independently from the consuming Android applications.

---

## Tech Stack

- Kotlin
- Gradle
- Gradle Kotlin DSL
- Android Gradle Plugin
- Gradle Convention Plugins
- JUnit
- AssertJ
- GitHub Actions
- GitHub Packages

---

## Engineering Goal

The primary engineering goal is to reduce duplicated Android build configuration and establish a reusable source of truth for common build conventions.

```text
Common configuration
        ↓
Convention Plugin
        ↓
Reusable across projects/modules
        ↓
Less duplication
        ↓
More consistent builds
```

---

## Status

Active build-engineering project used by Android repositories.
