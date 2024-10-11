package net.perfectdreams.loritta.helper.interactions.commands.vanilla

import dev.minn.jda.ktx.interactions.components.SelectOption
import dev.minn.jda.ktx.interactions.components.StringSelectMenu
import dev.minn.jda.ktx.messages.MessageCreate
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.listeners.ComponentInteractionListener
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.AddLoriResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.JoinLeaveResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.LoriMandarCmdsResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.LoriXpResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.MuteResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.english.SparklyPowerInfoResponse
import net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.*
import net.perfectdreams.loritta.helper.serverresponses.sparklypower.*
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.extensions.await
import net.perfectdreams.loritta.helper.utils.tickets.TicketSystemTypeData
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import net.perfectdreams.loritta.helper.utils.tickets.systems.FirstFanArtTicketSystem
import net.perfectdreams.loritta.helper.utils.tickets.systems.HelpDeskTicketSystem
import net.perfectdreams.loritta.morenitta.interactions.commands.*
import net.perfectdreams.loritta.morenitta.interactions.commands.options.ApplicationCommandOptions
import java.awt.Color

class TicketSenderCommand(val helper: LorittaHelper) : SlashCommandDeclarationWrapper {
    override fun command() = slashCommand(
        "ticketsender",
        "Envia a mensagem de tickets no canal selecionado"
    ) {
        executor = TicketSenderExecutor()
    }

