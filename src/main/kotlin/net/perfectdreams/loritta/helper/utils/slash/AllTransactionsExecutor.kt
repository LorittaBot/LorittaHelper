package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.tables.SonhosTransaction
import net.perfectdreams.loritta.helper.utils.Constants
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class AllTransactionsExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val user = user("user", "Usuário para ver as transações")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage()

        val user = args[options.user]

        val transactions = transaction(helper.databases.lorittaDatabase) {
            SonhosTransaction.select {
                (SonhosTransaction.receivedBy eq user.id.value.toLong()) or (SonhosTransaction.givenBy eq user.id.value.toLong())
            }.orderBy(SonhosTransaction.id, SortOrder.DESC)
                .toList()
        }

        val builder = StringBuilder()

        for (transaction in transactions) {
            val whenTheTransactionHappened = Instant.ofEpochMilli(transaction[SonhosTransaction.givenAt])
                .atZone(Constants.TIME_ZONE_ID)

            builder.append("[${whenTheTransactionHappened.format(Constants.PRETTY_DATE_FORMAT)}/${transaction[SonhosTransaction.reason]}] ${transaction[SonhosTransaction.givenBy]} -> ${transaction[SonhosTransaction.receivedBy]} (${transaction[SonhosTransaction.quantity]} sonhos)")
            builder.append("\n")
        }

        context.sendMessage {
            addFile("transactions.txt", builder.toString().toByteArray(Charsets.UTF_8).inputStream())
        }
    }
}