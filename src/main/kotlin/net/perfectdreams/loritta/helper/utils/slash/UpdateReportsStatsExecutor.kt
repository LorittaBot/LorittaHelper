package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.Snowflake
import dev.kord.rest.route.Position
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.tables.StaffProcessedReports
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class UpdateReportsStatsExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    companion object : SlashCommandExecutorDeclaration(UpdateReportsStatsExecutor::class) {
        object Options : ApplicationCommandOptions() {
            val min = integer("min", "Mensagem mais velha que será pega")
                .register()
        }

        override val options = Options
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessageEphemerally()

        var lastMessageId = Snowflake(args[options.min])

        while (true) {
            val messages = helper.helperRest.channel
                .getMessages(
                    context.channelId,
                    limit = 100,
                    position = Position.After(lastMessageId)
                )

            if (messages.isEmpty())
                break

            for (message in messages) {
                // TODO: We would need to get the reactions using the REST endpoint
                transaction(helper.databases.helperDatabase) {
                    StaffProcessedReports.insert {
                        it[StaffProcessedReports.timestamp] = Instant.parse(message.timestamp)
                        it[StaffProcessedReports.userId] = message.id.value.toLong()
                        it[StaffProcessedReports.messageId] = message.id.value.toLong()
                        it[StaffProcessedReports.messageId] = message.id.value.toLong()
                    }
                }
            }

            lastMessageId = messages.minOf { it.id }
        }

        context.sendEphemeralMessage {
            content = "Mensagens processadas!"
        }
    }
}