    inner class TicketSenderExecutor : LorittaSlashCommandExecutor() {
        inner class Options : ApplicationCommandOptions() {
            val channel = channel("channel", "Canal aonde a mensagem será enviada")

            val type = string("type", "O tipo da mensagem") {
                choice("Suporte (Inglês)", TicketUtils.TicketSystemType.HELP_DESK_ENGLISH.name)
                choice("Suporte (Português)", TicketUtils.TicketSystemType.HELP_DESK_PORTUGUESE.name)
                choice("Primeira Fan Art (Português)", TicketUtils.TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE.name)
                choice(
                    "SparklyPower Suporte (Português)",
                    TicketUtils.TicketSystemType.SPARKLYPOWER_HELP_DESK_PORTUGUESE.name
                )
            }
        }

        override val options = Options()

        override suspend fun execute(context: ApplicationCommandContext, args: SlashCommandArguments) {
            context.reply(true) {
                content = "As mensagens estão sendo enviadas! Segure firme!!"
            }

            val channel = args[options.channel] as MessageChannel
            val ticketSystemType = TicketUtils.TicketSystemType.valueOf(args[options.type])
            val systemInfo = helper.ticketUtils.getSystemBySystemType(ticketSystemType)
            val i18nContext = systemInfo.getI18nContext(helper.languageManager)

            if (systemInfo is HelpDeskTicketSystem) {
                if (systemInfo.systemType == TicketUtils.TicketSystemType.SPARKLYPOWER_HELP_DESK_PORTUGUESE) {
                    channel.sendMessage(
                        MessageCreate {
                            embed {
                                title = "<:pantufa_reading:853048447169986590> Central de Ajuda"
                                color = Color(26, 160, 254).rgb

                                description =
                                    """Seja bem-vind@ a Central de Ajuda do SparklyPower! Um lugar onde você pode encontrar as respostas para as suas perguntas, desde que elas sejam relacionadas ao SparklyPower, é claro!

Antes de perguntar, verifique se a resposta dela não está no <#${systemInfo.faqChannelId}>! Se você irá perguntar se o SparklyPower caiu, veja as <#332866197701918731> primeiro!
                                """.trimMargin()

                                image =
                                    "https://cdn.discordapp.com/attachments/691041345275691021/996989791054876692/Support_System_v3.png"
                            }
                        }
                    ).await()

                    channel.sendMessage(
                        MessageCreate {
                            content = LorittaReply(
                                i18nContext.get(I18nKeysData.Tickets.SelectYourQuestion),
                                "<:pantufa_reading:853048447169986590>"
                            ).build()

                            actionRow(
                                StringSelectMenu(
                                    "helper_response",
                                    i18nContext.get(
                                        I18nKeysData.Tickets.ClickToFindYourQuestion
                                    ),
                                    options = listOf(
                                        SelectOption(
                                            "IP, porta do servidor e versão",
                                            ServerInformationResponse::class.simpleName!!
                                        ),
                                        SelectOption(
                                            "IP, porta do servidor e versão",
                                            ServerInformationResponse::class.simpleName!!
                                        ),
                                        SelectOption(
                                            "Como se registrar?",
                                            HowToRegisterResponse::class.simpleName!!
                                        ),
                                        SelectOption(
                                            "Como conseguir sonhos/sonecas?",
                                            HowToEarnSonecasResponse::class.simpleName!!
                                        ),
                                        SelectOption(
                                            "Como transferir os sonhos?",
                                            HowToTransferSonhosResponse::class.simpleName!!
                                        ),
                                        SelectOption(
                                            "Como conseguir pesadelos?",
                                            HowToEarnPesadelosResponse::class.simpleName!!
                                        ),
                                        SelectOption(
                                            "Como redefinir a senha no servidor?",
                                            HowToResetPasswordResponse::class.simpleName!!
                                        ),
                                        SelectOption(
                                            "Como comprar pesadelos?",
                                            HowToBuyPesadelosResponse::class.simpleName!!
                                        )
                                    )
                                )
                            )

                            actionRow(
                                Button.of(
                                    net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.PRIMARY,
                                    "create_ticket:${
                                        ComponentDataUtils.encode(
                                            TicketSystemTypeData(systemInfo.systemType)
                                        )
                                    }",
                                    i18nContext.get(I18nKeysData.Tickets.CreateTicket)
                                ).withEmoji(Emoji.fromUnicode("➕"))
                            )
                        }
                    ).await()
                } else {
                    val faqChannelId = systemInfo.faqChannelId
                    val statusChannelId = systemInfo.statusChannelId

                    channel.sendMessage(
                        MessageCreate {
                            embed {
                                title = i18nContext.get(I18nKeysData.Tickets.LorittaHelpDesk)
                                color = Color(26, 160, 254).rgb

                                description = i18nContext.get(
                                    I18nKeysData.Tickets.HelpDeskDescription(
                                        "<#${faqChannelId}>",
                                        "<https://loritta.website/extras>",
                                        "<#${statusChannelId}>"
                                    )
                                ).joinToString("\n")

                                image = when (systemInfo.language) {
                                    TicketUtils.LanguageName.PORTUGUESE -> "https://cdn.discordapp.com/attachments/358774895850815488/891833452457000980/Support_System_Portuguese.png"
                                    TicketUtils.LanguageName.ENGLISH -> "https://cdn.discordapp.com/attachments/358774895850815488/891831248106963044/Support_System_English.png"
                                }
                            }
                        }
                    ).await()

                    channel.sendMessage(
                        MessageCreate {
                            content = LorittaReply(
                                i18nContext.get(I18nKeysData.Tickets.SelectYourQuestion),
                                "<:lori_reading:853052040430878750>"
                            ).build()

                            actionRow(
                                StringSelectMenu(
                                    "helper_response",
                                    i18nContext.get(
                                        I18nKeysData.Tickets.ClickToFindYourQuestion
                                    ),

                                    options = when (systemInfo.language) {
                                        TicketUtils.LanguageName.PORTUGUESE -> {
                                            listOf(
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowToAddLorittaToMyServer
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.AddLoriResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.WhyLorittaIsOffline
                                                    ),
                                                    LoriOfflineResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.LoriIsNotReplyingToMyCommands
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.LoriMandarCmdsResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowToSetupJoinLeaveMessages
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.JoinLeaveResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowLorittaExperienceSystemWorks
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.LoriXpResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowToEarnSonhos
                                                    ),
                                                    ReceiveSonhosResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.WhatCanIDoWithSonhos
                                                    ),
                                                    CanIExchangeSonhosForSomethingElseResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.DailyDoesNotWork
                                                    ),
                                                    DailyCaptchaDoesNotWorkResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.LorittaPremiumFeatures
                                                    ),
                                                    LorittaPremiumResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowMuteWorks
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.MuteResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.WhatAreReputations
                                                    ),
                                                    ReputationsResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.ChangeShipValue
                                                    ),
                                                    ValorShipResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowToReportAnotherUser
                                                    ),
                                                    HowDoIReportResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.WhatIsSparklyPower
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.SparklyPowerInfoResponse::class.simpleName!!
                                                ),

                                                // ===[ SPECIAL CASE ]===
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.CreateSupportTicket
                                                    ),
                                                    ComponentInteractionListener.MY_QUESTION_ISNT_HERE_SPECIAL_KEY,
                                                    emoji = Emoji.fromCustom("sad_cat18", 648695501398605825, false)
                                                )
                                            )
                                        }

                                        TicketUtils.LanguageName.ENGLISH -> {
                                            listOf(
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowToAddLorittaToMyServer
                                                    ),
                                                    AddLoriResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.WhyLorittaIsOffline
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.loritta.english.LoriOfflineResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.LoriIsNotReplyingToMyCommands
                                                    ),
                                                    LoriMandarCmdsResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowToSetupJoinLeaveMessages
                                                    ),
                                                    JoinLeaveResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowLorittaExperienceSystemWorks
                                                    ),
                                                    LoriXpResponse::class.simpleName!!
                                                ),
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowToEarnSonhos
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.loritta.english.ReceiveSonhosResponse::class.simpleName!!
                                                ),
                                                /* SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.WhatCanIDoWithSonhos
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.english.CanIExchangeSonhosForSomethingElseResponse::class.simpleName!!
                                                )
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.DailyDoesNotWork
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.english.DailyCaptchaDoesNotWorkResponse::class.simpleName!!
                                                )
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.LorittaPremiumFeatures
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.english.LorittaPremiumResponse::class.simpleName!!
                                                ) */
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowMuteWorks
                                                    ),
                                                    MuteResponse::class.simpleName!!
                                                ),
                                                /* SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.WhatAreReputations
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.english.ReputationsResponse::class.simpleName!!
                                                ) */
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.ChangeShipValue
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.loritta.english.ValorShipResponse::class.simpleName!!
                                                ),
                                                /* SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.HowToReportAnotherUser
                                                    ),
                                                    net.perfectdreams.loritta.helper.serverresponses.english.HowDoIReportResponse::class.simpleName!!
                                                ) */
                                                SelectOption(
                                                    i18nContext.get(
                                                        I18nKeysData.Tickets.Menu.WhatIsSparklyPower
                                                    ),
                                                    SparklyPowerInfoResponse::class.simpleName!!
                                                )
                                            )
                                        }
                                    }
                                )
                            )

                            actionRow(
                                Button.of(
                                    net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.PRIMARY,
                                    "create_ticket:${
                                        ComponentDataUtils.encode(
                                            TicketSystemTypeData(systemInfo.systemType)
                                        )
                                    }",
                                    i18nContext.get(I18nKeysData.Tickets.CreateTicket)
                                ).withEmoji(Emoji.fromUnicode("➕"))
                            )
                        }
                    ).await()
                }
            } else if (systemInfo is FirstFanArtTicketSystem) {
                val rulesChannelId = systemInfo.fanArtRulesChannelId

                channel.sendMessage(
                    MessageCreate {
                        embed {
                            title = "${Emotes.LORI_HEART} Enviar Primeira Fan Art"
                            color = Color(26, 160, 254).rgb

                            description =
                                """Quer enviar uma fan art da Loritta e receber um cargo especial de Desenhista?
                                    |
                                    |Então você veio ao lugar certo! Aqui você poderá enviar todas as suas maravilhosas fan-arts, basta apenas clicar no botão abaixo para criar um ticket
                                    |
                                    |**Mas lembre-se!** Não iremos aprovar fan-arts mal feitas ou que não estejam de acordo com as regras em <#${rulesChannelId}>!
                                """.trimMargin()

                            image = "https://loritta.website/v3/assets/img/faq/fanarts/banner.png"
                        }

                        actionRow(
                            Button.of(
                                net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle.PRIMARY,
                                "create_ticket:${
                                    ComponentDataUtils.encode(
                                        TicketSystemTypeData(systemInfo.systemType)
                                    )
                                }",
                                i18nContext.get(I18nKeysData.Tickets.CreateTicket)
                            ).withEmoji(Emoji.fromUnicode("➕"))
                        )
                    }
                ).await()
            }
        }
    }
}