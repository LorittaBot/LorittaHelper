package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.loritta.morenitta.interactions.commands.ApplicationCommandContext
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import net.perfectdreams.loritta.morenitta.interactions.commands.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.interactions.commands.vanilla.HelperExecutor
import net.perfectdreams.sequins.text.StringUtils

class RetrieveMessageExecutor(helper: LorittaHelper/* , val rest: RestClient */) : HelperExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val messageUrl = string("message_url", "Link da Mensagem")
    }

    override val options = Options()

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        /* val messageUrl = args[options.messageUrl]

        val split = messageUrl.split("/")
        val length = split.size

        val messageId = split[length - 1]
        val channelId = split[length - 2]

        try {
            val message = rest.channel.getMessage(
                channelId,
                messageId
            )

            val builder = StringBuilder()
            val channel = rest.channel.getChannel(message.channelId)
         
            if (message.guildId.value != null) {
                val guild = rest.guild.getGuild(message.guildId.value!!)

                builder.append(
                    "**Guild:** `${guild.name}` (`${guild.id}`)" + "\n"
                )
            }

            builder.append("""
                |**Channel:** `${channel.name.value ?: "Canal sem nome!"}` (`${channel.id}`)
                |**Author:** `${message.author.username}#${message.author.discriminator}` (`${message.author.id.value}`)
                |
                |""".trimMargin()
            )

            if (message.content.length < 2000) {
                context.reply(false) {
                    content = builder.append("""
                                |```
                                |${message.content}
                                |```
                            """.trimMargin()
                    ).toString()
                }
            } else {
                context.reply(false) {
                    content = builder.toString()
                }

                val chunkedLines = StringUtils.chunkedLines(message.content.split("\n"), 2000, true)

                for (line in chunkedLines) {
                    context.reply(false) {
                        content = """
                            |```
                            |${line}
                            |```
                        """.trimMargin()
                    }
                }
            }
        } catch (e: KtorRequestException) {
            if (e.error?.code == JsonErrorCode.UnknownChannel) {
                context.reply(false) {
                    content = "Canal desconhecido!"
                }
            }

            if (e.error?.code == JsonErrorCode.UnknownMessage) {
                context.reply(false) {
                    content = "Mensagem desconhecida!"
                }
            }

            if (e.error?.code == JsonErrorCode.MissingAccess) {
                context.reply(false) {
                    content = "Não tenho permissão pra ver mensagens deste canal!"
                }
            }
        } */
    }
}