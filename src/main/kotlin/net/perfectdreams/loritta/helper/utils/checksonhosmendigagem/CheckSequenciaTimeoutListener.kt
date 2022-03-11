package net.perfectdreams.loritta.helper.utils.checksonhosmendigagem

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Gateway
import dev.kord.gateway.MessageCreate
import dev.kord.gateway.on
import kotlinx.datetime.Clock
import net.perfectdreams.loritta.helper.LorittaHelperKord
import kotlin.time.Duration.Companion.seconds

class CheckSequenciaTimeoutListener(val m: LorittaHelperKord) {
    val activeChannels = listOf(
        Snowflake(547119872568459284), // open-bar
        Snowflake(673531793546149899), // comandos raiz
        Snowflake(704874923104927835), // comandos nutella
        Snowflake(798014569191571506) // comandos brocolis
    )

    fun installCheckSequenciaTimeoutListener(gateway: Gateway) = gateway.on<MessageCreate> {
        if (this.message.channelId !in activeChannels)
            return@on

        if (this.message.author.id.value == m.config.applicationId.toULong())
            return@on

        if (this.message.content.contains("sequência", true) || this.message.content.contains("sequencia", true)) {
            if (!(this.message.content.contains("vitória", true) ||
                        this.message.content.contains("vitoria", true) ||
                        this.message.content.contains("não existe", true) ||
                        this.message.content.contains("nao existe", true) ||
                        this.message.content.contains("n existe", true) ||
                        this.message.content.contains("balela", true) ||
                        this.message.content.contains("mentira", true) ||
                        this.message.content.contains("burr", true) ||
                        this.message.content.contains("idiota", true) ||
                        this.message.content.contains("demente", true) ||
                        this.message.content.contains("retardad", true))) {
                m.helperRest.guild.modifyGuildMember(
                    message.guildId.value!!,
                    message.author.id
                ) {
                    this.communicationDisabledUntil = Clock.System.now()
                        .plus(7.seconds)

                    this.reason = "User matched sequência!"
                }

                m.helperRest.channel.createMessage(this.message.channelId) {
                    this.content = """<@${message.author.id.value}> 👏SEQUÊNCIA👏DE👏APOSTAS👏NÃO👏EXISTE👏
                        |
                        |<:lori_clown:950111543574536212> Contas novas não tem chance maior de ganharem.
                        |<:lori_clown:950111543574536212> Usuários premiums da Loritta não tem chance maior de ganharem.
                        |<:lori_clown:950111543574536212> Usar o comando de girar a moeda não aumenta a sua chance de ganhar.
                        |<:lori_clown:950111543574536212> Usar o comando de apostas em servidores diferentes não aumenta a sua chance de ganhar.
                        |<:lori_clown:950111543574536212> Demorar para aceitar a aposta não aumenta as suas chances de ganhar.
                        |<:lori_clown:950111543574536212> Não existe programas ou apps que aumentem as suas chances de ganhar, como também eles podem ser vírus que podem invadir a sua conta!
                        |<:lori_clown:950111543574536212> E é claro, enfiar o dedo no fiofo não aumenta a sua chance de ganhar. Pare de usar isso como desculpa para enfiar o dedo no fiofo meu deus.
                        |
                        |Tudo isso dai é superstição, igual falar que cloroquina ajuda contra coronavirus <:lori_rage:950114905091096586>
                        |
                        |Pare de acreditar que sequência existe, quem acredita nessas coisas é mais burro que o Bolsonaro, e eu não acho que você é mais burro que o Bolsonaro <:lori_sob:950109140880080956>
                        |
                        |Se você quer saber mais, veja: <https://loritta.website/br/extras/faq-loritta/coinflip-bug?utm_source=discord&utm_medium=sequence-warn&utm_campaign=sequence-psa>
                    """.trimMargin()
                }
            }
        }
    }
}