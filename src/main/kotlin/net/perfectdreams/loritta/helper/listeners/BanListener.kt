package net.perfectdreams.loritta.helper.listeners

import net.dv8tion.jda.api.events.guild.GuildBanEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.await

class BanListener(val m: LorittaHelper) : ListenerAdapter() {
    override fun onGuildBan(event: GuildBanEvent) {
        val jda = event.jda

        m.launch {
            val banInfo = try {
                event.guild.retrieveBan(event.user).await()
            } catch (e: ErrorResponseException) {
                // Ban does not exist
                null
            }

            val banForReason = "(Synchronized Ban / ${event.guild.name}) ${banInfo?.reason}"

            jda.guilds.forEach {
                val banInfoOnGuild = try {
                    event.guild.retrieveBan(event.user).await()
                } catch (e: ErrorResponseException) {
                    // Ban does not exist
                    null
                }

                // If the banInfoOnGuild is null, then it means that the user is *not* banned on the server!
                if (banInfoOnGuild == null) {
                    event.guild.ban(
                            event.user,
                            0,
                            banForReason
                    ).queue()
                }
            }
        }
    }
}