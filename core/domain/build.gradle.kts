plugins {
    alias(libs.plugins.ledvance.android.library)
    alias(libs.plugins.ledvance.android.hilt)
    alias(libs.plugins.ledvance.kotlinx.serialization)
}

android {
    namespace = "com.ledvance.domain"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}