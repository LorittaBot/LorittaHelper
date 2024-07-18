package net.perfectdreams.loritta.helper.interactions.commands.vanilla

import dev.minn.jda.ktx.messages.MessageCreate
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.*
import net.perfectdreams.loritta.helper.serverresponses.sparklypower.*
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.morenitta.interactions.commands.*
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions

class ReportMessageSenderCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "reportmessagesender",
        "Envia a mensagem de denúncias no canal selecionado"
    ) {
        executor = ReportMessageSenderExecutor()
    }

    inner class ReportMessageSenderExecutor : LorittaSlashCommandExecutor() {
        inner class Options : ApplicationCommandOptions() {
            val channel = channel("channel", "Canal aonde a mensagem será enviada")
        }

        override val options = Options()

        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.reply(true) {
                content = "As mensagens estão sendo enviadas! Segure firme!!"
            }

            val channel = args[options.channel] as MessageChannel

            channel.sendMessage(
                MessageCreate {
                    embed {
                        title = "<:lorota_jubinha:429715687317962772> Querendo Denunciar um Meliante?"

                        description = """<:nb:908745485353877545><:nde:908745430265896961> Alguém quebrou as regras da Loritta ou de algum dos servidores da DreamLand? Conta pra gente!""".trimMargin()

                        field(
                            "~~                                     ~~",
                            "<:lori_ban_hammer:741058240455901254> **Como denunciar?**\n\n<:nd:908743835285344276> **Clique no botão para começar** Ela vai te enviar um link para um formulário do Google, basta preencher e enviar. Simples assim! <:lori_coffee:727631176432484473>\n<:nb:908745485353877545><:nde:908745430265896961> **Lembre-se:** Nunca compartilhe o link do formulário ou o código com outros usuários! Não nos responsabilizamos caso você faça isso, pois você foi avisado.\n<:nb:908745485353877545><:nde:908745430265896961> **Confira** se o usuário realmente quebrou as regras da Loritta ([clique para ler](https://loritta.website/br/guidelines)) ou as regras do servidor (<#504032844394397697>).",
                            false
                        )

                        field(
                            "~~                                     ~~",
                            "<:lori_lurk:1012854272817381487> **Por que não posso denunciar direto pra um Staff ou no Suporte?**\n\n<:nd:908743835285344276> **A Equipe da Loritta aceita denúncias apenas via formulário** para manter uma organização e sempre ter um histórico do que aconteceu, evitando futuros problemas e facilitando resoluções. Além de ter um processo mais prático, pois pedimos todas as informações essenciais para a denúncia.\n<:nb:908745485353877545><:nde:908745430265896961> Não se preocupe, as denúncias são anônimas <:smol_gessy:593907632784408644>",
                            false
                        )

                        field(
                            "~~                                     ~~",
                            "<:nd:908743835285344276> Alguma dúvida? Mencione o cargo <@&399301696892829706> com a sua pergunta ou abra um ticket no <#1077726822160142386>!",
                            false
                        )

                        color = 2729726

                        actionRow(
                            Button.of(
                                ButtonStyle.PRIMARY,
                                "open_report_form:",
                                "Abrir Denúncia"
                            ).withEmoji(Emoji.fromUnicode("\uD83D\uDCDD"))
                        )
                    }
                }
            ).await()
        }
    }
}