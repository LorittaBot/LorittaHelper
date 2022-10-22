package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.GuildApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.MessageCommandExecutor
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.entities.messages.Message
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.tables.SonhosTransaction
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.galleryofdreams.commands.GalleryOfDreamsUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class DirectDiscordCdnExecutor(helper: LorittaHelperKord) : MessageCommandExecutor() {
    override suspend fun execute(context: ApplicationCommandContext, targetMessage: Message) {
        if (context !is GuildApplicationCommandContext || !context.member.roleIds.any { it in HelperSlashExecutor.HELPER_ROLES }) {
            context.sendEphemeralMessage {
                content = "Você não tem o poder de usar isto!"
            }
            return
        }

        val remappedUrls = targetMessage.attachments.map { it.url.replace("cdn.discordapp.com", "txt.lori.fun") }.joinToString("\n")

        context.sendEphemeralMessage {
            if (remappedUrls.isNotBlank()) {
                content = targetMessage.attachments.map { it.url.replace("cdn.discordapp.com", "txt.lori.fun") }.joinToString("\n")
            } else {
                content = "Nenhum anexo encontrado na mensagem..."
            }
        }
    }
}