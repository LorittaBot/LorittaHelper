package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.tables.EconomyState
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class LoriEconomyStateExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    val DISABLED_ECONOMY_ID = UUID.fromString("3da6d95b-edb4-4ae9-aa56-4b13e91f3844")

    inner class Options : ApplicationCommandOptions() {
        val reason = boolean("state", "Define se a economia está ativada ou desativada")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val r = args[options.reason]

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
            context.sendMessage {
                content = "Economia está ativada!"
            }
        } else {
            context.sendMessage {
                content = "Economia está desativada..."
            }
        }
    }
}
