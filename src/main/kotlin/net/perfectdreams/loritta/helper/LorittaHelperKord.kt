package net.perfectdreams.loritta.helper

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.DefaultGateway
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.kord.gateway.start
import io.ktor.client.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.perfectdreams.discordinteraktions.common.commands.CommandManager
import net.perfectdreams.discordinteraktions.platforms.kord.commands.KordCommandRegistry
import net.perfectdreams.discordinteraktions.platforms.kord.installDiscordInteraKTions
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.LanguageManager
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleColorButtonExecutor
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleCoolBadgeButtonExecutor
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleToggleButtonExecutor
import net.perfectdreams.loritta.helper.utils.cache.ChannelsCache
import net.perfectdreams.loritta.helper.utils.cache.TicketsCache
import net.perfectdreams.loritta.helper.utils.checksonhosmendigagem.CheckSequenciaTimeoutListener
import net.perfectdreams.loritta.helper.utils.checksonhosmendigagem.CheckSonhosMendigagemTimeoutListener
import net.perfectdreams.loritta.helper.utils.config.FanArtsConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtSelectAttachmentSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtSelectBadgesSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtToGalleryButtonExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtToGalleryMessageExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtToGallerySlashExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.declarations.AddFanArtToGalleryMessageCommand
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.declarations.GalleryOfDreamsSlashCommand
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch.PatchFanArtOnGalleryButtonExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch.PatchFanArtSelectBadgesSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch.PatchFanArtSlashExecutor
import net.perfectdreams.loritta.helper.utils.generateserverreport.ShowFilesExecutor
import net.perfectdreams.loritta.helper.utils.generateserverreport.ShowUserIdExecutor
import net.perfectdreams.loritta.helper.utils.loribantimeout.LorittaBanTimeoutListener
import net.perfectdreams.loritta.helper.utils.slash.AllTransactionsExecutor
import net.perfectdreams.loritta.helper.utils.slash.AttachDenyReasonExecutor
import net.perfectdreams.loritta.helper.utils.slash.BroadcastDailyShopWinnersExecutor
import net.perfectdreams.loritta.helper.utils.slash.ButtonRoleSenderExecutor
import net.perfectdreams.loritta.helper.utils.slash.CheckCommandsExecutor
import net.perfectdreams.loritta.helper.utils.slash.CloseTicketExecutor
import net.perfectdreams.loritta.helper.utils.slash.DailyCatcherCheckExecutor
import net.perfectdreams.loritta.helper.utils.slash.DailyCheckExecutor
import net.perfectdreams.loritta.helper.utils.slash.DriveImageRetrieverExecutor
import net.perfectdreams.loritta.helper.utils.slash.FindTicketExecutor
import net.perfectdreams.loritta.helper.utils.slash.IPLocationExecutor
import net.perfectdreams.loritta.helper.utils.slash.LoriBanExecutor
import net.perfectdreams.loritta.helper.utils.slash.LoriBanRenameExecutor
import net.perfectdreams.loritta.helper.utils.slash.LoriUnbanExecutor
import net.perfectdreams.loritta.helper.utils.slash.PendingReportsExecutor
import net.perfectdreams.loritta.helper.utils.slash.PendingScarletExecutor
import net.perfectdreams.loritta.helper.utils.slash.RetrieveMessageExecutor
import net.perfectdreams.loritta.helper.utils.slash.ServerMembersExecutor
import net.perfectdreams.loritta.helper.utils.slash.StatsReportsExecutor
import net.perfectdreams.loritta.helper.utils.slash.StatsTicketsExecutor
import net.perfectdreams.loritta.helper.utils.slash.TicketInfoExecutor
import net.perfectdreams.loritta.helper.utils.slash.TicketSenderExecutor
import net.perfectdreams.loritta.helper.utils.slash.declarations.AllTransactionsCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.AttachDenyReasonCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.BroadcastDailyShopWinnersCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.ButtonRoleSenderCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.CheckCommandsCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.CloseTicketCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.DailyCatcherCheckCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.DailyCheckCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.DriveImageRetrieverCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.IPLocationCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.LoriToolsCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.PendingReportsCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.PendingScarletCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.RetrieveMessageCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.ServerMembersCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.StatsCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.TicketSenderCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.TicketUtilsCommand
import net.perfectdreams.loritta.helper.utils.tickets.AutoCloseTicketWhenMemberLeavesGuildListener
import net.perfectdreams.loritta.helper.utils.tickets.CloseTicketButtonExecutor
import net.perfectdreams.loritta.helper.utils.tickets.CreateTicketButtonExecutor
import net.perfectdreams.loritta.helper.utils.tickets.HelperResponseSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.tickets.TicketListener
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

