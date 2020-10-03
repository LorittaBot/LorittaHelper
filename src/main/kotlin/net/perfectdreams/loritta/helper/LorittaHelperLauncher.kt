package net.perfectdreams.loritta.helper

import com.typesafe.config.ConfigFactory
import kotlinx.serialization.hocon.Hocon
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import java.io.File

object LorittaHelperLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val lightbendConfig = ConfigFactory.parseFile(File("./helper.conf"))
            .resolve()

        val config = Hocon.decodeFromConfig(LorittaHelperConfig.serializer(), lightbendConfig)

        val m = LorittaHelper(config)
        m.start()
    }
}