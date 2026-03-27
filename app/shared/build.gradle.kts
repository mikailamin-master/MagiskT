plugins {
    alias(libs.plugins.android.library")
}

setupCommon()

android {
    namespace = "pro.magisk.shared"
    enableKotlin = false
}
