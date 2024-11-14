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
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class CheckSonhosMendigagem(val m: LorittaHelper) {
    companion object {
        val logger = KotlinLogging.logger {}

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
                mentionUser = false
            )
        )

        private val SOMEONE = "(algu([eé])m|alg|algm|agl|vo?c[eê]?)"
        private val GIVE = "(doar?|d[aá]|emprestar?|doem|deem|dê)"
        private val SOME = "(alguns|algns|algms|algn|algm|um pou[ck]o? d[eêií])"
        private val SONHOS = "(s ?[o0] ?n? ?h ?[ou0] ?s?)"
        private val ME = "me"
        private val SONHOS_QUANTITY = "([0-9\\.,]+ ?(k|m(il(h[õo0][e3]s)?)?))"
        private val COULD = "(pode|poderia)"
        private val DUDE = "(mano|mana|cara|doido)"
        private val LORITTA_COMMAND = "(\\+[A-z]+)"
        private val USER_MENTION = "<@!?[0-9]+>"
        private val THERE = "a[ií]"
        private val A_BIT = "($THERE )?um pouco"
        private val PO = "p[oô]"
        private val FARMING = "(farm|farmar|apostar)"
        private val TO = "(pr[aá]|para)"
        private val JUST = "s[oó]"
        private val OF_SONHOS = "de $SONHOS"
        private val PLEASE = "(p[ou0]r ?fav[ou0]r|pfv|plis|pliz|plz|pls|fav[ou0]r)"
        private val QUESTION_MARK_WITH_SPACE = " ?\\?"
        private val HEY = "(o[ií]|ol[aá])"
        private val EVERYONE = "(galera|galerinha|gente|povo|pess?oal|pessoas)"
        private val STUPID_STORY_1 = "(eu( (estou|me|t[oô]))? (fali|falido|falida|faliram|pobre|triste|mendigando|mendigo))"
        private val STUPID_STORY_2 = "(((estou|me|t[oô]) )?(fali|falido|falida|faliram|pobre|triste|mendigando|mendigo))"
        private val STUPID_STORIES = "($STUPID_STORY_1|$STUPID_STORY_2)"
        private val GENERIC_PREFIX = "($USER_MENTION )?($LORITTA_COMMAND )?($PO,? )?$STUPID_STORIES? ?($HEY )?($EVERYONE )?$STUPID_STORIES? ?"

        val regexes = listOf(
            NamedRegex(
                "Alguém dá sonhos",
                Regex(
                    "$GENERIC_PREFIX$SOMEONE( $COULD| me| mim)? $GIVE( $SOME)?( $SONHOS).*",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ),
            ),
            NamedRegex(
                "Me doa sonhos",
                Regex(
                    "$GENERIC_PREFIX$ME $GIVE( $SOME)?( $SONHOS).*",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ),
            ),
            NamedRegex(
                "Alguém dá 10k sonhos",
                Regex(
                    "$GENERIC_PREFIX$SOMEONE( $COULD| me)?( $ME)? $GIVE $SONHOS_QUANTITY( de)? $SONHOS( $THERE)?.*",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ),
            ),
            NamedRegex(
                "Alguém me dá sonhos",
                Regex(
                    "$GENERIC_PREFIX$SOMEONE( $COULD| me)?( $ME) $GIVE $SONHOS( $THERE)?.*",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ), // same as above but more strict
            ),
            NamedRegex(
                "Alguém poderia dar 10k",
                Regex(
                    "$GENERIC_PREFIX$SOMEONE( $COULD| me)( $ME)? $GIVE $SONHOS_QUANTITY( $THERE)?( $OF_SONHOS)?( $THERE)?.*",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ), // same as above but more strict
            ),
            NamedRegex(
                "Alguém dá 10k (strict)",
                Regex(
                    "$GENERIC_PREFIX$SOMEONE( $COULD| me)?( $ME)? $GIVE $SONHOS_QUANTITY( $THERE)?( $OF_SONHOS)?( $THERE)?( (to|eu|mim) (pobre|falido|falida))?${QUESTION_MARK_WITH_SPACE}?",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ), // same as above but way more strict
            ),
            NamedRegex(
                "Dá 10k aí por favor",
                Regex(
                    "$GENERIC_PREFIX$GIVE $SONHOS_QUANTITY( $THERE)? $PLEASE.*",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ),
            ),
            NamedRegex(
                "Alguém doa por favor",
                Regex(
                    "$GENERIC_PREFIX$SOMEONE $GIVE $PLEASE$QUESTION_MARK_WITH_SPACE",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ),
            ),
            NamedRegex(
                "Sonhos para Farmar",
                Regex(
                    "$GENERIC_PREFIX$SOMEONE( $COULD| me)?( $ME)? $GIVE( $SONHOS_QUANTITY|$SONHOS)?( $JUST)? $TO( (me|eu|mim))? $FARMING.*",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ),
            ),
            NamedRegex(
                "Dá sonhos cara",
                Regex(
                    "$GENERIC_PREFIX($ME )?$GIVE $SONHOS $DUDE.*",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ),
            ),
            NamedRegex(
                "Dá sonhos? (strict)",
                Regex(
                    "$GENERIC_PREFIX$GIVE $SONHOS${QUESTION_MARK_WITH_SPACE}?",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ), // super strict because it is just "doa sonhos"
            ),
            NamedRegex(
                "Dá um pouco de sonhos",
                Regex(
                    "$GENERIC_PREFIX($ME )?$GIVE $A_BIT( de)? $SONHOS.*",
                    setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
                ),
            ),
            NamedRegex(
                "Alguém me doa (strict)",
                Regex("($PO,? )?$SOMEONE $ME $GIVE", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            )
        )

        data class NamedRegex(
            val name: String,
            val regex: Regex
        )
    }

    fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.channel.idLong in channels) {
            val memberXp = transaction(m.databases.lorittaDatabase) {
                GuildProfiles.selectAll()
                    .where { (GuildProfiles.userId eq event.author.idLong) and (GuildProfiles.guildId eq event.guild.idLong) }
                    .firstOrNull()?.get(GuildProfiles.xp)
            }

            if (memberXp != null && memberXp >= 5000) return

            logger.info { "Guild/Channel/Message: ${event.guild.id}/${event.channel.id}/${event.messageId} | XP: $memberXp" }

            val rawContent = event.message.contentRaw
                .let {
                    MarkdownSanitizer.sanitize(it)
                }

            for ((name, regex) in regexes) {
                val matches = regex.matches(rawContent)

                if (matches) {
                    event.channel.sendMessage(buildReply("warned-beg").joinToString("\n") { it.build(event.author) })
                        .setMessageReference(event.message)
                        .queue()
                    return
                }
            }
        }
    }
}