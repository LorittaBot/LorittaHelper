package net.perfectdreams.loritta.helper.utils.checkbannedusers

import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.isLorittaBanned

class LorittaBannedRoleTask(val m: LorittaHelper, val jda: JDA) : Runnable {
    companion object {
        val logger = KotlinLogging.logger {}
    }

    override fun run() {
        try {
            for (lorittaGuild in m.config.tasks.lorittaBannedRole.guilds) {
                val guild = jda.getGuildById(lorittaGuild.id) ?: continue
                val bannedRole = guild.getRoleById(lorittaGuild.bannedRoleId)
                val tempBanRole = guild.getRoleById(lorittaGuild.tempBannedRoleId)

                if (bannedRole != null && tempBanRole != null) {
                    checkBannedMembers(guild, bannedRole, tempBanRole)
                    checkGuildChannels(guild, bannedRole, lorittaGuild.allowedChannels)
                }
            }
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong while checking loritta-banned users!" }
        }
    }

    // Checks banned members and remove the banned role if they are not banned anymore
    private fun checkBannedMembers(guild: Guild, permBanRole: Role, tempBanRole: Role) {
        logger.info { "Checking members with loritta-banned role in ${guild.id} guild" }

        val members = guild.getMembersWithRoles(permBanRole)

        for (member in members) {
            val isBanned = member.user.isLorittaBanned(m)

            logger.info { "id: ${member.id}, isBanned: $isBanned" }

            if (!isBanned) {
                logger.info { "Removing banned role from ${member.id} because they not banned anymore!" }
                if (member.roles.contains(permBanRole))
                    guild.removeRoleFromMember(member, permBanRole).queue()

                if (member.roles.contains(tempBanRole))
                    guild.removeRoleFromMember(member, tempBanRole).queue()
            }
        }
    }

    // Checks guild channels and set override to deny view channel permission to loritta-banned role if possible
    private fun checkGuildChannels(guild: Guild, role: Role, allowedChannels: List<Long>?) {
        logger.info { "Checking guild channels in ${guild.id} guild!" }

        val channels = guild.channels
        val everyoneRole = guild.publicRole

        for (channel in channels) {
            if (allowedChannels != null && allowedChannels.contains(channel.idLong))
                continue

            val overrides = channel.permissionContainer.rolePermissionOverrides
            if (overrides.find { it.role!! == everyoneRole
                        && it.denied.contains(Permission.VIEW_CHANNEL) } == null) {
                channel.permissionContainer.upsertPermissionOverride(
                    role
                ).deny(Permission.VIEW_CHANNEL)
                    .queue()
            }
        }
    }
}