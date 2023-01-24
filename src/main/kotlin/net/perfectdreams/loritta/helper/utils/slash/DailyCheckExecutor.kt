package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater.BrowserFingerprints
import net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater.Dailies
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.Constants
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class DailyCheckExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    inner class Options : ApplicationCommandOptions() {
        init {
            // Register 25 different users
            repeat(25) {
                optionalUser("user${it + 1}", "Usuário para ver as transações")
            }
        }
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage()

        // Because we did stuff in a... unconventional way, we will get all matched user arguments in a unconventional way: By getting all resolved objects!
        val users = context.interactionData.resolved?.users?.values ?: run {
            context.sendMessage {
                content = "Nenhum usuário encontrado!"
            }
            return
        }

        val emotes = mutableListOf<String>("⬛", "⬜", "🟧", "🟦", "🟥", "🟫", "🟪", "🟩", "🟨", "⚫", "⚪", "🔴", "🔵", "🟤", "🟣", "🟢", "🟡", "🟠", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎")
        val idToEmotes = mutableMapOf<Long, String>()

        val dailies = transaction(helper.databases.lorittaDatabase) {
            Dailies.innerJoin(BrowserFingerprints).select {
                Dailies.receivedById inList users.map { it.id.value.toLong() }
            }.orderBy(Dailies.id, SortOrder.DESC)
                .toList()
        }

        val builder = StringBuilder()

        for (daily in dailies) {
            val whenTheTransactionHappened = Instant.ofEpochMilli(daily[Dailies.receivedAt])
                .atZone(Constants.TIME_ZONE_ID)

            val userEmote = idToEmotes.getOrPut(daily[Dailies.receivedById]) {
                val emote = emotes.random()
                emotes.remove(emote)
                emote
            }

            val userData = users.find { it.id.value.toLong() == daily[Dailies.receivedById] }

            builder.append("${userEmote} [${whenTheTransactionHappened.format(Constants.PRETTY_DATE_FORMAT)}] ${userData?.tag} (${daily[Dailies.receivedById]})")
            builder.append("\n")
            builder.append("- Email: ${daily[Dailies.email]}")
            builder.append("\n")
            builder.append("- IP: ${daily[Dailies.ip]}")
            builder.append("\n")
            builder.append("- User-Agent: ${daily[Dailies.userAgent]}")
            val fingerprintData = daily[Dailies.browserFingerprints]
            if (fingerprintData != null) {
                builder.append("\n")
                builder.append("- Client ID: ${daily[BrowserFingerprints.clientId]}")
                builder.append("\n")
                builder.append("- Accept: ${daily[BrowserFingerprints.accept]}")
                builder.append("\n")
                builder.append("- Content-Language: ${daily[BrowserFingerprints.contentLanguage]}")
                builder.append("\n")
                builder.append("- Screen Size: ${daily[BrowserFingerprints.width]}x${daily[BrowserFingerprints.height]}")
                builder.append("\n")
                builder.append("- Available Screen Size: ${daily[BrowserFingerprints.availWidth]}x${daily[BrowserFingerprints.availHeight]}")
                builder.append("\n")
                builder.append("- Timezone Offset: ${daily[BrowserFingerprints.availWidth]}x${daily[BrowserFingerprints.timezoneOffset]}")
            }
            builder.append("\n\n")
        }

        context.sendMessage {
            addFile("dailies.txt", builder.toString().toByteArray(Charsets.UTF_8).inputStream())
        }
    }
}
