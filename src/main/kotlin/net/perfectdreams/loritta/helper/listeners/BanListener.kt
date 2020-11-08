package net.perfectdreams.loritta.helper.listeners

import mu.KotlinLogging
import net.dv8tion.jda.api.events.guild.GuildBanEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.await

class BanListener(val m: LorittaHelper) : ListenerAdapter() {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

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
                val banInfoOnGuild = try {
                    it.retrieveBan(event.user).await()
                } catch (e: ErrorResponseException) {
                    // Ban does not exist
                    null
                }

                // If the banInfoOnGuild is null, then it means that the user is *not* banned on the server!
                if (banInfoOnGuild == null) {
                    logger.info { "User ${event.user} is not banned yet in $it! Banning..." }
                    it.ban(
                            event.user,
                            0,
                            banForReason
                    ).queue()
                }
            }
        }
    }
}