plugins {
    // Apply the java plugin to add support for Java
    java
    id("org.openjfx.javafxplugin") version "0.1.0"
    // Apply the application plugin to add support for building a CLI application
    // You can run your app via task "run": ./gradlew run
    application

    /*
     * Adds tasks to export a runnable jar.
     * In order to create it, launch the "shadowJar" task.
     * The runnable jar will be found in build/libs/projectname-all.jar
     */
    id("com.gradleup.shadow") version "9.4.1"
    id("org.danilopianini.gradle-java-qa") version "1.172.0"
}

javafx {
    version = "17" // Inserisci la versione del JDK che state usando (solitamente 17 o 21 per i progetti universitari)
    modules = listOf("javafx.controls", "javafx.graphics", "javafx.media") 
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories { // Where to search for dependencies
    mavenCentral()
}

dependencies {
    // Suppressions for SpotBugs
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.9.8")

    // Maven dependencies are composed by a group name, a name and a version, separated by colons
    implementation("com.omertron:API-OMDB:1.5")
    implementation("org.jooq:jool:0.9.15")

    /*
     * Simple Logging Facade for Java (SLF4J)
     * See: http://www.slf4j.org/
     */
    val slf4jVersion = "2.0.17"
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    // Logback backend for SLF4J
    runtimeOnly("ch.qos.logback:logback-classic:1.5.32")

    // JUnit API and testing engine
    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    implementation("com.google.code.gson:gson:2.10.1")
}

application {
    // Define the main class for the application.
    mainClass.set("it.unibo.KikiStore.app.Main")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform() // Enables the engine of JUnit 5/6
    testLogging { // Additional Options
        // Display all events (test started, succeeded, failed...)
        events(*org.gradle.api.tasks.testing.logging.TestLogEvent.entries.toTypedArray())
        showStandardStreams = true // Show the standard output
    }
}
