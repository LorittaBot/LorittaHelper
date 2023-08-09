package net.perfectdreams.loritta.helper

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.*
import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.interactions.commands.updateCommands
import io.ktor.client.*
import io.ktor.client.plugins.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.perfectdreams.discordinteraktions.common.DiscordInteraKTions
import net.perfectdreams.discordinteraktions.platforms.kord.installDiscordInteraKTions
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.utils.LanguageManager
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleColorButtonExecutor
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleCoolBadgeButtonExecutor
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleToggleButtonExecutor
import net.perfectdreams.loritta.helper.utils.cache.ChannelsCache
import net.perfectdreams.loritta.helper.utils.checksonhosmendigagem.CheckSequenciaTimeoutListener
import net.perfectdreams.loritta.helper.utils.checksonhosmendigagem.CheckSonhosMendigagemTimeoutListener
import net.perfectdreams.loritta.helper.utils.config.FanArtsConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtSelectAttachmentSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtSelectBadgesSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtToGalleryButtonExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.declarations.AddFanArtToGalleryMessageCommand
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.declarations.GalleryOfDreamsSlashCommand
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch.PatchFanArtOnGalleryButtonExecutor
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch.PatchFanArtSelectBadgesSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.generateserverreport.ShowFilesExecutor
import net.perfectdreams.loritta.helper.utils.generateserverreport.ShowUserIdExecutor
import net.perfectdreams.loritta.helper.utils.loribantimeout.LorittaBanTimeoutListener
import net.perfectdreams.loritta.helper.utils.slash.declarations.*
import net.perfectdreams.loritta.helper.utils.tickets.*

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

    val interaKTions = DiscordInteraKTions(config.token, Snowflake(config.applicationId))

    val helperRest = helper.helperRest
    val lorittaRest = helper.lorittaRest
    val databases = helper.databases
    val dailyCatcherManager = helper.dailyCatcherManager
    val dailyShopWinners = helper.dailyShopWinners
    val languageManager = LanguageManager(
        LorittaHelperKord::class,
        "en",
        "/languages/"
    )
    val ticketUtils = TicketUtils(this)
    val channelsCache = ChannelsCache(helperRest)

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
            for (system in ticketUtils.systems.values) {
                val type = system.systemType
                val cache = system.cache
                logger.info { "Populating ${type}'s ticket cache..." }
                cache.populateCache()
                logger.info { "Now tracking ${cache.tickets.size} tickets!" }
            }

            // Register Commands
            with(interaKTions.manager) {
                register(CheckCommandsCommand(this@LorittaHelperKord))

                register(DailyCatcherCheckCommand(this@LorittaHelperKord))
                register(PendingScarletCommand(this@LorittaHelperKord, jda))
                register(PendingReportsCommand(this@LorittaHelperKord, jda))
                register(IPLocationCommand(this@LorittaHelperKord))
                register(AttachDenyReasonCommand(this@LorittaHelperKord, jda))
                register(AllTransactionsCommand(this@LorittaHelperKord))
                register(DailyCheckCommand(this@LorittaHelperKord))
                register(DirectDiscordCdnMessageCommand(this@LorittaHelperKord))

                // ===[ BUTTON ROLES ]===
                register(ButtonRoleSenderCommand(this@LorittaHelperKord))
                register(RoleToggleButtonExecutor(this@LorittaHelperKord))
                register(RoleCoolBadgeButtonExecutor(this@LorittaHelperKord))
                register(RoleColorButtonExecutor(this@LorittaHelperKord))

                // ===[ TICKETS ]===
                register(TicketSenderCommand(this@LorittaHelperKord))
                register(CloseTicketCommand(this@LorittaHelperKord))
                register(TicketUtilsCommand(this@LorittaHelperKord))
                register(CreateTicketButtonExecutor(this@LorittaHelperKord))
                register(CloseTicketButtonExecutor(this@LorittaHelperKord))
                register(HelperResponseSelectMenuExecutor(this@LorittaHelperKord))
                register(DriveImageRetrieverCommand(this@LorittaHelperKord))

                // ===[ REPORTS ]===
                register(ShowUserIdExecutor(this@LorittaHelperKord))
                register(ShowFilesExecutor(this@LorittaHelperKord))

                // ===[ STATS ]===
                register(StatsCommand(this@LorittaHelperKord))

                if (galleryOfDreamsClient != null) {
                    // ===[ FAN ARTS ]===
                    register(GalleryOfDreamsSlashCommand(this@LorittaHelperKord, galleryOfDreamsClient))

                    register(AddFanArtToGalleryMessageCommand(this@LorittaHelperKord, galleryOfDreamsClient))

                    register(AddFanArtToGalleryButtonExecutor(this@LorittaHelperKord, galleryOfDreamsClient))

                    register(AddFanArtSelectAttachmentSelectMenuExecutor(this@LorittaHelperKord, galleryOfDreamsClient))

                    register(AddFanArtSelectBadgesSelectMenuExecutor(this@LorittaHelperKord, galleryOfDreamsClient))

                    register(PatchFanArtOnGalleryButtonExecutor(this@LorittaHelperKord, galleryOfDreamsClient))

                    register(PatchFanArtSelectBadgesSelectMenuExecutor(this@LorittaHelperKord, galleryOfDreamsClient))
                }

                if (lorittaRest != null) {
                    register(RetrieveMessageCommand(this@LorittaHelperKord, lorittaRest))

                    register(ServerMembersCommand(this@LorittaHelperKord, lorittaRest))
                }
            }

            listOf(297732013006389252L, 420626099257475072L, 320248230917046282L).forEach {
                val guild = jda.getGuildById(it)

                guild?.updateCommands {
                    val commands = interaKTions.manager.applicationCommandsDeclarations.map { helper.commandManager.convertInteraKTionsDeclarationToJDA(it) } + helper.commandManager.slashCommands.map { helper.commandManager.convertDeclarationToJDA(it) } +
                            helper.commandManager.userCommands.map {
                                helper.commandManager.convertDeclarationToJDA(
                                    it
                                )
                            } +
                            helper.commandManager.messageCommands.map {
                                helper.commandManager.convertDeclarationToJDA(
                                    it
                                )
                            }
                    addCommands(commands)
                }?.await()
            }

            TicketListener(this@LorittaHelperKord).installAutoReplyToMessagesInTicketListener(gateway)
            AutoCloseTicketWhenMemberLeavesGuildListener(this@LorittaHelperKord).installAutoCloseTicketWhenMemberLeavesGuildListener(
                gateway
            )
            CheckSonhosMendigagemTimeoutListener(this@LorittaHelperKord).installCheckSonhosMendigagemTimeoutListener(
                gateway
            )
            CheckSequenciaTimeoutListener(this@LorittaHelperKord).installCheckSequenciaTimeoutListener(gateway)
            LorittaBanTimeoutListener(this@LorittaHelperKord).installLorittaBanTimeout(gateway)

            gateway.start(config.token) {
                intents = Intents {
                    +Intent.GuildMessages
                    +Intent.GuildMembers
                    +Intent.MessageContent
                }
            }
        }
    }
}