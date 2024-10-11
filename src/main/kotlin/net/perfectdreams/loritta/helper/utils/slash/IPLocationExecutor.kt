package net.perfectdreams.loritta.helper.utils.slash

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import net.perfectdreams.loritta.morenitta.interactions.commands.ApplicationCommandContext
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.HelperExecutor

class IPLocationExecutor(helper: LorittaHelper) : HelperExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val ip = string("address", "Endereço a ser verificado")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage(false)
        val userIp = args[options.ip]

        // pls don't ban us :pray:
        val response = LorittaHelper.http.post("https://iplocation.com/") {
            userAgent("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0")
            parameter("ip", userIp)
        }.bodyAsText(Charsets.UTF_8)

        val data = Json.parseToJsonElement(response)
            .jsonObject


        context.reply(true) {
            content = buildString {
                for ((key, value) in data.entries) {
                    append("**$key:** `$value`")
                    append("\n")
                }
            }
        }
    }
}