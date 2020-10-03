package responses.portuguese

import net.perfectdreams.loritta.helper.serverresponses.PortugueseResponses
import responses.ResponseTestBase

abstract class PortugueseResponseTestBase(questions: List<String>) : ResponseTestBase(PortugueseResponses.responses, questions)