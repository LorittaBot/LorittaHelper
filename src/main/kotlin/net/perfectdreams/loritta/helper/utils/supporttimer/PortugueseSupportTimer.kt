package net.perfectdreams.loritta.helper.utils.supporttimer

import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Constants

class PortugueseSupportTimer(m: LorittaHelper, jda: JDA) : SupportTimer(m, jda) {
    override val channelId = Constants.PORTUGUESE_SUPPORT_CHANNEL_ID
    override val replies = listOf(
        LorittaReply(
            "<a:rat_jam:720643637033304442> **LEIA ANTES DE PERGUNTAR ALGO!** <a:rat_jam:720643637033304442>",
            prefix = "<:lori_nice:726845783344939028>",
            mentionUser = false
        ),
        LorittaReply(
            "**Se for uma dúvida sobre a Loritta:** Veja se a resposta da sua pergunta está no <#${Constants.PORTUGUESE_FAQ_CHANNEL_ID}>! Caso não esteja lá, envie a sua pergunta aqui e, na mensagem, mencione o <@&${Constants.PORTUGUESE_LORITTA_SUPPORT_ROLE_ID}>, nós iremos tentar te ajudar o mais breve possível!",
            "<:lori_ok:731873534036541500>",
            mentionUser = false
        ),
        LorittaReply(
            "**Lembre-se que aqui é o *suporte da Loritta*:** Nós não iremos te ajudar com problemas diversos (por exemplo: problemas em outros bots, problemas no Discord, etc)",
            "<a:lori_fight:731871119400894525>",
            mentionUser = false
        ),
        LorittaReply(
            "**Se você irá perguntar \"aaaah lori caiu <:smol_lori_putassa:395010059157110785>\":** Veja o <#${Constants.PORTUGUESE_STATUS_CHANNEL_ID}> e as <#${Constants.PORTUGUESE_NEWS_CHANNEL_ID}> para mais informações!",
            "<:smol_lori_putassa:395010059157110785>",
            mentionUser = false
        ),
        LorittaReply(
            "**Se você irá perguntar se algo foi mudado/adicionado/removido:** Veja as <#${Constants.PORTUGUESE_STATUS_CHANNEL_ID}> para saber!",
            "<a:lori_dabbing:727888868711334287>",
            mentionUser = false
        )
    )
    override val containsString = "LEIA ANTES DE PERGUNTAR ALGO"
}