package net.perfectdreams.loritta.helper.utils.slash

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelper

class IPLocationExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(IPLocationExecutor::class) {
        override val options = Options

        object Options : CommandOptions() {
            val ip = string("address", "Endereço a ser verificado")
                .register()
        }
    }

    override suspend fun executeHelper(context: SlashCommandContext, args: SlashCommandArguments) {
        context.deferReply()
        val userIp = args[options.ip]

        // pls don't ban us :pray:
        val response = LorittaHelper.http.post<String>("https://iplocation.com/") {
            userAgent("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0")
            parameter("ip", userIp)
        }

        val data = Json.parseToJsonElement(response)
            .jsonObject


        context.sendMessage {
            content = buildString {
                for ((key, value) in data.entries) {
                    append("**$key:** `$value`")
                    append("\n")
                }
            }

            isEphemeral = true
        }
    }
}