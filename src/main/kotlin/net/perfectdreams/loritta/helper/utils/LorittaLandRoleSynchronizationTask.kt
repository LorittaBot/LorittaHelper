package net.perfectdreams.loritta.helper.utils

import com.github.benmanes.caffeine.cache.Caffeine
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.managers.RoleManager
import net.dv8tion.jda.api.requests.ErrorResponse
import net.perfectdreams.galleryofdreams.common.data.DiscordSocialConnection
import net.perfectdreams.galleryofdreams.common.data.api.GalleryOfDreamsDataResponse
import net.perfectdreams.loritta.cinnamon.pudding.tables.Payments
import net.perfectdreams.loritta.cinnamon.pudding.utils.PaymentReason
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.dao.Payment
import net.perfectdreams.loritta.helper.utils.buttonroles.LorittaCommunityRoleButtons
import net.perfectdreams.loritta.helper.utils.extensions.await
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

class LorittaLandRoleSynchronizationTask(val m: LorittaHelper, val jda: JDA) : Runnable {
    companion object {
        private val roleFieldComparators = listOf(
            RoleColorComparator(),
            RolePermissionsComparator(),
            RoleHoistedComparator(),
            RoleIsMentionableComparator()
        )

        private val logger = KotlinLogging.logger {}
    }

    private val rolesRemap = m.config.tasks.roleSynchronization.rolesRemap.map {
        it.key.toLong() to it.value
    }.toList()

    private val community = m.config.guilds.community
    private val sparklyPower = m.config.guilds.sparklyPower
    private val english = m.config.guilds.english

    private val userNotInCommunityServerCache = Collections.newSetFromMap(
        Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build<Long, Boolean>()
            .asMap()
    )

