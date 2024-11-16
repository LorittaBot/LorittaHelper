package net.perfectdreams.loritta.helper.interactions.commands.vanilla

import dev.minn.jda.ktx.coroutines.await
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.utils.FileUpload
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater.BrowserFingerprints
import net.perfectdreams.loritta.cinnamon.pudding.tablesrefactorlater.Dailies
import net.perfectdreams.loritta.common.utils.text.TextUtils.shortenWithEllipsis
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.morenitta.interactions.commands.*
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class DailyCheckCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    companion object {
        private val EMOTES = listOf(
            "⬛",
            "⬜",
            "🟧",
            "🟦",
            "🟥",
            "🟫",
            "🟪",
            "🟩",
            "🟨",
            "⚫",
            "⚪",
            "🔴",
            "🔵",
            "🟤",
            "🟣",
            "🟢",
            "🟡",
            "🟠",
            "🧡",
            "💛",
            "💚",
            "💙",
            "💜",
            "🖤",
            "🤍",
            "🤎"
        )
    }
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
            val userIds = string("user_ids", "ID do usuário que você deseja ver os dailies (pode ser vários)")
        }

        override val options = Options()

        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.deferChannelMessage(true)

            val usersIds = args[options.userIds]
                .replace(", ", "")
                .split(" ")
                .mapNotNull { it.toLongOrNull() }
                .toSet()

            if (usersIds.isEmpty()) {
                context.reply(true) {
                    content = "Nenhum usuário encontrado!"
                }
                return
            }

            val emotes = EMOTES.toMutableList()
            val idToEmotes = mutableMapOf<Long, String>()
            val matchedSameClientIds = mutableMapOf<UUID, MutableSet<Long>>()

            val dailies = transaction(helper.databases.lorittaDatabase) {
                Dailies.leftJoin(BrowserFingerprints).selectAll().where { Dailies.receivedById inList usersIds }
                    .orderBy(Dailies.id, SortOrder.DESC)
                    .toList()
            }

            val builder = StringBuilder()
            val cachedUserData = mutableMapOf<Long, User>()

            for (daily in dailies) {
                val whenTheTransactionHappened = Instant.ofEpochMilli(daily[Dailies.receivedAt])
                    .atZone(Constants.TIME_ZONE_ID)

                val userEmote = idToEmotes.getOrPut(daily[Dailies.receivedById]) {
                    if (emotes.isEmpty())
                        return@getOrPut " " // Not enough emotes, bail out

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
            }

            context.reply(true) {
                val messageContent = buildString {
                    if (matchedSameClientIds.isNotEmpty()) {
                        appendLine("**Loritta Client IDs:**")
                        for (matchedSameClientId in matchedSameClientIds) {
                            appendLine("- **${matchedSameClientId.key}:** ${matchedSameClientId.value.joinToString()}")
                        }
                    }
                }

                if (messageContent.length > 2_000) {
                    this.content = "*mensagem grande demais*"
                    files += FileUpload.fromData(messageContent.toByteArray(Charsets.UTF_8).inputStream(), "description.txt")
                } else {
                    this.content = messageContent
                }

                files += FileUpload.fromData(builder.toString().toByteArray(Charsets.UTF_8).inputStream(), "dailies.txt")
            }
        }
    }

    inner class DailyCheckByIpExecutor : LorittaSlashCommandExecutor() {
        inner class Options : ApplicationCommandOptions() {
            val ips = string("ips", "IP para ver os dailies")
        }

        override val options = Options()

        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.deferChannelMessage(true)

            val ips = args[options.ips]
                .replace(", ", "")
                .split(" ")

            if (ips.isEmpty()) {
                context.reply(true) {
                    content = "Nenhum usuário encontrado!"
                }
                return
            }

            val emotes = EMOTES.toMutableList()
            val idToEmotes = mutableMapOf<Long, String>()

            val dailies = transaction(helper.databases.lorittaDatabase) {
                Dailies.leftJoin(BrowserFingerprints).selectAll().where { Dailies.ip inList ips }
                    .orderBy(Dailies.id, SortOrder.DESC)
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
                    if (emotes.isEmpty())
                        return@getOrPut " " // Not enough emotes, bail out

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

            val banStates = transaction(helper.databases.lorittaDatabase) {
                BannedUsers.selectAll()
                    .where { BannedUsers.userId inList foundIds and (BannedUsers.valid eq true) and (BannedUsers.expiresAt.isNull() or (BannedUsers.expiresAt.isNotNull() and (BannedUsers.expiresAt greaterEq System.currentTimeMillis()))) }
                    .toList()
            }

            context.reply(true) {
                val messageContent = buildString {
                    appendLine("**IDs encontrados:**")
                    for (userId in foundIds) {
                        append("- ")
                        append(userId)
                        if (banStates.any { userId == it[BannedUsers.userId] }) {
                            append(" [BANIDO]")
                        }
                        appendLine()
                    }
                    if (matchedSameClientIds.isNotEmpty()) {
                        appendLine("**Loritta Client IDs:**")
                        for (matchedSameClientId in matchedSameClientIds) {
                            appendLine("- **${matchedSameClientId.key}:** ${matchedSameClientId.value.joinToString()}")
                        }
                    }
                }

                if (messageContent.length > 2_000) {
                    this.content = "*mensagem grande demais*"
                    files += FileUpload.fromData(messageContent.toByteArray(Charsets.UTF_8).inputStream(), "description.txt")
                } else {
                    this.content = messageContent
                }

                files += FileUpload.fromData(builder.toString().toByteArray(Charsets.UTF_8).inputStream(), "dailies.txt")
            }
        }
    }

    inner class DailyCheckByLorittaClientIdExecutor : LorittaSlashCommandExecutor() {
        inner class Options : ApplicationCommandOptions() {
            val clientIds = string("cids", "Client IDs para ver os dailies")
        }

        override val options = Options()

        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.deferChannelMessage(true)

            // Because we did stuff in a... unconventional way, we will get all matched user arguments in a unconventional way: By getting all resolved objects!
            val clientIds = args[options.clientIds]
                .replace(", ", "")
                .split(" ")
                .map { UUID.fromString(it) }

            if (clientIds.isEmpty()) {
                context.reply(true) {
                    content = "Nenhum usuário encontrado!"
                }
                return
            }

            val emotes = EMOTES.toMutableList()
            val idToEmotes = mutableMapOf<Long, String>()

            val dailies = transaction(helper.databases.lorittaDatabase) {
                Dailies.leftJoin(BrowserFingerprints).selectAll()
                    .where { BrowserFingerprints.clientId inList clientIds }.orderBy(Dailies.id, SortOrder.DESC)
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
                    if (emotes.isEmpty())
                        return@getOrPut " " // Not enough emotes, bail out

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

            val banStates = transaction(helper.databases.lorittaDatabase) {
                BannedUsers.selectAll()
                    .where { BannedUsers.userId inList foundIds and (BannedUsers.valid eq true) and (BannedUsers.expiresAt.isNull() or (BannedUsers.expiresAt.isNotNull() and (BannedUsers.expiresAt greaterEq System.currentTimeMillis()))) }
                    .toList()
            }

            context.reply(true) {
                val messageContent = buildString {
                    appendLine("**IDs encontrados:**")
                    for (userId in foundIds) {
                        append("- ")
                        append(userId)
                        if (banStates.any { userId == it[BannedUsers.userId] }) {
                            append(" [BANIDO]")
                        }
                        appendLine()
                    }
                    if (matchedSameClientIds.isNotEmpty()) {
                        appendLine("**Loritta Client IDs:**")
                        for (matchedSameClientId in matchedSameClientIds) {
                            appendLine("- **${matchedSameClientId.key}:** ${matchedSameClientId.value.joinToString()}")
                        }
                    }
                }

                if (messageContent.length > 2_000) {
                    this.content = "*mensagem grande demais*"
                    files += FileUpload.fromData(messageContent.toByteArray(Charsets.UTF_8).inputStream(), "description.txt")
                } else {
                    this.content = messageContent
                }

                files += FileUpload.fromData(builder.toString().toByteArray(Charsets.UTF_8).inputStream(), "dailies.txt")
            }
        }
    }
}