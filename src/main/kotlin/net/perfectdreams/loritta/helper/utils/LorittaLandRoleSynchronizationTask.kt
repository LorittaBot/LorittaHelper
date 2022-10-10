package net.perfectdreams.loritta.helper.utils

import io.ktor.client.call.*
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
import kotlin.math.ceil

class LorittaLandRoleSynchronizationTask(val m: LorittaHelper, val jda: JDA) : Runnable {
    companion object {
        private val roleRemap = mutableListOf(
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
            653207858707562497L to 762377883339325460L,
            341343754336337921L to 467750037812936704L, // Desenhistas
            385579854336360449L to 467750852610752561L, // Tradutores
            364201981016801281L to 420640526711390208L, // Doador
            463652112656629760L to 568506127977938977L, // Super Doador
            534659343656681474L to 568505810825642029L, // Magnata
        )

        private val roleFieldComparators = listOf(
            RoleColorComparator(),
            RolePermissionsComparator(),
            RoleHoistedComparator(),
            RoleIsMentionableComparator()
        )

        private val logger = KotlinLogging.logger {}
    }

    override fun run() {
        logger.info { "Synchronizing roles..." }

        try {
            val communityGuild = jda.getGuildById(297732013006389252L)
            if (communityGuild != null)
                logger.info { "Community Guild Members: ${communityGuild.members.size}" }
            else
                logger.warn { "Community Guild is missing..." }

            val supportGuild = jda.getGuildById(420626099257475072L)
            if (supportGuild != null)
                logger.info { "Support Guild Members: ${supportGuild.members.size}" }
            else
                logger.warn { "Support Guild is missing..." }

            val sparklyGuild = jda.getGuildById(320248230917046282L)
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

        val donatorRole = communityGuild.getRoleById(364201981016801281L)
        val superDonatorRole = communityGuild.getRoleById(463652112656629760L)
        val megaDonatorRole = communityGuild.getRoleById(534659343656681474L)
        val advertisementRole = communityGuild.getRoleById(619691791041429574L)

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
                val filter = roles.filter { userRole -> LorittaCommunityRoleButtons.colors.any { it.roleId.value.toLong() == userRole.idLong } }
                roles.removeAll(filter)

                // Remove custom badges if the user is not Level 10
                val coolBadgesFilter = roles.filter { userRole -> LorittaCommunityRoleButtons.coolBadges.any { it.roleId.value.toLong() == userRole.idLong } }
                if (!member.roles.any { it.idLong == 655132411566358548L })
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

        val drawingRole = communityGuild.getRoleById(341343754336337921L)

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
                    if (e.errorResponse == ErrorResponse.UNKNOWN_MEMBER)
                        logger.warn { "Member $userId is not in Loritta's community server!" }
                    else if (e.errorResponse == ErrorResponse.UNKNOWN_USER)
                        logger.warn { "User $userId does not exist!" }
                    else
                        logger.warn(e) { "Exception while retrieving $userId" }
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