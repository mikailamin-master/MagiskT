plugins {
    id("com.android.application")
    id("org.lsposed.lsparanoid")
}

lsparanoid {
    seed = if (RAND_SEED != 0) RAND_SEED else null
    includeDependencies = true
    classFilter = { true }
}

android {
    namespace = "pro.magisk"

    val canary = !Config.version.contains(".")
    val base = "https://github.com/mikailamin-master/MagiskPro/releases/download/"
    val urlRelease = base + "build/app-release.apk"
    val urlDebug = base + "build/app-debug.apk"
    val canaryUrl = base + "canary_build/"

    defaultConfig {
        applicationId = "pro.magisk"
        versionCode = 1
        versionName = "1.0"

        buildConfigField("int", "STUB_VERSION", Config.stubVersion)
    }

    buildTypes {
        release {
            val apkUrl = if (canary) {
                "${canaryUrl}app-release.apk"
            } else {
                urlRelease
            }

            buildConfigField("String", "APK_URL", "\"$apkUrl\"")

            proguardFiles("proguard-rules.pro")
            isMinifyEnabled = true
            isShrinkResources = false
        }

        debug {
            val apkUrl = if (canary) {
                "${canaryUrl}app-debug.apk"
            } else {
                urlDebug
            }

            buildConfigField("String", "APK_URL", "\"$apkUrl\"")
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