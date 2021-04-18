package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import net.perfectdreams.discordinteraktions.commands.get
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.listeners.ApproveFanArtListener
import net.perfectdreams.loritta.helper.utils.slash.declarations.FanArtsOverrideDeclaration

class FanArtsOverrideSetCommand(helper: LorittaHelper) : HelperSlashCommand(helper, FanArtsOverrideDeclaration.Set, FanArtsOverrideDeclaration.Root) {
    override suspend fun executesHelper(context: SlashCommandContext) {
        with(FanArtsOverrideDeclaration.Set.options) {
            val fileName = imageFileName.get(context)
            val tags = tags.get(context)?.split(",")?.map { it.trim() }
            val artistFileName = artistFileName.get(context)
            val artistFileNameOnImage = artistImageFileName.get(context)

            ApproveFanArtListener.fanArtOverrideSettings = ApproveFanArtListener.FanArtOverrideSettings(
                fileName,
                tags,
                artistFileName,
                artistFileNameOnImage
            )

            context.sendMessage {
                content = "Override criado! ${ApproveFanArtListener.fanArtOverrideSettings}"

                flags = MessageFlags(MessageFlag.Ephemeral)
            }
        }
    }
}