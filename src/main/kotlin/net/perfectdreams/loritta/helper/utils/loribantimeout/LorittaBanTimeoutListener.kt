package net.perfectdreams.loritta.helper.utils.loribantimeout

import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Gateway
import dev.kord.gateway.GuildMemberAdd
import dev.kord.gateway.MessageCreate
import dev.kord.gateway.on
import kotlinx.datetime.Clock
import net.perfectdreams.loritta.cinnamon.pudding.tables.BannedUsers
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.Constants
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.days

class LorittaBanTimeoutListener(val m: LorittaHelperKord) {
    fun installLorittaBanTimeout(gateway: Gateway) {
        gateway.on<MessageCreate> {
            if (this.message.guildId.value?.value?.toLong() != Constants.COMMUNITY_SERVER_ID)
                return@on

            m.helperRest.guild.modifyGuildMember(
                message.guildId.value!!,
                message.author.id
            ) {
                this.communicationDisabledUntil = Clock.System.now()
                    .plus(28.days)

                this.reason = "User is Loritta Banned!"
            }

            m.helperRest.channel.deleteMessage(
                message.channelId,
                message.id,
                "User is Loritta Banned!"
            )
        }

        gateway.on<GuildMemberAdd> {
            if (this.member.guildId.value.toLong() != Constants.COMMUNITY_SERVER_ID)
                return@on

            if (isLorittaBanned(m, this.member.user.value!!.id)) {
                m.helperRest.guild.modifyGuildMember(
                    this.member.guildId,
                    this.member.user.value!!.id
                ) {
                    this.communicationDisabledUntil = Clock.System.now()
                        .plus(28.days)

                    this.reason = "User is Loritta Banned!"
                }
            }
        }
    }

    private fun getBannedState(m: LorittaHelperKord, userId: Long): ResultRow? {
        return transaction(m.databases.lorittaDatabase) {
            BannedUsers.select {
                BannedUsers.userId eq userId and
                        (BannedUsers.valid eq true) and
                        (
                                BannedUsers.expiresAt.isNull()
                                        or
                                        (
                                                BannedUsers.expiresAt.isNotNull() and
                                                        (BannedUsers.expiresAt greaterEq System.currentTimeMillis()))
                                )
            }
                .orderBy(BannedUsers.bannedAt, SortOrder.DESC)
                .firstOrNull()
        }
    }

    private fun isLorittaBanned(m: LorittaHelperKord, userId: Snowflake): Boolean {
        return isLorittaBanned(m, userId.value.toLong())
    }

    private fun isLorittaBanned(m: LorittaHelperKord, userId: Long): Boolean {
        getBannedState(m, userId) ?: return false
        return true
    }
}