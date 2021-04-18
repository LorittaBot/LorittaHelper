package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.listeners.ApproveFanArtListener
import net.perfectdreams.loritta.helper.utils.slash.declarations.FanArtsOverrideDeclaration

class FanArtsOverrideGetCommand(helper: LorittaHelper) : HelperSlashCommand(helper, FanArtsOverrideDeclaration.Get, FanArtsOverrideDeclaration.Root) {
    override suspend fun executesHelper(context: SlashCommandContext) {
        context.sendMessage {
            content = "Override: ${ApproveFanArtListener.fanArtOverrideSettings}"

            flags = MessageFlags(MessageFlag.Ephemeral)
        }
    }
}