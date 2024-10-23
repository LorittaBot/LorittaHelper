package net.perfectdreams.loritta.helper.utils.slash

import dev.minn.jda.ktx.messages.InlineEmbed
import dev.minn.jda.ktx.messages.MessageCreate
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.utils.TimeFormat
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.extensions.await
import java.awt.Color

object LoriToolsUtils {
    suspend fun logToSaddestOfTheSads(
        helper: LorittaHelper,
        moderator: User,
        punishedUserId: Long,
        title: String,
        expiresAt: Long?,
        reason: String,
        color: Color
    ) {
        val community = helper.config.guilds.community

        val punishedUser = try {
            helper.jda.retrieveUserById(punishedUserId).await()
        } catch (e: Exception) {
            // May trigger an exception if the user does not exist
            null
        }

        val channel = helper.jda.getTextChannelById(community.channels.saddestOfTheSads) ?: return

        channel.sendMessage(
            MessageCreate {
                embed {
                    author("${moderator.name}#${moderator.discriminator} (${moderator.id})", null, moderator.avatarUrl)
                    this.title = "$punishedUserId | $title"
                    field("Motivo", reason, true)
                    this.color = color.rgb

                    if (expiresAt != null) {
                        field("Expira em", TimeFormat.DATE_TIME_LONG.format(expiresAt), true)
                    }

                    if (punishedUser != null)
                        footer(
                            "${punishedUser.name}#${punishedUser.discriminator} (${punishedUser.id})",
                            punishedUser.avatarUrl
                        )
                }
            }
        ).await()
    }

    suspend fun logToSaddestOfTheSads(
        helper: LorittaHelper,
        moderator: User,
        embed: InlineEmbed.() -> (Unit),
    ) {
        val community = helper.config.guilds.community

        val channel = helper.jda.getTextChannelById(community.channels.saddestOfTheSads) ?: return

        channel.sendMessage(
            MessageCreate {
                embed {
                    author("${moderator.name}#${moderator.discriminator} (${moderator.id})", null, moderator.avatarUrl)
                }
            }
        ).await()
    }
}