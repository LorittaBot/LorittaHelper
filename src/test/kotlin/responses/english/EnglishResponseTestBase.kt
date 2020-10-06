package responses.english

import net.perfectdreams.loritta.helper.serverresponses.EnglishResponses
import responses.ResponseTestBase

abstract class EnglishResponseTestBase(questions: List<String>) : ResponseTestBase(EnglishResponses.responses, questions)