package net.perfectdreams.loritta.helper.utils.gotolangchannel

import com.github.pemistahl.lingua.api.Language
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.Emotes

/**
 * Checks and points users to the correct channel if they are talking in the incorrect channel
 */
class GoToCorrectLanguageChannel(val m: LorittaHelper) {
    companion object {
        val detector = LanguageDetectorBuilder.fromLanguages(
            Language.PORTUGUESE,
            Language.ENGLISH
        ).build()
    }

    val languageChannel = mapOf(
        Language.ENGLISH to Constants.ENGLISH_SUPPORT_CHANNEL_ID,
        Language.PORTUGUESE to Constants.PORTUGUESE_SUPPORT_CHANNEL_ID
    )

    fun onMessageReceived(event: GuildMessageReceivedEvent) {
        val language = detector.detectLanguageOf(event.message.contentRaw)

        val channelIdForLanguage = languageChannel[language]

        if (channelIdForLanguage != event.channel.idLong) {
            val messages = when (language) {
                Language.PORTUGUESE -> listOf(
                    LorittaReply(
                        "Parece que você está falando em Português no canal de suporte Inglês! Por favor pergunte a sua pergunta no <#761337439095881748> e mencione <@&${Constants.PORTUGUESE_LORITTA_SUPPORT_ROLE_ID}> ao enviar a sua mensagem lá, obrigada!",
                        Emotes.LORI_SOB
                    )
                )
                Language.ENGLISH -> listOf(
                    LorittaReply(
                        "It looks like you are talking in English on the Portuguese support channel! Please ask your question in the <#420628148044955648> channel and mention <@&${Constants.ENGLISH_LORITTA_SUPPORT_ROLE_ID}> when sending your message there, thank you!",
                        Emotes.LORI_SOB
                    )
                )
                else -> null
            }

            if (messages != null) {
                event.channel.sendMessage(
                    MessageBuilder()
                        // We mention roles in some of the messages, so we don't want the mention to actually go off!
                        .setAllowedMentions(listOf(Message.MentionType.USER, Message.MentionType.CHANNEL, Message.MentionType.EMOTE))
                        .setContent(messages.joinToString("\n") { it.build(event) })
                        .build()
                ).queue()
            }
        }
    }
}