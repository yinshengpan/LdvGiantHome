plugins {
    alias(libs.plugins.ledvance.compose.feature)
}

android {
    namespace = "com.ledvance.profile"
}

dependencies {
    implementation(projects.core.log)
}