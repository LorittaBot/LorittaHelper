package net.perfectdreams.loritta.helper.utils.generateserverreport

import net.perfectdreams.discordinteraktions.api.entities.User
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.buttons.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.context.components.ComponentContext
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class ShowUserIdExecutor(val m: LorittaHelperKord) : ButtonClickWithDataExecutor {
    companion object : ButtonClickExecutorDeclaration(ShowUserIdExecutor::class, "show_uid")

    override suspend fun onClick(user: User, context: ComponentContext, data: String) {
        // Makes copying the User's ID in mobile phones easier and less cumbersome!
        val reportedUserId = ComponentDataUtils.decode<ShowUserIdData>(data)
        context.sendEphemeralMessage {
            content = reportedUserId.userId.toString()
        }
    }
}