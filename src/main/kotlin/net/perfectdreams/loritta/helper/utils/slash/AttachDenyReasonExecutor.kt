package net.perfectdreams.loritta.helper.utils.slash

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.helper.utils.generateserverreport.GenerateAppealsReport
import net.perfectdreams.loritta.helper.utils.generateserverreport.GenerateServerReport

class AttachDenyReasonExecutor(helper: LorittaHelper, val jda: JDA) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(AttachDenyReasonExecutor::class) {
        override val options = Options
        val VALID_CHANNEL_IDS = listOf(
            GenerateAppealsReport.SERVER_APPEALS_CHANNEL_ID,
            GenerateServerReport.SERVER_REPORTS_CHANNEL_ID
        )

        object Options : CommandOptions() {
            val messageUrl = string("message_url", "Link da Mensagem")
                .register()

            val reason = string("reason", "O motivo por qual a denúncia está sendo negada")
                .register()
        }
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        val messageUrl = args[options.messageUrl]
        val reason = args[options.reason]

        val split = messageUrl.split("/")

        val length = split.size
        val messageId = split[length - 1]
        val channelId = split[length - 2]

        try {
            if (channelId.toLong() !in VALID_CHANNEL_IDS) {
                context.sendMessage {
                    content= "${Emotes.LORI_THINKING} **|** O canal deste link não corresponde ao <#790308357713559582>, suspeito isso aí."
                }
                return
            }

            val message = try {
                jda.getTextChannelById(channelId)!!
                    .retrieveMessageById(messageId)
                    .await()
            } catch(e: Exception) {
                null
            }

            if (message == null) {
                context.sendMessage {
                    content = "${Emotes.LORI_SOB} **|** Talvez isto não seja uma mensagem válida..."
                }
                return
            }

            if (message.author.idLong != jda.selfUser.idLong) {
                context.sendMessage {
                    content = "${Emotes.LORI_THINKING} **|** Essa mensagem não é uma mensagem enviada por mim!"
                }
                return
            }

            val firstEmbed = message.embeds.firstOrNull()

            if (firstEmbed == null) {
                context.sendMessage {
                    content = "${Emotes.LORI_THINKING} **|** Essa mensagem não tem embed"
                }
                return
            }

            val builder = EmbedBuilder(firstEmbed)

            if (firstEmbed.fields.any { it.name == "Resposta da Staff" }) {
                val oldReason = firstEmbed.fields.find { it.name == "Resposta da Staff" }?.value
                val fields = firstEmbed.fields
                //Clear embed fields to re-add older fields again
                builder.clearFields()

                for (field in fields) {
                    if (field.name == "Resposta da Staff") {
                        builder.addField(field.name, reason, field.isInline)
                    } else {
                        builder.addField(field.name, field.value, field.isInline)
                    }
                }

                message.editMessageEmbeds(builder.build()).queue()
                context.sendMessage {
                    content = """${Emotes.LORI_PAT} **|** Motivo atualizado com sucesso!
                        |${Emotes.LORI_OWO} **|** Motivo anterior: `${oldReason}`
                        """.trimMargin()
                }
                return
            }


            builder.addField("Resposta da Staff", reason, false)

            message.editMessageEmbeds(builder.build()).queue()

            context.sendMessage {
                content = "${Emotes.LORI_COFFEE} **|** Motivo atualizado com sucesso!"
            }
        } catch (e: Exception) {
            context.sendMessage {
                content = "Alguma coisa deu errada ao processar este comando! Desculpe pelo ocorrido..."
            }
        }
    }
}