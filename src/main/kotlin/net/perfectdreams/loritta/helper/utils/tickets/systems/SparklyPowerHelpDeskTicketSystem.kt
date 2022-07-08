package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.common.entities.User
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.LorittaResponse
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

class SparklyPowerHelpDeskTicketSystem(
    rest: RestClient,
    systemType: TicketUtils.TicketSystemType,
    language: TicketUtils.LanguageName,
    guildId: Snowflake,
    channelId: Snowflake,
    channelResponses: List<LorittaResponse>,
    faqChannelId: Snowflake,
    statusChannelId: Snowflake,
    supportRoleId: Snowflake
) : HelpDeskTicketSystem(rest, systemType, language, guildId, channelId, channelResponses, faqChannelId, statusChannelId, supportRoleId) {
    override val ticketCreatedMessage: UserMessageCreateBuilder.(User, I18nContext) -> Unit = { sender, language ->
        content = (
                listOf(
                    LorittaReply(
                        "Prontinho! Eu criei um ticket para você, faça sua pergunta e aguarde até que um membro da equipe venha tirar sua dúvida.",
                        "<:pantufa_hangloose:982762886105534565>",
                        mentionUser = true
                    ),
                    LorittaReply(
                        "Faça sua pergunta de uma forma simples e objetiva, se você precisar, anexe imagens. Para que a <@&$supportRoleId> possa te ajudar com mais eficiência.",
                        "<:pantufa_ameno:854811058992447530>",
                        mentionUser = false
                    ),
                    LorittaReply(
                        "**E EU ESPERO que você tenha lido o <#$faqChannelId> que a gente fez com tanto amor e carinho, vai que sua pergunta já foi respondida lá.**",
                        "<:pantufa_analise:853048446813470762>",
                        mentionUser = false
                    ),
                    LorittaReply(
                        "Após a sua pergunta ser respondida, você pode usar `/closeticket` para fechar o ticket! E depois todos podem viver felizes para sempre~",
                        "<a:pantufa_calca_1:657227425977204782>",
                        mentionUser = false
                    ),
                )
                )
            .joinToString("\n") { it.build(sender) }
    }
}