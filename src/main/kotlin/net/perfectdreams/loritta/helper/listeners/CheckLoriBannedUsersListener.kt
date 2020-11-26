package net.perfectdreams.loritta.helper.listeners

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.checkbannedusers.LorittaBannedRoleTask
import net.perfectdreams.loritta.helper.utils.extensions.isLorittaBanned

class CheckLoriBannedUsersListener(val m: LorittaHelper): ListenerAdapter() {
    private val lorittaGuilds = LorittaBannedRoleTask.lorittaGuilds

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)

        handleMemberIfBanned(event.message, event.guild, event.channel, event.message.author)
    }

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        super.onGuildMemberJoin(event)

        m.launch {
            val lorittaGuild = lorittaGuilds.find { it.guildId == event.guild.idLong }

            if (lorittaGuild != null) {
                val bannedRole = event.guild.getRoleById(lorittaGuild.lorittaBannedRole)

                if (bannedRole != null)
                    giveBannedRoleIfPossible(event.member, event.guild, bannedRole)
            }
        }
    }

    private fun handleMemberIfBanned(message: Message, guild: Guild, channel: GuildChannel, author: User) {
        m.launch {
            // Check if the member is banned from using Loritta
            for (lorittaGuild in lorittaGuilds) {
                if (guild.idLong == lorittaGuild.guildId) {
                    val allowedChannels = lorittaGuild.allowedChannels

                    if (allowedChannels != null && allowedChannels.contains(channel.idLong))
                        return@launch

                    val bannedRole = guild.getRoleById(lorittaGuild.lorittaBannedRole)

                    if (message.member != null && bannedRole != null) {
                        if (giveBannedRoleIfPossible(message.member!!, guild, bannedRole)) {
                            message.delete().queue()
                        } else {
                            guild.removeRoleFromMember(message.member!!, bannedRole).queue()
                        }
                    }

                    return@launch
                }
            }
        }
    }

    private fun giveBannedRoleIfPossible(member: Member, guild: Guild, bannedRole: Role): Boolean {
        if (member.user.isLorittaBanned(m)) {
            if (!member.roles.contains(bannedRole))
                guild.addRoleToMember(member, bannedRole).queue()
            return true
        }
        return false
    }
}