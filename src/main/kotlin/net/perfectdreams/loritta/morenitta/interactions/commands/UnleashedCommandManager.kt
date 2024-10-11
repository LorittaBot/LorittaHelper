package net.perfectdreams.loritta.morenitta.interactions.commands

import dev.minn.jda.ktx.interactions.commands.Option
import dev.minn.jda.ktx.interactions.commands.choice
import dev.minn.jda.ktx.interactions.commands.group
import dev.minn.jda.ktx.interactions.commands.subcommand
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.morenitta.interactions.commands.options.*

class UnleashedCommandManager(val loritta: LorittaHelper) {
    val slashCommands = mutableListOf<SlashCommandDeclaration>()
    val userCommands = mutableListOf<UserCommandDeclaration>()
    val messageCommands = mutableListOf<MessageCommandDeclaration>()

    fun register(declaration: SlashCommandDeclarationWrapper) {
        slashCommands += declaration.command().build()
    }

    fun register(declaration: UserCommandDeclarationWrapper) {
        userCommands += declaration.command().build()
    }

    fun register(declaration: MessageCommandDeclarationWrapper) {
        messageCommands += declaration.command().build()
    }

    init {
    }

    /**
     * Converts a InteraKTions Unleashed [declaration] to JDA
     */
    fun convertDeclarationToJDA(declaration: SlashCommandDeclaration): SlashCommandData {
        return Commands.slash(declaration.name, declaration.description).apply {
            if (declaration.defaultMemberPermissions != null)
                this.defaultPermissions = declaration.defaultMemberPermissions
            this.isGuildOnly = declaration.isGuildOnly

            if (declaration.subcommands.isNotEmpty() || declaration.subcommandGroups.isNotEmpty()) {
                if (declaration.executor != null)
                    error("Command ${declaration::class.simpleName} has a root executor, but it also has subcommand/subcommand groups!")

                for (subcommand in declaration.subcommands) {
                    subcommand(subcommand.name, subcommand.description) {
                        val executor = subcommand.executor ?: error("Subcommand does not have a executor!")

                        for (ref in executor.options.registeredOptions) {
                            addOptions(*createOption(ref).toTypedArray())
                        }
                    }
                }

                for (group in declaration.subcommandGroups) {
                    group(group.name, group.description) {
                        for (subcommand in group.subcommands) {
                            subcommand(subcommand.name, subcommand.description) {
                                val executor = subcommand.executor ?: error("Subcommand does not have a executor!")

                                for (ref in executor.options.registeredOptions) {
                                    addOptions(*createOption(ref).toTypedArray())
                                }
                            }
                        }
                    }
                }
            } else {
                val executor = declaration.executor

                if (executor != null) {
                    for (ref in executor.options.registeredOptions) {
                        addOptions(*createOption(ref).toTypedArray())
                    }
                }
            }
        }
    }

    /**
     * Converts a InteraKTions Unleashed [declaration] to JDA
     */
    fun convertDeclarationToJDA(declaration: UserCommandDeclaration): CommandData {
        return Commands.user(declaration.name).apply {
            if (declaration.defaultMemberPermissions != null)
                this.defaultPermissions = declaration.defaultMemberPermissions
            this.isGuildOnly = declaration.isGuildOnly
        }
    }

    /**
     * Converts a InteraKTions Unleashed [declaration] to JDA
     */
    fun convertDeclarationToJDA(declaration: MessageCommandDeclaration): CommandData {
        return Commands.message(declaration.name).apply {
            if (declaration.defaultMemberPermissions != null)
                this.defaultPermissions = declaration.defaultMemberPermissions
            this.isGuildOnly = declaration.isGuildOnly
        }
    }

    private fun createOption(interaKTionsOption: OptionReference<*>): List<OptionData> {
        when (interaKTionsOption) {
            is DiscordOptionReference -> {
                val description = interaKTionsOption.description

                when (interaKTionsOption) {
                    is LongDiscordOptionReference -> {
                        return listOf(
                            Option<Long>(
                                interaKTionsOption.name,
                                description,
                                interaKTionsOption.required
                            ).apply {
                                if (interaKTionsOption.requiredRange != null) {
                                    setRequiredRange(interaKTionsOption.requiredRange.first, interaKTionsOption.requiredRange.last)
                                }
                            }
                        )
                    }

                    is StringDiscordOptionReference -> {
                        return listOf(
                            Option<String>(
                                interaKTionsOption.name,
                                description,
                                interaKTionsOption.required
                            ).apply {
                                for (choice in interaKTionsOption.choices) {
                                    when (choice) {
                                        is StringDiscordOptionReference.Choice.LocalizedChoice -> {
                                            addChoices(
                                                Command.Choice(choice.name, choice.value)
                                            )
                                        }
                                        is StringDiscordOptionReference.Choice.RawChoice -> choice(choice.name, choice.value)
                                    }
                                }
                            }
                        )
                    }

                    is BooleanDiscordOptionReference -> {
                        return listOf(
                            Option<Boolean>(
                                interaKTionsOption.name,
                                description,
                                interaKTionsOption.required
                            )
                        )
                    }

                    is UserDiscordOptionReference -> {
                        return listOf(
                            Option<User>(
                                interaKTionsOption.name,
                                description,
                                interaKTionsOption.required
                            )
                        )
                    }

                    is ChannelDiscordOptionReference -> {
                        return listOf(
                            Option<Channel>(
                                interaKTionsOption.name,
                                description,
                                interaKTionsOption.required
                            )
                        )
                    }
                }
            }
        }
    }
}