package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add

import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.GuildApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.GalleryOfDreamsUtils
import net.perfectdreams.loritta.helper.utils.slash.HelperSlashExecutor
import net.perfectdreams.loritta.helper.utils.slash.PermissionLevel

class AddFanArtToGallerySlashExecutor(helper: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : HelperSlashExecutor(helper, PermissionLevel.FAN_ARTS_MANAGER) {
    companion object {
        val messageLinkRegex = Regex("https?://(?:[A-z]+\\.)?discord\\.com/channels/([0-9]+)/([0-9]+)/([0-9]+)")
    }

    inner class Options : ApplicationCommandOptions() {
        val messageUrl = string("message_url", "Link da Mensagem da Fan Art")

        val extensionOverride = optionalString("extension_override", "Substitui a extensão da Fan Art enviada caso o usuário tenha enviado com uma extensão errada")

        val userOverride = optionalUser("user_override", "Substitui o usuário da Fan Art enviada para outro usuário")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessageEphemerally()

        if (context !is GuildApplicationCommandContext || !context.member.roleIds.any { it in GalleryOfDreamsUtils.ALLOWED_ROLES }) {
            context.sendEphemeralMessage {
                content = "Você não tem o poder de adicionar fan arts na galeria!"
            }
            return
        }

        val link = messageLinkRegex.matchEntire(args[options.messageUrl])

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

        val artistId = args[options.userOverride]?.id ?: targetMessage.author.id
        val artistName = args[options.userOverride]?.username ?: targetMessage.author.username

        val artist = galleryOfDreamsClient.getFanArtArtistByDiscordId(artistId.value.toLong())

        val builtMessage = GalleryOfDreamsUtils.createAddFanArtMessage(
            if (artist == null) {
                AddFanArtToNewArtistData(
                    artistId,
                    artistName,
                    // Cleans up the user's name to make it be the user's name, if the result is a empty string we use a "ifEmpty" call to change it to the user's ID
                    artistName.lowercase().replace(" ", "-").replace(Regex("[^A-Za-z0-9-]"), "").trim()
                        .ifEmpty { targetMessage.author.id.value.toString() },
                    targetMessage.channelId,
                    targetMessage.id,
                    args[options.extensionOverride],
                    null,
                    listOf()
                )
            } else {
                AddFanArtToExistingArtistData(
                    artistId,
                    artist.id,
                    artist.slug,
                    targetMessage.channelId,
                    targetMessage.id,
                    args[options.extensionOverride],
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
