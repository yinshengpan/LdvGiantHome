plugins {
    alias(libs.plugins.ledvance.android.library)
    alias(libs.plugins.ledvance.kotlinx.serialization)
}

android {
    namespace = "com.ledvance.utils"
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.datastore)
    implementation(libs.timber)
    implementation(libs.core.ktx)
    implementation(libs.startup.runtime)
    implementation(libs.mmkv)
    implementation(libs.browser)
}