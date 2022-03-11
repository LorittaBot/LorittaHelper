package net.perfectdreams.loritta.helper.utils.checksonhosmendigagem

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Gateway
import dev.kord.gateway.MessageCreate
import dev.kord.gateway.on
import kotlinx.datetime.Clock
import net.perfectdreams.loritta.helper.LorittaHelperKord
import kotlin.time.Duration.Companion.seconds

class CheckSonhosMendigagemTimeoutListener(val m: LorittaHelperKord) {
    val activeChannels = listOf(
        Snowflake(297732013006389252), // bate-papo
        Snowflake(547119872568459284), // open-bar
        Snowflake(673531793546149899), // comandos raiz
        Snowflake(704874923104927835), // comandos nutella
        Snowflake(798014569191571506) // comandos brocolis
    )

    val regexes = listOf(
        Regex("((sou pobre|[eés]t[oô]u? falid[oae]|(eu )?fali) )?(algu([eé])m|alg|algm)? ?(me )?(d[áa]|doa|doar|d[eê]|doem) ([0-9A-z]+ )?(de )?sonhos ?(porfavor*|por favor|pfv*|pliz*)?", RegexOption.IGNORE_CASE), // ALGUÉM ME DÁ 30K DE SONHOS POR FAVOR
    )

    fun installCheckSonhosMendigagemTimeoutListener(gateway: Gateway) = gateway.on<MessageCreate> {
        if (this.message.channelId !in activeChannels)
            return@on

        if (this.message.author.id.value == m.config.applicationId.toULong())
            return@on

        for (regex in regexes) {
            val matches = regex.matches(this.message.content)

            if (matches) {
                m.helperRest.guild.modifyGuildMember(
                    message.guildId.value!!,
                    message.author.id
                ) {
                    this.communicationDisabledUntil = Clock.System.now()
                        .plus(60.seconds)

                    this.reason = "User matched mendigagem RegEx! ${regex.toString()}"
                }

                m.helperRest.channel.deleteMessage(
                    message.channelId,
                    message.id,
                    "User matched mendigagem RegEx! ${regex.toString()}"
                )
            }
        }
    }
}