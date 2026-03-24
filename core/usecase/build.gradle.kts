plugins {
    alias(libs.plugins.ledvance.android.library)
    alias(libs.plugins.ledvance.android.hilt)
    alias(libs.plugins.ledvance.kotlinx.serialization)
    alias(libs.plugins.ledvance.android.licenses)
}

android {
    namespace = "com.ledvance.usecase"
}

dependencies {
    implementation(projects.core.ble)
    implementation(projects.core.database)
    implementation(projects.core.domain)
    implementation(projects.core.utils)
    implementation(projects.core.ui)

    implementation(libs.timber)
}