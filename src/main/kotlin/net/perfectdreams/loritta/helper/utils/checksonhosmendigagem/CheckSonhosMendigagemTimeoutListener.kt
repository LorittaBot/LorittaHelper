package net.perfectdreams.loritta.helper.utils.checksonhosmendigagem

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Gateway
import dev.kord.gateway.MessageCreate
import dev.kord.gateway.on
import dev.kord.rest.json.request.DMCreateRequest
import dev.kord.rest.request.KtorRequestException
import kotlinx.datetime.Clock
import net.perfectdreams.loritta.helper.LorittaHelperKord
import kotlin.time.Duration.Companion.seconds

class CheckSonhosMendigagemTimeoutListener(val m: LorittaHelperKord) {
    companion object {
        private val SOMEONE = "(algu([eé])m|alg|algm|agl)"
        private val GIVE = "(doar?|d[aá]|empresta|doem|deem|dê)"
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
        private val PLEASE = "(p[ou0]r ?fav[ou0]r|pfv|plis|pliz|plz|pls)"
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
                Regex("$GENERIC_PREFIX$SOMEONE( $COULD| me)? $GIVE( $SOME)?( $SONHOS).*", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            ),
            NamedRegex(
                "Me doa sonhos",
                Regex("$GENERIC_PREFIX$ME $GIVE( $SOME)?( $SONHOS).*", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            ),
            NamedRegex(
                "Alguém dá 10k sonhos",
                Regex("$GENERIC_PREFIX$SOMEONE( $COULD| me)?( $ME)? $GIVE $SONHOS_QUANTITY( de)? $SONHOS( $THERE)?.*", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            ),
            NamedRegex(
                "Alguém me dá sonhos",
                Regex("$GENERIC_PREFIX$SOMEONE( $COULD| me)?( $ME) $GIVE $SONHOS( $THERE)?.*", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)), // same as above but more strict
            ),
            NamedRegex(
                "Alguém poderia dar 10k",
                Regex("$GENERIC_PREFIX$SOMEONE( $COULD| me)( $ME)? $GIVE $SONHOS_QUANTITY( $THERE)?( $OF_SONHOS)?( $THERE)?.*", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)), // same as above but more strict
            ),
            NamedRegex(
                "Alguém dá 10k (strict)",
                Regex("$GENERIC_PREFIX$SOMEONE( $COULD| me)?( $ME)? $GIVE $SONHOS_QUANTITY( $THERE)?( $OF_SONHOS)?( $THERE)?( (to|eu|mim) (pobre|falido|falida))?${QUESTION_MARK_WITH_SPACE}?", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)), // same as above but way more strict
            ),
            NamedRegex(
                "Dá 10k aí por favor",
                Regex("$GENERIC_PREFIX$GIVE $SONHOS_QUANTITY( $THERE)? $PLEASE.*", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            ),
            NamedRegex(
                "Sonhos para Farmar",
                Regex("$GENERIC_PREFIX$SOMEONE( $COULD| me)?( $ME)? $GIVE( $SONHOS_QUANTITY|$SONHOS)?( $JUST)? $TO( (me|eu|mim))? $FARMING.*", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            ),
            NamedRegex(
                "Dá sonhos cara",
                Regex("$GENERIC_PREFIX($ME )?$GIVE $SONHOS $DUDE.*", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            ),
            NamedRegex(
                "Dá sonhos? (strict)",
                Regex("$GENERIC_PREFIX$GIVE $SONHOS${QUESTION_MARK_WITH_SPACE}?", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)), // super strict because it is just "doa sonhos"
            ),
            NamedRegex(
                "Dá um pouco de sonhos",
                Regex("$GENERIC_PREFIX($ME )?$GIVE $A_BIT( de)? $SONHOS.*", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            ),
            NamedRegex(
                "Alguém me doa (strict)",
                Regex("($PO,? )?$SOMEONE $ME $GIVE", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)),
            )
        )
    }

    val activeChannels = listOf(
        Snowflake(297732013006389252), // bate-papo
        Snowflake(547119872568459284), // open-bar
        Snowflake(673531793546149899), // comandos raiz
        Snowflake(704874923104927835), // comandos nutella
        Snowflake(798014569191571506) // comandos brocolis
    )

    fun installCheckSonhosMendigagemTimeoutListener(gateway: Gateway) = gateway.on<MessageCreate> {
        if (this.message.channelId !in activeChannels)
            return@on

        if (this.message.author.id.value == m.config.applicationId.toULong())
            return@on

        for ((name, regex) in regexes) {
            val matches = regex.matches(this.message.content)

            if (matches) {
                try {
                    m.helperRest.guild.modifyGuildMember(
                        message.guildId.value!!,
                        message.author.id
                    ) {
                        this.communicationDisabledUntil = Clock.System.now()
                            .plus(60.seconds)

                        this.reason = "User matched mendigagem RegEx \"$name\"!"
                    }
                } catch (e: KtorRequestException) {
                    // Staff member
                }

                m.helperRest.channel.deleteMessage(
                    message.channelId,
                    message.id,
                    "User matched mendigagem RegEx \"$name\"!"
                )

                val channel = m.helperRest.user.createDM(DMCreateRequest(message.author.id))
                m.helperRest.channel.createMessage(channel.id) {
                    content = CheckSonhosMendigagem.buildReply("blocked-beg").joinToString("\n") { it.build(message.author) }
                }
                break
            }
        }
    }

    data class NamedRegex(
        val name: String,
        val regex: Regex
    )
}