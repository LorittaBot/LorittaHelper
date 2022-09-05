import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.google.cloud.tools.jib") version "3.1.4"
    id("net.perfectdreams.i18nhelper.plugin") version "0.0.5-SNAPSHOT"
}

val generateI18nKeys = tasks.register<net.perfectdreams.i18nhelper.plugin.GenerateI18nKeysTask>("generateI18nKeys") {
    generatedPackage.set("net.perfectdreams.loritta.helper.i18n")
    languageSourceFolder.set(file("src/resources/languages/en/"))
    languageTargetFolder.set(file("$buildDir/generated/languages"))
    translationLoadTransform.set { file, map -> map }
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
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha14")
    implementation("io.github.microutils:kotlin-logging:2.1.21")

    implementation("net.dv8tion:JDA:5.0.0-alpha.11")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.3.2")

    // Kord
    implementation("dev.kord:kord-rest:0.8.x-SNAPSHOT")
    implementation("dev.kord:kord-gateway:0.8.x-SNAPSHOT")

    // Sequins
    implementation("net.perfectdreams.sequins.text:text-utils:1.0.0")

    // Pudding
    api("net.perfectdreams.loritta.cinnamon.pudding:client:0.0.2-20220306.142003-161")

    // Discord InteraKTions
    // We use the Gateway Kord impl because Gateway JDA is disabled for now, so we will convert the raw gateway events to Kord events
    implementation("net.perfectdreams.discordinteraktions:common:0.0.17-20220818.132550-4")
    implementation("net.perfectdreams.discordinteraktions:gateway-kord:0.0.17-20220818.132550-4")

    // Used to serialize state on components
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.2")
    // Used to serialize state on components
    implementation("io.github.netvl.ecoji:ecoji:1.0.0")

    // i18nHelper
    api("net.perfectdreams.i18nhelper.formatters:icu-messageformat-jvm:0.0.5-SNAPSHOT")

    // GalleryOfDreams client
    implementation("net.perfectdreams.galleryofdreams:common:1.0.9")
    implementation("net.perfectdreams.galleryofdreams:client:1.0.9")

    // Used for the LocaleManager
    implementation("org.yaml:snakeyaml:1.30")
    implementation("com.charleskorn.kaml:kaml:0.43.0")

    // ICU
    implementation("com.ibm.icu:icu4j:71.1")

    // Database
    implementation("org.postgresql:postgresql:42.3.3")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.jetbrains.exposed:exposed-core:0.37.3")
    implementation("org.jetbrains.exposed:exposed-dao:0.37.3")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.37.3")
    implementation("net.perfectdreams.exposedpowerutils:postgres-power-utils:1.0.0")
    implementation("net.perfectdreams.exposedpowerutils:postgres-java-time:1.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    implementation("io.ktor:ktor-client-cio:2.1.0")

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
    java.srcDir(generateI18nKeys)
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        showStandardStreams = true
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.javaParameters = true
}