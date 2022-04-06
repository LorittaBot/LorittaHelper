package net.perfectdreams.loritta.helper.utils.faqembed

import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.helper.LorittaHelper

class FAQEmbedUpdaterPortuguese(m: LorittaHelper, jda: JDA) : FAQEmbedUpdater(m, jda) {
    override val title = "FAQ da Staff"
    override val channelId = 961059506840809473L
}