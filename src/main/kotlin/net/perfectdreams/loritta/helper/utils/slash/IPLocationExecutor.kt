package net.perfectdreams.loritta.helper.utils.slash

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord

class IPLocationExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(IPLocationExecutor::class) {
        override val options = Options

        object Options : ApplicationCommandOptions() {
            val ip = string("address", "Endereço a ser verificado")
                .register()
        }
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.deferChannelMessage()
        val userIp = args[options.ip]

        // pls don't ban us :pray:
        val response = LorittaHelperKord.http.post<String>("https://iplocation.com/") {
            userAgent("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0")
            parameter("ip", userIp)
        }

        val data = Json.parseToJsonElement(response)
            .jsonObject


        context.sendEphemeralMessage {
            content = buildString {
                for ((key, value) in data.entries) {
                    append("**$key:** `$value`")
                    append("\n")
                }
            }
        }
    }
}