package net.perfectdreams.loritta.helper.interactions.commands.vanilla

import dev.kord.common.entity.Snowflake
import dev.kord.rest.request.KtorRequestException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.datetime.toKotlinInstant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.UserSnowflake
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.tables.EconomyState
import net.perfectdreams.loritta.helper.utils.TimeUtils
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.helper.utils.slash.LoriToolsUtils
import net.perfectdreams.loritta.helper.utils.slash.PermissionLevel
import net.perfectdreams.loritta.morenitta.interactions.commands.*
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import net.perfectdreams.loritta.morenitta.interactions.styled
import net.perfectdreams.loritta.serializable.dashboard.requests.LorittaDashboardRPCRequest
import net.perfectdreams.loritta.serializable.dashboard.responses.LorittaDashboardRPCResponse
import net.perfectdreams.pantufa.rpc.BanSparklyPowerPlayerLorittaBannedRequest
import net.perfectdreams.pantufa.rpc.BanSparklyPowerPlayerLorittaBannedResponse
import net.perfectdreams.pantufa.rpc.PantufaRPCRequest
import net.perfectdreams.pantufa.rpc.PantufaRPCResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.awt.Color
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*


class LoriToolsCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand("loritools", "Ferramentas de administração relacionadas a Loritta") {
        subcommand("loriban", "Bane alguém de usar a Loritta") {
            executor = LoriBanExecutor(helper)
        }

        subcommand("loribanpredefined", "Bane alguém de usar a Loritta com motivos pré-definidos") {
            executor = LoriBanPredefinedExecutor(helper)
        }

        subcommand("loriunban", "Desbane alguém de usar a Loritta") {
            executor = LoriUnbanExecutor(helper)
        }

        subcommand("loribanrename", "Altera o motivo do ban de um usuário") {
            executor = LoriBanRenameExecutor(helper)
        }

        subcommand("economy", "Altera o estado da economia da Loritta") {
            executor = LoriEconomyStateExecutor(helper)
        }

        subcommand("status", "Altera o status da Loritta") {
            executor = LoriStatusExecutor(helper)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}

