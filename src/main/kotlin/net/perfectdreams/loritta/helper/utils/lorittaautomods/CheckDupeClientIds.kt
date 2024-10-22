package net.perfectdreams.loritta.helper.utils.lorittaautomods

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.dv8tion.jda.api.entities.User
import net.perfectdreams.loritta.cinnamon.pudding.services.UsersService
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.cinnamon.pudding.tables.Dailies
import net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater.BrowserFingerprints
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.LoriToolsCommand
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.RunnableCoroutine
import net.perfectdreams.loritta.helper.utils.extensions.await
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.UUID

class CheckDupeClientIds(val helper: LorittaHelper) : RunnableCoroutine {
    private val mutex = Mutex()

    override suspend fun run() {
        checkAndBanDupeClientIds(null)
    }

    suspend fun checkAndBanDupeClientIds(whoRequested: User?) {
        val channel = helper.jda.getTextChannelById(helper.config.guilds.community.channels.lorittaAutoMod)!!

        mutex.withLock {
            if (whoRequested != null) {
                channel.sendMessage("${Emotes.LORI_COFFEE} Verificando meliantes que estão evadindo ban... Pedido por ${whoRequested.asMention}").await()
            } else {
                channel.sendMessage("${Emotes.LORI_COFFEE} Verificando meliantes que estão evadindo ban... *Verificação automática*").await()
            }

            val usersToBeBanned = transaction(helper.databases.lorittaDatabase) {
                val now = Instant.now()
                    .minusSeconds(86_400)

                val dailiesGotInLast24Hours = Dailies.innerJoin(BrowserFingerprints)
                    .selectAll()
                    .where {
                        Dailies.receivedAt greaterEq now.toEpochMilli() and (Dailies.receivedById notInSubQuery UsersService.validBannedUsersList(
                            now.toEpochMilli()
                        ))
                    }
                    .toList()

                val clientIds = dailiesGotInLast24Hours.map { it[BrowserFingerprints.clientId] }

                val clientIdsThatAreBanned = Dailies
                    .innerJoin(BrowserFingerprints)
                    .innerJoin(BannedUsers, { Dailies.receivedById }, { BannedUsers.userId })
                    .selectAll()
                    .where {
                        BrowserFingerprints.clientId inList clientIds and (BannedUsers.userId inSubQuery UsersService.validBannedUsersList(
                            now.toEpochMilli()
                        ))
                    }
                    .toList()

                val usersToBeBanned = mutableListOf<BannedUser>()
                val alreadyChecked = mutableSetOf<Long>()

                for (clientIdThatAreBanned in clientIdsThatAreBanned) {
                    if (!alreadyChecked.contains(clientIdThatAreBanned[BannedUsers.userId])) {
                        val user = dailiesGotInLast24Hours.first { it[BrowserFingerprints.clientId] == clientIdThatAreBanned[BrowserFingerprints.clientId] }
                        // println("User ${user[Dailies.receivedById]} should be banned because ${clientIdThatAreBanned[BannedUsers.userId]} is banned")

                        usersToBeBanned.add(
                            BannedUser(
                                user[Dailies.receivedById],
                                clientIdThatAreBanned[BannedUsers.userId],
                                clientIdThatAreBanned[BrowserFingerprints.clientId],
                                clientIdThatAreBanned[BannedUsers.reason]
                            )
                        )
                        alreadyChecked.add(clientIdThatAreBanned[BannedUsers.userId])
                    }
                }

                usersToBeBanned
            }

            for (userToBeBanned in usersToBeBanned) {
                channel.sendMessage("${Emotes.LORI_BAN_HAMMER} Banindo <@${userToBeBanned.userToBeBannedId}> (`${userToBeBanned.userToBeBannedId}`) pois ele é evasão de ban de <@${userToBeBanned.relatedUserId}> (`${userToBeBanned.relatedUserId}`), o meliante está banido por `${userToBeBanned.reason}` e o client ID dele é `${userToBeBanned.clientId}`").await()

                LoriToolsCommand.banUser(
                    helper,
                    helper.jda.selfUser.idLong,
                    setOf(userToBeBanned.userToBeBannedId),
                    "Evasão de Ban! (ID da conta banida: ${userToBeBanned.relatedUserId})",
                    null
                )
            }

            if (whoRequested != null) {
                channel.sendMessage("${Emotes.LORI_OWO} Verificação terminada. Gostou ${whoRequested.asMention}? Eu bani ${usersToBeBanned.size} meliantes!").await()
            } else {
                channel.sendMessage("${Emotes.LORI_OWO} Verificação terminada. Eu mesmo que pedi essa verificação, e ainda bani ${usersToBeBanned.size} meliantes. Sinceramente eu amei essa verificação que eu fiz, na minha humilde opinião ninguém da equipe conseguiria fazer ela melhor.").await()
            }
        }
    }

    private data class BannedUser(
        val userToBeBannedId: Long,
        val relatedUserId: Long,
        val clientId: UUID,
        val reason: String
    )
}