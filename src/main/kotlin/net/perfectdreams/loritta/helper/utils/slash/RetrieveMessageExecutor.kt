package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.Snowflake
import dev.kord.rest.json.JsonErrorCode
import dev.kord.rest.request.KtorRequestException
import dev.kord.rest.service.RestClient
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.sequins.text.StringUtils

class RetrieveMessageExecutor(helper: LorittaHelperKord, val rest: RestClient) : HelperSlashExecutor(helper, PermissionLevel.HELPER) {
    inner class Options : ApplicationCommandOptions() {
        val messageUrl = string("message_url", "Link da Mensagem")
    }

    override val options = Options()

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
                context.sendMessage {
                    content = builder.append("""
                                |```
                                |${message.content}
                                |```
                            """.trimMargin()
                    ).toString()
                }
            } else {
                context.sendMessage {
                    content = builder.toString()
                }

                val chunkedLines = StringUtils.chunkedLines(message.content.split("\n"), 2000, true)

                for (line in chunkedLines) {
                    context.sendMessage {
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
                context.sendMessage {
                    content = "Canal desconhecido!"
                }
            }

            if (e.error?.code == JsonErrorCode.UnknownMessage) {
                context.sendMessage {
                    content = "Mensagem desconhecida!"
                }
            }

            if (e.error?.code == JsonErrorCode.MissingAccess) {
                context.sendMessage {
                    content = "Não tenho permissão pra ver mensagens deste canal!"
                }
            }
        }
    }
}