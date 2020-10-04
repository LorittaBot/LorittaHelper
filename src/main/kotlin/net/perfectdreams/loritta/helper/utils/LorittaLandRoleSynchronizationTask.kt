package net.perfectdreams.loritta.helper.utils

import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.managers.RoleManager
import net.perfectdreams.loritta.helper.LorittaHelper

class LorittaLandRoleSynchronizationTask(val m: LorittaHelper, val jda: JDA) : Runnable {
    companion object {
        private val roleRemap = mutableMapOf(
                316363779518627842L to 420630427837923328L, // Deusas Supremas
                505144985591480333L to 762374506173431809L, // beep & boops
                351473717194522647L to 421325022951637015L, // Moderators
                399301696892829706L to 421325387889377291L, // Suporte -> Portuguese
                399301696892829706L to 761586798971322370L, // Suporte -> English
                653207389729849374L to 762377821884121148L, // Blue colors
                653207676075114496L to 762377837264240640L,
                653207713119076362L to 762377848807227432L,
                653207737798230037L to 762377857149829163L,
                653207795084165121L to 762377866368254053L,
                653207830261923840L to 762377874891603979L,
                653207858707562497L to 762377883339325460L
        )

        private val roleFieldComparators = listOf(
                RoleColorComparator(),
                RolePermissionsComparator(),
                RoleHoistedComparator(),
                RoleIsMentionaableComparator()
        )

        private val logger = KotlinLogging.logger {}
    }

    override fun run() {
        logger.info { "Synchronizing roles..." }
        try {
            val communityGuild = jda.getGuildById(297732013006389252L) ?: return
            val supportGuild = jda.getGuildById(420626099257475072L) ?: return
            val sparklyGuild = jda.getGuildById(320248230917046282L) ?: return

            logger.info { "Community Guild Members: ${communityGuild.members.size}" }
            logger.info { "Support Guild Members: ${supportGuild.members.size}" }
            logger.info { "Sparkly Guild Members: ${sparklyGuild.members.size}" }

            for ((communityRoleId, supportRoleId) in roleRemap) {
                val communityRole = communityGuild.getRoleById(communityRoleId) ?: continue
                val supportRole = supportGuild.getRoleById(supportRoleId) ?: continue

                val manager = supportRole.manager

                var changed = false

                for (comparator in roleFieldComparators) {
                    val communityValue = comparator.getValue(communityRole)
                    val supportValue = comparator.getValue(supportRole)

                    if (communityValue != supportValue) {
                        comparator.setValue(manager, communityValue)
                        changed = true
                    }
                }

                if (changed) {
                    logger.info { "Updating role $supportRole because the role information doesn't match $communityRole information!" }
                    manager.queue()
                }

                synchronizeRoles(communityGuild, supportGuild, communityRoleId, supportRoleId)
            }
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong while trying to synchronize roles!" }
        }
    }

    fun synchronizeRoles(fromGuild: Guild, toGuild: Guild, originalRoleId: Long, giveRoleId: Long) {
        val originalRole = fromGuild.getRoleById(originalRoleId) ?: return
        val giveRole = toGuild.getRoleById(giveRoleId) ?: return

        val membersWithOriginalRole = fromGuild.getMembersWithRoles(originalRole)
        val membersWithNewRole = toGuild.getMembersWithRoles(giveRole)

        for (member in membersWithNewRole) {
            if (!membersWithOriginalRole.any { it.user.id == member.user.id }) {
                logger.info { "Removing role ${giveRole.id} of ${member.effectiveName} (${member.user.id})..." }
                toGuild.removeRoleFromMember(member, giveRole).queue()
            }
        }

        for (member in membersWithOriginalRole) {
            if (!membersWithNewRole.any { it.user.id == member.user.id }) {
                val usMember = toGuild.getMember(member.user) ?: continue

                logger.info { "Adding role ${giveRole.id} to ${member.effectiveName} (${member.user.id})..." }
                toGuild.addRoleToMember(usMember, giveRole).queue()
            }
        }
    }

    abstract class RoleFieldComparator<ValueType: Any> {
        abstract fun getValue(role: Role): ValueType

        abstract fun setValue(manager: RoleManager, newValue: Any)
    }

    class RoleColorComparator : RoleFieldComparator<Int>() {
        override fun getValue(role: Role) = role.colorRaw
        override fun setValue(manager: RoleManager, newValue: Any) { manager.setColor(newValue as Int) }
    }

    class RolePermissionsComparator : RoleFieldComparator<Long>() {
        override fun getValue(role: Role) = role.permissionsRaw
        override fun setValue(manager: RoleManager, newValue: Any) { manager.setPermissions(newValue as Long) }
    }

    class RoleHoistedComparator : RoleFieldComparator<Boolean>() {
        override fun getValue(role: Role) = role.isHoisted
        override fun setValue(manager: RoleManager, newValue: Any) { manager.setHoisted(newValue as Boolean) }
    }

    class RoleIsMentionaableComparator : RoleFieldComparator<Boolean>() {
        override fun getValue(role: Role) = role.isMentionable
        override fun setValue(manager: RoleManager, newValue: Any) { manager.setMentionable(newValue as Boolean) }
    }
}