package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.rest.request.KtorRequestException
import kotlinx.datetime.Clock
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.Constants
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.days

class LoriBanExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    inner class Options : ApplicationCommandOptions() {
        val userIds = string("user_ids", "ID do usuário que você deseja banir (pode ser vários)")

        val reason = string("reason", "Motivo que irá aparecer no ban")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessageEphemerally()

        val userIds = args[options.userIds]
            .split(" ")
            .mapNotNull { it.toLongOrNull() }
            .toSet()

        if (userIds.isEmpty()) {
            context.sendEphemeralMessage {
                content = "Você não colocou um ID válido... <:lori_sob:556524143281963008>"
            }
            return
        }

        val reason = args[options.reason]

        val results = mutableListOf<BanResult>()
        transaction(helper.databases.lorittaDatabase) {
            val currentBanStatuses = BannedUsers.select {
                BannedUsers.userId inList userIds and
                        (BannedUsers.valid eq true) and
                        (
                                BannedUsers.expiresAt.isNull()
                                        or
                                        (BannedUsers.expiresAt.isNotNull() and (BannedUsers.expiresAt greaterEq System.currentTimeMillis())))
            }
                .orderBy(BannedUsers.bannedAt, SortOrder.DESC)
                .toList()

            for (currentBanStatus in currentBanStatuses) {
                results.add(
                    UserIsAlreadyBannedResult(
                        currentBanStatus[BannedUsers.userId],
                        currentBanStatus[BannedUsers.reason],
                        currentBanStatus[BannedUsers.expiresAt],
                        currentBanStatus[BannedUsers.bannedBy]
                    )
                )
            }

            val bannedUsersIds = currentBanStatuses.map { it[BannedUsers.userId] }
            val usersThatCanBeBanned = userIds.filter { it !in bannedUsersIds }

            for (userId in usersThatCanBeBanned) {
                val banId = BannedUsers.insertAndGetId {
                    it[BannedUsers.userId] = userId
                    it[BannedUsers.valid] = true
                    it[BannedUsers.bannedAt] = System.currentTimeMillis()
                    it[BannedUsers.expiresAt] = null // TODO: Implement temp expiration
                    it[BannedUsers.reason] = reason
                    it[BannedUsers.bannedBy] = context.sender.id.value
                        .toLong()
                }
                results.add(UserBannedResult(banId.value, userId))
            }
        }

        for (result in results) {
            when (result) {
                is UserBannedResult -> {
                    context.sendEphemeralMessage {
                        content = "Usuário ${result.userId} (<@${result.userId}>) (ID do ban: ${result.id}) foi banido com sucesso. Obrigada por ter reportado o usuário! <:lori_heart:853052040425766923>"
                    }

                    LoriToolsUtils.logToSaddestOfTheSads(
                        helper,
                        context.sender,
                        Snowflake(result.userId),
                        "Usuário banido de usar a Loritta",
                        reason,
                        Color(237, 66, 69)
                    )

                    try {
                        helper.helperRest.guild.modifyGuildMember(
                            Snowflake(Constants.COMMUNITY_SERVER_ID),
                            Snowflake(result.userId)
                        ) {
                            this.communicationDisabledUntil = Clock.System.now()
                                .plus(28.days)

                            this.reason = "User is Loritta Banned!"
                        }
                    } catch (e: KtorRequestException) {
                    }
                }
                is UserIsAlreadyBannedResult -> {
                    context.sendEphemeralMessage {
                        content = if (result.bannedBy != null) {
                            "O usuário ${result.userId} (<@${result.userId}>) já está banido, bobinho! Ele foi banido pelo motivo `${result.reason}` por <@${result.bannedBy}>"
                        } else {
                            "O usuário ${result.userId} (<@${result.userId}>) já está banido, bobinho! Ele foi banido pelo motivo `${result.reason}`"
                        }
                    }
                }
            }
        }
    }

    private sealed class BanResult

    private class UserBannedResult(
        val id: Long,
        val userId: Long
    ) : BanResult()

    private class UserIsAlreadyBannedResult(
        val userId: Long,
        val reason: String,
        val expiresAt: Long?,
        val bannedBy: Long?
    ) : BanResult()
}
