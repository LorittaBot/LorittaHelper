package net.perfectdreams.loritta.helper.interactions.commands.vanilla

import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.LorittaLandGuild
import net.perfectdreams.loritta.helper.utils.buttonroles.LorittaCommunityRoleButtons
import net.perfectdreams.loritta.helper.utils.buttonroles.SparklyPowerRoleButtons
import net.perfectdreams.loritta.morenitta.interactions.commands.*
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import net.perfectdreams.loritta.morenitta.interactions.styled
import java.awt.Color

class ButtonRoleSenderCommand(val loritta: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand("buttonrolesender2", "Envia a mensagem de cargos no canal selecionado") {
        executor = ButtonRoleSenderCommandExecutor()
    }

    inner class ButtonRoleSenderCommandExecutor : LorittaSlashCommandExecutor() {
        inner class Options : ApplicationCommandOptions() {
            val channel = channel("channel", "Canal aonde a mensagem será enviada")
        }

        override val options = Options()

        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            if (context.guildId == null)
                return

            val channel = args[options.channel]
            val guildId = context.guildId!!

            context.reply(true) {
                styled(
                    "Enviando mensagem. Segure firme!!"
                )
            }

            when (guildId) {
                loritta.config.guilds.community.id -> {
                    // ===[ CUSTOM COLORS ]===
                    context.sendMessage(channel.idLong) {
                        embed {
                            title = "Cores Personalizadas"
                            color = Color(26, 160, 254).rgb

                            description = """Escolha uma cor personalizada para o seu nome e, de brinde, receba um ícone do lado do seu nome relacionado com a cor que você escolheu aqui no servidor da Loritta!
                    |
                    |**Apenas disponível para usuários Premium da Loritta (<@&364201981016801281>)!** Ficou interessado? Então [clique aqui](https://loritta.website/br/donate)!
                """.trimMargin()

                            thumbnail = "https://cdn.discordapp.com/attachments/358774895850815488/889932408177168394/gabriela_sortros_crop.png"
                        }

                        val chunkedRoleButtons = LorittaCommunityRoleButtons.colors.chunked(5)

                        for (actionRow in chunkedRoleButtons) {
                            actionRow(
                                actionRow.map {
                                    Button.of(
                                        ButtonStyle.SECONDARY,
                                        "color-${LorittaLandGuild.LORITTA_COMMUNITY}:${it.roleId}",
                                        it.emoji
                                    )
                                }
                            )
                        }
                    }

                    // ===[ CUSTOM ROLE ICONS ]===
                    context.sendMessage(channel.idLong) {
                        embed {
                            title = "Ícones Personalizados"
                            color = Color(26, 160, 254).rgb

                            description = """Escolha um ícone personalizado que irá aparecer ao lado do seu nome aqui no servidor da Loritta! O ícone personalizado irá substituir qualquer outro ícone que você possui!
                    |
                    |**Apenas disponível para usuários Premium da Loritta (<@&364201981016801281>) ou <@&655132411566358548>!** Ficou interessado? Então [clique aqui](https://loritta.website/br/donate)! Ou, se preferir, seja mais ativo no servidor para chegar no nível 10!
                """.trimMargin()

                            thumbnail = "https://cdn.discordapp.com/emojis/853048446974033960.png?v=1"
                        }

                        val chunkedRoleButtons = LorittaCommunityRoleButtons.coolBadges.chunked(5)

                        for (actionRow in chunkedRoleButtons) {
                            actionRow(
                                actionRow.map {
                                    Button.of(
                                        ButtonStyle.SECONDARY,
                                        "badge-${LorittaLandGuild.LORITTA_COMMUNITY}:${it.roleId}",
                                        it.emoji
                                    )
                                }
                            )
                        }
                    }

                    // ===[ NOTIFICATIONS ]===
                    context.sendMessage(channel.idLong) {
                        embed {
                            title = "Cargos de Notificações"
                            color = Color(26, 160, 254).rgb

                            description = buildString {
                                for (roleInfo in LorittaCommunityRoleButtons.notifications) {
                                    append("**")
                                    append(roleInfo.emoji.asMention)
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

                            thumbnail = "https://cdn.discordapp.com/emojis/640141673531441153.png?v=1"
                        }

                        actionRow(
                            LorittaCommunityRoleButtons.notifications.map {
                                Button.of(
                                    ButtonStyle.SECONDARY,
                                    "notif-${LorittaLandGuild.LORITTA_COMMUNITY}:${it.roleId}",
                                    it.label,
                                    it.emoji
                                )
                            }
                        )
                    }
                }
                loritta.config.guilds.sparklyPower.id -> {
                    // ===[ CUSTOM COLORS ]===
                    context.sendMessage(channel.idLong) {
                        embed {
                            title = "Cores Personalizadas"
                            color = Color(26, 160, 254).rgb

                            description = """Escolha uma cor personalizada para o seu nome e, de brinde, receba um ícone do lado do seu nome relacionado com a cor que você escolheu aqui no servidor da Loritta!
                    |
                    |**Apenas disponível para usuários VIPs (<@&332652664544428044>)!** Ficou interessado? Então [clique aqui](https://sparklypower.net/loja)!
                """.trimMargin()

                            thumbnail = "https://cdn.discordapp.com/attachments/358774895850815488/889932408177168394/gabriela_sortros_crop.png"
                        }

                        val chunkedRoleButtons = SparklyPowerRoleButtons.colors.chunked(5)

                        for (actionRow in chunkedRoleButtons) {
                            actionRow(
                                actionRow.map {
                                    Button.of(
                                        ButtonStyle.SECONDARY,
                                        "color-${LorittaLandGuild.SPARKLYPOWER}:${it.roleId}",
                                        it.emoji
                                    )
                                }
                            )
                        }
                    }

                    // ===[ CUSTOM ROLE ICONS ]===
                    context.sendMessage(channel.idLong) {
                        embed {
                            title = "Ícones Personalizados"
                            color = Color(26, 160, 254).rgb

                            description = """Escolha um ícone personalizado que irá aparecer ao lado do seu nome aqui no servidor do SparklyPower! O ícone personalizado irá substituir qualquer outro ícone que você possui!
                    |
                    |**Apenas disponível para usuários VIPs (<@&332652664544428044>) ou <@&834625069321551892>!** Ficou interessado? Então [clique aqui](https://sparklypower.net/loja)! Ou, se preferir, seja mais ativo no servidor para chegar no nível 10!
                """.trimMargin()

                            thumbnail = "https://cdn.discordapp.com/emojis/853048446974033960.png?v=1"
                        }

                        val chunkedRoleButtons = SparklyPowerRoleButtons.coolBadges.chunked(5)

                        for (actionRow in chunkedRoleButtons) {
                            actionRow(
                                actionRow.map {
                                    Button.of(
                                        ButtonStyle.SECONDARY,
                                        "badge-${LorittaLandGuild.SPARKLYPOWER}:${it.roleId}",
                                        it.emoji
                                    )
                                }
                            )
                        }
                    }

                    // ===[ NOTIFICATIONS ]===
                    context.sendMessage(channel.idLong) {
                        embed {
                            title = "Cargos de Notificações"
                            color = Color(26, 160, 254).rgb

                            description = buildString {
                                for (roleInfo in SparklyPowerRoleButtons.notifications) {
                                    append("**")
                                    append(roleInfo.emoji.asMention)
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

                            thumbnail = "https://cdn.discordapp.com/emojis/640141673531441153.png?v=1"
                        }

                        actionRow(
                            SparklyPowerRoleButtons.notifications.map {
                                Button.of(
                                    ButtonStyle.SECONDARY,
                                    "notif-${LorittaLandGuild.SPARKLYPOWER}:${it.roleId}",
                                    it.label,
                                    it.emoji
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}