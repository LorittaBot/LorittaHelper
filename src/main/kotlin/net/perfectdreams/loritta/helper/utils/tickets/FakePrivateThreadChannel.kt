package net.perfectdreams.loritta.helper.utils.tickets

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.internal.entities.GuildImpl
import net.dv8tion.jda.internal.entities.channel.concrete.ThreadChannelImpl

/**
 * A fake private thread channel used to avoid querying the archived threads just to reopen them
 */
class FakePrivateThreadChannel(
    id: Long,
    guild: Guild
) : ThreadChannelImpl(id, guild as GuildImpl, ChannelType.GUILD_PRIVATE_THREAD)