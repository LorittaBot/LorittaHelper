package net.perfectdreams.loritta.helper.utils.generateserverreport

import net.perfectdreams.discordinteraktions.common.components.ButtonExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.ButtonExecutor
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import dev.kord.core.entity.User
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class ShowUserIdExecutor(val m: LorittaHelperKord) : ButtonExecutor {
    companion object : ButtonExecutorDeclaration(ShowUserIdExecutor::class, "show_uid")

    override suspend fun onClick(user: User, context: ComponentContext) {
        // Makes copying the User's ID in mobile phones easier and less cumbersome!
        val reportedUserId = ComponentDataUtils.decode<ShowUserIdData>(context.data)
        context.sendEphemeralMessage {
            content = reportedUserId.userId.toString()
        }
    }
}