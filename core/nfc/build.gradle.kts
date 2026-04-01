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
    implementation(projects.core.domain)
    implementation(projects.sdk.nfc)
    implementation(libs.core.ktx)
    implementation(libs.commons.lang3)
    implementation(libs.startup.runtime)
}