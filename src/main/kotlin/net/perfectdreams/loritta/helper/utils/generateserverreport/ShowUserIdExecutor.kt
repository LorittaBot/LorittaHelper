package net.perfectdreams.loritta.helper.utils.generateserverreport

import net.perfectdreams.discordinteraktions.common.components.ButtonClickExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.ButtonClickWithDataExecutor
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.entities.User
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