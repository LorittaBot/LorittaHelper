package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelperKord
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class LoriBanRenameExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    inner class Options : ApplicationCommandOptions() {
        val userId = string("user_id", "ID do usuário que você deseja banir")

        val reason = string("reason", "Motivo que irá aparecer no ban")
    }

    override val options = Options()

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
                .firstOrNull() ?: return@transaction UserIsNotBannedResult

            BannedUsers.update({ BannedUsers.id eq currentBanStatus[BannedUsers.id] }) {
                it[BannedUsers.reason] = reason
            }

            return@transaction UserBanRenamedResult
        }

        when (result) {
            is UserBanRenamedResult -> {
                context.sendEphemeralMessage {
                    content = "Motivo do ban foi alterado! <:lori_heart:853052040425766923>"
                }

                LoriToolsUtils.logToSaddestOfTheSads(
                    helper,
                    context.sender,
                    Snowflake(userId),
                    "Motivo do Ban Alterado",
                    reason,
                    Color(214, 0, 255)
                )
            }
            is UserIsNotBannedResult -> {
                context.sendEphemeralMessage {
                    content = "O usuário $userId (<@$userId>) não está banido, então não dá para alterar o motivo do ban dele!"
                }
            }
        }
    }

    private sealed class BanRenameResult

    private object UserIsNotBannedResult : BanRenameResult()

    private object UserBanRenamedResult : BanRenameResult()
}
