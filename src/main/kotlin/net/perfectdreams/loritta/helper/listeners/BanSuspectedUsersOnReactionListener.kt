package net.perfectdreams.loritta.helper.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.entities.emoji.CustomEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.utils.FileUpload
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import net.perfectdreams.loritta.helper.utils.extensions.await
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction

class BanSuspectedUsersOnReactionListener(val m: LorittaHelper): ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        val user = event.user ?: return
        if (user.isBot)
            return

        if (event.channel.idLong != DailyCatcherManager.SCARLET_POLICE_CHANNEL_ID)
            return

        val emoji = event.emoji
        if (emoji !is CustomEmoji)
            return

        if (emoji.idLong != 750509326782824458L && emoji.idLong != 412585701054611458L)
            return

        m.launch {
            val deleteReport = emoji.idLong == 412585701054611458L
            val retrievedMessage = event.retrieveMessage()
                .await()

            // Only allow reactions if only two users reacted in the message (so, the bot itself and the user)
            val reactedUsers = event.reaction.retrieveUsers()
                .await()

            if (!deleteReport && reactedUsers.size != 2) {
                logger.info { "Not processing punishment for message ${event.messageId} because there is already two reactions" }
                return@launch
            }

            // If there is a cat police in the message, then it means that the punishment was already done
            val catPoliceReaction = retrievedMessage.getReaction(Emoji.fromCustom("catpolice", 585608392110899200L, false))
            if (catPoliceReaction != null) {
                logger.info { "Not processing punishment for message ${event.messageId} because there is already a cat police reaction" }
                return@launch
            }

            val metaLine = retrievedMessage.contentRaw.lines().firstOrNull { it.startsWith("**Meta:** ") } ?: return@launch

            val ids = metaLine.substringAfter("||")
                .substringBefore("||")
                .split(";")
                .map { it.toLong() }

            val channel = event.jda.getTextChannelById(DailyCatcherManager.SCARLET_POLICE_RESULTS_CHANNEL_ID)

            if (emoji.idLong == 412585701054611458L) {
                retrievedMessage.delete().queue()

                channel?.sendMessage("[Rejeitado] Denúncia Escarlate de ${ids.joinToString(", ")} foi rejeitada por ${user.asMention}...")
                    ?.addFiles(
                        FileUpload.fromData(
                            retrievedMessage.contentRaw.toByteArray(Charsets.UTF_8),
                            "message.txt"
                        )
                    )?.queue()
            } else {
                for (id in ids) {
                    val altAccountIds = ids.filter { id != it }

                    var reason = "Criar Alt Accounts (Contas Fakes/Contas Secundárias) para farmar sonhos no daily, será que os avisos no website não foram suficientes para você? ¯\\_(ツ)_/¯"

                    if (altAccountIds.isNotEmpty()) {
                        // Only add the IDs if there are more than one account to be banned
                        reason += " (Contas Alts: ${
                            altAccountIds.joinToString(", ")
                        })"
                    }

                    logger.info { "Banning $id for $reason" }

                    val successfullyBanned = transaction(m.databases.lorittaDatabase) {
                        if (BannedUsers.selectAll()
                                .where { BannedUsers.userId eq id and (BannedUsers.valid eq true) and (BannedUsers.expiresAt.isNull()) }
                                .count() != 0L) {
                            false
                        } else {
                            BannedUsers.insert {
                                it[BannedUsers.userId] = id
                                it[bannedAt] = System.currentTimeMillis()
                                it[bannedBy] = event.userIdLong
                                it[valid] = true
                                it[expiresAt] = null
                                it[BannedUsers.reason] = reason
                            }

                            true
                        }
                    }

                    if (successfullyBanned)
                        channel?.sendMessage("[Aprovado] Usuário $id foi banido por ${user.asMention} pela denúncia da polícia escarlate! <a:cat_groove:745273300850311228> ${retrievedMessage.jumpUrl}")
                            ?.queue()
                    else
                        channel?.sendMessage("[Whoops] Usuário $id já está banido ${user.asMention}! <:notlike:585607981639663633>")
                            ?.queue()
                }

                retrievedMessage.addReaction(Emoji.fromCustom("catpolice", 585608392110899200L, false))
                    .queue()
            }
        }
    }
}