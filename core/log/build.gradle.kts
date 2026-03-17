plugins {
    alias(libs.plugins.ledvance.android.library)
}

android {
    namespace = "com.ledvance.log"
}

dependencies {
    implementation(projects.core.utils)
    implementation(libs.timber)
    implementation(libs.core.ktx)
    implementation(libs.startup.runtime)
}