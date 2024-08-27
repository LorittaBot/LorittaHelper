package net.perfectdreams.loritta.helper.utils.faqembed

import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.helper.LorittaHelper

class FAQEmbedUpdaterEnglish(m: LorittaHelper, jda: JDA) : FAQEmbedUpdater(m, jda) {
    override val title = "Frequently Asked Questions"
    override val channelId = m.config.guilds.english.channels.faq
}