package net.perfectdreams.loritta.helper

import com.typesafe.config.ConfigFactory
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import net.perfectdreams.loritta.helper.utils.config.FanArtsConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaConfig
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import java.io.File
import kotlin.system.exitProcess

/**
 * Class that instantiates and initializes [LorittaHelper]
 */
object LorittaHelperLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        // Getting Loritta Helper config file
        val config = loadConfigOrNull<LorittaHelperConfig>("./helper.conf")
            ?: run {
                println("Expected helper.conf file to be present in the current directory!")
                println("Retrieving from resources...")

                copyFromJar("/helper.conf", "./helper.conf")
                copyFromJar("/fan_arts.conf", "./fan_arts.conf")

                println("Please fill the helper.conf file with the necessary information!")
                println("Retrieved fan_arts.conf and loritta.conf files from resources as well!")
                println("Exiting...")

                exitProcess(1)
            }

        val fanArtsConfig = loadConfigOrNull<FanArtsConfig>("./fan_arts.conf")

        // Getting config of
        // Initializing Loritta Helper
        LorittaHelper(
            config,
            fanArtsConfig
        ).start()
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

    private fun copyFromJar(inputPath: String, outputPath: String) {
        val inputStream = LorittaHelperLauncher::class.java.getResourceAsStream(inputPath)
        File(outputPath).writeBytes(inputStream.readAllBytes())
    }
}