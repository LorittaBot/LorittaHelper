package net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

/**
 * If you found a bug, you should report it!
 * This response teaches you how to do that on the finest way.
 */
class ReportBugsResponse : RegExResponse() {
    init {
        patterns.add("como|onde|quero".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("report(o|a|ar)|fal(a|ar)|falo".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("algum|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("bu(gs|g)|problem(a|s)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(message: String): List<LorittaReply> = listOf(
        LorittaReply(
            message = "Querendo reportar bugs? Você pode fazer isto no nosso servidor comunidade (<#761625835043291146>), no canal <#664431430159302674>!",
            prefix = "<:blobcatbughunter:795355653522325534>"
        ),
        LorittaReply(
            message = "Antes de reportar é bom sempre conferir se isto é realmente um bug, e fica válido relembrar que não existem bugs ou sequências em comandos de apostas como `+coinflip bet`!",
            mentionUser = false,
            prefix = "<:lori_what:626942886361038868>"
        ),
        LorittaReply(
            message = "E por favor, passe no <#752294116708319324> para ver se a Loritta está reiniciando ou está instável no momento! As vezes ela só está com problemas (como não respondendo comandos, ou o website está offline) pois ela está em manutenção!",
            mentionUser = false,
            prefix = Emotes.LORI_COFFEE
        )
    )

}
