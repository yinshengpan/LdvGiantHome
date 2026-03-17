plugins {
    alias(libs.plugins.ledvance.android.library)
    alias(libs.plugins.ledvance.kotlinx.serialization)
    alias(libs.plugins.ledvance.android.hilt)
}

android {
    namespace = "com.ledvance.network"
}

dependencies {
    implementation(projects.core.utils)
    implementation(libs.timber)
    implementation(libs.okhttp)
    implementation(libs.okhttp.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
}