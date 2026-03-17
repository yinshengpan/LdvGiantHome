plugins {
    alias(libs.plugins.ledvance.android.library)
    alias(libs.plugins.ledvance.kotlinx.serialization)
    alias(libs.plugins.ledvance.android.hilt)
}

android {
    namespace = "com.ledvance.nfc"
}

dependencies {
    implementation(projects.core.utils)
    implementation(projects.aars.nfc)
    implementation(libs.timber)
    implementation(libs.core.ktx)
    implementation(libs.commons.lang3)
    implementation(libs.startup.runtime)
}