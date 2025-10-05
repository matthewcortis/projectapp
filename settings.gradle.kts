pluginManagement {
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
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://api.mapbox.com/downloads/v2/releases/maven")

            credentials{
                username = "mapbox"
                password = "pk.eyJ1IjoiaG9hbmdkdW9uZzA3IiwiYSI6ImNtZ2MwcHlkbjAyb2MybW9neHQ5dWZicGMifQ.ifUBex4a_IlvEdpXNXVuLA"
            }
        }



    }
}

rootProject.name = "project"
include(":app")
