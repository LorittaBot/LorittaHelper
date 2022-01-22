package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.tables.BannedUsers
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class LoriUnbanExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(LoriUnbanExecutor::class) {
        override val options = Options

        object Options : ApplicationCommandOptions() {
            val userId = string("user_id", "ID do usuário que você deseja desbanir")
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
            // Checks if the user has any valid bans
            BannedUsers.select {
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

            val banId = BannedUsers.update({ BannedUsers.userId eq userId }) {
                it[BannedUsers.valid] = false
            }

            UserUnbannedResult
        }

        when (result) {
            is UserUnbannedResult -> {
                context.sendEphemeralMessage {
                    content = "Usuário $userId (<@$userId>) foi desbanido com sucesso. Obrigada por ter corrigido a cagada de alguém... eu acho né... <:lori_coffee:727631176432484473>"
                }

                LoriToolsUtils.logToSaddestOfTheSads(
                    helper,
                    context.sender,
                    Snowflake(userId),
                    "Usuário desbanido de usar a Loritta",
                    reason,
                    Color(88, 101, 242)
                )
            }
            is UserIsNotBannedResult -> {
                context.sendEphemeralMessage {
                    content = "O usuário $userId (<@$userId>) não está banido, bobão!"
                }
            }
        }
    }

    private sealed class UnbanResult

    private object UserUnbannedResult : UnbanResult()

    private object UserIsNotBannedResult : UnbanResult()
}
