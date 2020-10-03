import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
}

group = "net.perfectdreams.loritta.helper"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jcenter.bintray.com")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation("io.github.microutils:kotlin-logging:1.8.3")

    implementation("net.dv8tion:JDA:4.2.0_207")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("com.github.ben-manes.caffeine:caffeine:2.8.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.0.0-RC2")
    implementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.javaParameters = true
}