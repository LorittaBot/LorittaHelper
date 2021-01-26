package net.perfectdreams.loritta.helper.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.BannedUsers
import net.perfectdreams.loritta.helper.utils.dailycatcher.DailyCatcher
import net.perfectdreams.loritta.helper.utils.extensions.await
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class BanSuspectedUsersOnReactionListener(val m: LorittaHelper): ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
        if (event.user.isBot)
            return

        if (event.channel.idLong != DailyCatcher.SCARLET_POLICE_CHANNEL_ID)
            return

        if (event.reactionEmote.idLong != 750509326782824458L)
            return

        m.launch {
            val retrievedMessage = event.retrieveMessage()
                .await()

            // Only allow reactions if only two users reacted in the message (so, the bot itself and the user)
            val reactedUsers = event.reaction.retrieveUsers()
                .await()
            if (reactedUsers.size != 2) {
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

            for (id in ids) {
                val altAccountIds = ids.filter { id != it }

                val reason = """Criar Alt Accounts (Contas Fakes/Contas Secundárias) para farmar sonhos no daily, será que os avisos no website não foram suficientes para você? ¯\_(ツ)_/¯ (Contas Alts: ${altAccountIds.joinToString(", ")})"""

                logger.info { "Banning $id for $reason" }

                transaction {
                    BannedUsers.insert {
                        it[BannedUsers.userId] = id
                        it[bannedAt] = System.currentTimeMillis()
                        it[bannedBy] = event.userIdLong
                        it[valid] = true
                        it[expiresAt] = null
                        it[BannedUsers.reason] = reason
                    }
                }
            }

            retrievedMessage.addReaction("catpolice:585608392110899200")
                .queue()
        }
    }
}