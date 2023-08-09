package net.perfectdreams.loritta.helper.utils.slash

import dev.minn.jda.ktx.messages.MessageCreate
import net.dv8tion.jda.api.entities.User
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.extensions.await
import java.awt.Color

object LoriToolsUtils {
    suspend fun logToSaddestOfTheSads(
        helper: LorittaHelper,
        moderator: User,
        punishedUserId: Long,
        title: String,
        reason: String,
        color: Color
    ) {
        val punishedUser = try {
            helper.jda.retrieveUserById(punishedUserId).await()
        } catch (e: Exception) {
            // May trigger an exception if the user does not exist
            null
        }

        val channel = helper.jda.getTextChannelById(Constants.PORTUGUESE_SADDEST_OF_THE_SADS_CHANNEL_ID) ?: return

        channel.sendMessage(
            MessageCreate {
                embed {
                    author("${moderator.name}#${moderator.discriminator} (${moderator.id})", null, moderator.avatarUrl)
                    this.title = "$punishedUserId | $title"
                    field("Motivo", reason, true)
                    this.color = color.rgb

                    if (punishedUser != null)
                        footer(
                            "${punishedUser.name}#${punishedUser.discriminator} (${punishedUser.id})",
                            punishedUser.avatarUrl
                        )
                }
            }
        ).await()
    }
}