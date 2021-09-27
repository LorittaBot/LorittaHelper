package net.perfectdreams.loritta.helper

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.DefaultGateway
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.start
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.perfectdreams.discordinteraktions.common.commands.CommandManager
import net.perfectdreams.discordinteraktions.platforms.kord.commands.KordCommandRegistry
import net.perfectdreams.discordinteraktions.platforms.kord.installDiscordInteraKTions
import net.perfectdreams.loritta.helper.utils.LanguageManager
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleColorButtonExecutor
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleCoolBadgeButtonExecutor
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleToggleButtonExecutor
import net.perfectdreams.loritta.helper.utils.cache.ChannelsCache
import net.perfectdreams.loritta.helper.utils.config.FanArtsConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import net.perfectdreams.loritta.helper.utils.slash.AllTransactionsExecutor
import net.perfectdreams.loritta.helper.utils.slash.AttachDenyReasonExecutor
import net.perfectdreams.loritta.helper.utils.slash.BroadcastDailyShopWinnersExecutor
import net.perfectdreams.loritta.helper.utils.slash.ButtonRoleSenderExecutor
import net.perfectdreams.loritta.helper.utils.slash.CheckCommandsExecutor
import net.perfectdreams.loritta.helper.utils.slash.DailyCatcherCheckExecutor
import net.perfectdreams.loritta.helper.utils.slash.DailyCheckExecutor
import net.perfectdreams.loritta.helper.utils.slash.FanArtsOverrideGetExecutor
import net.perfectdreams.loritta.helper.utils.slash.FanArtsOverrideResetExecutor
import net.perfectdreams.loritta.helper.utils.slash.FanArtsOverrideSetExecutor
import net.perfectdreams.loritta.helper.utils.slash.IPLocationExecutor
import net.perfectdreams.loritta.helper.utils.slash.PendingScarletExecutor
import net.perfectdreams.loritta.helper.utils.slash.RetrieveMessageExecutor
import net.perfectdreams.loritta.helper.utils.slash.ServerMembersExecutor
import net.perfectdreams.loritta.helper.utils.slash.TicketSenderExecutor
import net.perfectdreams.loritta.helper.utils.slash.declarations.AllTransactionsCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.AttachDenyReasonCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.BroadcastDailyShopWinnersCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.ButtonRoleSenderCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.CheckCommandsCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.DailyCatcherCheckCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.DailyCheckCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.FanArtsOverrideCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.IPLocationCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.PendingScarletCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.RetrieveMessageCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.ServerMembersCommand
import net.perfectdreams.loritta.helper.utils.slash.declarations.TicketSenderCommand
import net.perfectdreams.loritta.helper.utils.tickets.CloseTicketButtonExecutor
import net.perfectdreams.loritta.helper.utils.tickets.CreateTicketButtonExecutor
import net.perfectdreams.loritta.helper.utils.tickets.HelperResponseSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.tickets.TicketListener

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

    fun start() {
        val gateway = DefaultGateway()
        languageManager.loadLanguagesAndContexts()

        runBlocking {
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
                    FanArtsOverrideCommand,
                    FanArtsOverrideGetExecutor(this@LorittaHelperKord),
                    FanArtsOverrideSetExecutor(this@LorittaHelperKord),
                    FanArtsOverrideResetExecutor(this@LorittaHelperKord)
                )
                register(
                    PendingScarletCommand,
                    PendingScarletExecutor(this@LorittaHelperKord, jda)
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

            registry.updateAllCommandsInGuild(
                Snowflake(297732013006389252L),
                true
            )

            registry.updateAllCommandsInGuild(
                Snowflake(420626099257475072L),
                true
            )

            gateway.installDiscordInteraKTions(
                Snowflake(config.applicationId),
                helperRest,
                commandManager
            )

            TicketListener(this@LorittaHelperKord).installTicketListener(gateway)

            gateway.start(config.token) {
                intents = Intents {
                    + Intent.GuildMessages
                }
            }
        }
    }
}