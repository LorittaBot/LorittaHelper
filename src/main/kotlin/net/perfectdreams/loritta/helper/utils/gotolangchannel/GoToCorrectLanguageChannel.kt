package net.perfectdreams.loritta.helper.utils.gotolangchannel

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.pemistahl.lingua.api.Language
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.Emotes
import java.util.*
import java.util.concurrent.TimeUnit

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

    // Used to avoid checking the same user multiple times because some times false positives can happen
    // So if we already checked, we are going to ignore the user until it is expired
    val ignoreLanguageCheck = Collections.newSetFromMap(
        Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build<Long, Boolean>()
            .asMap()
    )

    fun onMessageReceived(event: GuildMessageReceivedEvent) {
        // Only in channels that do have a language-specific channel ID
        if (languageChannel.values.contains(event.channel.idLong) && !ignoreLanguageCheck.contains(event.author.idLong)) {
            // We need to "clean up" the message a little bit before checking
            val cleanMessage = event.message.contentRaw
                .let { message ->
                    var newMessage = message
                    event.message.mentionedRoles.forEach {
                        newMessage = newMessage.replace(it.asMention, "")
                    }
                    event.message.mentionedUsers.forEach {
                        newMessage = newMessage.replace(it.asMention, "")
                    }
                    event.message.mentionedChannels.forEach {
                        newMessage = newMessage.replace(it.asMention, "")
                    }
                    event.message.emotes.forEach {
                        newMessage = newMessage.replace(it.asMention, "")
                    }
                    newMessage
                }
                .let { stripLinks(it) }
                .lines()
                .dropWhile { it.startsWith(">") || it.startsWith("<") || it.startsWith("{") || it.startsWith("}") }
                .joinToString("\n")
                .split(" ")
                .dropWhile { it.startsWith(">") || it.startsWith("<") || it.startsWith(":") || it.startsWith("#") }
                .joinToString(" ")

            // Checking the message's language takes a while, so we do a >= length check to avoid checking spam messages and stuff like that
            if (cleanMessage.length >= 15) {
                val language = detector.detectLanguageOf(cleanMessage)

                val channelIdForLanguage = languageChannel[language]

                if (channelIdForLanguage != event.channel.idLong) {
                    val messages = when (language) {
                        Language.PORTUGUESE -> listOf(
                            LorittaReply(
                                "Parece que você está falando em Português no canal de suporte Inglês! Por favor faça a sua pergunta no <#761337439095881748> e mencione <@&${Constants.PORTUGUESE_LORITTA_SUPPORT_ROLE_ID}> ao enviar a sua mensagem lá, obrigada!",
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
                                .setAllowedMentions(
                                    listOf(
                                        Message.MentionType.USER,
                                        Message.MentionType.CHANNEL,
                                        Message.MentionType.EMOTE
                                    )
                                )
                                .setContent(messages.joinToString("\n") { it.build(event) })
                                .build()
                        ).reference(event.message)
                            .queue()
                    }
                } else {
                    ignoreLanguageCheck.add(event.author.idLong)
                }
            }
        }
    }

    /**
     * Strips all links from the [string]
     */
    fun stripLinks(string: String): String {
        var output = string
        val matcher = Constants.URL_PATTERN.matcher(
            string.replace("\u200B", "")
                .replace("\\", "")
        )

        while (matcher.find()) {
            val url = matcher.group()
            output = string.replace(url, "")
        }
        return output
    }
}
