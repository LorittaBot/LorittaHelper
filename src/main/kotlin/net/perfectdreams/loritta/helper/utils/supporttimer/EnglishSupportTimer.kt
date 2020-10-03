package net.perfectdreams.loritta.helper.utils.supporttimer

import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Constants

class EnglishSupportTimer(m: LorittaHelper, jda: JDA) : SupportTimer(m, jda) {
    override val channelId = Constants.ENGLISH_SUPPORT_CHANNEL_ID
    override val replies = listOf(
        LorittaReply(
            "<a:rat_jam:720643637033304442> **READ THIS BEFORE ASKING SOMETHING!** <a:rat_jam:720643637033304442>",
            prefix = "<:lori_nice:726845783344939028>",
            mentionUser = false
        ),
        LorittaReply(
            "**If you are going to ask something about Loritta:** Check if your question has already been answered in the <#761337709720633392>! If it isn't there, send your question here and, in the message, mention <@&${Constants.ENGLISH_LORITTA_SUPPORT_ROLE_ID}>, we will try to help you as soon as possible!",
            "<:lori_ok:731873534036541500>",
            mentionUser = false
        ),
        LorittaReply(
            "**Remember that we are *Loritta's support***: We will not help you with other problems (for example: problems with other bots, problems with Discord, etc.)",
            "<a:lori_fight:731871119400894525>",
            mentionUser = false
        ),
        LorittaReply(
            "Loritta was made for portuguese speakers, but we trying to bring all of her cuteness and fun to other languages! We know that some parts are not translated yet but we're trying to improve it, be patient and sorry for the inconvenience ;w;",
            "<:sad_cat_thumbs_up:686370257308483612>",
            mentionUser = false
        ),
        LorittaReply(
            "**If you are going to ask \"aaaaw, lori's down <:smol_lori_putassa:395010059157110785>\"**: Check out <#761385919479414825> for more information!",
            "<:smol_lori_putassa:395010059157110785>",
            mentionUser = false
        ),
        LorittaReply(
            "**If you are going to ask if something was changed/added/removed**: Check out <#420627916028641280> to know!",
            "<a:lori_dabbing:727888868711334287>",
            mentionUser = false
        )
    )
    override val containsString = "READ THIS BEFORE ASKING SOMETHING"
}