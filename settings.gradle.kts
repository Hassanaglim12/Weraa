pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "com.android.application" || requested.id.id == "com.android.library") {
        if (org.gradle.util.GradleVersion.current() >= org.gradle.util.GradleVersion.version("9.0")) {
          useVersion("9.1.1")
        } else {
          useVersion("8.1.0")
        }
      }
    }
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "WarEra Companion"

include(":app")
