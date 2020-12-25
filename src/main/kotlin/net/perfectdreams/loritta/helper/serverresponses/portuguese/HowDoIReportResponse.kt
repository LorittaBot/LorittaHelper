package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import java.util.regex.Pattern

class HowDoIReportResponse: RegExResponse() {

    init {
        patterns.add("onde|como".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("posso|".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("den(ú|u)nci(ar|o)".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> = listOf(
        LorittaReply(
            message = "Você pode fazer uma denúncia enviando `'denúncia'` no privado da <@!761931447207460864>, após fazer isto, ela irá te enviar um formulário com um link e código único, por isso não tire prints ou divulgue o seu código e o link!",
            prefix = "👮"
        )
    )

}