package net.perfectdreams.loritta.helper

import dev.kord.rest.service.RestClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mu.KotlinLogging
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.perfectdreams.loritta.helper.listeners.AddReactionsToMessagesListener
import net.perfectdreams.loritta.helper.listeners.ApproveAppealsOnReactionListener
import net.perfectdreams.loritta.helper.listeners.ApproveReportsOnReactionListener
import net.perfectdreams.loritta.helper.listeners.BanListener
import net.perfectdreams.loritta.helper.listeners.BanSuspectedUsersOnReactionListener
import net.perfectdreams.loritta.helper.listeners.CheckLoriBannedUsersListener
import net.perfectdreams.loritta.helper.listeners.MessageListener
import net.perfectdreams.loritta.helper.listeners.PrivateMessageListener
import net.perfectdreams.loritta.helper.network.Databases
import net.perfectdreams.loritta.helper.utils.LorittaLandRoleSynchronizationTask
import net.perfectdreams.loritta.helper.utils.checkbannedusers.LorittaBannedRoleTask
import net.perfectdreams.loritta.helper.utils.config.FanArtsConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherTask
import net.perfectdreams.loritta.helper.utils.dailyshopwinners.DailyShopWinners
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterEnglish
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterPortuguese
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterSparklyPower
import net.perfectdreams.loritta.helper.utils.generateserverreport.PendingReportsListTask
import net.perfectdreams.loritta.helper.utils.topsonhos.TopSonhosRankingSender
import net.perfectdreams.loritta.helper.utils.whydothisifyouaregoingtogetbannedanyway.WhyDoThisIfYouAreGoingToGetBannedAnywayListener
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream

/**
 * An instance of Loritta Helper, that is initialized at [LorittaHelperLauncher]
 * With an custom [LorittaHelperConfig]
 */
class LorittaHelper(val config: LorittaHelperConfig, val fanArtsConfig: FanArtsConfig?, val lorittaConfig: LorittaConfig?) {
    companion object {
        val http = HttpClient {
            expectSuccess = false
            followRedirects = false
        }

        private val logger = KotlinLogging.logger {}
    }

    // We only need one single thread because <3 coroutines
    // As long we don't do any blocking tasks inside of the executor, Loritta Helper will work fiiiine
    // and will be very lightweight!
    val executor = Executors.newFixedThreadPool(8)
        .asCoroutineDispatcher()

    val timedTaskExecutor = Executors.newScheduledThreadPool(1)
    val databases = Databases(this)
    var dailyCatcherManager: DailyCatcherManager? = null

    var dailyShopWinners: DailyShopWinners? = null

    val helperRest = RestClient(config.token)
    val lorittaRest = lorittaConfig?.token?.let { RestClient(it) }

