package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.MessageCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.messageCommand
import net.perfectdreams.galleryofdreams.client.GalleryOfDreamsClient
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.add.AddFanArtToGalleryMessageExecutor
import net.perfectdreams.loritta.helper.utils.slash.DirectDiscordCdnExecutor

class DirectDiscordCdnMessageCommand(val helper: LorittaHelperKord) : MessageCommandDeclarationWrapper {
    override fun declaration() = messageCommand("DirectDiscordCdn", DirectDiscordCdnExecutor(helper))
}