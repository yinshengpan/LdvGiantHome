plugins {
    alias(libs.plugins.ledvance.android.library)
}

android {
    namespace = "com.ledvance.domain"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}