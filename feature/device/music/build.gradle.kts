plugins {
    alias(libs.plugins.ledvance.compose.feature)
}

android {
    namespace = "com.ledvance.music"
}

dependencies {
    implementation(libs.media3.exoplayer)
}
