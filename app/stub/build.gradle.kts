plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.lsparanoid)
}

lsparanoid {
    seed = if (RAND_SEED != 0) RAND_SEED else null
    includeDependencies = true
    classFilter = { true }
}

android {
    namespace = "pro.magisk"

    val canary = Config.version.contains(":T")
    val base = "https://github.com/mikailamin-master/MagiskT/releases/download/"
    val url = base + "build/app-release.apk"
    val canaryUrl = base + "test_build/"

    defaultConfig {
        applicationId = "pro.magisk"
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "APK_URL", "\"$url\"")
        buildConfigField("int", "STUB_VERSION", Config.stubVersion)
    }

    buildTypes {
        release {
            if (canary) buildConfigField("String", "APK_URL", "\"${canaryUrl}app-release.apk\"")
            proguardFiles("proguard-rules.pro")
            isMinifyEnabled = true
            isShrinkResources = false
        }
        debug {
            if (canary) buildConfigField("String", "APK_URL", "\"${canaryUrl}app-debug.apk\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

setupStubApk()

dependencies {
    implementation(project(":shared"))
}
