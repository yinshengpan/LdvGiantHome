import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.ledvance.android.application)
    alias(libs.plugins.ledvance.android.application.compose)
    alias(libs.plugins.ledvance.application.signing)
    alias(libs.plugins.ledvance.android.hilt)
    alias(libs.plugins.ledvance.android.licenses)
    alias(libs.plugins.ledvance.kotlinx.serialization)
}
android {
    val useApplicationId = providers.gradleProperty("applicationId").orNull ?: ""
    namespace = useApplicationId
    defaultConfig {
        applicationId = useApplicationId
        versionCode = providers.gradleProperty("versionCode").orNull?.toIntOrNull() ?: 0
        versionName = providers.gradleProperty("versionName").orNull ?: ""

        manifestPlaceholders["COMMIT_ID"] = getCommitId()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.core.utils)
    implementation(projects.core.ble)
    implementation(projects.core.database)
    implementation(projects.core.network)
    implementation(projects.core.log)
    implementation(projects.core.domain)
    implementation(projects.core.usecase)

    implementation(projects.feature.home)
    implementation(projects.feature.profile)
    implementation(projects.feature.search)
    implementation(projects.feature.device.light)

    implementation(libs.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.splashscreen)
    implementation(libs.datastore)
    implementation(libs.startup.runtime)
    implementation(libs.media3.exoplayer)
    implementation(libs.accompanist.permissions)
    implementation(libs.work)

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.process)
    implementation(libs.lifecycle.viewmodel.compose)

    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.navigation3)
//    implementation(libs.material3.adaptive.navigation3)

    implementation(libs.hilt.navigation.compose)

    implementation(libs.timber)
    implementation(libs.tracing.ktx)
}


fun getDate(): String {
    val dateFormatter = DateTimeFormatter.ofPattern("yyMMddHH")
    return dateFormatter.format(LocalDateTime.now())
}

fun getCommitId(): String {
    val process = ProcessBuilder("git", "rev-parse", "HEAD")
        .redirectErrorStream(true)
        .start()
    val commitId = process.inputStream.bufferedReader().readText().trim()
    println("getCommitId Commit ID: $commitId")
    return commitId
}