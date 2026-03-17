plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.aboutlibraries) apply false
}

tasks.register<Exec>("downloadLocalazyStrings") {
    group = "localazy"
    description = "从 Localazy 下载多语言字符串"
    commandLine("bash", "localazy/localazy.sh")
    // 忽略非0退出码, 不影响编译流程
    isIgnoreExitValue = true
}