// Hack, hack, hack!
class LorittaHelperKord(
    val config: LorittaHelperConfig,
    val fanArtsConfig: FanArtsConfig?,
    val lorittaConfig: LorittaConfig?,
    private val helper: LorittaHelper,
    private val jda: JDA
) {
    companion object {
        val http = HttpClient {
            expectSuccess = false
            followRedirects = false
        }

        private val logger = KotlinLogging.logger {}
    }

    val helperRest = helper.helperRest
    val lorittaRest = helper.lorittaRest
    val databases = helper.databases
    val dailyCatcherManager = helper.dailyCatcherManager
    val dailyShopWinners = helper.dailyShopWinners
    val commandManager = CommandManager()
    val languageManager = LanguageManager(
        LorittaHelperKord::class,
        "en",
        "/languages/"
    )
    val channelsCache = ChannelsCache(helperRest)
    val ticketsCache = mapOf(
        TicketUtils.TicketSystemType.HELP_DESK_PORTUGUESE to TicketsCache(
            Snowflake(Constants.SUPPORT_SERVER_ID),
            Snowflake(891834050073997383L),
            helperRest
        ),

        TicketUtils.TicketSystemType.HELP_DESK_ENGLISH to TicketsCache(
            Snowflake(Constants.SUPPORT_SERVER_ID),
            Snowflake(891834950159044658L),
            helperRest
        ),

        TicketUtils.TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE to TicketsCache(
            Snowflake(Constants.COMMUNITY_SERVER_ID),
            Snowflake(938247721775661086L),
            helperRest
        )
    )

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

    @OptIn(PrivilegedIntent::class)
    fun start() {
        val gateway = DefaultGateway()
        languageManager.loadLanguagesAndContexts()

        runBlocking {
            for ((type, cache) in ticketsCache) {
                logger.info { "Populating ${type}'s ticket cache..." }
                cache.populateCache()
                logger.info { "Now tracking ${cache.tickets.size} tickets!" }
            }

            // Register Commands
            commandManager.apply {
                register(
                    BroadcastDailyShopWinnersCommand,
                    BroadcastDailyShopWinnersExecutor(this@LorittaHelperKord)
                )
                register(
                    CheckCommandsCommand,
                    CheckCommandsExecutor(this@LorittaHelperKord)
                )
                register(
                    DailyCatcherCheckCommand,
                    DailyCatcherCheckExecutor(this@LorittaHelperKord)
                )
                register(
                    PendingScarletCommand,
                    PendingScarletExecutor(this@LorittaHelperKord, jda)
                )
                register(
                    PendingReportsCommand,
                    PendingReportsExecutor(this@LorittaHelperKord, jda)
                )
                register(
                    IPLocationCommand,
                    IPLocationExecutor(this@LorittaHelperKord)
                )
                register(
                    AttachDenyReasonCommand,
                    AttachDenyReasonExecutor(this@LorittaHelperKord, jda)
                )
                register(
                    AllTransactionsCommand,
                    AllTransactionsExecutor(this@LorittaHelperKord)
                )
                register(
                    DailyCheckCommand,
                    DailyCheckExecutor(this@LorittaHelperKord)
                )

                // ===[ BUTTON ROLES ]===
                register(
                    ButtonRoleSenderCommand,
                    ButtonRoleSenderExecutor(this@LorittaHelperKord)
                )
                register(
                    RoleToggleButtonExecutor,
                    RoleToggleButtonExecutor(this@LorittaHelperKord)
                )
                register(
                    RoleCoolBadgeButtonExecutor,
                    RoleCoolBadgeButtonExecutor(this@LorittaHelperKord)
                )
                register(
                    RoleColorButtonExecutor,
                    RoleColorButtonExecutor(this@LorittaHelperKord)
                )

                // ===[ TICKETS ]===
                register(
                    TicketSenderCommand,
                    TicketSenderExecutor(this@LorittaHelperKord)
                )
                register(
                    CloseTicketCommand,
                    CloseTicketExecutor(this@LorittaHelperKord)
                )
                register(
                    TicketUtilsCommand,
                    TicketInfoExecutor(this@LorittaHelperKord),
                    FindTicketExecutor(this@LorittaHelperKord)
                )
                register(
                    CreateTicketButtonExecutor,
                    CreateTicketButtonExecutor(this@LorittaHelperKord)
                )
                register(
                    CloseTicketButtonExecutor,
                    CloseTicketButtonExecutor(this@LorittaHelperKord)
                )
                register(
                    HelperResponseSelectMenuExecutor,
                    HelperResponseSelectMenuExecutor(this@LorittaHelperKord)
                )
                register(
                    DriveImageRetrieverCommand,
                    DriveImageRetrieverExecutor(this@LorittaHelperKord)
                )

                // ===[ REPORTS ]===
                register(
                    ShowUserIdExecutor,
                    ShowUserIdExecutor(this@LorittaHelperKord)
                )

                register(
                    ShowFilesExecutor,
                    ShowFilesExecutor(this@LorittaHelperKord)
                )

                // ===[ STATS ]===
                register(
                    StatsCommand,
                    StatsReportsExecutor(this@LorittaHelperKord),
                    StatsTicketsExecutor(this@LorittaHelperKord)
                )

                // ===[ LORI TOOLS ]===
                register(
                    LoriToolsCommand,
                    LoriBanExecutor(this@LorittaHelperKord),
                    LoriUnbanExecutor(this@LorittaHelperKord),
                    LoriBanRenameExecutor(this@LorittaHelperKord)
                )

                if (galleryOfDreamsClient != null) {
                    // ===[ FAN ARTS ]===
                    register(
                        GalleryOfDreamsSlashCommand,
                        AddFanArtToGallerySlashExecutor(this@LorittaHelperKord, galleryOfDreamsClient),
                        PatchFanArtSlashExecutor(this@LorittaHelperKord, galleryOfDreamsClient)
                    )

                    register(
                        AddFanArtToGalleryMessageCommand,
                        AddFanArtToGalleryMessageExecutor(this@LorittaHelperKord, galleryOfDreamsClient)
                    )

                    register(
                        AddFanArtToGalleryButtonExecutor,
                        AddFanArtToGalleryButtonExecutor(this@LorittaHelperKord, galleryOfDreamsClient)
                    )

                    register(
                        AddFanArtSelectAttachmentSelectMenuExecutor,
                        AddFanArtSelectAttachmentSelectMenuExecutor(this@LorittaHelperKord, galleryOfDreamsClient)
                    )

                    register(
                        AddFanArtSelectBadgesSelectMenuExecutor,
                        AddFanArtSelectBadgesSelectMenuExecutor(this@LorittaHelperKord, galleryOfDreamsClient)
                    )

                    register(
                        PatchFanArtOnGalleryButtonExecutor,
                        PatchFanArtOnGalleryButtonExecutor(this@LorittaHelperKord, galleryOfDreamsClient)
                    )

                    register(
                        PatchFanArtSelectBadgesSelectMenuExecutor,
                        PatchFanArtSelectBadgesSelectMenuExecutor(this@LorittaHelperKord, galleryOfDreamsClient)
                    )
                }
            }

            if (lorittaRest != null) {
                commandManager.register(
                    RetrieveMessageCommand,
                    RetrieveMessageExecutor(this@LorittaHelperKord, lorittaRest)
                )
                commandManager.register(
                    ServerMembersCommand,
                    ServerMembersExecutor(this@LorittaHelperKord, lorittaRest)
                )
            }

            val registry = KordCommandRegistry(
                Snowflake(config.applicationId),
                helperRest,
                commandManager
            )

            registry.updateAllCommandsInGuild(Snowflake(297732013006389252L))
            registry.updateAllCommandsInGuild(Snowflake(420626099257475072L))
            registry.updateAllCommandsInGuild(Snowflake(320248230917046282L))

            gateway.installDiscordInteraKTions(
                Snowflake(config.applicationId),
                helperRest,
                commandManager
            )

            TicketListener(this@LorittaHelperKord).installAutoReplyToMessagesInTicketListener(gateway)
            AutoCloseTicketWhenMemberLeavesGuildListener(this@LorittaHelperKord).installAutoCloseTicketWhenMemberLeavesGuildListener(gateway)
            CheckSonhosMendigagemTimeoutListener(this@LorittaHelperKord).installCheckSonhosMendigagemTimeoutListener(gateway)
            CheckSequenciaTimeoutListener(this@LorittaHelperKord).installCheckSequenciaTimeoutListener(gateway)
            LorittaBanTimeoutListener(this@LorittaHelperKord).installLorittaBanTimeout(gateway)

            gateway.start(config.token) {
                intents = Intents {
                    + Intent.GuildMessages
                    + Intent.GuildMembers
                    + Intent.MessageContent
                }
            }
        }
    }

    fun getTicketsCacheBySystemType(type: TicketUtils.TicketSystemType) = ticketsCache[type]!!
}