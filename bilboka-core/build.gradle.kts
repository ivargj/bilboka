import bilboka.dependencies.Libs
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")

    id("bilboka.plugin")
}

group = "ivaralek"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports { mavenBom(Libs.springbootDependencies) }
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:${bilboka.dependencies.Versions.exposed}")
    implementation("org.jetbrains.exposed:exposed-dao:${bilboka.dependencies.Versions.exposed}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${bilboka.dependencies.Versions.exposed}")
    implementation("org.jetbrains.exposed:exposed-java-time:${bilboka.dependencies.Versions.exposed}")

    runtimeOnly("com.h2database:h2")

    testImplementation("io.mockk:mockk:1.10.6") // Feilet ved nyere versjon
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
