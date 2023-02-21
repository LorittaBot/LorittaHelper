package net.perfectdreams.loritta.helper.listeners

import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper

class AddReactionsToMessagesListener(val m: LorittaHelper): ListenerAdapter() {
    companion object {
        private val ADD_APPROVAL_REACT_CHANNEL_IDS = listOf(
                359139508681310212L, // sugestões da galera
                664431430159302674L, // aaah bugs e insetos
                550815054279213064L, // sugestões (sparkly)
                543222942482432001L // bugs (sparkly)
        )

        private val ADD_FAN_ARTS_REACT_CHANNEL_IDS = listOf(
                583406099047252044L, // lori fan arts
                510601125221761054L // artes gerais
        )
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val content = event.message.contentRaw

        if (event.isWebhookMessage || event.author.isBot || content.startsWith(">"))
            return

        if (event.channel.idLong in ADD_APPROVAL_REACT_CHANNEL_IDS) {
            event.message.addReaction(Emoji.fromUnicode("\uD83D\uDC4D"))
                    .queue()

            event.message.addReaction(Emoji.fromCustom("lori_what", 626942886361038868L, false))
                    .queue()
        }

        if (event.channel.idLong in ADD_FAN_ARTS_REACT_CHANNEL_IDS) {
            event.message.addReaction(Emoji.fromUnicode("❤"))
                    .queue()

            event.message.addReaction(Emoji.fromCustom("grand_cat", 587347657866084352L, false))
                    .queue()

            event.message.addReaction(Emoji.fromCustom("catblush", 585608228679712778L, false))
                    .queue()

            event.message.addReaction(Emoji.fromCustom("lori_pat", 706263175892566097L, true))
                    .queue()
        }
    }
}