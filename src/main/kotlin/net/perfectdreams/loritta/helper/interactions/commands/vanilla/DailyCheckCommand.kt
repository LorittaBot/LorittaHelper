package net.perfectdreams.loritta.helper.interactions.commands.vanilla

import dev.minn.jda.ktx.coroutines.await
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.utils.FileUpload
import net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater.BrowserFingerprints
import net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater.Dailies
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.morenitta.interactions.commands.*
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class DailyCheckCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand("dailycheck", "Pega todos os dailies de vários usuários") {
        subcommand("users", "Pega todos os dailies de vários usuários") {
            executor = DailyCheckExecutor()
        }

        subcommand("ips", "Pega todos os dailies de vários IPs") {
            executor = DailyCheckByIpExecutor()
        }

        subcommand("loriclientid", "Pega todos os dailies de vários Loritta Client IDs") {
            executor = DailyCheckByLorittaClientIdExecutor()
        }
    }

    inner class DailyCheckExecutor : LorittaSlashCommandExecutor() {
        inner class Options : ApplicationCommandOptions() {
            init {
                // Register 25 different users
                repeat(25) {
                    optionalUser("user${it + 1}", "Usuário para ver os dailies")
                }
            }
        }

        override val options = Options()

        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.deferChannelMessage(true)

            // Because we did stuff in a... unconventional way, we will get all matched user arguments in a unconventional way: By getting all resolved objects!
            val users = context.event.options.map { it.asUser }

            if (users.isEmpty()) {
                context.reply(true) {
                    content = "Nenhum usuário encontrado!"
                }
                return
            }

            val emotes = mutableListOf<String>("⬛", "⬜", "🟧", "🟦", "🟥", "🟫", "🟪", "🟩", "🟨", "⚫", "⚪", "🔴", "🔵", "🟤", "🟣", "🟢", "🟡", "🟠", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎")
            val idToEmotes = mutableMapOf<Long, String>()
            val matchedSameClientIds = mutableMapOf<UUID, MutableSet<Long>>()

            val dailies = transaction(helper.databases.lorittaDatabase) {
                Dailies.leftJoin(BrowserFingerprints).select {
                    Dailies.receivedById inList users.map { it.idLong }
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

                val userData = users.find { it.idLong == daily[Dailies.receivedById] }

                builder.append("${userEmote} [${whenTheTransactionHappened.format(Constants.PRETTY_DATE_FORMAT)}] ${userData?.name} [${userData?.globalName}] (${daily[Dailies.receivedById]})")
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
                    builder.append("- Accept-Language: ${daily[BrowserFingerprints.contentLanguage]}")
                    builder.append("\n")
                    builder.append("- Screen Size: ${daily[BrowserFingerprints.width]}x${daily[BrowserFingerprints.height]}")
                    builder.append("\n")
                    builder.append("- Available Screen Size: ${daily[BrowserFingerprints.availWidth]}x${daily[BrowserFingerprints.availHeight]}")
                    builder.append("\n")
                    builder.append("- Timezone Offset: ${daily[BrowserFingerprints.timezoneOffset]}")
                    matchedSameClientIds.getOrPut(daily[BrowserFingerprints.clientId]) { mutableSetOf() }.add(daily[Dailies.receivedById])
                }
                builder.append("\n\n")
            }

            context.reply(true) {
                content = buildString {
                    val moreThanOneUsersMatches = matchedSameClientIds.filter { it.value.size > 1 }
                    if (moreThanOneUsersMatches.isNotEmpty()) {
                        appendLine("**Loritta Client IDs com múltiplos users:**")
                        for (matchedSameClientId in moreThanOneUsersMatches) {
                            appendLine("- **${matchedSameClientId.key}:** ${matchedSameClientId.value.joinToString()}")
                        }
                    }
                }

                files += FileUpload.fromData(builder.toString().toByteArray(Charsets.UTF_8).inputStream(), "dailies.txt")
            }
        }
    }

    inner class DailyCheckByIpExecutor : LorittaSlashCommandExecutor() {
        inner class Options : ApplicationCommandOptions() {
            init {
                // Register 25 different users
                repeat(25) {
                    optionalString("ip${it + 1}", "IP para ver os dailies")
                }
            }
        }

        override val options = Options()

        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.deferChannelMessage(true)

            // Because we did stuff in a... unconventional way, we will get all matched user arguments in a unconventional way: By getting all resolved objects!
            val ips = context.event.options.map { it.asString }

            if (ips.isEmpty()) {
                context.reply(true) {
                    content = "Nenhum usuário encontrado!"
                }
                return
            }

            val emotes = mutableListOf<String>("⬛", "⬜", "🟧", "🟦", "🟥", "🟫", "🟪", "🟩", "🟨", "⚫", "⚪", "🔴", "🔵", "🟤", "🟣", "🟢", "🟡", "🟠", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎")
            val idToEmotes = mutableMapOf<Long, String>()

            val dailies = transaction(helper.databases.lorittaDatabase) {
                Dailies.leftJoin(BrowserFingerprints).select {
                    Dailies.ip inList ips
                }.orderBy(Dailies.id, SortOrder.DESC)
                    .toList()
            }

            val builder = StringBuilder()

            val cachedUserData = mutableMapOf<Long, User>()

            val foundIds = mutableSetOf<Long>()
            val matchedSameClientIds = mutableMapOf<UUID, MutableSet<Long>>()

            for (daily in dailies) {
                val whenTheTransactionHappened = Instant.ofEpochMilli(daily[Dailies.receivedAt])
                    .atZone(Constants.TIME_ZONE_ID)

                val userEmote = idToEmotes.getOrPut(daily[Dailies.receivedById]) {
                    val emote = emotes.random()
                    emotes.remove(emote)
                    emote
                }

                val userId = daily[Dailies.receivedById]
                val userData = cachedUserData[userId] ?: try {
                    val user = context.event.jda.retrieveUserById(userId).await()
                    cachedUserData[userId] = user
                    user
                } catch (e: Exception) {
                    null
                }

                builder.append("${userEmote} [${whenTheTransactionHappened.format(Constants.PRETTY_DATE_FORMAT)}] ${userData?.name} [${userData?.globalName}] (${daily[Dailies.receivedById]})")
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
                    builder.append("- Accept-Language: ${daily[BrowserFingerprints.contentLanguage]}")
                    builder.append("\n")
                    builder.append("- Screen Size: ${daily[BrowserFingerprints.width]}x${daily[BrowserFingerprints.height]}")
                    builder.append("\n")
                    builder.append("- Available Screen Size: ${daily[BrowserFingerprints.availWidth]}x${daily[BrowserFingerprints.availHeight]}")
                    builder.append("\n")
                    builder.append("- Timezone Offset: ${daily[BrowserFingerprints.timezoneOffset]}")
                    matchedSameClientIds.getOrPut(daily[BrowserFingerprints.clientId]) { mutableSetOf() }.add(daily[Dailies.receivedById])
                }
                builder.append("\n\n")
                foundIds.add(userId)
            }

            context.reply(true) {
                content = buildString {
                    appendLine("**IDs encontrados:**")
                    for (userId in foundIds) {
                        appendLine("- $userId")
                    }
                    val moreThanOneUsersMatches = matchedSameClientIds.filter { it.value.size > 1 }
                    if (moreThanOneUsersMatches.isNotEmpty()) {
                        appendLine("**Loritta Client IDs com múltiplos users:**")
                        for (matchedSameClientId in moreThanOneUsersMatches) {
                            appendLine("- **${matchedSameClientId.key}:** ${matchedSameClientId.value.joinToString()}")
                        }
                    }
                }

                files += FileUpload.fromData(builder.toString().toByteArray(Charsets.UTF_8).inputStream(), "dailies.txt")
            }
        }
    }

    inner class DailyCheckByLorittaClientIdExecutor : LorittaSlashCommandExecutor() {
        inner class Options : ApplicationCommandOptions() {
            init {
                // Register 25 different users
                repeat(25) {
                    optionalString("cid${it + 1}", "Client ID para ver os dailies")
                }
            }
        }

        override val options = Options()

        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.deferChannelMessage(true)

            // Because we did stuff in a... unconventional way, we will get all matched user arguments in a unconventional way: By getting all resolved objects!
            val clientIds = context.event.options.map { it.asString }
                .map { UUID.fromString(it) }

            if (clientIds.isEmpty()) {
                context.reply(true) {
                    content = "Nenhum usuário encontrado!"
                }
                return
            }

            val emotes = mutableListOf<String>("⬛", "⬜", "🟧", "🟦", "🟥", "🟫", "🟪", "🟩", "🟨", "⚫", "⚪", "🔴", "🔵", "🟤", "🟣", "🟢", "🟡", "🟠", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎")
            val idToEmotes = mutableMapOf<Long, String>()

            val dailies = transaction(helper.databases.lorittaDatabase) {
                Dailies.leftJoin(BrowserFingerprints).select {
                    BrowserFingerprints.clientId inList clientIds
                }.orderBy(Dailies.id, SortOrder.DESC)
                    .toList()
            }

            val builder = StringBuilder()

            val cachedUserData = mutableMapOf<Long, User>()

            val foundIds = mutableSetOf<Long>()
            val matchedSameClientIds = mutableMapOf<UUID, MutableSet<Long>>()

            for (daily in dailies) {
                val whenTheTransactionHappened = Instant.ofEpochMilli(daily[Dailies.receivedAt])
                    .atZone(Constants.TIME_ZONE_ID)

                val userEmote = idToEmotes.getOrPut(daily[Dailies.receivedById]) {
                    val emote = emotes.random()
                    emotes.remove(emote)
                    emote
                }

                val userId = daily[Dailies.receivedById]
                val userData = cachedUserData[userId] ?: try {
                    val user = context.event.jda.retrieveUserById(userId).await()
                    cachedUserData[userId] = user
                    user
                } catch (e: Exception) {
                    null
                }

                builder.append("${userEmote} [${whenTheTransactionHappened.format(Constants.PRETTY_DATE_FORMAT)}] ${userData?.name} [${userData?.globalName}] (${daily[Dailies.receivedById]})")
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
                    builder.append("- Accept-Language: ${daily[BrowserFingerprints.contentLanguage]}")
                    builder.append("\n")
                    builder.append("- Screen Size: ${daily[BrowserFingerprints.width]}x${daily[BrowserFingerprints.height]}")
                    builder.append("\n")
                    builder.append("- Available Screen Size: ${daily[BrowserFingerprints.availWidth]}x${daily[BrowserFingerprints.availHeight]}")
                    builder.append("\n")
                    builder.append("- Timezone Offset: ${daily[BrowserFingerprints.timezoneOffset]}")
                    matchedSameClientIds.getOrPut(daily[BrowserFingerprints.clientId]) { mutableSetOf() }.add(daily[Dailies.receivedById])
                }
                builder.append("\n\n")
                foundIds.add(userId)
            }

            context.reply(true) {
                content = buildString {
                    appendLine("**IDs encontrados:**")
                    for (userId in foundIds) {
                        appendLine("- $userId")
                    }
                    val moreThanOneUsersMatches = matchedSameClientIds.filter { it.value.size > 1 }
                    if (moreThanOneUsersMatches.isNotEmpty()) {
                        appendLine("**Loritta Client IDs com múltiplos users:**")
                        for (matchedSameClientId in moreThanOneUsersMatches) {
                            appendLine("- **${matchedSameClientId.key}:** ${matchedSameClientId.value.joinToString()}")
                        }
                    }
                }

                files += FileUpload.fromData(builder.toString().toByteArray(Charsets.UTF_8).inputStream(), "dailies.txt")
            }
        }
    }
}