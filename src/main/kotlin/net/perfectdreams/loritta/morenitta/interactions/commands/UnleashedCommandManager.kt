package net.perfectdreams.loritta.morenitta.interactions.commands

import dev.kord.common.Locale
import dev.minn.jda.ktx.interactions.commands.Option
import dev.minn.jda.ktx.interactions.commands.choice
import dev.minn.jda.ktx.interactions.commands.group
import dev.minn.jda.ktx.interactions.commands.subcommand
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandDeclaration
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandGroupDeclaration
import net.perfectdreams.discordinteraktions.common.commands.options.*
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.morenitta.interactions.commands.options.*
import net.perfectdreams.loritta.morenitta.interactions.commands.options.OptionReference

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

    /**
     * Converts a Discord InteraKTions [declaration] to JDA
     *
     * This is provided for backwards compatibility!
     */
    fun convertInteraKTionsDeclarationToJDA(declaration: ApplicationCommandDeclaration): CommandData {
        when (declaration) {
            is net.perfectdreams.discordinteraktions.common.commands.UserCommandDeclaration -> {
                return Commands.user(declaration.name).apply {
                    declaration.nameLocalizations?.mapKeysToJDALocales()
                        ?.also { setNameLocalizations(it) }
                    val defPermissions = declaration.defaultMemberPermissions
                    if (defPermissions != null)
                        defaultPermissions = DefaultMemberPermissions.enabledFor(defPermissions.code.value.toLong())
                    val dmPermission = declaration.dmPermission
                    if (dmPermission != null)
                        isGuildOnly = !dmPermission
                }
            }

            is net.perfectdreams.discordinteraktions.common.commands.MessageCommandDeclaration -> {
                return Commands.message(declaration.name).apply {
                    declaration.nameLocalizations?.mapKeysToJDALocales()
                        ?.also { setNameLocalizations(it) }
                    val defPermissions = declaration.defaultMemberPermissions
                    if (defPermissions != null)
                        defaultPermissions = DefaultMemberPermissions.enabledFor(defPermissions.code.value.toLong())
                    val dmPermission = declaration.dmPermission
                    if (dmPermission != null)
                        isGuildOnly = !dmPermission
                }
            }

            is net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclaration -> {
                return Commands.slash(declaration.name, declaration.description).apply {
                    declaration.nameLocalizations?.mapKeysToJDALocales()
                        ?.also { setNameLocalizations(it) }
                    declaration.descriptionLocalizations?.mapKeysToJDALocales()
                        ?.also { setDescriptionLocalizations(it) }
                    val defPermissions = declaration.defaultMemberPermissions
                    if (defPermissions != null)
                        defaultPermissions = DefaultMemberPermissions.enabledFor(defPermissions.code.value.toLong())
                    val dmPermission = declaration.dmPermission
                    if (dmPermission != null)
                        isGuildOnly = !dmPermission

                    // We can only have (subcommands OR subcommand groups) OR arguments
                    if (declaration.subcommands.isNotEmpty() || declaration.subcommandGroups.isNotEmpty()) {
                        for (subcommandDeclaration in declaration.subcommands) {
                            subcommand(subcommandDeclaration.name, subcommandDeclaration.description) {
                                val executor = subcommandDeclaration.executor

                                require(executor != null) { "Subcommand command without a executor!" }

                                subcommandDeclaration.nameLocalizations?.mapKeysToJDALocales()
                                    ?.also { setNameLocalizations(it) }
                                subcommandDeclaration.descriptionLocalizations?.mapKeysToJDALocales()
                                    ?.also { setDescriptionLocalizations(it) }

                                for (option in executor.options.registeredOptions) {
                                    addOptions(*createOption(option).toTypedArray())
                                }
                            }
                        }

                        for (subcommandGroupDeclaration in declaration.subcommandGroups) {
                            group(subcommandGroupDeclaration.name, subcommandGroupDeclaration.description) {
                                subcommandGroupDeclaration.nameLocalizations?.mapKeysToJDALocales()
                                    ?.also { setNameLocalizations(it) }
                                subcommandGroupDeclaration.descriptionLocalizations?.mapKeysToJDALocales()
                                    ?.also { setDescriptionLocalizations(it) }

                                for (subcommandDeclaration in subcommandGroupDeclaration.subcommands) {
                                    subcommand(subcommandDeclaration.name, subcommandDeclaration.description) {
                                        val executor = subcommandDeclaration.executor

                                        require(executor != null) { "Subcommand command without a executor!" }

                                        subcommandDeclaration.nameLocalizations?.mapKeysToJDALocales()
                                            ?.also { setNameLocalizations(it) }
                                        subcommandDeclaration.descriptionLocalizations?.mapKeysToJDALocales()
                                            ?.also { setDescriptionLocalizations(it) }

                                        for (option in executor.options.registeredOptions) {
                                            addOptions(*createOption(option).toTypedArray())
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        val executor = declaration.executor

                        require(executor != null) { "Root command without a executor!" }

                        for (option in executor.options.registeredOptions) {
                            addOptions(*createOption(option).toTypedArray())
                        }
                    }
                }
            }
            is SlashCommandGroupDeclaration -> {
                error("This should never be called because the convertInteraKTionsDeclarationToJDA method is only called on a root command!")
            }
        }
    }

    /**
     * Converts a Discord InteraKTions [InteraKTionsCommandOption] to JDA
     *
     * This is provided for backwards compatibility!
     */
    private fun createOption(interaKTionsOption: InteraKTionsCommandOption<*>): List<OptionData> {
        when (interaKTionsOption) {
            is StringCommandOption -> {
                return listOf(
                    Option<String>(
                        name = interaKTionsOption.name,
                        description = interaKTionsOption.description,
                        required = interaKTionsOption.required,
                        autocomplete = interaKTionsOption.autocompleteExecutor != null,
                    ) {
                        val localizedNames = interaKTionsOption.nameLocalizations?.mapKeysToJDALocales()
                        if (localizedNames != null)
                            this.setNameLocalizations(localizedNames)
                        val localizedDescriptions = interaKTionsOption.descriptionLocalizations?.mapKeysToJDALocales()
                        if (localizedDescriptions != null)
                            this.setDescriptionLocalizations(localizedDescriptions)
                        interaKTionsOption.minLength?.let {
                            if (it != 0)
                                setMinLength(it)
                        }
                        interaKTionsOption.maxLength?.let {
                            if (it != 0)
                                setMaxLength(it)
                        }

                        interaKTionsOption.choices?.forEach {
                            this.addChoices(
                                net.dv8tion.jda.api.interactions.commands.Command.Choice(it.name, it.value)
                                    .apply {
                                        val localizedOptionNames = it.nameLocalizations?.mapKeysToJDALocales()
                                        if (localizedOptionNames != null)
                                            this.setNameLocalizations(localizedOptionNames)
                                    }
                            )
                        }
                    }
                )
            }
            is IntegerCommandOption -> {
                return listOf(
                    Option<Long>(
                        name = interaKTionsOption.name,
                        description = interaKTionsOption.description,
                        required = interaKTionsOption.required,
                        autocomplete = interaKTionsOption.autocompleteExecutor != null,
                    ) {
                        val localizedNames = interaKTionsOption.nameLocalizations?.mapKeysToJDALocales()
                        if (localizedNames != null)
                            this.setNameLocalizations(localizedNames)
                        val localizedDescriptions = interaKTionsOption.descriptionLocalizations?.mapKeysToJDALocales()
                        if (localizedDescriptions != null)
                            this.setDescriptionLocalizations(localizedDescriptions)
                        interaKTionsOption.minValue?.let {
                            setMinValue(it)
                        }
                        interaKTionsOption.maxValue?.let {
                            setMaxValue(it)
                        }

                        interaKTionsOption.choices?.forEach {
                            this.addChoices(
                                net.dv8tion.jda.api.interactions.commands.Command.Choice(it.name, it.value)
                                    .apply {
                                        val localizedOptionNames = it.nameLocalizations?.mapKeysToJDALocales()
                                        if (localizedOptionNames != null)
                                            this.setNameLocalizations(localizedOptionNames)
                                    }
                            )
                        }
                    }
                )
            }
            is NumberCommandOption -> {
                return listOf(
                    Option<Double>(
                        name = interaKTionsOption.name,
                        description = interaKTionsOption.description,
                        required = interaKTionsOption.required,
                        autocomplete = interaKTionsOption.autocompleteExecutor != null,
                    ) {
                        val localizedNames = interaKTionsOption.nameLocalizations?.mapKeysToJDALocales()
                        if (localizedNames != null)
                            this.setNameLocalizations(localizedNames)
                        val localizedDescriptions = interaKTionsOption.descriptionLocalizations?.mapKeysToJDALocales()
                        if (localizedDescriptions != null)
                            this.setDescriptionLocalizations(localizedDescriptions)
                        interaKTionsOption.minValue?.let {
                            setMinValue(it)
                        }
                        interaKTionsOption.maxValue?.let {
                            setMaxValue(it)
                        }

                        interaKTionsOption.choices?.forEach {
                            this.addChoices(
                                net.dv8tion.jda.api.interactions.commands.Command.Choice(it.name, it.value)
                                    .apply {
                                        val localizedOptionNames = it.nameLocalizations?.mapKeysToJDALocales()
                                        if (localizedOptionNames != null)
                                            this.setNameLocalizations(localizedOptionNames)
                                    }
                            )
                        }
                    }
                )
            }
            is BooleanCommandOption -> {
                return listOf(
                    Option<Boolean>(
                        name = interaKTionsOption.name,
                        description = interaKTionsOption.description,
                        required = interaKTionsOption.required,
                    ) {
                        val localizedNames = interaKTionsOption.nameLocalizations?.mapKeysToJDALocales()
                        if (localizedNames != null)
                            this.setNameLocalizations(localizedNames)
                        val localizedDescriptions = interaKTionsOption.descriptionLocalizations?.mapKeysToJDALocales()
                        if (localizedDescriptions != null)
                            this.setDescriptionLocalizations(localizedDescriptions)
                    }
                )
            }
            is UserCommandOption -> {
                return listOf(
                    Option<User>(
                        name = interaKTionsOption.name,
                        description = interaKTionsOption.description,
                        required = interaKTionsOption.required,
                    ) {
                        val localizedNames = interaKTionsOption.nameLocalizations?.mapKeysToJDALocales()
                        if (localizedNames != null)
                            this.setNameLocalizations(localizedNames)
                        val localizedDescriptions = interaKTionsOption.descriptionLocalizations?.mapKeysToJDALocales()
                        if (localizedDescriptions != null)
                            this.setDescriptionLocalizations(localizedDescriptions)
                    }
                )
            }
            is RoleCommandOption -> {
                return listOf(
                    Option<Role>(
                        name = interaKTionsOption.name,
                        description = interaKTionsOption.description,
                        required = interaKTionsOption.required,
                    ) {
                        val localizedNames = interaKTionsOption.nameLocalizations?.mapKeysToJDALocales()
                        if (localizedNames != null)
                            this.setNameLocalizations(localizedNames)
                        val localizedDescriptions = interaKTionsOption.descriptionLocalizations?.mapKeysToJDALocales()
                        if (localizedDescriptions != null)
                            this.setDescriptionLocalizations(localizedDescriptions)
                    }
                )
            }
            is ChannelCommandOption -> {
                return listOf(
                    Option<Channel>(
                        name = interaKTionsOption.name,
                        description = interaKTionsOption.description,
                        required = interaKTionsOption.required,
                    ) {
                        val localizedNames = interaKTionsOption.nameLocalizations?.mapKeysToJDALocales()
                        if (localizedNames != null)
                            this.setNameLocalizations(localizedNames)
                        val localizedDescriptions = interaKTionsOption.descriptionLocalizations?.mapKeysToJDALocales()
                        if (localizedDescriptions != null)
                            this.setDescriptionLocalizations(localizedDescriptions)
                    }
                )
            }
            is MentionableCommandOption -> {
                return listOf(
                    Option<IMentionable>(
                        name = interaKTionsOption.name,
                        description = interaKTionsOption.description,
                        required = interaKTionsOption.required,
                    ) {
                        val localizedNames = interaKTionsOption.nameLocalizations?.mapKeysToJDALocales()
                        if (localizedNames != null)
                            this.setNameLocalizations(localizedNames)
                        val localizedDescriptions = interaKTionsOption.descriptionLocalizations?.mapKeysToJDALocales()
                        if (localizedDescriptions != null)
                            this.setDescriptionLocalizations(localizedDescriptions)
                    }
                )
            }
            is AttachmentCommandOption -> {
                return listOf(
                    Option<Message.Attachment>(
                        name = interaKTionsOption.name,
                        description = interaKTionsOption.description,
                        required = interaKTionsOption.required,
                    ) {
                        val localizedNames = interaKTionsOption.nameLocalizations?.mapKeysToJDALocales()
                        if (localizedNames != null)
                            this.setNameLocalizations(localizedNames)
                        val localizedDescriptions = interaKTionsOption.descriptionLocalizations?.mapKeysToJDALocales()
                        if (localizedDescriptions != null)
                            this.setDescriptionLocalizations(localizedDescriptions)
                    }
                )
            }
            else -> error("Unknown Discord InteraKTions option type ${interaKTionsOption::class}")
        }
    }

    private fun Map<Locale, String>.mapKeysToJDALocales() = this.mapKeys {
        val language = it.key.language
        val country = it.key.country
        var locale = language
        if (country != null)
            locale += "-$country"

        DiscordLocale.from(locale)
    }
}