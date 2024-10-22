package net.perfectdreams.loritta.helper.utils.generatebanstatusreport

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.await
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class GenerateBanStatusReport(val m: LorittaHelper) {
    private val logger = KotlinLogging.logger {}

    suspend fun onMessageReceived(event: MessageReceivedEvent) {
        val (messageUrl, state, reason) = event.message.contentRaw.split("\n")

        val messageUrlSplitted = messageUrl.split("/")

        val messageId = messageUrlSplitted[messageUrlSplitted.size - 1]
        val channelId = messageUrlSplitted[messageUrlSplitted.size - 2]

        val message = event.jda.getTextChannelById(channelId)!!
                .retrieveMessageById(messageId)
                .await()

        val embed = message.embeds.first()
        val responseId = embed.title!!.removePrefix("Pedido de Unban #")
        val userId = embed.footer!!.text!!.removePrefix("ID do usuário: ")

        val bannedState = transaction(m.databases.lorittaDatabase) {
            BannedUsers.selectAll().where { BannedUsers.userId eq userId.toLong() }
                .orderBy(BannedUsers.bannedAt, SortOrder.DESC)
                .firstOrNull()
        }

        val responseStuff = """
⸻⸻⸻⸻⸻⸻⸻⸻⸻
<:lori_ban_hammer:741058240455901254> **RESULTADO:**
**ID da pergunta:** `$responseId`
**Usuário:** `$userId`
**Motivo do Ban:** `${bannedState?.getOrNull(BannedUsers.reason)}`
**Link da Form:** $messageUrl
**Resultado:** $state
**Motivo:** $reason
        """.trimIndent()

        event.message.delete().await()

        event.channel.sendMessage(
                responseStuff
        ).await()

        message.addReaction(Emoji.fromUnicode("☑️"))
                .await()
    }
}