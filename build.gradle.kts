import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"
}

group = "net.perfectdreams.loritta.helper"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots") // Required by Kord
    maven("https://repo.perfectdreams.net/")
    maven("https://jitpack.io")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
    implementation("io.github.microutils:kotlin-logging:2.0.11")

    implementation("net.dv8tion:JDA:4.3.0_330")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.2")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.2.2")

    // Kord
    implementation("dev.kord:kord-rest:0.8.x-SNAPSHOT")

    // Sequins
    implementation("net.perfectdreams.sequins.text:text-utils:1.0.0")

    // Discord InteraKTions
    // We use the Gateway Kord impl because Gateway JDA is disabled for now, so we will convert the raw gateway events to Kord events
    implementation("net.perfectdreams.discordinteraktions:gateway-kord:0.0.9-SNAPSHOT")

    // Used to serialize state on components
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.2.2")
    // Used to serialize state on components
    implementation("io.github.netvl.ecoji:ecoji:1.0.0")

    // Database
    implementation("org.postgresql:postgresql:42.2.23")
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("org.jetbrains.exposed:exposed-core:0.34.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.34.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.34.2")

    implementation("com.github.pemistahl:lingua:6a6d284145")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")

    implementation("io.ktor:ktor-client-cio:1.6.3")

    implementation("org.apache.commons:commons-text:1.9")
}

tasks {
    val fatJar = task("fatJar", type = Jar::class) {
        println("Building fat jar for ${project.name}...")

        archiveBaseName.set("${project.name}-fat")

        manifest {
            fun addIfAvailable(name: String, attrName: String) {
                attributes[attrName] = System.getProperty(name) ?: "Unknown"
            }

            addIfAvailable("build.number", "Build-Number")
            addIfAvailable("commit.hash", "Commit-Hash")
            addIfAvailable("git.branch", "Git-Branch")
            addIfAvailable("compiled.at", "Compiled-At")

            attributes["Main-Class"] = "net.perfectdreams.loritta.helper.LorittaHelperLauncher"
            attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ", transform = { "libs/" + it.name })
        }

        val libs = File(rootProject.projectDir, "libs")
        // libs.deleteRecursively()
        libs.mkdirs()

        from(configurations.runtimeClasspath.get().mapNotNull {
            val output = File(libs, it.name)

            if (!output.exists())
                it.copyTo(output, true)

            null
        })

        with(jar.get() as CopySpec)
    }

    "build" {
        dependsOn(fatJar)
    }
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