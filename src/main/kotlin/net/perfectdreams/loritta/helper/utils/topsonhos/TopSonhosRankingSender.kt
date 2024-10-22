package net.perfectdreams.loritta.helper.utils.topsonhos

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.utils.FileUpload
import net.perfectdreams.loritta.cinnamon.pudding.tables.Profiles
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.sequins.text.StringUtils
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.io.File
import java.time.Instant
import java.time.ZoneId

class TopSonhosRankingSender(val m: LorittaHelper, val jda: JDA) {
    companion object {
        const val ACCOUNTS_TO_BE_RETRIEVED = 100_000
    }

    private val logger = KotlinLogging.logger {}

    fun start() = GlobalScope.launch {
        while (true) {
            val results = newSuspendedTransaction(db = m.databases.lorittaDatabase) {
                Profiles.selectAll().where { Profiles.money neq 0 }.orderBy(Profiles.money, SortOrder.DESC)
                    .limit(ACCOUNTS_TO_BE_RETRIEVED)
                    .toList()
            }

            val topSonhosChannel = jda.getTextChannelById(740395879567065129L)

            val dataFile = File("top_sonhos.json")
            var storedSonhos = listOf<StoredSonhos>()
            val newStoredSonhos = mutableListOf<StoredSonhos>()

            if (dataFile.exists()) {
                storedSonhos = Json.decodeFromString(dataFile.readText())
            }

            val biggestUserIdIndex = results.map { it[Profiles.id] }
                .maxOf { it.value.toString().length }

            val biggestSonhosLength = results.first()[Profiles.money].toString().length

            val builder = StringBuilder()

            for ((index, result) in results.withIndex()) {
                val userId = result[Profiles.id].value
                val sonhos = result[Profiles.money]

                val oldData = storedSonhos.firstOrNull { it.id == userId }

                val tag = if (oldData == null) {
                    "[NEW!]"
                } else {
                    val diff = sonhos - oldData.sonhos
                    val diffDisplay = when {
                        diff > 0 -> "+$diff"
                        0 > diff -> diff.toString()
                        else -> "-"
                    }

                    "[$diffDisplay]"
                }

                val indexDisplay = ((index + 1).toString() + ".")

                // + 7 is due to the " sonhos" text
                builder.append("${indexDisplay.padStart("$ACCOUNTS_TO_BE_RETRIEVED.".length, ' ')} ${userId.toString().padEnd(biggestUserIdIndex, ' ')} ${"$sonhos sonhos".padEnd(biggestSonhosLength + 7, ' ')} $tag")
                builder.append("\n")

                newStoredSonhos.add(
                    StoredSonhos(
                        userId,
                        sonhos
                    )
                )
            }

            // Used in the message itself
            val result = builder.toString()
            val linesToBeSent = StringUtils.chunkedLines(result, 1900, true)
                .firstOrNull()

            val givenAtTime = Instant.now()
                .atZone(ZoneId.systemDefault())

            val day = givenAtTime.dayOfMonth.toString().padStart(2, '0')
            val month = givenAtTime.monthValue.toString().padStart(2, '0')
            val year = givenAtTime.year

            val hour = givenAtTime.hour.toString().padStart(2, '0')
            val minute = givenAtTime.minute.toString().padStart(2, '0')

            topSonhosChannel?.sendMessage(
                "**Top Sonhos @ $day/$month/$year $hour:$minute**\n```\n$linesToBeSent\n```"
            )?.addFiles(
                FileUpload.fromData(
                    result.toByteArray(
                        Charsets.UTF_8
                    ).inputStream(),
                    "top-sonhos_$day$month${year}_$hour$minute.txt"
                )
            )?.queue()

            dataFile.writeText(
                Json.encodeToString(
                    ListSerializer(
                        StoredSonhos.serializer()
                    ),
                    newStoredSonhos
                )
            )

            delay(15 * 60 * 1_000) // every 15m
        }
    }

    @Serializable
    data class StoredSonhos(
        val id: Long,
        val sonhos: Long
    )
}