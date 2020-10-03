package net.perfectdreams.loritta.helper.serverresponses.portuguese

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.serverresponses.RegExResponse
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.regex.Pattern

class JoinLeaveResponse : RegExResponse() {
    init {
        patterns.add("ativ|coloc|uso|adicio|add|boto|fasso|faz".toPattern(Pattern.CASE_INSENSITIVE))
        patterns.add("(entra|entrou|sai|saí|bem( |-)?vind|boa(s)?( |-)?vind)".toPattern(Pattern.CASE_INSENSITIVE))
    }
    
    override fun getResponse(event: GuildMessageReceivedEvent, message: String) =
            listOf(
                LorittaReply(
                    "**Ativar as mensagens de entrada e saída é bem fácil!**",
                    prefix = Emotes.LORI_PAC
                ),
                LorittaReply(
                    "Vá no painel de administração clicando aqui <https://loritta.website/dashboard> e escolha o servidor que você deseja ativar as mensagens!",
                    mentionUser = false
                ),
                LorittaReply(
                    "Clique em \"Mensagens de Entrada/Saída\"",
                    mentionUser = false
                ),
                LorittaReply(
                    "Agora é só configurar do jeito que você queira! <:eu_te_moido:366047906689581085>",
                    mentionUser = false
                ),
                LorittaReply(
                    "(Dica: Se você quiser fazer aquelas mensagens bonitinhas quadradas, use o nosso editor de embeds! <https://embeds.loritta.website/>)",
                    prefix = Emotes.LORI_OWO,
                    mentionUser = false
                )
            )
}