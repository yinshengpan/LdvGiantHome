plugins {
    alias(libs.plugins.ledvance.android.library)
    alias(libs.plugins.ledvance.android.hilt)
}

android {
    namespace = "com.ledvance.domain"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}