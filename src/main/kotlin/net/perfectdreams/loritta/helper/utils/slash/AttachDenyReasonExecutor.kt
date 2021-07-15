package net.perfectdreams.loritta.helper.utils.slash

import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.context.commands.SlashCommandContext
import net.perfectdreams.discordinteraktions.declarations.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.LorittaHelper
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA

class AttachDenyReasonExecutor(helper: LorittaHelper, val jda: JDA) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(AttachDenyReasonExecutor::class) {
        override val options = Options
        const val SERVER_REPORTS_CHANNEL_ID = "790308357713559582"

        object Options : CommandOptions() {
            val messageUrl = string("message_url", "Link da Mensagem")
                .register()
            
            val reason = string("reason", "O motivo por qual a denúncia está sendo negada")
                .register()
        }
    }

    override suspend fun executeHelper(context: SlashCommandContext, args: SlashCommandArguments) {
        val messageUrl = args[options.messageUrl]
        val reason = args[options.reason]

        val split = messageUrl.split("/")
        var channelId = SERVER_REPORTS_CHANNEL_ID
        var messageId = messageUrl

        if (split.size >= 2) {
            val length = split.size
            messageId = split[length - 1]
            channelId = split[length - 2]
        }

        try {
            if (channelId != SERVER_REPORTS_CHANNEL_ID) {
                context.sendMessage {
                    content= "${Emotes.LORI_THINKING} **|** O canal deste link não corresponde ao <#790308357713559582>, suspeito isso aí."
                }
                return
            }

            var message = try {
                jda.getTextChannelById(channelId)!!
                    .retrieveMessageById(messageId)
                    .await()
            } catch(e: Exception) {
                null
            }

            if (message == null) {
                context.sendMessage {
                    content = "${Emotes.LORI_SOB} **|** Talvez isto não seja uma mensagem minha..."
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
            
            if (firstEmbed.fields == null) {
                context.sendMessage {
                    content = "${Emotes.LORI_THINKING} **|** Esse embed não tem fields! Creio que não seja um embed de denúncias..."
                }
                return
            }
            
            val builder = EmbedBuilder(firstEmbed!!)
            builder.addField("Resposta da Staff", reason, false)
            
            message.editMessage(builder.build()).queue()
            
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
