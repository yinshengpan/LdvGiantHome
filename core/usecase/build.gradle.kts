plugins {
    alias(libs.plugins.ledvance.android.library)
    alias(libs.plugins.ledvance.android.hilt)
    alias(libs.plugins.ledvance.kotlinx.serialization)
}

android {
    namespace = "com.ledvance.usecase"
}

dependencies {
    implementation(projects.core.ble)
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.core.utils)

    implementation(libs.timber)
}