plugins {
    alias(libs.plugins.ledvance.android.library)
    alias(libs.plugins.ledvance.android.hilt)
    alias(libs.plugins.ledvance.kotlinx.serialization)
}

android {
    namespace = "com.ledvance.core.ota"
}

dependencies {
    implementation(projects.sdk.giantOta)
    implementation(projects.core.utils)
    implementation(projects.core.domain)
    implementation(libs.timber)
    implementation(libs.core.ktx)
}