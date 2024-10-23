package net.perfectdreams.loritta.helper

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.perfectdreams.exposedpowerutils.sql.createOrUpdatePostgreSQLEnum
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.*
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.ButtonRoleSenderCommand
import net.perfectdreams.loritta.helper.listeners.*
import net.perfectdreams.loritta.helper.network.Databases
import net.perfectdreams.loritta.helper.tables.*
import net.perfectdreams.loritta.helper.utils.LanguageManager
import net.perfectdreams.loritta.helper.utils.LorittaLandRoleSynchronizationTask
import net.perfectdreams.loritta.helper.utils.StaffProcessedReportResult
import net.perfectdreams.loritta.helper.utils.checkbannedusers.LorittaBannedRoleTask
import net.perfectdreams.loritta.helper.utils.config.FanArtsConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import net.perfectdreams.loritta.helper.utils.dailyshopwinners.DailyShopWinners
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterEnglish
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterPortuguese
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterSparklyPower
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterStaffFAQ
import net.perfectdreams.loritta.helper.utils.generateserverreport.PendingReportsListTask
import net.perfectdreams.loritta.helper.utils.lorittaautomods.CheckDupeClientIds
import net.perfectdreams.loritta.helper.utils.scheduleCoroutineEveryDayAtSpecificTime
import net.perfectdreams.loritta.helper.utils.slash.declarations.*
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import net.perfectdreams.loritta.helper.utils.topsonhos.TopSonhosRankingSender
import net.perfectdreams.loritta.morenitta.interactions.InteractionsListener
import net.perfectdreams.loritta.morenitta.interactions.InteractivityManager
import net.perfectdreams.loritta.morenitta.interactions.commands.UnleashedCommandManager
import net.perfectdreams.loritta.serializable.dashboard.requests.LorittaDashboardRPCRequest
import net.perfectdreams.loritta.serializable.dashboard.responses.LorittaDashboardRPCResponse
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * An instance of Loritta Helper, that is initialized at [LorittaHelperLauncher]
 * With an custom [LorittaHelperConfig]
 */
class LorittaHelper(val config: LorittaHelperConfig, val fanArtsConfig: FanArtsConfig?) {
    companion object {
        val http = HttpClient {
            expectSuccess = false
            followRedirects = false
        }

        private val logger = KotlinLogging.logger {}
    }

    lateinit var jda: JDA
    lateinit var ticketUtils: TicketUtils

    // We only need one single thread because <3 coroutines
    // As long we don't do any blocking tasks inside the executor, Loritta Helper will work fiiiine
    // and will be very lightweight!
    val executor = Executors.newFixedThreadPool(8)
        .asCoroutineDispatcher()

    val timedTaskExecutor = Executors.newScheduledThreadPool(4)
    val databases = Databases(this)
    val commandManager = UnleashedCommandManager(this)
    val interactivityManager = InteractivityManager()
    val languageManager = LanguageManager(
        LorittaHelper::class,
        "en",
        "/languages/"
    )

    var dailyCatcherManager: DailyCatcherManager? = null
    var dailyShopWinners: DailyShopWinners? = null
    val banEvasionChecker = CheckDupeClientIds(this)

    val galleryOfDreamsClient = fanArtsConfig?.let {
        GalleryOfDreamsClient(
            "https://fanarts.perfectdreams.net/",
            it.token,
            HttpClient {
                expectSuccess = false
                followRedirects = false

                // Because some fan arts are gigantic
                install(HttpTimeout) {
                    socketTimeoutMillis = 120_000
                    connectTimeoutMillis = 120_000
                    requestTimeoutMillis = 120_000
                }
            }
        )
    }

