package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.common.entities.User
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.serverresponses.LorittaResponse
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

class LorittaHelpDeskTicketSystem(
    rest: RestClient,
    systemType: TicketUtils.TicketSystemType,
    language: TicketUtils.LanguageName,
    guildId: Snowflake,
    channelId: Snowflake,
    val channelResponses: List<LorittaResponse>,
    val faqChannelId: Snowflake,
    val statusChannelId: Snowflake,
    val supportRoleId: Snowflake
) : TicketSystem(rest, systemType, language, guildId, channelId, ArchiveDuration.Day) {
    override val ticketCreatedMessage: UserMessageCreateBuilder.(User, I18nContext) -> Unit = { sender, language ->
        content = (
                listOf(
                    LorittaReply(
                        language.get(I18nKeysData.Tickets.ThreadCreated.Ready),
                        "<:lori_coffee:727631176432484473>",
                        mentionUser = true
                    ),
                    LorittaReply(
                        language.get(I18nKeysData.Tickets.ThreadCreated.QuestionTips("<@&${supportRoleId.value}>")),
                        "<:lori_coffee:727631176432484473>",
                        mentionUser = false
                    ),
                    LorittaReply(
                        "**${
                            language.get(
                                I18nKeysData.Tickets.ThreadCreated.PleaseRead(
                                    "<#${faqChannelId.value}>",
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