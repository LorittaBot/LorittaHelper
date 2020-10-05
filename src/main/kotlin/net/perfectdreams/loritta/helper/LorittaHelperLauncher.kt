package net.perfectdreams.loritta.helper

import com.typesafe.config.ConfigFactory
import kotlinx.serialization.hocon.Hocon
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import java.io.File

/**
 * Class that instantiates and initializes [LorittaHelper]
 */
object LorittaHelperLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        // Getting Loritta Helper config file
        val lightbendConfig = ConfigFactory.parseFile(File("./helper.conf"))
            .resolve()

        // Parsing HOCON config
        val config = Hocon.decodeFromConfig(LorittaHelperConfig.serializer(), lightbendConfig)

        // Initializing Loritta Helper
        LorittaHelper(config).start()
    }
}