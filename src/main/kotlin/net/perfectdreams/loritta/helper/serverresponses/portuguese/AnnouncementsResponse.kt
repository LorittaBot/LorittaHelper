package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern.CASE_INSENSITIVE

/**
 * Response when people ask how to do an announcement with
 * Loritta (usually using the private channel)
 */
class AnnouncementsResponse: RegExResponse() {

    init {
        patterns.add(ACTIVATE_OR_CHANGE.toPattern(CASE_INSENSITIVE))
        patterns.add("anunci(o|a|ar)|an(ú | u)ncios".toPattern(CASE_INSENSITIVE))
    }

    override fun getResponse(event: GuildMessageReceivedEvent, message: String): List<LorittaReply> = listOf(
        LorittaReply(
            message = "Caso você queira fazer um anúncio utilizando a Loritta, você pode usar o `+say` para isto!",
            prefix = Emotes.WUMPUS_KEYBOARD
        ), LorittaReply(
            message = "Se você estiver procurando uma maneira de anunciar no privado de seus membros, eu não tenho essa opção, pois se encaixa como `Spam` nos Termos de Serviço do discord!",
            prefix = Emotes.LORI_SOB
        )
    )

}