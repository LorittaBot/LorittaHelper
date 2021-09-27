package net.perfectdreams.loritta.helper.utils.cache

import com.github.benmanes.caffeine.cache.Caffeine
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.Snowflake
import dev.kord.rest.service.RestClient
import java.util.concurrent.TimeUnit

class ChannelsCache(private val rest: RestClient) {
    val channels = Caffeine.newBuilder()
        .expireAfterWrite(1L, TimeUnit.HOURS)
        .build<Snowflake, DiscordChannel>()
        .asMap()

    suspend fun getChannel(id: Snowflake) = channels.getOrPut(id) { rest.channel.getChannel(id) }
}