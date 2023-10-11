package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.minn.jda.ktx.messages.InlineMessage
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

class FirstFanArtTicketSystem(
    jda: JDA,
    systemType: TicketUtils.TicketSystemType,
    language: TicketUtils.LanguageName,
    guildId: Long,
    channelId: Long,
    val fanArtsManagerRoleId: Long,
    val fanArtRulesChannelId: Long
) : TicketSystem(jda, systemType, language, guildId, channelId, ThreadChannel.AutoArchiveDuration.TIME_1_WEEK) {
    override val ticketCreatedMessage: InlineMessage<*>.(User, I18nContext) -> Unit = { sender, language ->
        content = (
                listOf(
                    LorittaReply(
                        "Envie a sua fan art e, caso tenha, envie o processo de criação dela!",
                        "<:lori_coffee:727631176432484473>",
                        mentionUser = true
                    ),
                    LorittaReply(
                        "Após enviado, os <@&${fanArtsManagerRoleId}> irão averiguar a sua fan art e, caso ela tenha uma qualidade excepcional, ela será incluida na nossa Galeria de Fan Arts!",
                        "<:lori_analise:853052040425766922>",
                        mentionUser = false
                    ),
                )
                )
            .joinToString("\n")
            { it.build(sender) }
    }
}