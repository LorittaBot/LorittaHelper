package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.JsonErrorCode
import dev.kord.rest.request.KtorRequestException
import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelperKord

class RetrieveMessageExecutor(helper: LorittaHelperKord, val rest: RestClient) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(RetrieveMessageExecutor::class) {
        override val options = Options

        object Options : CommandOptions() {
            val messageUrl = string("message_url", "Link da Mensagem")
                .register()
        }
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val messageUrl = args[options.messageUrl]

        val split = messageUrl.split("/")
        val length = split.size

        val messageId = split[length - 1]
        val channelId = split[length - 2]

        try {
            val message = rest.channel.getMessage(
                Snowflake(channelId),
                Snowflake(messageId)
            )

            context.sendMessage {
                content = """
                    |**Author:** `${message.author.username}#${message.author.discriminator}` (`${message.author.id.value}`)
                    |
                    |```
                    |${message.content}
                    |```
                """.trimMargin()
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