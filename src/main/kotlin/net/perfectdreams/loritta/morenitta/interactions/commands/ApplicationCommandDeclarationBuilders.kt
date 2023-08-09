package net.perfectdreams.loritta.morenitta.interactions.commands

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.perfectdreams.i18nhelper.core.keydata.StringI18nData

// ===[ SLASH COMMANDS ]===
fun slashCommand(name: String, description: String, block: SlashCommandDeclarationBuilder.() -> (Unit)) = SlashCommandDeclarationBuilder(
    name,
    description
).apply(block)

@InteraKTionsUnleashedDsl
class SlashCommandDeclarationBuilder(
    val name: String,
    val description: String
) {
    var executor: LorittaSlashCommandExecutor? = null
    var defaultMemberPermissions: DefaultMemberPermissions? = null
    var isGuildOnly = false
    val subcommands = mutableListOf<SlashCommandDeclarationBuilder>()
    val subcommandGroups = mutableListOf<SlashCommandGroupDeclarationBuilder>()

    fun subcommand(name: String, description: String, block: SlashCommandDeclarationBuilder.() -> (Unit)) {
        subcommands.add(
            SlashCommandDeclarationBuilder(
                name,
                description
            ).apply(block)
        )
    }

    fun subcommandGroup(name: String, description: String, block: SlashCommandGroupDeclarationBuilder.() -> (Unit)) {
        subcommandGroups.add(
            SlashCommandGroupDeclarationBuilder(
                name,
                description
            ).apply(block)
        )
    }

    fun build(): SlashCommandDeclaration {
        return SlashCommandDeclaration(
            name,
            description,
            defaultMemberPermissions,
            isGuildOnly,
            executor,
            subcommands.map { it.build() },
            subcommandGroups.map { it.build() }
        )
    }
}

@InteraKTionsUnleashedDsl
class SlashCommandGroupDeclarationBuilder(
    val name: String,
    val description: String
) {
    // Groups can't have executors!
    val subcommands = mutableListOf<SlashCommandDeclarationBuilder>()

    fun subcommand(name: String, description: String, block: SlashCommandDeclarationBuilder.() -> (Unit)) {
        subcommands += SlashCommandDeclarationBuilder(
            name,
            description,
        ).apply(block)
    }

    fun build(): SlashCommandGroupDeclaration {
        return SlashCommandGroupDeclaration(
            name,
            description,
            subcommands.map { it.build() }
        )
    }
}

// ===[ USER COMMANDS ]===
fun userCommand(name: String, executor: LorittaUserCommandExecutor) = UserCommandDeclarationBuilder(name, executor)

@InteraKTionsUnleashedDsl
class UserCommandDeclarationBuilder(
    val name: String,
    val executor: LorittaUserCommandExecutor
) {
    var defaultMemberPermissions: DefaultMemberPermissions? = null
    var isGuildOnly = false

    fun build(): UserCommandDeclaration {
        return UserCommandDeclaration(
            name,
            defaultMemberPermissions,
            isGuildOnly,
            executor
        )
    }
}

// ===[ MESSAGE COMMANDS ]===
fun messageCommand(name: String, executor: LorittaMessageCommandExecutor) = MessageCommandDeclarationBuilder(name, executor)

@InteraKTionsUnleashedDsl
class MessageCommandDeclarationBuilder(
    val name: String,
    val executor: LorittaMessageCommandExecutor
) {
    var defaultMemberPermissions: DefaultMemberPermissions? = null
    var isGuildOnly = false

    fun build(): MessageCommandDeclaration {
        return MessageCommandDeclaration(
            name,
            defaultMemberPermissions,
            isGuildOnly,
            executor
        )
    }
}