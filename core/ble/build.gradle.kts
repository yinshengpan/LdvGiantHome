plugins {
    alias(libs.plugins.ledvance.android.library)
    alias(libs.plugins.ledvance.android.hilt)
    alias(libs.plugins.ledvance.kotlinx.serialization)
}

android {
    namespace = "com.ledvance.ble"
}

dependencies {
    implementation(projects.core.utils)
    implementation(projects.core.domain)
    implementation(libs.timber)
    implementation(libs.core.ktx)
    implementation(libs.startup.runtime)

    implementation(libs.nordic.kotlin.ble.scanner)
    implementation(libs.nordic.kotlin.ble.client)
}