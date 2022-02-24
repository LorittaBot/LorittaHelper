package net.perfectdreams.loritta.helper.utils.cache

import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.request.ListThreadsByTimestampRequest
import dev.kord.rest.service.RestClient
import kotlinx.datetime.Instant

class TicketsCache(
    private val guildId: Snowflake,
    private val channelId: Snowflake,
    private val rest: RestClient
) {
    val tickets = mutableMapOf<Snowflake, DiscordThreadTicketData>()

    // We cache tickets so tickets can be created faster, because if we have too many archived tickets, when a user that never created a ticket before tries
    // opening a ticket, it takes a looong time (1+ minute) just to create it, which is kinda bad for UX
    suspend fun populateCache() {
        // Populate cache with the active threads
        rest.guild.listActiveThreads(guildId)
            .threads
            .forEach {
                val name = it.name.value ?: return@forEach
                if (!name.contains("(") && !name.contains(")"))
                    return@forEach

                val onlyTheId = name.substringAfterLast("(").substringBeforeLast(")")
                val idAsULong = onlyTheId.toULongOrNull() ?: return@forEach
                val userIdAsSnowflake = Snowflake(idAsULong)

                tickets[userIdAsSnowflake] = DiscordThreadTicketData(it.id)
            }

        // Populate cache with the inactive threads
        var searchedAll = false
        var lastInstant: Instant? = null

        while (!searchedAll) {
            val result = rest.channel.listPrivateArchivedThreads(
                channelId,
                ListThreadsByTimestampRequest(
                    before = lastInstant
                )
            )

            result
                .threads
                .forEach {
                    val name = it.name.value ?: return@forEach
                    if (!name.contains("(") && !name.contains(")"))
                        return@forEach

                    val onlyTheId = name.substringAfterLast("(").substringBeforeLast(")")
                    val idAsULong = onlyTheId.toULongOrNull() ?: return@forEach
                    val userIdAsSnowflake = Snowflake(idAsULong)

                    tickets[userIdAsSnowflake] = DiscordThreadTicketData(it.id)
                }

            searchedAll = result.threads.isEmpty()
            if (!searchedAll) {
                // Gets the minimum archive timestamp of the thread, because Discord seems to sort them by archival timestamp
                lastInstant = result.threads.mapNotNull {
                    it.threadMetadata.value?.archiveTimestamp?.let {
                        Instant.parse(it)
                    }
                }.minOrNull()
            }
        }
    }

    data class DiscordThreadTicketData(
        val id: Snowflake
    )
}