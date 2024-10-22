package net.perfectdreams.loritta.helper.utils.slash

import net.dv8tion.jda.api.utils.FileUpload
import net.perfectdreams.loritta.morenitta.interactions.commands.ApplicationCommandContext
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.HelperExecutor
import net.perfectdreams.loritta.helper.tables.SonhosTransaction
import net.perfectdreams.loritta.helper.utils.Constants
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class AllTransactionsExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val user = user("user", "Usuário para ver as transações")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage(false)

        val user = args[options.user].user

        val transactions = transaction(helper.databases.lorittaDatabase) {
            SonhosTransaction.selectAll()
                .where { (SonhosTransaction.receivedBy eq user.idLong) or (SonhosTransaction.givenBy eq user.idLong) }
                .orderBy(SonhosTransaction.id, SortOrder.DESC)
                .toList()
        }

        val builder = StringBuilder()

        for (transaction in transactions) {
            val whenTheTransactionHappened = Instant.ofEpochMilli(transaction[SonhosTransaction.givenAt])
                .atZone(Constants.TIME_ZONE_ID)

            builder.append("[${whenTheTransactionHappened.format(Constants.PRETTY_DATE_FORMAT)}/${transaction[SonhosTransaction.reason]}] ${transaction[SonhosTransaction.givenBy]} -> ${transaction[SonhosTransaction.receivedBy]} (${transaction[SonhosTransaction.quantity]} sonhos)")
            builder.append("\n")
        }

        context.reply(false) {
            files += FileUpload.fromData(builder.toString().toByteArray(Charsets.UTF_8).inputStream(), "transactions.txt")
        }
    }
}