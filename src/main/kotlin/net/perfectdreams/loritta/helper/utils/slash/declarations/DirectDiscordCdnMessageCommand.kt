package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.loritta.morenitta.interactions.commands.MessageCommandDeclarationWrapper
import net.perfectdreams.loritta.morenitta.interactions.commands.messageCommand
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.slash.DirectDiscordCdnExecutor

class DirectDiscordCdnMessageCommand(val helper: LorittaHelper) : MessageCommandDeclarationWrapper {
    override fun command() = messageCommand("DirectDiscordCdn", DirectDiscordCdnExecutor(helper))
}