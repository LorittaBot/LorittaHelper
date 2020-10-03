package responses

import net.perfectdreams.loritta.helper.serverresponses.LorittaResponse
import org.junit.jupiter.api.Test

abstract class ResponseTestBase(
    val responses: List<LorittaResponse>,
    val questions: List<String>
) {
    @Test
    fun `test response`() {
        val clazzName = this::class.simpleName!!.removeSuffix("Test")

        questionLoop@for (question in questions) {
            println("[$clazzName] Testing $question")

            for (response in responses) {
                if (response.handleResponse(question)) {
                    if (clazzName != response::class.simpleName)
                        throw IllegalArgumentException("Matched ${response::class.simpleName} when it shouldn't match")
                    else {
                        continue@questionLoop
                    }
                }
            }

            throw IllegalArgumentException("Missing match for $question!")
        }
    }
}