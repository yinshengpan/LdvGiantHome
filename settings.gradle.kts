pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.aliyun.com/repository/central") }
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}

rootProject.name = "LdvHome"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:log")
include(":core:ui")
include(":core:utils")
include(":core:database")
include(":core:log-no-op")
include(":core:ble")
include(":core:network")
include(":core:domain")
include(":core:nfc")
include(":feature:home")
include(":feature:search")
include(":feature:profile")
include(":feature:device:light-detail")
include(":feature:device:setting")
include(":feature:device:ota")
include(":feature:device:music")
include(":feature:device:mode")
include(":feature:device:scene")
include(":feature:device:timer")
include(":core:usecase")
include(":core:ota")
include(":sdk:giant-ota")
include(":sdk:nfc")
