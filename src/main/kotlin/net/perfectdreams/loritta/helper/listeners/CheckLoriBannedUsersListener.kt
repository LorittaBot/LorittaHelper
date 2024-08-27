package net.perfectdreams.loritta.helper.listeners

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.getBannedState

class CheckLoriBannedUsersListener(val m: LorittaHelper): ListenerAdapter() {
    private val lorittaGuilds = m.config.tasks.lorittaBannedRole.guilds


    override fun onMessageReceived(event: MessageReceivedEvent) {
        handleMemberIfBanned(event.message, event.guild, event.channel, event.message.author)
    }

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        super.onGuildMemberJoin(event)

        m.launch {
            val lorittaGuild = lorittaGuilds.find { it.id == event.guild.idLong }

            if (lorittaGuild != null) {
                val bannedRole = event.guild.getRoleById(lorittaGuild.bannedRoleId)
                val tempBannedRole = event.guild.getRoleById(lorittaGuild.tempBannedRoleId)

                if (bannedRole != null && tempBannedRole != null)
                    giveBannedRoleIfPossible(event.member, event.guild, bannedRole, tempBannedRole)
            }
        }
    }

    private fun handleMemberIfBanned(message: Message, guild: Guild, channel: MessageChannel, author: User) {
        val member = message.member ?: return

        m.launch {
            // Check if the member is banned from using Loritta
            for (lorittaGuild in lorittaGuilds) {
                if (guild.idLong == lorittaGuild.id) {
                    val allowedChannels = lorittaGuild.allowedChannels

                    if (allowedChannels != null && allowedChannels.contains(channel.idLong))
                        return@launch

                    val bannedRole = guild.getRoleById(lorittaGuild.bannedRoleId)
                    val tempBannedRole = guild.getRoleById(lorittaGuild.tempBannedRoleId)

                    if (message.member != null && bannedRole != null && tempBannedRole != null) {
                        if (giveBannedRoleIfPossible(member, guild, bannedRole, tempBannedRole)) {
                            message.delete().queue()
                        } else {
                            if (member.roles.contains(bannedRole))
                                guild.removeRoleFromMember(member, bannedRole).queue()

                            if (member.roles.contains(tempBannedRole))
                                guild.removeRoleFromMember(member, tempBannedRole).queue()
                        }
                    }

                    return@launch
                }
            }
        }
    }

    private fun giveBannedRoleIfPossible(member: Member, guild: Guild, permBanBannedRole: Role, tempBanBannedRole: Role): Boolean {
        val bannedState = member.user.getBannedState(m)

        if (bannedState != null) {
            if (bannedState[BannedUsers.expiresAt] != null) {
                if (member.roles.contains(permBanBannedRole))
                    guild.removeRoleFromMember(member, permBanBannedRole).queue()

                if (!member.roles.contains(tempBanBannedRole))
                    guild.addRoleToMember(member, tempBanBannedRole).queue()
            } else {
                if (member.roles.contains(tempBanBannedRole))
                    guild.removeRoleFromMember(member, tempBanBannedRole).queue()

                if (!member.roles.contains(permBanBannedRole))
                    guild.addRoleToMember(member, permBanBannedRole).queue()
            }
            return true
        }
        return false
    }
}