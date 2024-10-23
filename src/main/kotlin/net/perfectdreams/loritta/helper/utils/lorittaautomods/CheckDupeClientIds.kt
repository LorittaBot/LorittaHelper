package net.perfectdreams.loritta.helper.utils.lorittaautomods

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.utils.TimeFormat
import net.perfectdreams.loritta.cinnamon.pudding.services.UsersService
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.cinnamon.pudding.tables.Profiles
import net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater.BrowserFingerprints
import net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater.Dailies
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.LoriToolsCommand
import net.perfectdreams.loritta.helper.tables.LorittaAutoModIgnoredClientIds
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.RunnableCoroutine
import net.perfectdreams.loritta.helper.utils.extensions.await
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class CheckDupeClientIds(val helper: LorittaHelper) : RunnableCoroutine {
    companion object {
        fun validBannedUsersList(currentMillis: Long) = BannedUsers.select(BannedUsers.userId).where {
            (BannedUsers.valid eq true) and
                    (
                            BannedUsers.expiresAt.isNull()
                                    or
                                    (
                                            BannedUsers.expiresAt.isNotNull() and
                                                    (BannedUsers.expiresAt greaterEq currentMillis))
                            )

        }
    }

    private val mutex = Mutex()

    override suspend fun run() {
        checkAndBanDupeClientIds(null, false)
    }

    suspend fun checkAndBanDupeClientIds(
        whoRequested: User?,
        dryRun: Boolean
    ) {
        val channel = helper.jda.getTextChannelById(helper.config.guilds.community.channels.lorittaAutoMod)!!

        mutex.withLock {
            if (whoRequested != null) {
                channel.sendMessage("# ${Emotes.SUPER_VIEIRINHA} VERIFICAÇÃO DE MELIANTES - ${TimeFormat.DATE_TIME_SHORT.format(Instant.now())}\n${Emotes.LORI_COFFEE} Verificando meliantes que estão evadindo ban... Pedido por ${whoRequested.asMention} - Ensaio? $dryRun").await()
            } else {
                channel.sendMessage("# ${Emotes.SUPER_VIEIRINHA} VERIFICAÇÃO DE MELIANTES - ${TimeFormat.DATE_TIME_SHORT.format(Instant.now())}\n${Emotes.LORI_COFFEE} Verificando meliantes que estão evadindo ban... *Verificação automática* - Ensaio? $dryRun").await()
            }

            val whitelistedClientIds = transaction(helper.databases.helperDatabase) {
                LorittaAutoModIgnoredClientIds.selectAll()
                    .map {
                        it[LorittaAutoModIgnoredClientIds.clientId]
                    }
                    .toSet()
            }

            val usersToBeBanned = transaction(helper.databases.lorittaDatabase) {
                val now = Instant.now()
                    .minusSeconds(604_800) // 7 days

                val dailiesRecentlyRetrievedHours = Dailies
                    .innerJoin(BrowserFingerprints)
                    .innerJoin(Profiles, { Profiles.id }, { Dailies.receivedById })
                    .selectAll()
                    .where {
                        Dailies.receivedAt greaterEq now.toEpochMilli() and (Dailies.receivedById notInSubQuery validBannedUsersList(now.toEpochMilli()))
                    }
                    .toList()

                val allClientIds = dailiesRecentlyRetrievedHours.map { it[BrowserFingerprints.clientId] }
                val alreadyChecked = mutableSetOf<Long>()
                val usersToBeBanned = mutableListOf<BannedUser>()

                // We can't actually chunk by 65_535 because it is ALL PARAMETERS of the query (every client ID + the subquery)
                val clientIdsThatAreBanned = allClientIds.chunked(65_530).flatMap { clientIds ->
                    Dailies
                        .innerJoin(BrowserFingerprints)
                        .innerJoin(BannedUsers, { Dailies.receivedById }, { BannedUsers.userId })
                        .selectAll()
                        .where {
                            BrowserFingerprints.clientId inList clientIds and (BannedUsers.userId inSubQuery validBannedUsersList(
                                now.toEpochMilli()
                            ))
                        }
                        .toList()
                }

                for (user in dailiesRecentlyRetrievedHours) {
                    if (user[Dailies.receivedById] in alreadyChecked)
                        continue

                    val bannedUsersAssociatedWithThisUser = clientIdsThatAreBanned.filter { it[BrowserFingerprints.clientId] == user[BrowserFingerprints.clientId] }

                    if (bannedUsersAssociatedWithThisUser.isNotEmpty()) {
                        usersToBeBanned.add(
                            BannedUser(
                                user[Dailies.receivedById],
                                bannedUsersAssociatedWithThisUser.map { it[BannedUsers.userId] }.distinct(),
                                user[BrowserFingerprints.clientId],
                                bannedUsersAssociatedWithThisUser.minBy { it[BannedUsers.bannedAt] }[BannedUsers.reason],
                                user[Profiles.money],
                                user[BrowserFingerprints.clientId] in whitelistedClientIds
                            )
                        )
                        alreadyChecked.add(user[Dailies.receivedById])
                    }
                }

                usersToBeBanned
            }

            for (userToBeBanned in usersToBeBanned) {
                if (userToBeBanned.isClientIdWhitelisted) {
                    if (!dryRun) {
                        channel.sendMessage("${Emotes.LORI_BAN_HAMMER} Não irei banir <@${userToBeBanned.userToBeBannedId}> (`${userToBeBanned.userToBeBannedId}`) mesmo sendo evasão de ban de ${userToBeBanned.relatedUserIds.joinToString { "<@$it> (`$it`)" }} pois o Client ID dele está na whitelist, o client ID dele é `${userToBeBanned.clientId}`").await()
                    } else {
                        channel.sendMessage("${Emotes.LORI_PAC} Não irei banir <@${userToBeBanned.userToBeBannedId}> (`${userToBeBanned.userToBeBannedId}`) mesmo sendo evasão de ban de ${userToBeBanned.relatedUserIds.joinToString { "<@$it> (`$it`)" }} pois o Client ID dele está na whitelist, o client ID dele é `${userToBeBanned.clientId}` (ensaio, usuário não foi banido)").await()
                    }
                } else {
                    if (!dryRun) {
                        channel.sendMessage("${Emotes.LORI_BAN_HAMMER} Banindo <@${userToBeBanned.userToBeBannedId}> (`${userToBeBanned.userToBeBannedId}`) pois ele é evasão de ban de ${userToBeBanned.relatedUserIds.joinToString { "<@$it> (`$it`)" }}, o meliante está banido por `${userToBeBanned.reason}` (motivo do ban mais antigo), o client ID dele é `${userToBeBanned.clientId}`, e atualmente ele possui ${userToBeBanned.sonhos} sonhos")
                            .await()

                        LoriToolsCommand.banUser(
                            helper,
                            helper.jda.selfUser,
                            setOf(userToBeBanned.userToBeBannedId),
                            if (userToBeBanned.relatedUserIds.size == 1) {
                                "Evasão de Ban! (ID da conta banida: ${userToBeBanned.relatedUserIds.first()})"
                            } else {
                                "Evasão de Ban! (IDs das contas banidas: ${userToBeBanned.relatedUserIds.joinToString()})"
                            },
                            null
                        )
                    } else {
                        channel.sendMessage("${Emotes.LORI_BAN_HAMMER} Banindo <@${userToBeBanned.userToBeBannedId}> (`${userToBeBanned.userToBeBannedId}`) pois ele é evasão de ban de ${userToBeBanned.relatedUserIds.joinToString { "<@$it> (`$it`)" }}, o meliante está banido por `${userToBeBanned.reason}` (motivo do ban mais antigo), o client ID dele é `${userToBeBanned.clientId}`, e atualmente ele possui ${userToBeBanned.sonhos} sonhos (ensaio, usuário não foi banido)").await()
                    }
                }
            }

            val usersThatGotReallyBanned = usersToBeBanned.filter { !it.isClientIdWhitelisted }

            if (whoRequested != null) {
                channel.sendMessage("${Emotes.LORI_OWO} Verificação terminada. Gostou ${whoRequested.asMention}? Eu bani ${usersThatGotReallyBanned.size} meliantes! (${usersThatGotReallyBanned.sumOf { it.sonhos }} sonhos) - Ensaio? $dryRun").await()
            } else {
                channel.sendMessage("${Emotes.LORI_OWO} Verificação terminada. Eu mesmo que pedi essa verificação, e ainda bani ${usersThatGotReallyBanned.size} meliantes. (${usersThatGotReallyBanned.sumOf { it.sonhos }} sonhos) Sinceramente eu amei essa verificação que eu fiz, na minha humilde opinião ninguém da equipe conseguiria fazer ela melhor. - Ensaio? $dryRun").await()
            }
        }
    }

    private data class BannedUser(
        val userToBeBannedId: Long,
        val relatedUserIds: List<Long>,
        val clientId: UUID,
        val reason: String,
        val sonhos: Long,
        val isClientIdWhitelisted: Boolean,
    )
}