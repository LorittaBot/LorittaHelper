package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.JsonErrorCode
import dev.kord.rest.request.KtorRequestException
import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.commands.get
import net.perfectdreams.discordinteraktions.context.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.required
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.config.LorittaConfig

class RetrieveMessageCommand(helper: LorittaHelper, lorittaConfig: LorittaConfig) : HelperSlashCommand(helper, this) {
    companion object : SlashCommandDeclaration(
        name = "retrievemessage",
        description = "Pega o conteúdo de uma mensagem a partir de um link"
    ) {
        override val options = Options

        object Options : SlashCommandDeclaration.Options() {
            val messageUrl = string("message_url", "Link da Mensagem")
                .required()
                .register()
        }
    }

    val rest = RestClient(lorittaConfig.token)

    override suspend fun executesHelper(context: SlashCommandContext) {
        val messageUrl = options.messageUrl.get(context)

        val split = messageUrl.split("/")
        val length = split.size

        val messageId = split[length - 1]
        val channelId = split[length - 2]
        val guildId = split[length - 3]

        try {
            val message = rest.channel.getMessage(
                Snowflake(channelId),
                Snowflake(messageId)
            )

            context.sendMessage {
                content = """
                    ${message.author.username}#${message.author.discriminator} (${message.author.id.value})
                    
                    ```
                    ${message.content}
                    ```
                """.trimIndent()
            }
        } catch (e: KtorRequestException) {
            if (e.error?.code == JsonErrorCode.UnknownChannel) {
                context.sendMessage {
                    content = "Canal desconhecido!"
                }
            }

            if (e.error?.code == JsonErrorCode.UnknownMessage) {
                context.sendMessage {
                    content = "Mensagem desconhecida!"
                }
            }
        }
    }
}