@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
pluginManagement {
    includeBuild("build_logic")
    repositories {
        gradlePluginPortal()
        google()
    }
}

rootProject.name = "MagiskT"
include(":apk", ":core", ":shared", ":stub", ":test")
