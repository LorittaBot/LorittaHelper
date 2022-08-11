package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.GuildApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.MessageCommandExecutor
import net.perfectdreams.discordinteraktions.common.entities.messages.Message
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.GalleryOfDreamsUtils

class AddFanArtToGalleryMessageExecutor(private val m: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : MessageCommandExecutor() {
    override suspend fun execute(context: ApplicationCommandContext, targetMessage: Message) {
        context.deferChannelMessageEphemerally()

        if (context !is GuildApplicationCommandContext || !context.member.roleIds.any { it in GalleryOfDreamsUtils.ALLOWED_ROLES }) {
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

        val builtMessage = GalleryOfDreamsUtils.createAddFanArtMessage(
            if (artist == null) {
                AddFanArtToNewArtistData(
                    targetMessage.author.id,
                    targetMessage.author.username,
                    // Cleans up the user's name to make it be the user's name, if the result is a empty string we use a "ifEmpty" call to change it to the user's ID
                    targetMessage.author.username.lowercase().replace(" ", "-").replace(Regex("[^A-Za-z0-9-]"), "").trim()
                        .ifEmpty { targetMessage.author.id.value.toString() },
                    targetMessage.channelId,
                    targetMessage.id,
                    null,
                    null,
                    listOf()
                )
            } else {
                AddFanArtToExistingArtistData(
                    targetMessage.author.id,
                    artist.id,
                    artist.slug,
                    targetMessage.channelId,
                    targetMessage.id,
                    null,
                    null,
                    listOf()
                )
            },
            attachments
        )

        context.sendEphemeralMessage {
            apply(builtMessage)
        }
    }
}