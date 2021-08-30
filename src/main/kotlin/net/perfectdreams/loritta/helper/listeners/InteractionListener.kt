package net.perfectdreams.loritta.helper.listeners

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordInteraction
import dev.kord.common.entity.InteractionType
import dev.kord.rest.service.RestClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import net.dv8tion.jda.api.events.RawGatewayEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.discordinteraktions.api.entities.Snowflake
import net.perfectdreams.discordinteraktions.common.commands.CommandManager
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithNoDataExecutor
import net.perfectdreams.discordinteraktions.common.context.InteractionRequestState
import net.perfectdreams.discordinteraktions.common.context.RequestBridge
import net.perfectdreams.discordinteraktions.common.context.buttons.ButtonClickContext
import net.perfectdreams.discordinteraktions.common.interactions.InteractionData
import net.perfectdreams.discordinteraktions.common.utils.Observable
import net.perfectdreams.discordinteraktions.platforms.kord.context.manager.InitialHttpRequestManager
import net.perfectdreams.discordinteraktions.platforms.kord.entities.KordUser
import net.perfectdreams.discordinteraktions.platforms.kord.entities.messages.KordPublicMessage
import net.perfectdreams.discordinteraktions.platforms.kord.utils.KordCommandChecker
import net.perfectdreams.discordinteraktions.platforms.kord.utils.toDiscordInteraKTionsResolvedObjects
import net.perfectdreams.discordinteraktions.platforms.kord.utils.toKordSnowflake

@OptIn(KordPreview::class)
class InteractionListener(
    val rest: RestClient,
    val applicationId: Snowflake,
    val commandManager: CommandManager
) : ListenerAdapter() {
    companion object {
        private val json = Json {
            // If there're any unknown keys, we'll ignore them instead of throwing an exception.
            this.ignoreUnknownKeys = true
        }

        private val logger = KotlinLogging.logger {}
    }

    private val kordCommandChecker = KordCommandChecker(commandManager)

    override fun onRawGateway(event: RawGatewayEvent) {
        logger.info { "Received event ${event.type}" }

        // Workaround for Discord InteraKTions!
        if (event.type != "INTERACTION_CREATE")
            return

        // From "GatewayKordInteractions.kt"
        val interactionsEvent = event.payload.toString()

        // Kord still has some fields missing (like "deaf") so we need to decode ignoring missing fields
        val request = json.decodeFromString<DiscordInteraction>(interactionsEvent)

        val observableState = Observable(InteractionRequestState.NOT_REPLIED_YET)
        val bridge = RequestBridge(observableState)

        val requestManager = InitialHttpRequestManager(
            bridge,
            rest,
            applicationId.toKordSnowflake(),
            request.token,
            request
        )

        bridge.manager = requestManager

        if (request.type == InteractionType.ApplicationCommand)
            kordCommandChecker.checkAndExecute(
                request,
                requestManager
            )
        else if (request.type == InteractionType.Component) {
            // If the button doesn't have a custom ID, we won't process it
            val buttonCustomId = request.data.customId.value ?: return

            val executorId = buttonCustomId.substringBefore(":")
            val data = buttonCustomId.substringAfter(":")

            val buttonExecutorDeclaration = commandManager.buttonDeclarations
                .asSequence()
                .filter {
                    it.id == executorId
                }
                .first()

            val executor = commandManager.buttonExecutors.first {
                it.signature() == buttonExecutorDeclaration.parent
            }

            val kordUser = KordUser(request.member.value?.user?.value ?: request.user.value ?: error("oh no"))
            val guildId =
                request.guildId.value?.let { net.perfectdreams.discordinteraktions.api.entities.Snowflake(it.value) }

            val interactionData = InteractionData(request.data.resolved.value?.toDiscordInteraKTionsResolvedObjects())

            val buttonClickContext = ButtonClickContext(
                bridge,
                kordUser,
                KordPublicMessage(request.message.value!!), // This should NEVER be null if it is a component message
                interactionData
            )

            GlobalScope.launch {
                if (executor is ButtonClickWithNoDataExecutor)
                    executor.onClick(
                        kordUser,
                        buttonClickContext
                    )
                else if (executor is ButtonClickWithDataExecutor)
                    executor.onClick(
                        kordUser,
                        buttonClickContext,
                        data
                    )
            }
        }
    }
}