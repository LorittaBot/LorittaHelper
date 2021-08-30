package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.Dailies
import net.perfectdreams.loritta.helper.tables.SonhosTransaction
import net.perfectdreams.loritta.helper.utils.Constants
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class AllTransactionsExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(AllTransactionsExecutor::class) {
        override val options = Options

        object Options : CommandOptions() {
            init {
                repeat(25) {
                    optionalUser("user", "Usuário para ver as transações")
                        .register()
                }
            }
        }
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage()

        val users = args.types
            .filter { it is User }
            .mapNotNull { it.value }
            .filterIsInstance<User>()

        val dailies = transaction(helper.databases.lorittaDatabase) {
            Dailies.select {
                Dailies.receivedById inList users.map { it.id.value }
            }.orderBy(SonhosTransaction.id, SortOrder.DESC)
                .toList()
        }

        val builder = StringBuilder()

        for (daily in dailies) {
            val whenTheTransactionHappened = Instant.ofEpochMilli(daily[Dailies.receivedAt])
                .atZone(Constants.TIME_ZONE_ID)

            builder.append("[${whenTheTransactionHappened.format(Constants.PRETTY_DATE_FORMAT)}] ${daily[Dailies.receivedById]}: ${daily[SonhosTransaction.quantity]} sonhos")
            builder.append("- Email: ${daily[Dailies.email]}")
            builder.append("\n")
            builder.append("- IP: ${daily[Dailies.ip]}")
            builder.append("\n")
            builder.append("- User-Agent: ${daily[Dailies.userAgent]}")
            builder.append("\n")
        }

        context.sendMessage {
            file("transactions.txt", builder.toString().toByteArray(Charsets.UTF_8).inputStream())
        }
    }
}