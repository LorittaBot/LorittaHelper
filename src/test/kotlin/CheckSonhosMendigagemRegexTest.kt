import net.perfectdreams.loritta.helper.utils.checksonhosmendigagem.CheckSonhosMendigagem
import org.junit.jupiter.api.Test

class CheckSonhosMendigagemRegexTest {
    @Test
    fun `test mendigagem responses`() {
        val mendigagemMessages = listOf(
            "Alguém doa alguns sonhos aí?",
            "me doa sonhos",
            "ALGUÉM ME DÁ 30K DE SONHOS POR FAVOR",
            "alguem poderia doar 20k",
            "alguém doa 10k",
            "Alguém me doa 10k",
            "Doa sonhos cara?",
            "doa sonhos",
            "Doa um pouco de sonhos aí, por favor.",
            "Alguém doa alguns sonhos aí?",
            "Doa sonhos cara? ^^",
            "agl me doa 5k?",
            "Alguém doa sonhos para mim pfv",
            "Algm me doa sonho ai nmrl",
            "Alguém doa 1k ?",
            "Alguém doa pra me farmar ?",
            "Alguém doa 100k aí de sonhos",
            "Alguém me doa 10k so pra eu farmar e tentar recuperar meus 1mi?",
            "Alg doa 10k?",
            "ALGUEM ME DOA",
            "Alg me doa sonhos?",
            "Me da sonhos ai alguém kkk",
            "Alguém me doa 50k to pobre",
            "doa 1k ai por favor",
            "oi gente me doa sonhos",
            "oi gente doa sonhos",
            "estou falido me doa sonhos",
            "to falida gente me doa sonhos",
            "oi galera me doem sonhos",
            "gente eu fali me doem sonhos",
            "oi gente fali me doa sonhos",
            "me deem sonhos",
            "Alg me doa um pouco de sonhos",
            "Vc mim doa sonhos?",
            "Alguém doa pfv ?",
            "doa 20k favor?",
            "Alguém pode me emprestar 5k de sonhos?"
        )

        val stuffThatShouldntBeBlocked = listOf(
            "Alguém me dá uma dica"
        )

        messageLoop@for (message in mendigagemMessages) {
            println("Testing $message")

            for ((name, regex) in CheckSonhosMendigagem.regexes) {
                println("Testing RegEx \"$name\": $regex")
                if (regex.matches(message))
                    continue@messageLoop
            }

            throw IllegalArgumentException("Missing match for \"$message\"!")
        }

        messageLoop@for (message in stuffThatShouldntBeBlocked) {
            println("Testing $message")

            for ((name, regex) in CheckSonhosMendigagem.regexes) {
                println("Testing RegEx \"$name\": $regex")
                if (regex.matches(message))
                    throw IllegalArgumentException("RegEx was matched for \"$message\", but it shouldn't!")
            }
        }
    }
}