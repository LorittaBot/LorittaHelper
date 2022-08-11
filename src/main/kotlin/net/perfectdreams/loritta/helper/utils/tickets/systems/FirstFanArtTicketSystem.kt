package net.perfectdreams.loritta.helper.utils.tickets.systems

import dev.kord.common.entity.ArchiveDuration
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.service.RestClient
import net.perfectdreams.i18nhelper.core.I18nContext
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

class FirstFanArtTicketSystem(
    rest: RestClient,
    systemType: TicketUtils.TicketSystemType,
    language: TicketUtils.LanguageName,
    guildId: Snowflake,
    channelId: Snowflake,
    val fanArtsManagerRoleId: Snowflake,
    val fanArtRulesChannelId: Snowflake
) : TicketSystem(rest, systemType, language, guildId, channelId, ArchiveDuration.Week) {
    override val ticketCreatedMessage: UserMessageCreateBuilder.(User, I18nContext) -> Unit = { sender, language ->
        content = (
                listOf(
                    LorittaReply(
                        "Envie a sua fan art e, caso tenha, envie o processo de criação dela!",
                        "<:lori_coffee:727631176432484473>",
                        mentionUser = true
                    ),
                    LorittaReply(
                        "Após enviado, os <@&${fanArtsManagerRoleId.value}> irão averiguar a sua fan art e, caso ela tenha uma qualidade excepcional, ela será incluida na nossa Galeria de Fan Arts!",
                        "<:lori_analise:853052040425766922>",
                        mentionUser = false
                    ),
                )
                )
            .joinToString("\n")
            { it.build(sender) }
    }
}