    val tasksScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun start() {
        languageManager.loadLanguagesAndContexts()

        transaction(databases.helperDatabase) {
            createOrUpdatePostgreSQLEnum(TicketUtils.TicketSystemType.values())
            createOrUpdatePostgreSQLEnum(StaffProcessedReportResult.values())

            SchemaUtils.createMissingTablesAndColumns(
                SelectedResponsesLog,
                StaffProcessedReports,
                StartedSupportSolicitations,
                TicketMessagesActivity,
                LorittaAutoModIgnoredClientIds
            )
        }


        commandManager.register(LoriToolsCommand(this))
        commandManager.register(TicketUtilsCommand(this))
        commandManager.register(CloseTicketCommand(this))
        commandManager.register(DailyCheckCommand(this))
        commandManager.register(TicketSenderCommand(this))
        commandManager.register(ReportMessageSenderCommand(this))

        commandManager.register(CheckCommandsCommand(this))

        commandManager.register(DailyCatcherCheckCommand(this))
        commandManager.register(PendingScarletCommand(this))
        commandManager.register(PendingReportsCommand(this))
        commandManager.register(IPLocationCommand(this))
        commandManager.register(AttachDenyReasonCommand(this))
        commandManager.register(AllTransactionsCommand(this))
        commandManager.register(DirectDiscordCdnMessageCommand(this))

        // ===[ BUTTON ROLES ]===
        commandManager.register(ButtonRoleSenderCommand(this))
        // commandManager.register(RoleToggleButtonExecutor(this))
        // commandManager.register(RoleCoolBadgeButtonExecutor(this))
        // commandManager.register(RoleColorButtonExecutor(this))

        // ===[ TICKETS ]===
        commandManager.register(DriveImageRetrieverCommand(this))

        // ===[ REPORTS ]===
        // commandManager.register(ShowUserIdExecutor(this))
        // commandManager.register(ShowFilesExecutor(this))

        // ===[ STATS ]===
        commandManager.register(StatsCommand(this))

        if (galleryOfDreamsClient != null) {
            // ===[ FAN ARTS ]===
            // commandManager.register(GalleryOfDreamsSlashCommand(this, galleryOfDreamsClient))

            // commandManager.register(AddFanArtToGalleryMessageCommand(this, galleryOfDreamsClient))

            // commandManager.register(AddFanArtToGalleryButtonExecutor(this, galleryOfDreamsClient))

            // commandManager.register(AddFanArtSelectAttachmentSelectMenuExecutor(this, galleryOfDreamsClient))

            // commandManager.register(AddFanArtSelectBadgesSelectMenuExecutor(this, galleryOfDreamsClient))

            // commandManager.register(PatchFanArtOnGalleryButtonExecutor(this, galleryOfDreamsClient))

            // commandManager.register(PatchFanArtSelectBadgesSelectMenuExecutor(this, galleryOfDreamsClient))
        }

        // We only care about specific intents
        val jda = JDABuilder.createLight(
            config.helper.token,
            GatewayIntent.DIRECT_MESSAGES,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_BANS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGE_REACTIONS
        )
            .addEventListeners(
                InteractionsListener(this),
                MessageListener(this),
                BanListener(this),
                CheckLoriBannedUsersListener(this),
                PrivateMessageListener(this),
                ApproveReportsOnReactionListener(this),
                AddReactionsToMessagesListener(this),
                ApproveAppealsOnReactionListener(this),
                CreateSparklyThreadsListener(),
                LorittaBanTimeoutListener(this),
                ComponentInteractionListener(this),
                AutoCloseTicketWhenMemberLeavesListener(this),
            )
            .setMemberCachePolicy(
                MemberCachePolicy.ALL
            )
            .setRawEventsEnabled(true)
            .setActivity(Activity.customStatus("https://youtu.be/CNPdO5TZ1DQ"))
            .build()
            .awaitReady()

        this.jda = jda
        ticketUtils = TicketUtils(this)
        
        runBlocking {
            for (system in ticketUtils.systems.values) {
                val type = system.systemType
                val cache = system.cache
                logger.info { "Populating ${type}'s ticket cache..." }
                cache.populateCache()
                logger.info { "Now tracking ${cache.tickets.size} tickets!" }
            }
        }

        // TODO: Fix this!
        /* if (lorittaRest != null) {
            commandManager.register(RetrieveMessageCommand(this, lorittaRest))

            commandManager.register(ServerMembersCommand(this, lorittaRest))
        } */
        
        if (config.loritta.database != null) {
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
            PendingReportsListTask(this, jda),
            0,
            30,
            TimeUnit.MINUTES
        )

        scheduleCoroutineEveryDayAtSpecificTime(
            CheckDupeClientIds::class.simpleName!!,
            tasksScope,
            LocalTime.of(5, 0),
            banEvasionChecker
        )

        FAQEmbedUpdaterPortuguese(this, jda).start()
        FAQEmbedUpdaterEnglish(this, jda).start()
        FAQEmbedUpdaterSparklyPower(this, jda).start()
        FAQEmbedUpdaterStaffFAQ(this, jda).start()
        TopSonhosRankingSender(this, jda).start()

        if (config.tasks.roleSynchronization.enabled)
            timedTaskExecutor.scheduleWithFixedDelay(LorittaLandRoleSynchronizationTask(this, jda), 0, 15, TimeUnit.SECONDS)

        if (config.tasks.lorittaBannedRole.enabled)
            timedTaskExecutor.scheduleWithFixedDelay(LorittaBannedRoleTask(this, jda), 0, 15, TimeUnit.SECONDS)
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(executor) {
        block.invoke(this)
    }

    suspend inline fun <reified T : LorittaDashboardRPCResponse> makeLorittaRPCRequest(rpc: LorittaDashboardRPCRequest): T {
        return Json.decodeFromString<T>(
            http.post("${config.loritta.api.url.removeSuffix("/")}/api/v1/rpc") {
                header("Authorization", config.loritta.api.token)
                setBody(Json.encodeToString<LorittaDashboardRPCRequest>(rpc))
            }.bodyAsText()
        )
    }
}