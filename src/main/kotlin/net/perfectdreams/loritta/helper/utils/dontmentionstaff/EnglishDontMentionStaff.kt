package net.perfectdreams.loritta.helper.utils.dontmentionstaff

import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.utils.Constants

class EnglishDontMentionStaff : DontMentionStaff() {
    override val roleId = Constants.ENGLISH_LORITTA_SUPPORT_ROLE_ID
    override val channelId = Constants.ENGLISH_SUPPORT_CHANNEL_ID

    override fun getResponse() = listOf(
        LorittaReply(
            "**Don't mention staff members!** Maybe they are busy... what if they are pooping and you are here, disturbing them with mentions...",
            prefix = "<:lori_rage:556525700425711636>"
        ),
        LorittaReply(
            "If you need help, mention <@&$roleId> in your message, thank you!",
            mentionUser = false
        )
    )
}