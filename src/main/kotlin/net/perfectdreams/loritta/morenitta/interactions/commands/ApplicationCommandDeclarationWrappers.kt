package net.perfectdreams.loritta.morenitta.interactions.commands

interface ApplicationCommandDeclarationWrapper

interface SlashCommandDeclarationWrapper {
    fun command(): SlashCommandDeclarationBuilder
}

interface UserCommandDeclarationWrapper : ApplicationCommandDeclarationWrapper {
    fun command(): UserCommandDeclarationBuilder
}

interface MessageCommandDeclarationWrapper : ApplicationCommandDeclarationWrapper {
    fun command(): MessageCommandDeclarationBuilder
}