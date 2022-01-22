package net.perfectdreams.loritta.helper.listeners

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.GuildChannel
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.tables.BannedUsers
import net.perfectdreams.loritta.helper.utils.checkbannedusers.LorittaBannedRoleTask
import net.perfectdreams.loritta.helper.utils.extensions.getBannedState

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
                val tempBannedRole = event.guild.getRoleById(lorittaGuild.lorittaTempBannedRole)

                if (bannedRole != null && tempBannedRole != null)
                    giveBannedRoleIfPossible(event.member, event.guild, bannedRole, tempBannedRole)
            }
        }
    }

    private fun handleMemberIfBanned(message: Message, guild: Guild, channel: GuildChannel, author: User) {
        val member = message.member ?: return

        m.launch {
            // Check if the member is banned from using Loritta
            for (lorittaGuild in lorittaGuilds) {
                if (guild.idLong == lorittaGuild.guildId) {
                    val allowedChannels = lorittaGuild.allowedChannels

                    if (allowedChannels != null && allowedChannels.contains(channel.idLong))
                        return@launch

                    val bannedRole = guild.getRoleById(lorittaGuild.lorittaBannedRole)
                    val tempBannedRole = guild.getRoleById(lorittaGuild.lorittaTempBannedRole)

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