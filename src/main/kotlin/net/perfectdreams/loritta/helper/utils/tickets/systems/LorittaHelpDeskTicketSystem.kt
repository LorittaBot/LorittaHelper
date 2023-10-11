package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.minn.jda.ktx.messages.InlineMessage
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.serverresponses.LorittaResponse
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

class LorittaHelpDeskTicketSystem(
    jda: JDA,
    systemType: TicketUtils.TicketSystemType,
    language: TicketUtils.LanguageName,
    guildId: Long,
    channelId: Long,
    channelResponses: List<LorittaResponse>,
    faqChannelId: Long,
    statusChannelId: Long,
    supportRoleId: Long
) : HelpDeskTicketSystem(jda, systemType, language, guildId, channelId, channelResponses, faqChannelId, statusChannelId, supportRoleId) {
    override val ticketCreatedMessage: InlineMessage<*>.(User, I18nContext) -> Unit = { sender, language ->
        content = (
                listOf(
                    LorittaReply(
                        language.get(I18nKeysData.Tickets.ThreadCreated.Ready),
                        "<:lori_coffee:727631176432484473>",
                        mentionUser = true
                    ),
                    LorittaReply(
                        language.get(I18nKeysData.Tickets.ThreadCreated.QuestionTips("<@&${supportRoleId}>")),
                        "<:lori_coffee:727631176432484473>",
                        mentionUser = false
                    ),
                    LorittaReply(
                        "**${
                            language.get(
                                I18nKeysData.Tickets.ThreadCreated.PleaseRead(
                                    "<#${faqChannelId}>",
                                    "<https://loritta.website/extras>"
                                )
                            )
                        }**",
                        "<:lori_analise:853052040425766922>",
                        mentionUser = false
                    ),
                    LorittaReply(
                        language.get(I18nKeysData.Tickets.ThreadCreated.AfterAnswer),
                        "<a:lori_pat:706263175892566097>",
                        mentionUser = false
                    )
                )
                )
            .joinToString("\n") { it.build(sender) }
    }
}