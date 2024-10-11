package net.perfectdreams.loritta.helper.utils.slash

import net.dv8tion.jda.api.entities.Message
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.HelperExecutor
import net.perfectdreams.loritta.morenitta.interactions.commands.ApplicationCommandContext
import net.perfectdreams.loritta.morenitta.interactions.commands.LorittaMessageCommandExecutor

class DirectDiscordCdnExecutor(helper: LorittaHelper) : LorittaMessageCommandExecutor() {
    override suspend fun execute(context: ApplicationCommandContext, targetMessage: Message) {
        if (!context.member.roles.map { it.idLong }.any { it in HelperExecutor.HELPER_ROLES }) {
            context.reply(true) {
                content = "Você não tem o poder de usar isto!"
            }
            return
        }

        val remappedUrls = targetMessage.attachments.map { it.url.replace("cdn.discordapp.com", "txt.lori.fun") }.joinToString("\n")

        context.reply(true) {
            if (remappedUrls.isNotBlank()) {
                content = targetMessage.attachments.map { it.url.replace("cdn.discordapp.com", "txt.lori.fun") }.joinToString("\n")
            } else {
                content = "Nenhum anexo encontrado na mensagem..."
            }
        }
    }
}