package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.minn.jda.ktx.messages.InlineMessage
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.User
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.LorittaResponse
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

class SparklyPowerHelpDeskTicketSystem(
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
                        "Prontinho! Eu criei um ticket para você, faça sua pergunta e aguarde até que um membro da equipe venha tirar sua dúvida.",
                        "<:pantufa_hangloose:982762886105534565>",
                        mentionUser = true
                    ),
                    LorittaReply(
                        "Faça sua pergunta de uma forma simples e objetiva, se você precisar, anexe imagens. Para que a <@&$supportRoleId> e <@&1262475261706899688> possa te ajudar com mais eficiência.",
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