package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class DJLorittaResponse : RegExResponse() {
    init {
        patterns.add(("$WHERE_IT_IS|loritta").toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("coloc[ar|a|o]|toc[ar|a|an]|adicion[a|o|ar]|ouvir|escuta|escuto|ouvo|ativa|bota|config|consigo|consegu".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("musicas|musica|música|músicas|msc|dj".toPattern(Pattern.CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
        listOf(
            LorittaReply(
                "Infelizmente o YouTube e a Google fizeram alterações e os comandos de música foram removidos... Leia mais aqui <https://loritta.website/blog/youtube-google-block>",
                prefix = Emotes.LORI_SOB
            ),
            LorittaReply(
                "No futuro o sistema de música irá voltar! Maaaas apenas para doadores, já que manter um sistema de música é beeeem difícil.",
                mentionUser = false
            ),
            LorittaReply(
                "Se você tá com fogo de escutar ondas sonoras no seu ouvido pelo Discord, nós recomendamos utilizar o Groovy! <https://groovy.bot/>",
                mentionUser = false
            )
        )
}