plugins {
    alias(libs.plugins.ledvance.compose.feature)
}

android {
    namespace = "com.ledvance.light"
}

dependencies {
    implementation(projects.feature.device.music)
    implementation(projects.feature.device.mode)
    implementation(projects.feature.device.scene)
    implementation(projects.feature.device.timer)
    implementation(projects.feature.device.setting)
    implementation(projects.feature.device.ota)
}