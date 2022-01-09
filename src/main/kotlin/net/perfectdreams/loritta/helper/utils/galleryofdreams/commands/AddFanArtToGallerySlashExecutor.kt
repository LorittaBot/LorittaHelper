package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.GuildApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.HelperSlashExecutor

class AddFanArtToGallerySlashExecutor(helper: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(AddFanArtToGallerySlashExecutor::class) {
        object Options : CommandOptions() {
            val messageUrl = string("message_url", "Link da Mensagem da Fan Art")
                .register()
        }

        override val options = Options

        val messageLinkRegex = Regex("https?://(?:[A-z]+\\.)?discord\\.com/channels/([0-9]+)/([0-9]+)/([0-9]+)")
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessageEphemerally()

        if (context !is GuildApplicationCommandContext || !context.member.roles.any { it in GalleryOfDreamsUtils.ALLOWED_ROLES }) {
            context.sendEphemeralMessage {
                content = "Você não tem o poder de adicionar fan arts na galeria!"
            }
            return
        }

        val link = messageLinkRegex.matchEntire(args[Options.messageUrl])

        if (link == null) {
            context.sendEphemeralMessage {
                content = "Você não passou o link de uma mensagem!"
            }
            return
        }

        val (_, guildIdAsString, channelIdAsString, messageIdAsString) = link.groupValues

        val targetMessage = helper.helperRest.channel.getMessage(Snowflake(channelIdAsString), Snowflake(messageIdAsString))
        val attachments = targetMessage.attachments

        if (attachments.isEmpty()) {
            context.sendEphemeralMessage {
                content = "Não existe nenhuma imagem na mensagem que você selecionou!"
            }
            return
        }

        val artist = galleryOfDreamsClient.getFanArtArtistByDiscordId(targetMessage.author.id.value.toLong())

        if (artist == null) {
            context.sendEphemeralMessage {
                content = "O usuário não é um artista!"
            }
            return
        }

        val builtMessage = GalleryOfDreamsUtils.createMessage(
            AddFanArtData(
                artist.id,
                artist.slug,
                targetMessage.channelId,
                targetMessage.id,
                null,
                listOf()
            ),
            attachments
        )

        context.sendEphemeralMessage {
            apply(builtMessage)
        }
    }
}