    override fun run() {
        logger.info { "Synchronizing roles..." }

        try {
            val communityGuild = jda.getGuildById(community.id)
            if (communityGuild != null)
                logger.info { "Community Guild Members: ${communityGuild.members.size}" }
            else
                logger.warn { "Community Guild is missing..." }

            val supportGuild = jda.getGuildById(english.id)
            if (supportGuild != null)
                logger.info { "Support Guild Members: ${supportGuild.members.size}" }
            else
                logger.warn { "Support Guild is missing..." }

            val sparklyGuild = jda.getGuildById(sparklyPower.id)
            if (sparklyGuild != null)
                logger.info { "Sparkly Guild Members: ${sparklyGuild.members.size}" }
            else
                logger.warn { "Sparkly Guild is missing..." }

            if (communityGuild != null) {
                // ===[ DONATORS ]===
                updateMembersDonationRoles(communityGuild)

                // ===[ FAN ARTISTS ]===
                updateFanArtistsRoles(communityGuild)
            }

            if (communityGuild != null && supportGuild != null) {
                logger.info { "Synchronizing roles between Community Guild and Support Guild..." }
                for ((communityRoleId, supportRoleId) in rolesRemap) {
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
            }
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong while trying to synchronize roles!" }
        }
    }

    private fun updateMembersDonationRoles(communityGuild: Guild) {
        // Apply donators roles
        logger.info { "Applying donator roles in the community server..." }

        val payments = transaction(m.databases.lorittaDatabase) {
            Payment.find {
                (Payments.reason eq PaymentReason.DONATION) and (Payments.paidAt.isNotNull())
            }.toMutableList()
        }

        val donatorsPlusQuantity = mutableMapOf<Long, Double>()
        val donatorsPlusFirstDate = mutableMapOf<Long, Long>()
        val inactiveDonators = mutableSetOf<Long>()

        val donatorRole = communityGuild.getRoleById(community.roles.donator)
        val superDonatorRole = communityGuild.getRoleById(community.roles.superDonator)
        val megaDonatorRole = communityGuild.getRoleById(community.roles.megaDonator)
        val advertisementRole = communityGuild.getRoleById(community.roles.advertisement)

        for (payment in payments) {
            if ((payment.expiresAt ?: 0) >= System.currentTimeMillis()) {
                donatorsPlusQuantity[payment.userId] = payment.money.toDouble() + donatorsPlusQuantity.getOrDefault(payment.userId, 0.0)
                if (!donatorsPlusFirstDate.containsKey(payment.userId)) {
                    donatorsPlusFirstDate[payment.userId] = payment.paidAt ?: 0L
                }
            } else {
                inactiveDonators.add(payment.userId)
            }
        }

        for (member in communityGuild.members) {
            val roles = member.roles.toMutableSet()

            if (donatorsPlusQuantity.containsKey(member.user.idLong)) {
                // Loritta also ceil the result
                val donated = ceil(donatorsPlusQuantity[member.user.idLong] ?: 0.0)

                if (!roles.contains(donatorRole))
                    roles.add(donatorRole)

                if (donated >= 99.99) {
                    if (!roles.contains(megaDonatorRole))
                        roles.add(megaDonatorRole)
                } else {
                    if (roles.contains(megaDonatorRole))
                        roles.remove(megaDonatorRole)
                }

                if (donated >= 39.99) {
                    if (!roles.contains(superDonatorRole))
                        roles.add(superDonatorRole)
                    if (!roles.contains(advertisementRole))
                        roles.add(advertisementRole)
                } else {
                    if (roles.contains(superDonatorRole))
                        roles.remove(superDonatorRole)
                    if (roles.contains(advertisementRole))
                        roles.remove(advertisementRole)
                }
            } else {
                // Remove custom colors
                val filter = roles.filter { userRole -> LorittaCommunityRoleButtons.colors.any { it.roleId == userRole.idLong } }
                roles.removeAll(filter)

                // Remove custom badges if the user is not Level 10
                val coolBadgesFilter = roles.filter { userRole -> LorittaCommunityRoleButtons.coolBadges.any { it.roleId == userRole.idLong } }
                if (!member.roles.any { it.idLong == community.roles.level10 })
                    roles.removeAll(coolBadgesFilter)

                if (roles.contains(advertisementRole))
                    roles.remove(advertisementRole)

                if (roles.contains(donatorRole))
                    roles.remove(donatorRole)

                if (roles.contains(superDonatorRole))
                    roles.remove(superDonatorRole)

                if (roles.contains(megaDonatorRole))
                    roles.remove(megaDonatorRole)
            }

            if (!(roles.containsAll(member.roles) && member.roles.containsAll(roles))) {// Novos cargos foram adicionados
                logger.info { "Changing roles of $member, current roles are ${member.roles}, new roles will be $roles" }
                member.guild.modifyMemberRoles(member, roles).queue()
            }
        }

        logger.info { "Finished synchronizing donator roles!" }
    }

    private fun updateFanArtistsRoles(communityGuild: Guild) {
        // Apply fan artists roles
        logger.info { "Applying fan artists roles in the community server..." }

        val drawingRole = communityGuild.getRoleById(community.roles.drawing)

        if (drawingRole == null) {
            logger.warn { "Artist role in the community server does not exist!" }
            return
        }

        runBlocking {
            val response = LorittaHelper.http.get("https://fanarts.perfectdreams.net/api/v1/fan-arts")

            if (response.status != HttpStatusCode.OK) {
                logger.warn { "Gallery of Dreams' Get Fan Arts API response was ${response.status}!" }
                return@runBlocking
            }

            val payload = response.bodyAsText(Charsets.UTF_8)
            val galleryOfDreamsDataResponse = Json.decodeFromString<GalleryOfDreamsDataResponse>(payload)

            val validIllustratorIds = galleryOfDreamsDataResponse.artists.mapNotNull {
                it.socialConnections
                    .filterIsInstance<DiscordSocialConnection>()
                    .firstOrNull()
                    ?.id
            }

            // First we will give the members
            for (userId in validIllustratorIds) {
                try {
                    if (userNotInCommunityServerCache.contains(userId)) {
                        logger.info { "Skipping $userId because it is in the \"not in community server\" cache..." }
                        continue
                    }

                    val member = communityGuild.retrieveMemberById(userId)
                        .await()

                    if (!member.roles.contains(drawingRole)) {
                        logger.info { "Giving artist role to ${member.idLong}..." }

                        communityGuild
                            .addRoleToMember(member, drawingRole)
                            .reason("The member is present in the Gallery of Dreams!")
                            .await()
                    }
                } catch (e: ErrorResponseException) {
                    when (e.errorResponse) {
                        ErrorResponse.UNKNOWN_MEMBER -> {
                            userNotInCommunityServerCache.add(userId)
                            logger.warn { "Member $userId is not in Loritta's community server!" }
                        }
                        ErrorResponse.UNKNOWN_USER -> {
                            userNotInCommunityServerCache.add(userId)
                            logger.warn { "User $userId does not exist!" }
                        }
                        else -> logger.warn(e) { "Exception while retrieving $userId" }
                    }
                }
            }

            val invalidIllustrators = communityGuild.getMembersWithRoles(drawingRole).filter { !validIllustratorIds.contains(it.idLong) }
            invalidIllustrators.forEach {
                logger.info { "Removing artist role from ${it.user.id}..." }

                communityGuild
                    .removeRoleFromMember(it, drawingRole)
                    .reason("The member is not present in the Gallery of Dreams...")
                    .await()
            }

            logger.info { "Finished synchronizing artist roles!" }
        }
    }

    private fun synchronizeRoles(fromGuild: Guild, toGuild: Guild, originalRoleId: Long, giveRoleId: Long) {
        val originalRole = fromGuild.getRoleById(originalRoleId) ?: return
        val giveRole = toGuild.getRoleById(giveRoleId) ?: return

        val membersWithOriginalRole = fromGuild.getMembersWithRoles(originalRole)
        val membersWithNewRole = toGuild.getMembersWithRoles(giveRole)

        for (member in membersWithNewRole) {
            if (fromGuild.isMember(member.user) && toGuild.isMember(member.user)) {
                if (!membersWithOriginalRole.any { it.user.id == member.user.id }) {
                    logger.info { "Removing role ${giveRole.id} of ${member.effectiveName} (${member.user.id})..." }
                    toGuild.removeRoleFromMember(member, giveRole).queue()
                }
            }
        }

        for (member in membersWithOriginalRole) {
            if (fromGuild.isMember(member.user) && toGuild.isMember(member.user)) {
                if (!membersWithNewRole.any { it.user.id == member.user.id }) {
                    val usMember = toGuild.getMember(member.user) ?: continue

                    logger.info { "Adding role ${giveRole.id} to ${member.effectiveName} (${member.user.id})..." }
                    toGuild.addRoleToMember(usMember, giveRole).queue()
                }
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

    class RoleIsMentionableComparator : RoleFieldComparator<Boolean>() {
        override fun getValue(role: Role) = role.isMentionable
        override fun setValue(manager: RoleManager, newValue: Any) { manager.setMentionable(newValue as Boolean) }
    }
}