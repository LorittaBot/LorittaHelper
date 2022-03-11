package net.perfectdreams.loritta.helper.utils.checksonhosmendigagem

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.utils.MarkdownSanitizer
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.GuildProfiles
import net.perfectdreams.loritta.helper.utils.toNaiveBayesClassifier
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.checkillegalnitrosell.CheckIllegalNitroSell
import net.perfectdreams.loritta.helper.utils.splitWords
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class CheckSonhosMendigagem(val m: LorittaHelper) {
    val messages = LorittaHelper::class.java.getResourceAsStream("/mendigando-sonhos-spam.txt")
        .readAllBytes().toString(Charsets.UTF_8).lines().map {
            CheckIllegalNitroSell.DiscordMessage(it, true)
        } + LorittaHelper::class.java.getResourceAsStream("/good-messages.txt")
        .readAllBytes().toString(Charsets.UTF_8).lines().map {
            CheckIllegalNitroSell.DiscordMessage(it, false)
        }

    val nbc = messages.toNaiveBayesClassifier(
        featuresSelector = { it.message.splitWords().toSet() },
        categorySelector = { it.isSpam }
    )

    companion object {
        val logger = KotlinLogging.logger {  }

        private val channels = listOf(297732013006389252L)
        private val reply = listOf(
            LorittaReply(
                "Que feio... Pare de mendigar! Pegue seus sonhos diários usando `+daily`!",
                Emotes.LORI_RAGE,
                mentionUser = true
            ),
            LorittaReply(
                "Você já pegou o daily? Então você pode votar em mim usando `+dbl`! Sabia que você ganha sonhos votando em mim?",
                Emotes.LORI_THINKING,
                mentionUser = false
            ),
            LorittaReply(
                "Ainda não está satisfeito? Então jogue no servidor de Minecraft SparklyPower! `mc.sparklypower.net`",
                Emotes.PANTUFA_GASP,
                mentionUser = false
            ),
            LorittaReply(
                "**Se você continuar a mendigar você será punido!**",
                Emotes.LORI_BAN_HAMMER,
                mentionUser = false
            )
        )
    }

    fun onMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.channel.idLong in channels) {
            val memberXp = transaction(m.databases.lorittaDatabase) {
                GuildProfiles.select {
                    (GuildProfiles.userId eq event.author.idLong) and (GuildProfiles.guildId eq event.guild.idLong)
                }.firstOrNull()?.get(GuildProfiles.xp)
            }

            if (memberXp != null && memberXp >= 5000) return

            logger.info { "Guild/Channel/Message: ${event.guild.id}/${event.channel.id}/${event.messageId} | XP: $memberXp" }

            val rawContent = event.message.contentRaw
                .let {
                    MarkdownSanitizer.sanitize(it)
                }

            val input = rawContent.splitWords().toSet()
            val predictedCategory = nbc.predictWithProbability(input)

            logger.info { "Category: ${predictedCategory?.category} | Probability: ${predictedCategory?.probability}" }

            if (predictedCategory?.category == true && predictedCategory.probability >= 0.8) {
                event.channel.sendMessage(reply.joinToString("\n") { it.build(event.author) })
                    .reference(event.message)
                    .queue()
            }
        }
    }
}