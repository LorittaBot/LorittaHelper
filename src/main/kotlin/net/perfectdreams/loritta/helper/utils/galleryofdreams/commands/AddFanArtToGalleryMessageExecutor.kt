package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands

import net.perfectdreams.discordinteraktions.common.commands.message.MessageCommandExecutor
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.GuildApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.entities.messages.Message
import net.perfectdreams.discordinteraktions.declarations.commands.message.MessageCommandExecutorDeclaration
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.LorittaHelperKord

class AddFanArtToGalleryMessageExecutor(private val m: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : MessageCommandExecutor() {
    companion object : MessageCommandExecutorDeclaration(AddFanArtToGalleryMessageExecutor::class)

    override suspend fun execute(context: ApplicationCommandContext, targetMessage: Message) {
        context.deferChannelMessageEphemerally()

        if (context !is GuildApplicationCommandContext || !context.member.roles.any { it in GalleryOfDreamsUtils.ALLOWED_ROLES }) {
            context.sendEphemeralMessage {
                content = "Você não tem o poder de adicionar fan arts na galeria!"
            }
            return
        }

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