package net.perfectdreams.loritta.helper

import com.typesafe.config.ConfigFactory
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import net.perfectdreams.loritta.helper.utils.config.FanArtsConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import java.io.File

/**
 * Class that instantiates and initializes [LorittaHelper]
 */
object LorittaHelperLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        // Getting Loritta Helper config file
        val config = loadConfig<LorittaHelperConfig>("./helper.conf")

        val fanArtsConfig = loadConfigOrNull<FanArtsConfig>("./fan_arts.conf")
        val lorittaConfig = loadConfigOrNull<LorittaConfig>("./loritta.conf")

        // Getting config of
        // Initializing Loritta Helper
        LorittaHelper(
            config,
            fanArtsConfig,
            lorittaConfig
        ).start()
    }

    inline fun <reified T> loadConfig(path: String): T {
        // Getting Loritta Helper config file
        val lightbendConfig = ConfigFactory.parseFile(File(path))
            .resolve()

        // Parsing HOCON config
        return Hocon.decodeFromConfig(lightbendConfig)
    }

    inline fun <reified T> loadConfigOrNull(path: String): T? {
        val file = File(path)
        if (!file.exists())
            return null

        // Getting Loritta Helper config file
        val lightbendConfig = ConfigFactory.parseFile(File(path))
            .resolve()

        // Parsing HOCON config
        return Hocon.decodeFromConfig(lightbendConfig)
    }
}