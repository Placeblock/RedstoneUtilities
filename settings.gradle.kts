pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "RedstoneUtilities"

include(":BetterInventories")
project(":BetterInventories").projectDir = file("../BetterInventories")
