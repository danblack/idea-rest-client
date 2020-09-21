import org.jetbrains.changelog.closure
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.grammarkit.tasks.GenerateLexer
import org.jetbrains.grammarkit.tasks.GenerateParser

plugins {
    // plugin development
    id("org.jetbrains.intellij") version "0.4.26"
    // for parsing grammer files & generate lexer & grammer
    id("org.jetbrains.grammarkit") version "2020.2.1"
    // for patching plugin.xml with the content from CHANGELOG.md file
    id("org.jetbrains.changelog") version "0.5.0"

    java
    idea

    // Plugin which can check for Gradle dependencies, use the help/dependencyUpdates task.
    id("com.github.ben-manes.versions") version "0.28.0"
    // Plugin which can update Gradle dependencies, use the help/useLatestVersions task.
    id("se.patrikerdes.use-latest-versions") version "0.2.14"

    // Used to debug in a different IDE
    maven
    id("de.undercouch.download") version "4.0.4"
}

// Import variables from gradle.properties file
val pluginGroup: String by project
// `pluginName_` variable ends with `_` because of the collision with Kotlin magic getter in the `intellij` closure.
// Read more about the issue: https://github.com/JetBrains/intellij-platform-plugin-template/issues/29
val pluginName_: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project

val platformType: String by project
val platformVersion: String by project
val platformDownloadSources: String by project

group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains:annotations:20.1.0")

    // lombok
    compileOnly("org.projectlombok:lombok:1.18.12")
    annotationProcessor("org.projectlombok:lombok:1.18.12")
    testCompileOnly("org.projectlombok:lombok:1.18.12")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.12")
    compileOnly("org.jetbrains:annotations:20.1.0")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName = pluginName_
    version = platformVersion
    type = platformType
    downloadSources = platformDownloadSources.toBoolean()
    updateSinceUntilBuild = true
}

val generateRestLexer = task<GenerateLexer>("generateRestLexer") {
    source = "src/main/grammars/Rest.flex"
    targetDir = "gen/ru/basecode/ide/rest/plugin/grammar/"
    targetClass = "_RestLexer"
    purgeOldFiles = true
}

val generateRestParser = task<GenerateParser>("generateRestParser") {
    source = "src/main/grammars/Rest.bnf"
    targetRoot = "gen"
    pathToParser = "ru/basecode/ide/rest/plugin/parser/_RestParser.java"
    pathToPsiRoot = "ru/basecode/ide/rest/plugin/psi"
    purgeOldFiles = true
}

tasks {
    compileJava {
        dependsOn(generateRestLexer, generateRestParser)
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
        options.compilerArgs = listOf("-Xlint:deprecation")
    }

    patchPluginXml {
        version(pluginVersion)
        sinceBuild(pluginSinceBuild)
        untilBuild(pluginUntilBuild)

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription(
                closure {
                    File("./README.md").readText().lines().run {
                        val start = "<!-- Plugin description -->"
                        val end = "<!-- Plugin description end -->"

                        if (!containsAll(listOf(start, end))) {
                            throw GradleException("Plugin description section not found in README.md file:\n$start ... $end")
                        }
                        subList(indexOf(start) + 1, indexOf(end))
                    }.joinToString("\n").run { markdownToHTML(this) }
                }
        )

        // Get the latest available change notes from the changelog file
        changeNotes(
                closure {
                    changelog.getLatest().toHTML()
                }
        )
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token(System.getenv("PUBLISH_TOKEN"))
        channels(pluginVersion.split('-').getOrElse(1) { "default" }.split('.').first())
    }
}

sourceSets {
    main {
        java.srcDirs("gen")
    }
}

idea {
    module {
        generatedSourceDirs.add(file("gen"))
    }
}
