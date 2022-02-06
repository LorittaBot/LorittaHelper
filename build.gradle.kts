import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("com.google.cloud.tools.jib") version "3.1.4"
    id("net.perfectdreams.i18nhelper.plugin") version "0.0.3-SNAPSHOT"
}

i18nHelper {
    generatedPackage.set("net.perfectdreams.loritta.helper.i18n")
    languageSourceFolder.set("src/main/resources/languages/en/")
}

group = "net.perfectdreams.loritta.helper"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots") // Required by Kord
    maven("https://repo.perfectdreams.net/")
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha12")
    implementation("io.github.microutils:kotlin-logging:2.1.21")

    implementation("net.dv8tion:JDA:4.3.0_330")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.0")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.3.1")

    // Kord
    implementation("dev.kord:kord-rest:0.8.x-SNAPSHOT")
    implementation("dev.kord:kord-gateway:0.8.x-SNAPSHOT")

    // Sequins
    implementation("net.perfectdreams.sequins.text:text-utils:1.0.0")

    // Discord InteraKTions
    // We use the Gateway Kord impl because Gateway JDA is disabled for now, so we will convert the raw gateway events to Kord events
    implementation("net.perfectdreams.discordinteraktions:gateway-kord:0.0.12-SNAPSHOT")

    // Used to serialize state on components
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.1")
    // Used to serialize state on components
    implementation("io.github.netvl.ecoji:ecoji:1.0.0")

    // i18nHelper
    api("net.perfectdreams.i18nhelper.formatters:icu-messageformat-jvm:0.0.3-SNAPSHOT")

    // GalleryOfDreams client
    implementation("net.perfectdreams.galleryofdreams:client:1.0.3-SNAPSHOT")

    // Used for the LocaleManager
    implementation("org.yaml:snakeyaml:1.29")
    implementation("com.charleskorn.kaml:kaml:0.38.0")

    // ICU
    implementation("com.ibm.icu:icu4j:70.1")

    // Database
    implementation("org.postgresql:postgresql:42.3.1")
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("org.jetbrains.exposed:exposed-core:0.36.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.36.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.36.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    implementation("io.ktor:ktor-client-cio:1.6.7")

    implementation("org.apache.commons:commons-text:1.9")
}

jib {
    to {
        image = "ghcr.io/lorittabot/loritta-helper"

        auth {
            username = System.getProperty("DOCKER_USERNAME") ?: System.getenv("DOCKER_USERNAME")
            password = System.getProperty("DOCKER_PASSWORD") ?: System.getenv("DOCKER_PASSWORD")
        }
    }

    from {
        image = "openjdk:17-slim-bullseye"
    }
}

sourceSets.main {
    java.srcDir("build/generated/languages")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        showStandardStreams = true
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.javaParameters = true
}