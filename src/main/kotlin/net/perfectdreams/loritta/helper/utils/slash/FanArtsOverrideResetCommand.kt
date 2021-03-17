package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.listeners.ApproveFanArtListener

class FanArtsOverrideResetCommand(helper: LorittaHelper) : HelperSlashCommand(helper, FanArtsOverrideDeclaration.Reset, FanArtsOverrideDeclaration.Root) {
    override suspend fun executesHelper(context: SlashCommandContext) {
        ApproveFanArtListener.fanArtOverrideSettings = null

        context.sendMessage {
            content = "Override resetado!"

            flags = MessageFlags(MessageFlag.Ephemeral)
        }
    }
}