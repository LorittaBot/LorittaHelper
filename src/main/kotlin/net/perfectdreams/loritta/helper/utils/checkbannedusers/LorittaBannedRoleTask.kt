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
        val lorittaGuilds = listOf(
                // Support server
                LorittaGuild(
                        420626099257475072L,
                        781591507849052200L,
                        785226414474395670L,
                        listOf(781583967837093928, 781583967837093928)
                )
        )

        val logger = KotlinLogging.logger {}
    }

    override fun run() {
        try {
            for (lorittaGuild in lorittaGuilds) {
                val guild = jda.getGuildById(lorittaGuild.guildId) ?: continue
                val bannedRole = guild.getRoleById(lorittaGuild.lorittaBannedRole)

                if (bannedRole != null) {
                    checkBannedMembers(guild, bannedRole)
                    checkGuildChannels(guild, bannedRole, lorittaGuild.allowedChannels)
                }
            }
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong while checking loritta-banned users!" }
        }
    }

    // Checks banned members and remove the banned role if they not banned anymore
    private fun checkBannedMembers(guild: Guild, role: Role) {
        logger.info { "Checking members with loritta-banned role in ${guild.id} guild" }

        val members = guild.getMembersWithRoles(role)

        for (member in members) {
            val isBanned = member.user.isLorittaBanned(m)

            logger.info { "id: ${member.id}, isBanned: $isBanned" }

            if (!isBanned) {
                logger.info { "Removing banned role from ${member.id} because they not banned anymore!" }
                guild.removeRoleFromMember(member, role).queue()
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

            val overrides = channel.rolePermissionOverrides
            if (overrides.find { it.role!! == everyoneRole
                            && it.denied.contains(Permission.VIEW_CHANNEL) } == null) {
                channel.manager.putPermissionOverride(
                        role,
                        null,
                        listOf(Permission.VIEW_CHANNEL)
                ).queue()
            }
        }
    }
}

class LorittaGuild(
        val guildId: Long,
        val lorittaBannedRole: Long,
        val lorittaTempBannedRole: Long,
        val allowedChannels: List<Long>? = null
)