    fun start() {
        // We only care about specific intents
        val jda = JDABuilder.createLight(
            config.token,
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_BANS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGE_REACTIONS
        )
            .addEventListeners(
                MessageListener(this),
                BanListener(this),
                CheckLoriBannedUsersListener(this),
                PrivateMessageListener(this),
                ApproveReportsOnReactionListener(this),
                AddReactionsToMessagesListener(this),
                ApproveAppealsOnReactionListener(this),
                WhyDoThisIfYouAreGoingToGetBannedAnywayListener()
            )
            .setMemberCachePolicy(
                MemberCachePolicy.ALL
            )
            .setRawEventsEnabled(true)
            .setActivity(Activity.listening("https://youtu.be/CNPdO5TZ1DQ"))
            .build()
            .awaitReady()

        if (config.lorittaDatabase != null) {
            val dailyCatcher = DailyCatcherManager(this, jda)

            jda.addEventListener(BanSuspectedUsersOnReactionListener(this))

            val fiveInTheMorningTomorrowLocalDateTime = LocalDateTime.now()
                .let {
                    if (it.hour >= 5)
                        it.plusDays(1)
                    else it
                }
                .withHour(5)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .atZone(ZoneId.of("America/Sao_Paulo"))

            this.dailyCatcherManager = dailyCatcher

            val whenItShouldBeStarted = TimeUnit.SECONDS.toMinutes(fiveInTheMorningTomorrowLocalDateTime.toEpochSecond() - (System.currentTimeMillis() / 1000))
            logger.info { "Daily Catcher will be executed in $whenItShouldBeStarted minutes!" }

            // Will execute the task every day at 05:00
            // 1440 = 24 hours in minutes
            timedTaskExecutor.scheduleAtFixedRate(
                DailyCatcherTask(dailyCatcher),
                whenItShouldBeStarted,
                1440,
                TimeUnit.MINUTES
            )
        }

        // Get pending reports
        timedTaskExecutor.scheduleAtFixedRate(
            PendingReportsListTask(jda),
            0,
            15,
            TimeUnit.MINUTES
        )

        if (config.lorittaDatabase != null) {
            dailyShopWinners = DailyShopWinners(this, jda)
            dailyShopWinners?.start()
        }

        FAQEmbedUpdaterPortuguese(this, jda).start()
        FAQEmbedUpdaterEnglish(this, jda).start()
        FAQEmbedUpdaterSparklyPower(this, jda).start()
        TopSonhosRankingSender(this, jda).start()

        timedTaskExecutor.scheduleWithFixedDelay(LorittaLandRoleSynchronizationTask(this, jda), 0, 15, TimeUnit.SECONDS)
        timedTaskExecutor.scheduleWithFixedDelay(LorittaBannedRoleTask(this, jda), 0, 15, TimeUnit.SECONDS)

        // This is a hack!!
        LorittaHelperKord(
            config,
            fanArtsConfig,
            lorittaConfig,
            this,
            jda
        ).start()
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(executor) {
        block.invoke(this)
    }

    /**
     * Downloads the latest artifact from GitHub and shuts down
     */
    suspend fun update() {
        var archiveDownloadUrl: String? = null

        logger.info { "Update Webhook received! Waiting 5 seconds until we check for a new build..." }

        for (i in 0 until 12) {
            delay(5_000)

            val response = http.get<String>("https://api.github.com/repos/LorittaBot/LorittaHelper/actions/artifacts") {
                header("Authorization", "token ${config.githubToken}")
                header("Accept", "application/vnd.github.v3+json")
            }

            logger.debug { response }

            val result = Json.parseToJsonElement(response)
                .jsonObject
            val artifacts = result["artifacts"]!!.jsonArray
            val artifact =
                artifacts.first { it.jsonObject["name"]!!.jsonPrimitive.content == "Loritta Helper (Discord)" }
                    .jsonObject

            val createdAt = artifact["created_at"]!!.jsonPrimitive.content

            val ta = DateTimeFormatter.ISO_INSTANT.parse(createdAt)
            val i = Instant.from(ta)

            val now = Instant.now()
                .minusMillis(120_000) // 2 minutes

            archiveDownloadUrl = artifact["archive_download_url"]!!.jsonPrimitive.content

            if (now.isBefore(i)) {
                logger.info { "Seems to be a fresh update... continuing! Created At: $i; Now: $now" }
                break
            } else {
                logger.info { "Doesn't seem to be a fresh update... waiting a few seconds... Checks: $i Created At: $i; Now: $now" }
            }
        }

        val response2 = http.get<HttpResponse>(archiveDownloadUrl
            ?: throw RuntimeException("Missing Archive Download URL!")) {
            header("Accept", "application/vnd.github.v3+json")
            header("Authorization", "token ${config.githubToken}")
        }

        val location = response2.headers["Location"] ?: run {
            logger.info { "Missing Location URL from headers! Restarting update process after 5s..." }
            delay(5_000)
            update()
            return
        }

        logger.info { "Build Location URL: $location" }

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

        logger.info { "Update finished, bye!" }
        System.exit(0)
    }
}