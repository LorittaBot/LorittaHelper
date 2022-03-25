package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.rest.Image
import dev.kord.rest.builder.message.create.embed
import net.perfectdreams.discordinteraktions.common.entities.Icon
import net.perfectdreams.discordinteraktions.common.entities.User
import net.perfectdreams.discordinteraktions.common.utils.author
import net.perfectdreams.discordinteraktions.common.utils.field
import net.perfectdreams.discordinteraktions.common.utils.footer
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.Constants

object LoriToolsUtils {
    suspend fun logToSaddestOfTheSads(
        helper: LorittaHelperKord,
        moderator: User,
        punishedUserId: Snowflake,
        title: String,
        reason: String,
        color: Color
    ) {
        val punishedUser = try {
            helper.helperRest.user.getUser(punishedUserId)
        } catch (e: Exception) {
            // May trigger an exception if the user does not exist
            null
        }

        helper.helperRest.channel.createMessage(
            Snowflake(Constants.PORTUGUESE_SADDEST_OF_THE_SADS_CHANNEL_ID)
        ) {
            embed {
                author("${moderator.name}#${moderator.discriminator} (${moderator.id})", null, moderator.avatar.cdnUrl.toUrl {
                    format = Image.Format.PNG
                })
                this.title = "$punishedUserId | $title"
                field("Motivo", reason, true)
                this.color = color

                if (punishedUser != null)
                    footer(
                        "${punishedUser.username}#${punishedUser.discriminator} (${punishedUser.id})",
                        (
                                punishedUser.avatar?.let {
                                    Icon.UserAvatar(Snowflake(punishedUserId.value), it)
                                } ?: Icon.DefaultUserAvatar(punishedUser.discriminator.toInt())
                                )
                            .cdnUrl
                            .toUrl {
                                format = Image.Format.PNG
                            }
                    )
            }
        }
    }
}