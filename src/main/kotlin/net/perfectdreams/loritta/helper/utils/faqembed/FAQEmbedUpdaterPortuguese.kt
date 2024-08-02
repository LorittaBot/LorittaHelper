package net.perfectdreams.loritta.helper.utils.faqembed

import net.dv8tion.jda.api.JDA
import net.perfectdreams.loritta.helper.LorittaHelper

class FAQEmbedUpdaterPortuguese(m: LorittaHelper, jda: JDA) : FAQEmbedUpdater(m, jda) {
    override val title = "Perguntas Frequentes"
    override val channelId = m.helperConfig.guilds.community.channels.faq
}