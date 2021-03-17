package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.MessageFlags
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import net.perfectdreams.discordinteraktions.commands.get
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.required
import net.perfectdreams.loritta.helper.LorittaHelper

class IPLocationCommand(helper: LorittaHelper) : HelperSlashCommand(helper, this) {
    companion object : SlashCommandDeclaration(
        name = "findthemeliante",
        description = "Em busca de meliantes pelo address"
    ) {
        override val options = Options

        object Options : SlashCommandDeclaration.Options() {
            val ip = user("address", "Endereço a ser verificado")
                .required()
                .register()
        }
    }

    override suspend fun executesHelper(context: SlashCommandContext) {
        val userIp = options.ip.get(context)

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

            flags = MessageFlags(MessageFlag.Ephemeral)
        }
    }
}