        suspend fun banUser(
            helper: LorittaHelper,
            context: ApplicationCommandContext,
            userIds: Set<Long>,
            reason: String,
            expiresAt: Long?
        ) {
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
                        it[valid] = true
                        it[bannedAt] = System.currentTimeMillis()
                        it[BannedUsers.expiresAt] = expiresAt
                        it[BannedUsers.reason] = reason
                        it[bannedBy] = context.user.idLong
                    }
                    results.add(UserBannedResult(banId.value, userId, reason))
                }
            }

            // Get all banned users and relay them to SparklyPower
            val sparklyResults = mutableMapOf<UserBannedResult, BanSparklyPowerPlayerLorittaBannedResponse>()
            val pantufaUrl = helper.config.pantufaUrl

            if (pantufaUrl != null) {
                for (result in results.filterIsInstance<UserBannedResult>()) {
                    try {
                        val response = Json.decodeFromString<PantufaRPCResponse>(
                            LorittaHelperKord.http.post(pantufaUrl.removeSuffix("/") + "/rpc") {
                                setBody(
                                    TextContent(
                                        Json.encodeToString<PantufaRPCRequest>(
                                            BanSparklyPowerPlayerLorittaBannedRequest(
                                                result.userId,
                                                result.reason
                                            )
                                        ),
                                        ContentType.Application.Json
                                    )
                                )
                            }.bodyAsText()
                        )

                        if (response is BanSparklyPowerPlayerLorittaBannedResponse)
                            sparklyResults[result] = response
                    } catch (e: Exception) {
                        // If an exception is thrown
                        logger.warn(e) { "Something went wrong while relaying user ${result.userId} ban to SparklyPower" }
                    }
                }
            }

            for (result in results) {
                when (result) {
                    is UserBannedResult -> {
                        val sparklyResult = sparklyResults[result]

                        context.reply(true) {
                            content = buildString {
                                appendLine("Usuário ${result.userId} (<@${result.userId}>) (ID do ban: ${result.id}) foi banido com sucesso. Obrigada por ter reportado o usuário! <:lori_heart:853052040425766923>")
                                if (sparklyResult is BanSparklyPowerPlayerLorittaBannedResponse.Success) {
                                    appendLine("Player ${sparklyResult.userName} foi banido do SparklyPower!")
                                }
                            }
                        }

                        LoriToolsUtils.logToSaddestOfTheSads(
                            helper,
                            context.user,
                            result.userId,
                            "Usuário banido de usar a Loritta",
                            expiresAt,
                            reason,
                            Color(237, 66, 69)
                        )

                        try {
                            val guild = helper.jda.getGuildById(helper.config.guilds.community.id)
                            guild?.timeoutFor(UserSnowflake.fromId(result.userId), Duration.ofDays(28))
                                ?.reason("User is Loritta Banned!")
                                ?.await()
                        } catch (e: Exception) {
                        }
                    }
                    is UserIsAlreadyBannedResult -> {
                        context.reply(true) {
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
            val userId: Long,
            val reason: String
        ) : BanResult()

        private class UserIsAlreadyBannedResult(
            val userId: Long,
            val reason: String,
            val expiresAt: Long?,
            val bannedBy: Long?
        ) : BanResult()
    }

    class LoriBanExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.ADMIN) {
        inner class Options : ApplicationCommandOptions() {
            val userIds = string("user_ids", "ID do usuário que você deseja banir (pode ser vários)")

            val reason = string("reason", "Motivo que irá aparecer no ban")

            val duration = optionalString("duration", "Por enquanto tempo o usuário está banido")
        }

        override val options = Options()

        override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.deferChannelMessage(true)

            val userIds = args[options.userIds]
                .split(" ")
                .mapNotNull { it.toLongOrNull() }
                .toSet()

            if (userIds.isEmpty()) {
                context.reply(true) {
                    content = "Você não colocou um ID válido... <:lori_sob:556524143281963008>"
                }
                return
            }

            val reason = args[options.reason]
            val durationAsString = args[options.duration]
            val duration = if (durationAsString != null) {
               TimeUtils.convertToMillisRelativeToNow(durationAsString)
            } else null

            banUser(
                helper,
                context,
                userIds,
                reason,
                duration
            )
        }
    }

    class LoriBanPredefinedExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.ADMIN) {
        inner class Options : ApplicationCommandOptions() {
            val userIds = string("user_ids", "ID do usuário que você deseja banir (pode ser vários)")
            val predefinedReason = string("predefined_reason", "Motivo de ban pré-definido") {
                for (predefinedReason in PredefinedBanReason.entries) {
                    val choiceName = buildString {
                        append(predefinedReason.fancyName + " (${predefinedReason.duration.toDays()} dias)")
                    }
                    choice(choiceName, predefinedReason.name)
                }
            }
            val reason = optionalString("reason", "Motivo adicionado que irá aparecer no final do motivo")
        }

        override val options = Options()

        override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.deferChannelMessage(true)

            val userIds = args[options.userIds]
                .split(" ")
                .mapNotNull { it.toLongOrNull() }
                .toSet()

            if (userIds.isEmpty()) {
                context.reply(true) {
                    content = "Você não colocou um ID válido... <:lori_sob:556524143281963008>"
                }
                return
            }

            val predefinedReason = PredefinedBanReason.valueOf(args[options.predefinedReason])
            val banReason = predefinedReason.banReason
            val additionalReason = args[options.reason]

            val reason = buildString {
                append(banReason)
                if (additionalReason != null) {
                    append(" ")
                    append(additionalReason)
                }
            }

            banUser(
                helper,
                context,
                userIds,
                reason,
                System.currentTimeMillis() + predefinedReason.duration.toMillis()
            )
        }
    }

    class LoriUnbanExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.ADMIN) {
        inner class Options : ApplicationCommandOptions() {
            val userIds = string("user_ids", "ID do usuário que você deseja desbanir (pode ser vários)")

            val reason = string("reason", "Motivo que irá aparecer no unban")
        }

        override val options = Options()

        override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.deferChannelMessage(true)

            val userIds = args[options.userIds]
                .split(" ")
                .mapNotNull { it.toLongOrNull() }
                .toSet()

            if (userIds.isEmpty()) {
                context.reply(true) {
                    content = "Você não colocou um ID válido... <:lori_sob:556524143281963008>"
                }
                return
            }

            val reason = args[options.reason]

            val results = mutableListOf<UnbanResult>()
            transaction(helper.databases.lorittaDatabase) {
                // Checks if the user has any valid bans
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

                val bannedUsersIds = currentBanStatuses.map { it[BannedUsers.userId] }
                val nonBannedUsers = userIds.filter { it !in bannedUsersIds }

                for (userId in nonBannedUsers) {
                    results.add(UserIsNotBannedResult(userId))
                }

                for (userId in bannedUsersIds) {
                    val banId = BannedUsers.update({ BannedUsers.userId eq userId }) {
                        it[BannedUsers.valid] = false
                    }

                    results.add(UserUnbannedResult(userId, reason))
                }
            }

            for (result in results) {
                when (result) {
                    is UserUnbannedResult -> {
                        context.reply(true) {
                            content =
                                "Usuário ${result.userId} (<@${result.userId}>) foi desbanido com sucesso. Obrigada por ter corrigido a cagada de alguém... eu acho né... <:lori_coffee:727631176432484473>"
                        }

                        LoriToolsUtils.logToSaddestOfTheSads(
                            helper,
                            context.user,
                            result.userId,
                            "Usuário desbanido de usar a Loritta",
                            null,
                            result.reason,
                            Color(88, 101, 242)
                        )

                        try {
                            helper.helperRest.guild.modifyGuildMember(
                                Snowflake(helper.config.guilds.community.id),
                                Snowflake(result.userId)
                            ) {
                                this.communicationDisabledUntil = null

                                this.reason = "User was Loritta Unbanned!"
                            }
                        } catch (e: KtorRequestException) {
                        } // Maybe they aren't on the server
                    }

                    is UserIsNotBannedResult -> {
                        context.reply(true) {
                            content = "O usuário ${result.userId} (<@${result.userId}>) não está banido, bobão!"
                        }
                    }
                }
            }
        }

        private sealed class UnbanResult

        private class UserUnbannedResult(
            val userId: Long,
            val reason: String,
        ) : UnbanResult()

        private class UserIsNotBannedResult(
            val userId: Long,
        ) : UnbanResult()
    }

    class LoriBanRenameExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.ADMIN) {
        inner class Options : ApplicationCommandOptions() {
            val userId = string("user_ids", "ID do usuário que você deseja desbanir")

            val reason = string("reason", "Motivo que irá aparecer no ban")
        }

        override val options = Options()

        override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
            val userId = args[options.userId].toLongOrNull() ?: run {
                context.reply(true) {
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
                    .firstOrNull() ?: return@transaction LoriBanRenameExecutor.UserIsNotBannedResult

                BannedUsers.update({ BannedUsers.id eq currentBanStatus[BannedUsers.id] }) {
                    it[BannedUsers.reason] = reason
                }

                return@transaction LoriBanRenameExecutor.UserBanRenamedResult
            }

            when (result) {
                is UserBanRenamedResult -> {
                    context.reply(true) {
                        content = "Motivo do ban foi alterado! <:lori_heart:853052040425766923>"
                    }

                    LoriToolsUtils.logToSaddestOfTheSads(
                        helper,
                        context.user,
                        userId,
                        "Motivo do Ban Alterado",
                        null,
                        reason,
                        Color(214, 0, 255)
                    )
                }
                is UserIsNotBannedResult -> {
                    context.reply(true) {
                        content = "O usuário $userId (<@$userId>) não está banido, então não dá para alterar o motivo do ban dele!"
                    }
                }
            }
        }

        private sealed class BanRenameResult

        private object UserIsNotBannedResult : BanRenameResult()

        private object UserBanRenamedResult : BanRenameResult()
    }

    class LoriEconomyStateExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.ADMIN) {
        val DISABLED_ECONOMY_ID = UUID.fromString("3da6d95b-edb4-4ae9-aa56-4b13e91f3844")

        inner class Options : ApplicationCommandOptions() {
            val state = boolean("state", "Define se a economia está ativada ou desativada")
        }

        override val options = Options()

        override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
            val r = args[options.state]

            transaction(helper.databases.lorittaDatabase) {
                if (r) {
                    EconomyState.deleteWhere {
                        EconomyState.id eq DISABLED_ECONOMY_ID
                    }
                } else {
                    EconomyState.insertIgnore {
                        it[EconomyState.id] = DISABLED_ECONOMY_ID
                    }
                }
            }

            if (r) {
                context.reply(false) {
                    content = "Economia está ativada!"
                }
            } else {
                context.reply(false) {
                    content = "Economia está desativada..."
                }
            }
        }
    }

    class LoriStatusExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.ADMIN) {
        companion object {
            // Define the format of the input string
            private val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")
            private var zoneId = ZoneId.of("America/Sao_Paulo")
        }

        inner class Options : ApplicationCommandOptions() {
            val text = string("text", "Texto do novo status")
            val type = string("type", "Tipo do novo status") {
                Activity.ActivityType.values().forEach {
                    choice(it.name, it.name)
                }
            }
            val priority = long("priority", "Prioridade do status, de menor para maior (padrão: 0, 1 para substituir)")
            val startsAt = string("starts_at", "Quando o status ficará visível (horário GMT-3)")
            val endsAt = string("ends_at", "Quando o status deixará de ser visível (horário GMT-3)")
            val streamUrl = optionalString("stream_url", "URL da Stream, caso o tipo seja STREAMING")
        }

        override val options = Options()

        override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.deferChannelMessage(false)

            val text = args[options.text]
            val type = args[options.type]
            val priority = args[options.priority].toInt()
            val startsAt = args[options.startsAt]
            val endsAt = args[options.endsAt]
            val streamUrl = args[options.streamUrl]

            // Parse the string into a LocalDateTime object
            val startsAtLocalDateTime = try {
                LocalDateTime.parse(startsAt, formatter)
            } catch (e: DateTimeParseException) {
                context.reply(false) {
                    styled("Não foi possível parsear a data que você passou...")
                }
                return
            }

            // Convert LocalDateTime to Instant using UTC (or desired) time zone offset
            val startsAtInstant = startsAtLocalDateTime.toInstant(zoneId.rules.getOffset(startsAtLocalDateTime))

            // Parse the string into a LocalDateTime object
            val endsAtLocalDateTime = try {
                LocalDateTime.parse(endsAt, formatter)
            } catch (e: DateTimeParseException) {
                context.reply(false) {
                    styled("Não foi possível parsear a data que você passou...")
                }
                return
            }

            // Convert LocalDateTime to Instant using UTC (or desired) time zone offset
            val endsAtInstant = endsAtLocalDateTime.toInstant(zoneId.rules.getOffset(endsAtLocalDateTime))

            val response = context.loritta.makeLorittaRPCRequest<LorittaDashboardRPCResponse.UpdateLorittaActivityResponse>(
                LorittaDashboardRPCRequest.UpdateLorittaActivityRequest(
                    text,
                    type,
                    priority,
                    startsAtInstant.toKotlinInstant(),
                    endsAtInstant.toKotlinInstant(),
                    streamUrl
                )
            )

            when (response) {
                is LorittaDashboardRPCResponse.UpdateLorittaActivityResponse.Success -> {
                    context.reply(false) {
                        content = "Status inserido na lista de status da Loritta!"
                    }
                }
                is LorittaDashboardRPCResponse.UpdateLorittaActivityResponse.Unauthorized -> {
                    context.reply(false) {
                        content = "Não autorizado, tem certeza que o token da API está correto?"
                    }
                }
            }
        }
    }

    enum class PredefinedBanReason(val fancyName: String, val banReason: String, val duration: Duration) {
        ASKING_TO_BE_BANNED(
            "Pedir para ser banido",
            "Pedir para ser banido de usar a Loritta",
            Duration.ofDays(30)
        ),
        INSULT_LORITTA(
            "Ofender a Loritta em servidores da DreamLand",
            "Ofender/Xingar a Loritta em servidores da DreamLand",
            Duration.ofDays(60)
        ),
        DAILY_ALT_ACCOUNTS(
            "Usar múltiplas contas para farmar no daily",
            "Criar Alt Accounts (Contas Fakes/Contas Secundárias) para farmar sonhos no daily, será que os avisos no website não foram suficientes para você? ¯\\_(ツ)_/¯",
            Duration.ofDays(270)
        ),
        REAL_LIFE_TRADING_BUY(
            "Comercialização de sonhos (Compra)",
            "Comercialização de produtos com valor monetário real por sonhos (Compra)",
            Duration.ofDays(180)
        ),
        REAL_LIFE_TRADING_SELL(
            "Comercialização de sonhos (Venda)",
            "Comercialização de produtos com valor monetário real por sonhos (Venda)",
            Duration.ofDays(540)
        )
    }
}