package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.message.MessageCommandExecutor
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.entities.messages.Message
import net.perfectdreams.discordinteraktions.declarations.commands.message.MessageCommandExecutorDeclaration
import net.perfectdreams.loritta.helper.LorittaHelperKord

class AddFanArtToGalleryExecutor(private val m: LorittaHelperKord) : MessageCommandExecutor() {
    companion object : MessageCommandExecutorDeclaration(AddFanArtToGalleryExecutor::class)

    override suspend fun execute(context: ApplicationCommandContext, targetMessage: Message) {
        context.deferChannelMessageEphemerally()

        context.sendEphemeralMessage {
            content = "hell yeah!"
        }
    }
}