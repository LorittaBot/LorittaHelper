package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.rest.builder.message.create.actionRow
import dev.kord.rest.builder.message.create.embed
import net.perfectdreams.discordinteraktions.common.components.interactiveButton
import net.perfectdreams.discordinteraktions.common.context.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.context.commands.slash.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.utils.thumbnailUrl
import net.perfectdreams.discordinteraktions.declarations.commands.slash.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.declarations.commands.slash.options.CommandOptions
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleButtonData
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleButtons
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleButtons.partialEmojiAsMention
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleColorButtonExecutor
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleCoolBadgeButtonExecutor
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleToggleButtonExecutor

class ButtonRoleSenderExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper) {
    companion object : SlashCommandExecutorDeclaration(ButtonRoleSenderExecutor::class) {
        object Options : CommandOptions() {
            val channel = channel("channel", "Canal aonde a mensagem será enviada")
                .register()
        }

        override val options = Options
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.sendEphemeralMessage {
            content = "As mensagens estão sendo enviadas! Segure firme!!"
        }

        val channel = args[options.channel]

        // ===[ CUSTOM COLORS ]===
        helper.helperRest.channel.createMessage(channel.id) {
            embed {
                title = "Cores Personalizadas"
                color = Color(26, 160, 254)

                description = """Escolha uma cor personalizada para o seu nome e, de brinde, receba um ícone do lado do seu nome relacionado com a cor que você escolheu aqui no servidor da Loritta!
                    |
                    |**Apenas disponível para usuários Premium da Loritta (<@&364201981016801281>)!** Ficou interessado? Então [clique aqui](https://loritta.website/br/donate)!
                """.trimMargin()

                thumbnailUrl = "https://cdn.discordapp.com/attachments/358774895850815488/889932408177168394/gabriela_sortros_crop.png"
            }

            // We can only fit 5 buttons per action row!
            val chunkedRoleButtons = RoleButtons.colors.chunked(5)

            for (roleList in chunkedRoleButtons) {
                actionRow {
                    for (roleInfo in roleList) {
                        interactiveButton(
                            ButtonStyle.Secondary,
                            RoleColorButtonExecutor,
                            ComponentDataUtils.encode(
                                RoleButtonData(roleInfo.roleId)
                            )
                        ) {
                            emoji = roleInfo.emoji
                        }
                    }
                }
            }
        }

        // ===[ CUSTOM ROLE ICONS ]===
        helper.helperRest.channel.createMessage(channel.id) {
            embed {
                title = "Ícones Personalizados"
                color = Color(26, 160, 254)

                description = """Escolha um ícone personalizado que irá aparecer ao lado do seu nome aqui no servidor da Loritta! O ícone personalizado irá substituir qualquer outro ícone que você possui!
                    |
                    |**Apenas disponível para usuários Premium da Loritta (<@&364201981016801281>) ou <@&655132411566358548>!** Ficou interessado? Então [clique aqui](https://loritta.website/br/donate)! Ou, se preferir, seja mais ativo no servidor para chegar no nível 10!
                """.trimMargin()

                thumbnailUrl = "https://cdn.discordapp.com/emojis/853048446974033960.png?v=1"
            }

            // We can only fit 5 buttons per action row!
            val chunkedRoleButtons = RoleButtons.coolBadges.chunked(5)

            for (roleList in chunkedRoleButtons) {
                actionRow {
                    for (roleInfo in roleList) {
                        interactiveButton(
                            ButtonStyle.Secondary,
                            RoleCoolBadgeButtonExecutor,
                            ComponentDataUtils.encode(
                                RoleButtonData(roleInfo.roleId)
                            )
                        ) {
                            emoji = roleInfo.emoji
                        }
                    }
                }
            }
        }

        // ===[ NOTIFICATIONS ]===
        helper.helperRest.channel.createMessage(channel.id) {
            embed {
                title = "Cargos de Notificações"
                color = Color(26, 160, 254)

                description = buildString {
                    for (roleInfo in RoleButtons.notifications) {
                        append("**")
                        append(partialEmojiAsMention(roleInfo.emoji))
                        append(' ')
                        append(roleInfo.label)
                        append(':')
                        append("**")
                        append(' ')
                        append(roleInfo.description)
                        append('\n')
                        append('\n')
                    }
                }

                thumbnailUrl = "https://cdn.discordapp.com/emojis/640141673531441153.png?v=1"
            }

            actionRow {
                for (roleInfo in RoleButtons.notifications) {
                    interactiveButton(
                        ButtonStyle.Secondary,
                        RoleToggleButtonExecutor,
                        ComponentDataUtils.encode(
                            RoleButtonData(roleInfo.roleId)
                        )
                    ) {
                        label = roleInfo.label
                        emoji = roleInfo.emoji
                    }
                }
            }
        }
    }
}