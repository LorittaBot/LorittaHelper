package net.perfectdreams.loritta.morenitta.interactions.commands

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions

data class SlashCommandDeclaration(
    val name: String,
    val description: String,
    val defaultMemberPermissions: DefaultMemberPermissions?,
    var isGuildOnly: Boolean,
    val executor: LorittaSlashCommandExecutor?,
    val subcommands: List<SlashCommandDeclaration>,
    val subcommandGroups: List<SlashCommandGroupDeclaration>
)

data class SlashCommandGroupDeclaration(
    val name: String,
    val description: String,
    val subcommands: List<SlashCommandDeclaration>
)

data class UserCommandDeclaration(
    val name: String,
    val defaultMemberPermissions: DefaultMemberPermissions?,
    var isGuildOnly: Boolean,
    val executor: LorittaUserCommandExecutor?
)

data class MessageCommandDeclaration(
    val name: String,
    val defaultMemberPermissions: DefaultMemberPermissions?,
    var isGuildOnly: Boolean,
    val executor: LorittaMessageCommandExecutor?
)