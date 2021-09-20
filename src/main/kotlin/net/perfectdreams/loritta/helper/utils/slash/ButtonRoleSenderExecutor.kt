package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.create.actionRow
import net.perfectdreams.discordinteraktions.common.components.interactiveButton
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleButtonData
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleButtonExecutor

class ButtonRoleSenderExecutor(helper: LorittaHelper) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(ButtonRoleSenderExecutor::class) {
        override val options = Options

        object Options : CommandOptions() {
            val channel = channel("channel", "Canal aonde a mensagem será enviada")
                .register()
        }
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.sendEphemeralMessage {
            content = "As mensagens estão sendo enviadas! Segure firme!!"
        }

        val channel = args[options.channel]

        helper.helperRest.channel.createMessage(channel.id) {
            content = "Escolha um cargo!"

            actionRow {
                interactiveButton(
                    ButtonStyle.Secondary,
                    RoleButtonExecutor,
                    ComponentDataUtils.encode(
                        RoleButtonData(
                            Snowflake(
                                334734175531696128L
                            )
                        )
                    )
                ) {
                    label = "Notificar Novidades"
                }
            }
        }
    }
}