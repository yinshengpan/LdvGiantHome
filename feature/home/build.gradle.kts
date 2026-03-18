plugins {
    alias(libs.plugins.ledvance.compose.feature)
}

android {
    namespace = "com.ledvance.home"
}

dependencies {
    implementation(projects.core.database)
}