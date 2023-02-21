package net.perfectdreams.loritta.helper.listeners

import com.github.benmanes.caffeine.cache.Caffeine
import mu.KotlinLogging
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.guild.GuildBanEvent
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.await
import java.util.concurrent.TimeUnit

class BanListener(val m: LorittaHelper) : ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private val unbanCache = Caffeine.newBuilder()
            .expireAfterWrite(15L, TimeUnit.SECONDS)
            .build<Long, Long>()

    override fun onGuildBan(event: GuildBanEvent) {
        val jda = event.jda

        m.launch {
            logger.info { "User ${event.user} was banned in ${event.guild}, relaying ban!" }

            val banInfo = try {
                event.guild.retrieveBan(event.user).await()
            } catch (e: ErrorResponseException) {
                // Ban does not exist
                null
            }

            if (banInfo?.reason?.startsWith("(Relayed Ban / ") == true) {
                logger.info { "User ${event.user} was banned in ${event.guild} but it looks like it was a relayed ban, so we are going to just ignore the event..." }
                return@launch
            }

            // This is from Loritta, this should be removed later when the feature is removed from Loritta.
            if (banInfo?.reason == "Banned on LorittaLand (Brazilian Server)" || banInfo?.reason == "Banido na LorittaLand (English Server)")
                return@launch

            val banForReason = "(Relayed Ban / ${event.guild.name}) ${banInfo?.reason}"
            logger.info { "Will relay ${event.user}'s ban with the reason $banForReason" }

            jda.guilds.forEach {
                logger.info { "Checking if ${event.user} is banned in $it..." }
                if (!it.selfMember.hasPermission(Permission.BAN_MEMBERS))
                    logger.warn { "I don't have permission to ban members in $it!" }
                else {
                    val banInfoOnGuild = try {
                        it.retrieveBan(event.user).await()
                    } catch (e: ErrorResponseException) {
                        // Ban does not exist
                        null
                    }

                    // If the banInfoOnGuild is null, then it means that the user is *not* banned on the server!
                    if (banInfoOnGuild == null) {
                        logger.info { "User ${event.user} is not banned yet in $it! Banning..." }
                        it.ban(event.user, 0, TimeUnit.SECONDS).reason(banForReason).queue()
                    }
                }
            }
        }
    }

    override fun onGuildUnban(event: GuildUnbanEvent) {
        val jda = event.jda

        m.launch {
            logger.info { "User ${event.user} was unbanned in ${event.guild}, relaying unban!" }

            if (unbanCache.getIfPresent(event.user.idLong) != null) {
                logger.info { "User ${event.user} is on the unban cache! Not relaying unban because this would cause a infinite loop..." }
                return@launch
            }

            unbanCache.put(event.user.idLong, System.currentTimeMillis())

            jda.guilds.forEach {
                logger.info { "Checking if ${event.user} is banned in $it..." }
                if (!it.selfMember.hasPermission(Permission.BAN_MEMBERS))
                    logger.warn { "I don't have permission to ban members in $it!" }
                else {
                    val banInfoOnGuild = try {
                        it.retrieveBan(event.user).await()
                    } catch (e: ErrorResponseException) {
                        // Ban does not exist
                        null
                    }

                    // If the banInfoOnGuild is null, then it means that the user is *not* banned on the server!
                    if (banInfoOnGuild != null) {
                        logger.info { "User ${event.user} is banned in $it! Unbanning..." }
                        it.unban(
                                event.user
                        ).queue()
                    }
                }
            }
        }
    }
}