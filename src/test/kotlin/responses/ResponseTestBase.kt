package responses

import com.typesafe.config.ConfigFactory
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import net.perfectdreams.loritta.helper.serverresponses.LorittaResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.EnglishResponses
import net.perfectdreams.loritta.helper.serverresponses.loritta.PortugueseResponses
import net.perfectdreams.loritta.helper.utils.config.LorittaHelperConfig
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.reflect.jvm.jvmName

abstract class ResponseTestBase(
    val questions: List<String>
) {
    private val tempFile = File.createTempFile("helper", ".conf")

    @Test
    fun `test response`() {
        tempFile.writeBytes(javaClass.getResourceAsStream("/helper.conf").readAllBytes())

        val config = Hocon.decodeFromConfig<LorittaHelperConfig>(
            ConfigFactory.parseFile(tempFile)
        )

        tempFile.delete()

        val allResponses = buildList<LorittaResponse> {
            addAll(EnglishResponses(config).responses)
            addAll(PortugueseResponses(config).responses)
            sortByDescending { it.priority }
        }

        val clazzName = this::class.simpleName!!.removeSuffix("Test")

        questionLoop@for (question in questions) {
            println("[$clazzName] Testing $question")

            for (response in allResponses) {
                if (response.handleResponse(question)) {
                    if (clazzName != response::class.simpleName)
                        throw IllegalArgumentException("Matched ${response::class.simpleName} when it shouldn't match. Try changing the ${response::class.jvmName} priority!")
                    else {
                        continue@questionLoop
                    }
                }
            }

            throw IllegalArgumentException("Missing match for $question!")
        }
    }
}