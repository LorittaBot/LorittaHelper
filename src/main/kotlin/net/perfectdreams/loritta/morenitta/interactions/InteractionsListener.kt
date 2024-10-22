package net.perfectdreams.loritta.morenitta.interactions

import dev.minn.jda.ktx.interactions.commands.updateCommands
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import mu.KotlinLogging
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.morenitta.interactions.commands.*
import net.perfectdreams.loritta.morenitta.interactions.components.ComponentContext
import net.perfectdreams.loritta.morenitta.interactions.modals.ModalArguments
import net.perfectdreams.loritta.morenitta.interactions.modals.ModalContext
import org.postgresql.util.PGobject
import java.util.*

class InteractionsListener(private val loritta: LorittaHelper) : ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }
    private var hasAlreadyGloballyUpdatedTheCommands = false

    override fun onReady(event: ReadyEvent) {
        listOf(loritta.config.guilds.community.id, loritta.config.guilds.english.id, loritta.config.guilds.sparklyPower.id).forEach {
            val guild = event.jda.getGuildById(it)!!

            val commands = loritta.commandManager.slashCommands.map { loritta.commandManager.convertDeclarationToJDA(it) } + loritta.commandManager.userCommands.map { loritta.commandManager.convertDeclarationToJDA(it) } + loritta.commandManager.messageCommands.map { loritta.commandManager.convertDeclarationToJDA(it) }

            guild.updateCommands {
                addCommands(*commands.toTypedArray())
            }.queue()
        }
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        GlobalScope.launch {
            var rootDeclaration: SlashCommandDeclaration? = null
            var slashDeclaration: SlashCommandDeclaration? = null

            for (declaration in loritta.commandManager.slashCommands) {
                val rootLabel = event.name
                val subcommandGroupLabel = event.subcommandGroup
                val subcommandLabel = event.subcommandName

                if (rootLabel == declaration.name) {
                    if (subcommandGroupLabel == null && subcommandLabel == null) {
                        // Already found it, yay!
                        slashDeclaration = declaration
                        rootDeclaration = declaration
                    } else {
                        // Check root subcommands
                        if (subcommandLabel != null) {
                            if (subcommandGroupLabel == null) {
                                // "/name subcommand"
                                slashDeclaration =
                                    declaration.subcommands.firstOrNull { it.name == subcommandLabel }
                                rootDeclaration = declaration
                                break
                            } else {
                                // "/name subcommandGroup subcommand"
                                slashDeclaration = declaration.subcommandGroups.firstOrNull {
                                    it.name == subcommandGroupLabel
                                }
                                    ?.subcommands
                                    ?.firstOrNull {
                                        it.name == subcommandLabel
                                    }
                                rootDeclaration = declaration
                                break
                            }
                        }
                    }
                    break
                }
            }

            // We should throw an error here
            // But we won't because we still use Discord InteraKTions
            if (rootDeclaration == null || slashDeclaration == null)
                return@launch

            val executor = slashDeclaration.executor ?: error("Missing executor on $slashDeclaration!")

            val rootDeclarationClazzName = rootDeclaration::class.simpleName ?: "UnknownCommand"
            val executorClazzName = executor::class.simpleName ?: "UnknownExecutor"

            // These variables are used in the catch { ... } block, to make our lives easier
            var i18nContext: I18nContext? = null
            var context: ApplicationCommandContext? = null
            var stacktrace: String? = null

            try {
                val guild = event.guild
                val member = event.member

                val args = SlashCommandArguments(event)
                context = ApplicationCommandContext(
                    loritta,
                    event
                )

                executor.execute(
                    context,
                    args
                )
            } catch (e: CommandException) {
                context?.reply(e.ephemeral, e.builder)
            } catch (e: Exception) {
                // TODO: Proper catch and throw
                e.printStackTrace()

                stacktrace = e.stackTraceToString()
            }
        }
    }

    override fun onUserContextInteraction(event: UserContextInteractionEvent) {
        GlobalScope.launch {
            var rootDeclaration: UserCommandDeclaration? = null
            var slashDeclaration: UserCommandDeclaration? = null

            for (declaration in loritta.commandManager.userCommands) {
                val rootLabel = event.name

                if (rootLabel == declaration.name) {
                    slashDeclaration = declaration
                    rootDeclaration = declaration
                    break
                }
            }

            // We should throw an error here
            // But we won't because we still use Discord InteraKTions
            if (rootDeclaration == null || slashDeclaration == null)
                return@launch

            val executor = slashDeclaration.executor ?: error("Missing executor on $slashDeclaration!")

            val rootDeclarationClazzName = rootDeclaration::class.simpleName ?: "UnknownCommand"
            val executorClazzName = executor::class.simpleName ?: "UnknownExecutor"

            // These variables are used in the catch { ... } block, to make our lives easier
            var i18nContext: I18nContext? = null
            var context: ApplicationCommandContext? = null
            var stacktrace: String? = null

            try {
                val guild = event.guild
                val member = event.member

                context = ApplicationCommandContext(
                    loritta,
                    event
                )

                executor.execute(
                    context,
                    event.target
                )
            } catch (e: CommandException) {
                context?.reply(e.ephemeral, e.builder)
            } catch (e: Exception) {
                // TODO: Proper catch and throw
                e.printStackTrace()

                stacktrace = e.stackTraceToString()
            }
        }
    }

    override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
        GlobalScope.launch {
            var rootDeclaration: MessageCommandDeclaration? = null
            var slashDeclaration: MessageCommandDeclaration? = null

            for (declaration in loritta.commandManager.messageCommands) {
                val rootLabel = event.name

                if (rootLabel == declaration.name) {
                    slashDeclaration = declaration
                    rootDeclaration = declaration
                    break
                }
            }

            // We should throw an error here
            // But we won't because we still use Discord InteraKTions
            if (rootDeclaration == null || slashDeclaration == null)
                return@launch

            val executor = slashDeclaration.executor ?: error("Missing executor on $slashDeclaration!")

            val rootDeclarationClazzName = rootDeclaration::class.simpleName ?: "UnknownCommand"
            val executorClazzName = executor::class.simpleName ?: "UnknownExecutor"

            // These variables are used in the catch { ... } block, to make our lives easier
            var i18nContext: I18nContext? = null
            var context: ApplicationCommandContext? = null
            var stacktrace: String? = null

            try {
                val guild = event.guild
                val member = event.member

                context = ApplicationCommandContext(
                    loritta,
                    event
                )

                executor.execute(
                    context,
                    event.target
                )
            } catch (e: CommandException) {
                context?.reply(e.ephemeral, e.builder)
            } catch (e: Exception) {
                // TODO: Proper catch and throw
                e.printStackTrace()

                stacktrace = e.stackTraceToString()
            }
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        GlobalScope.launch {
            // Check if it is a InteraKTions Unleashed component
            val componentId = try {
                UnleashedComponentId(event.componentId)
            } catch (e: IllegalArgumentException) {
                return@launch
            }

            // These variables are used in the catch { ... } block, to make our lives easier
            var i18nContext: I18nContext? = null
            var context: ComponentContext? = null

            try {
                val guild = event.guild
                val member = event.member

                val callbackId = loritta.interactivityManager.buttonInteractionCallbacks[componentId.uniqueId]
                context = ComponentContext(
                    loritta,
                    event
                )

                // We don't know about this callback! It probably has expired, so let's tell the user about it
                if (callbackId == null) {
                    context.reply(true) {
                        styled(
                            "Opa, parece que os dados de interação desapareceram porque você demorou muito para usá-los! Por favor, use o comando novamente... Desculpe!",
                            Emotes.LoriSleeping
                        )
                    }
                    return@launch
                }

                callbackId.invoke(context)
            } catch (e: Exception) {
                // TODO: Proper catch and throw
                e.printStackTrace()
            }
        }
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        GlobalScope.launch {
            // Check if it is a InteraKTions Unleashed component
            val componentId = try {
                UnleashedComponentId(event.componentId)
            } catch (e: IllegalArgumentException) {
                return@launch
            }

            // These variables are used in the catch { ... } block, to make our lives easier
            var i18nContext: I18nContext? = null
            var context: ComponentContext? = null

            try {
                val guild = event.guild
                val member = event.member

                val callback = loritta.interactivityManager.selectMenuInteractionCallbacks[componentId.uniqueId]
                context = ComponentContext(
                    loritta,
                    event
                )
                // We don't know about this callback! It probably has expired, so let's tell the user about it
                if (callback == null) {
                    context.reply(true) {
                        styled(
                            "Opa, parece que os dados de interação desapareceram porque você demorou muito para usá-los! Por favor, use o comando novamente... Desculpe!",
                            Emotes.LoriSleeping
                        )
                    }
                    return@launch
                }

                callback.invoke(context, event.interaction.values)
            } catch (e: Exception) {
                // TODO: Proper catch and throw
                e.printStackTrace()
            }
        }
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        GlobalScope.launch {
            // Check if it is a InteraKTions Unleashed modal
            val modalId = try {
                UnleashedComponentId(event.modalId)
            } catch (e: IllegalArgumentException) {
                return@launch
            }

            // These variables are used in the catch { ... } block, to make our lives easier
            var i18nContext: I18nContext? = null
            var context: ModalContext? = null

            try {
                val guild = event.guild
                val member = event.member

                val modalCallback = loritta.interactivityManager.modalCallbacks[modalId.uniqueId]
                context = ModalContext(
                    loritta,
                    event
                )
                // We don't know about this callback! It probably has expired, so let's tell the user about it
                if (modalCallback == null) {
                    context.reply(true) {
                        styled(
                            "Opa, parece que os dados de interação desapareceram porque você demorou muito para usá-los! Por favor, use o comando novamente... Desculpe!",
                            Emotes.LoriSleeping
                        )
                    }
                    return@launch
                }

                modalCallback.invoke(context, ModalArguments(event))
            } catch (e: Exception) {
                // TODO: Proper catch and throw
                e.printStackTrace()
            }
        }
    }
}