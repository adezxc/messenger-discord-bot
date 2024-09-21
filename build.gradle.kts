plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}


dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("org.slf4j:slf4j-api:1.6.1")
    implementation("org.slf4j:slf4j-simple:1.6.1")
}

tasks.register<Exec>("myPrebuildTask") {
    val db = File("$projectDir/sample.db")

    workingDir("$projectDir")
    db.delete()
    commandLine(
        listOf("sqlite3", "sample.db < $projectDir/src/sql/schema.sql")
    )
}

tasks.named("compileKotlin") {
    dependsOn("myPrebuildTask")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(22)
}