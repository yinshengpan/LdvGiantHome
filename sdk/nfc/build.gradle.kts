plugins {
    alias(libs.plugins.ledvance.jvm.library)
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
}