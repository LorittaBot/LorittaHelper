package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.loritta.morenitta.interactions.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.slash.DriveImageRetrieverExecutor

class DriveImageRetrieverCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "retrievedriveimg",
        "Extrai uma imagem do Google Drive e envia para o Discord"
    ) {
        executor = DriveImageRetrieverExecutor(helper)
    }
}