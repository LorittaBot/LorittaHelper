package net.perfectdreams.loritta.helper.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.BannedUsers
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcherManager
import net.perfectdreams.loritta.helper.utils.extensions.await
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class BanSuspectedUsersOnReactionListener(val m: LorittaHelper): ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (event.user.isBot)
            return

        if (event.channel.idLong != DailyCatcherManager.SCARLET_POLICE_CHANNEL_ID)
            return

        if (event.reactionEmote.idLong != 750509326782824458L && event.reactionEmote.idLong != 412585701054611458L)
            return

        m.launch {
            val deleteReport = event.reactionEmote.idLong == 412585701054611458L
            val retrievedMessage = event.retrieveMessage()
                .await()

            // Only allow reactions if only two users reacted in the message (so, the bot itself and the user)
            val reactedUsers = event.reaction.retrieveUsers()
                .await()

            if (!deleteReport && reactedUsers.size != 2) {
                logger.info { "Not processing punishment for message ${event.messageId} because there is already two messages" }
                return@launch
            }

            // If there is a cat police in the message, then it means that the punishment was already done
            val catPoliceReaction = retrievedMessage.getReactionById(585608392110899200L)
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

            if (event.reactionEmote.idLong == 412585701054611458L) {
                retrievedMessage.delete().queue()

                channel?.sendMessage("[Rejeitado] Denúncia Escarlate de ${ids.joinToString(", ")} foi rejeitada por ${event.user.asMention}...")
                    ?.addFile(
                        retrievedMessage.contentRaw.toByteArray(Charsets.UTF_8),
                        "message.txt"
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

                    val successfullyBanned = transaction {
                        if (BannedUsers.select {
                                BannedUsers.userId eq id and (BannedUsers.valid eq true) and (BannedUsers.expiresAt.isNull())
                            }.count() != 0L) {
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
                        channel?.sendMessage("[Aprovado] Usuário $id foi banido por ${event.user.asMention} pela denúncia da polícia escarlate! <a:cat_groove:745273300850311228> ${retrievedMessage.jumpUrl}")
                            ?.queue()
                    else
                        channel?.sendMessage("[Whoops] Usuário $id já está banido ${event.user.asMention}! <:notlike:585607981639663633>")
                            ?.queue()
                }

                retrievedMessage.addReaction("catpolice:585608392110899200")
                    .queue()
            }
        }
    }
}