package net.perfectdreams.loritta.helper.utils.checksonhosmendigagem

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.utils.MarkdownSanitizer
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.GuildProfiles
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.checkillegalnitrosell.CheckIllegalNitroSell
import net.perfectdreams.loritta.helper.utils.splitWords
import net.perfectdreams.loritta.helper.utils.toNaiveBayesClassifier
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

        fun buildReply(campaignContent: String) = listOf(
            LorittaReply(
                "**Pare de mendigar sonhos!** Isso incomoda e atrapalha as pessoas que estão conversando no chat e, se você continuar, você será banido do servidor!",
                Emotes.LORI_RAGE,
                mentionUser = true
            ),
            LorittaReply(
                "**Está procurando por métodos de como conseguir mais sonhos? Leia a imagem abaixo!**",
                Emotes.LORI_PEACE,
                mentionUser = false
            ),
            LorittaReply(
                "**Psiu, está querendo mais sonhos? Então compre na minha lojinha!** Nós aceitamos pagamentos via boleto, cartão de crédito e Pix e comprando por lá você me ajuda a ficar online enquanto você se diverte com mais sonhos! Mas não se preocupe, a escolha é sua e você pode continuar a usar a Loritta sem se preocupar em tirar dinheiro do seu bolso. Ficou interessado? Então acesse! <https://loritta.website/br/user/@me/dashboard/bundles?utm_source=discord&utm_medium=dont-beg-warn&utm_campaign=sonhos-wiki&utm_content=$campaignContent>",
                Emotes.LORI_CARD,
                mentionUser = false
            ),
            LorittaReply(
                "**Aprenda tudo sobre sonhos em:** <https://loritta.website/br/extras/faq-loritta/sonhos?utm_source=discord&utm_medium=dont-beg-warn&utm_campaign=sonhos-wiki&utm_content=$campaignContent>",
                Emotes.LORI_SMART,
                mentionUser = false
            ),
            LorittaReply(
                "https://cdn.discordapp.com/attachments/703353259938545744/1062039628124782743/Como_consigo_sonhos.png",
                null,
                mentionUser = false
            )
        )
    }

    fun onMessageReceived(event: MessageReceivedEvent) {
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

            if (predictedCategory?.category == true && predictedCategory.probability >= 0.95) {
                event.channel.sendMessage(buildReply("warned-beg").joinToString("\n") { it.build(event.author) })
                    .reference(event.message)
                    .queue()
            }
        }
    }
}