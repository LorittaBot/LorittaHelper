package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.Dailies
import net.perfectdreams.loritta.helper.utils.Constants
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class DailyCheckExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(DailyCheckExecutor::class) {
        override val options = Options

        object Options : CommandOptions() {
            init {
                // Register 25 different users
                repeat(25) {
                    optionalUser("user${it + 1}", "Usuário para ver as transações")
                        .register()
                }
            }
        }
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage()

        // Because we did stuff in a... unconventional way, we will get all matched user arguments in a unconventional way: By getting all resolved objects!
        val users = context.data.resolved?.users?.values ?: run {
            context.sendMessage {
                content = "Nenhum usuário encontrado!"
            }
            return
        }

        val dailies = transaction(helper.databases.lorittaDatabase) {
            Dailies.select {
                Dailies.receivedById inList users.map { it.id.value }
            }.orderBy(Dailies.id, SortOrder.DESC)
                .toList()
        }

        val builder = StringBuilder()

        for (daily in dailies) {
            val whenTheTransactionHappened = Instant.ofEpochMilli(daily[Dailies.receivedAt])
                .atZone(Constants.TIME_ZONE_ID)

            builder.append("[${whenTheTransactionHappened.format(Constants.PRETTY_DATE_FORMAT)}] ${daily[Dailies.receivedById]}")
            builder.append("\n")
            builder.append("- Email: ${daily[Dailies.email]}")
            builder.append("\n")
            builder.append("- IP: ${daily[Dailies.ip]}")
            builder.append("\n")
            builder.append("- User-Agent: ${daily[Dailies.userAgent]}")
            builder.append("\n")
        }

        context.sendMessage {
            file("dailies.txt", builder.toString().toByteArray(Charsets.UTF_8).inputStream())
        }
    }
}