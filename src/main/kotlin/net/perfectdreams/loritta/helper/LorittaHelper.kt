package net.perfectdreams.loritta.helper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.requests.GatewayIntent
import net.perfectdreams.loritta.helper.listeners.MessageListener
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterEnglish
import net.perfectdreams.loritta.helper.utils.faqembed.FAQEmbedUpdaterPortuguese
import net.perfectdreams.loritta.helper.utils.supporttimer.EnglishSupportTimer
import net.perfectdreams.loritta.helper.utils.supporttimer.PortugueseSupportTimer
import java.util.concurrent.Executors

class LorittaHelper(val config: LorittaHelperConfig) {
    // We only need one single thread because <3 coroutines
    // As long we don't do any blocking tasks inside of the executor, Loritta Helper will work fiiiine
    // and will be very lightweight!
    val executor = Executors.newSingleThreadExecutor()
        .asCoroutineDispatcher()

    fun start() {
        // We only care about GUILD MESSAGES and we don't need to cache any users
        val jda = JDABuilder.createLight(
            config.token,
            GatewayIntent.GUILD_MESSAGES
        )
            .addEventListeners(MessageListener(this))
            .build()

        // OwO whats this???
        jda.presence.activity = Activity.listening("OwO")

        FAQEmbedUpdaterPortuguese(this, jda).start()
        FAQEmbedUpdaterEnglish(this, jda).start()

        PortugueseSupportTimer(this, jda).start()
        EnglishSupportTimer(this, jda).start()
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(executor) {
        block.invoke(this)
    }
}