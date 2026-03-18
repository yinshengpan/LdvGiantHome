plugins {
    alias(libs.plugins.ledvance.compose.feature)
}

android {
    namespace = "com.ledvance.search"
}

dependencies {
    implementation(projects.core.ble)
    implementation(projects.core.database)
}