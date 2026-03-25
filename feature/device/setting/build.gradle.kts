plugins {
    alias(libs.plugins.ledvance.compose.feature)
}

android {
    namespace = "com.ledvance.setting"
}

dependencies {
    implementation(projects.feature.device.ota)
}