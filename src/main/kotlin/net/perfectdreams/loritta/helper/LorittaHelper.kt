package net.perfectdreams.loritta.helper

import dev.kord.rest.service.RestClient
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import mu.KotlinLogging
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.perfectdreams.loritta.cinnamon.pudding.utils.exposed.createOrUpdatePostgreSQLEnum
import net.perfectdreams.loritta.helper.listeners.AddReactionsToMessagesListener
import net.perfectdreams.loritta.helper.listeners.ApproveAppealsOnReactionListener
import net.perfectdreams.loritta.helper.listeners.ApproveReportsOnReactionListener
import net.perfectdreams.loritta.helper.listeners.BanListener
import net.perfectdreams.loritta.helper.listeners.BanSuspectedUsersOnReactionListener
import net.perfectdreams.loritta.helper.listeners.CheckLoriBannedUsersListener
import net.perfectdreams.loritta.helper.listeners.MessageListener
import net.perfectdreams.loritta.helper.listeners.PrivateMessageListener
import net.perfectdreams.loritta.helper.network.Databases
import net.perfectdreams.loritta.helper.tables.SelectedResponsesLog
import net.perfectdreams.loritta.helper.tables.StaffProcessedReports
import net.perfectdreams.loritta.helper.tables.StartedSupportSolicitations
import net.perfectdreams.loritta.helper.tables.TicketMessagesActivity
import net.perfectdreams.loritta.helper.utils.LorittaLandRoleSynchronizationTask
import net.perfectdreams.loritta.helper.utils.StaffProcessedReportResult
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
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterStaffFAQ
import net.perfectdreams.loritta.helper.utils.generateserverreport.PendingReportsListTask
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import net.perfectdreams.loritta.helper.utils.topsonhos.TopSonhosRankingSender
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

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

    val timedTaskExecutor = Executors.newScheduledThreadPool(4)
    val databases = Databases(this)
    var dailyCatcherManager: DailyCatcherManager? = null

    var dailyShopWinners: DailyShopWinners? = null

    val helperRest = RestClient(config.token)
    val lorittaRest = lorittaConfig?.token?.let { RestClient(it) }

    fun start() {
        transaction(databases.helperDatabase) {
            createOrUpdatePostgreSQLEnum(TicketUtils.TicketSystemType.values())
            createOrUpdatePostgreSQLEnum(StaffProcessedReportResult.values())

            SchemaUtils.createMissingTablesAndColumns(
                SelectedResponsesLog,
                StaffProcessedReports,
                StartedSupportSolicitations,
                TicketMessagesActivity
            )
        }

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
                ApproveAppealsOnReactionListener(this)
            )
            .setMemberCachePolicy(
                MemberCachePolicy.ALL
            )
            .setRawEventsEnabled(true)
            .setActivity(Activity.playing("https://youtu.be/CNPdO5TZ1DQ"))
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
            // Disabled daily catcher for now, because the thread gets stuck on the "onlyEcoCommandsCatcher.joinAll()" call
            /* timedTaskExecutor.scheduleAtFixedRate(
                DailyCatcherTask(dailyCatcher),
                whenItShouldBeStarted,
                1440,
                TimeUnit.MINUTES
            ) */
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
        FAQEmbedUpdaterStaffFAQ(this, jda).start()
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
}