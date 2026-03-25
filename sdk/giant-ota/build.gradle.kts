plugins {
    base
}

configurations.maybeCreate("default")
artifacts.add("default", file("libwsota.aar"))
