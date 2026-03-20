plugins {
    alias(libs.plugins.ledvance.compose.feature)
}

android {
    namespace = "com.ledvance.light"
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(libs.media3.exoplayer)
}