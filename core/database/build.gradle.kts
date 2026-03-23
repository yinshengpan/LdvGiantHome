plugins {
    alias(libs.plugins.ledvance.android.library)
    alias(libs.plugins.ledvance.android.room)
    alias(libs.plugins.ledvance.android.hilt)
    alias(libs.plugins.ledvance.kotlinx.serialization)
}

android {
    namespace = "com.ledvance.database"
}

dependencies {
    implementation(projects.core.utils)
    implementation(projects.core.domain)
    implementation(libs.timber)
}