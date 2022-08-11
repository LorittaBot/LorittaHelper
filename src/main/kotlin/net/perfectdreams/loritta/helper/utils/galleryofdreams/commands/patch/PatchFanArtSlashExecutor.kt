package net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.patch

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.GuildApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.GalleryOfDreamsUtils
import net.perfectdreams.loritta.helper.utils.slash.HelperSlashExecutor
import net.perfectdreams.loritta.helper.utils.slash.PermissionLevel

class PatchFanArtSlashExecutor(helper: LorittaHelperKord, val galleryOfDreamsClient: GalleryOfDreamsClient) : HelperSlashExecutor(helper,
    PermissionLevel.FAN_ARTS_MANAGER
) {
    inner class Options : ApplicationCommandOptions() {
        val messageUrl = string("fan_art_url", "Link da Fan Art na Galeria de Fan Arts")
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

        val fanArtId = args[options.messageUrl].split("/")
            .last() // Last parameter should be UUID

        val encodedData = ComponentDataUtils.encode(
            PatchFanArtData(
                fanArtId,
                listOf()
            )
        )

        val buildMessage = GalleryOfDreamsUtils.createPatchFanArtMessage(
            PatchFanArtData(
                fanArtId,
                listOf()
            )
        )

        context.sendEphemeralMessage {
            apply(buildMessage)
        }
    }
}