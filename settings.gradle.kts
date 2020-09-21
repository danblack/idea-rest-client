rootProject.name = "idea-rest-client"
pluginManagement {
    repositories {
        maven{
            url = uri("https://jetbrains.bintray.com/intellij-plugin-service")
        }
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
        mavenCentral()
    }
}
