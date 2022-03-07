package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelperKord
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class LoriBanExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    companion object : SlashCommandExecutorDeclaration(LoriBanExecutor::class) {
        override val options = Options

        object Options : ApplicationCommandOptions() {
            val userId = string("user_id", "ID do usuário que você deseja banir")
                .register()

            val reason = string("reason", "Motivo que irá aparecer no ban")
                .register()
        }
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val userId = args[options.userId].toLongOrNull() ?: run {
            context.sendEphemeralMessage {
                content = "Você não colocou um ID válido... <:lori_sob:556524143281963008>"
            }
            return
        }
        val reason = args[options.reason]

        val result = transaction(helper.databases.lorittaDatabase) {
            val currentBanStatus = BannedUsers.select {
                BannedUsers.userId eq userId and
                        (BannedUsers.valid eq true) and
                        (
                                BannedUsers.expiresAt.isNull()
                                        or
                                        (BannedUsers.expiresAt.isNotNull() and (BannedUsers.expiresAt greaterEq System.currentTimeMillis())))
            }
                .orderBy(BannedUsers.bannedAt, SortOrder.DESC)
                .limit(1)
                .firstOrNull()

            if (currentBanStatus != null)
                return@transaction UserIsAlreadyBannedResult(
                    currentBanStatus[BannedUsers.reason],
                    currentBanStatus[BannedUsers.expiresAt],
                    currentBanStatus[BannedUsers.bannedBy]
                )

            val banId = BannedUsers.insertAndGetId {
                it[BannedUsers.userId] = userId
                it[BannedUsers.valid] = true
                it[BannedUsers.bannedAt] = System.currentTimeMillis()
                it[BannedUsers.expiresAt] = null // TODO: Implement temp expiration
                it[BannedUsers.reason] = reason
                it[BannedUsers.bannedBy] = context.sender.id.value
                    .toLong()
            }
            UserBannedResult(banId.value)
        }

        when (result) {
            is UserBannedResult -> {
                context.sendEphemeralMessage {
                    content = "Usuário $userId (<@$userId>) (ID do ban: ${result.id}) foi banido com sucesso. Obrigada por ter reportado o usuário! <:lori_heart:853052040425766923>"
                }

                LoriToolsUtils.logToSaddestOfTheSads(
                    helper,
                    context.sender,
                    Snowflake(userId),
                    "Usuário banido de usar a Loritta",
                    reason,
                    Color(237, 66, 69)
                )
            }
            is UserIsAlreadyBannedResult -> {
                context.sendEphemeralMessage {
                    content = if (result.bannedBy != null) {
                        "O usuário $userId (<@$userId>) já está banido, bobinho! Ele foi banido pelo motivo `${result.reason}` por <@${result.bannedBy}>"
                    } else {
                        "O usuário $userId (<@$userId>) já está banido, bobinho! Ele foi banido pelo motivo `${result.reason}`"
                    }
                }
            }
        }
    }

    private sealed class BanResult

    private class UserBannedResult(
        val id: Long
    ) : BanResult()

    private class UserIsAlreadyBannedResult(
        val reason: String,
        val expiresAt: Long?,
        val bannedBy: Long?
    ) : BanResult()
}
