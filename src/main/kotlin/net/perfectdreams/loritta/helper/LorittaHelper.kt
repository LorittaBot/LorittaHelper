package net.perfectdreams.loritta.helper

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.perfectdreams.loritta.helper.listeners.MessageListener
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterEnglish
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterPortuguese
import net.perfectdreams.loritta.helper.utils.supporttimer.EnglishSupportTimer
import net.perfectdreams.loritta.helper.utils.supporttimer.PortugueseSupportTimer
import java.io.File
import java.util.concurrent.Executors
import java.util.jar.Attributes
import java.util.jar.JarFile
import java.util.zip.ZipInputStream

class LorittaHelper(val config: LorittaHelperConfig) {
    companion object {
        val http = HttpClient {
            expectSuccess = false
            followRedirects = false
        }
    }

    // We only need one single thread because <3 coroutines
    // As long we don't do any blocking tasks inside of the executor, Loritta Helper will work fiiiine
    // and will be very lightweight!
    val executor = Executors.newSingleThreadExecutor()
        .asCoroutineDispatcher()

    fun start() {
        // We only care about GUILD MESSAGES and we don't need to cache any users
        val jda = JDABuilder.createLight(
            config.token,
            GatewayIntent.GUILD_MESSAGES
        )
            .addEventListeners(MessageListener(this))
            .build()

        val path = this::class.java.protectionDomain.codeSource.location.path
        val jar = JarFile(path)
        val mf = jar.manifest
        val mattr = mf.mainAttributes

        val commitHash = mattr[Attributes.Name("Commit-Hash")] as String

        // OwO whats this???
        jda.presence.activity = Activity.listening("OwO | commit $commitHash")

        FAQEmbedUpdaterPortuguese(this, jda).start()
        FAQEmbedUpdaterEnglish(this, jda).start()

        PortugueseSupportTimer(this, jda).start()
        EnglishSupportTimer(this, jda).start()
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(executor) {
        block.invoke(this)
    }

    /**
     * Downloads the latest artifact from GitHub and shuts down
     */
    suspend fun update() {
        val response = http.get<String>("https://api.github.com/repos/LorittaBot/LorittaHelper/actions/artifacts") {
            header("Accept", "application/vnd.github.v3+json")
        }

        println(response)

        val result = Json.parseToJsonElement(response)
            .jsonObject
        val artifacts = result["artifacts"]!!.jsonArray
        val artifact = artifacts.first { it.jsonObject["name"]!!.jsonPrimitive.content == "Loritta Helper (Discord)" }
            .jsonObject

        val archiveDownloadUrl = artifact["archive_download_url"]!!.jsonPrimitive.content

        val response2 = http.get<HttpResponse>(archiveDownloadUrl) {
            header("Accept", "application/vnd.github.v3+json")
            header("Authorization", "token ${config.githubToken}")
        }

        val location = response2.headers["Location"]!!

        println("Location: $location)")
        val response3 = http.get<HttpResponse>(location) {
        }

        val bytes = response3.readBytes()
        val zip = ZipInputStream(bytes.inputStream())

        while (true) {
            val next = zip.nextEntry ?: break
            if (next.isDirectory)
                continue

            if (next.name.startsWith("loritta-helper-fat-")) {
                val fat = withContext(Dispatchers.IO) { zip.readAllBytes() }
                File("./update.jar").writeBytes(fat)
                break
            }
        }

        System.exit(0)
    }
}