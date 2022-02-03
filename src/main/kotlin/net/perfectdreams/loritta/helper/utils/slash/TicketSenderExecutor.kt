package net.perfectdreams.loritta.helper.utils.slash

import dev.kord.common.Color
import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.create.actionRow
import dev.kord.rest.builder.message.create.embed
import net.perfectdreams.discordinteraktions.common.commands.ApplicationCommandContext
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.components.interactiveButton
import net.perfectdreams.discordinteraktions.common.components.selectMenu
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import net.perfectdreams.loritta.helper.utils.Emotes
import net.perfectdreams.loritta.helper.utils.tickets.CreateTicketButtonExecutor
import net.perfectdreams.loritta.helper.utils.tickets.HelperResponseSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.tickets.TicketSystemTypeData
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils

class TicketSenderExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    companion object : SlashCommandExecutorDeclaration(TicketSenderExecutor::class) {
        object Options : ApplicationCommandOptions() {
            val channel = channel("channel", "Canal aonde a mensagem será enviada")
                .register()

            val type = string("type", "O tipo da mensagem")
                .choice(TicketUtils.TicketSystemType.HELP_DESK_ENGLISH.name, "Suporte (Inglês)")
                .choice(TicketUtils.TicketSystemType.HELP_DESK_PORTUGUESE.name, "Suporte (Português)")
                .choice(TicketUtils.TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE.name, "Primeira Fan Art (Português)")
                .register()
        }

        override val options = Options
    }

    override suspend fun executeHelper(context: ApplicationCommandContext, args: SlashCommandArguments) {
        context.sendEphemeralMessage {
            content = "As mensagens estão sendo enviadas! Segure firme!!"
        }

        val channel = args[options.channel]
        val ticketSystemType = TicketUtils.TicketSystemType.valueOf(args[options.type])
        val systemInfo = TicketUtils.getInformationBySystemType(ticketSystemType)
        val i18nContext = systemInfo.getI18nContext(helper.languageManager)

        if (systemInfo is TicketUtils.HelpDeskTicketSystemInformation) {
            val faqChannelId = systemInfo.faqChannelId
            val statusChannelId = systemInfo.statusChannelId

            helper.helperRest.channel.createMessage(channel.id) {
                embed {
                    title = i18nContext.get(I18nKeysData.Tickets.LorittaHelpDesk)
                    color = Color(26, 160, 254)

                    description = i18nContext.get(
                        I18nKeysData.Tickets.HelpDeskDescription(
                            "<#${faqChannelId.value}>",
                            "<https://loritta.website/extras>",
                            "<#${statusChannelId.value}>"
                        )
                    ).joinToString("\n")

                    image = when (systemInfo.language) {
                        TicketUtils.LanguageName.PORTUGUESE -> "https://cdn.discordapp.com/attachments/358774895850815488/891833452457000980/Support_System_Portuguese.png"
                        TicketUtils.LanguageName.ENGLISH -> "https://cdn.discordapp.com/attachments/358774895850815488/891831248106963044/Support_System_English.png"
                    }
                }
            }

            helper.helperRest.channel.createMessage(channel.id) {
                content = LorittaReply(
                    i18nContext.get(I18nKeysData.Tickets.SelectYourQuestion),
                    "<:lori_reading:853052040430878750>"
                ).build()

                actionRow {
                    selectMenu(HelperResponseSelectMenuExecutor) {
                        placeholder = i18nContext.get(
                            I18nKeysData.Tickets.ClickToFindYourQuestion
                        )

                        when (systemInfo.language) {
                            TicketUtils.LanguageName.PORTUGUESE -> {
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowToAddLorittaToMyServer
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.AddLoriResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.WhyLorittaIsOffline
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.LoriOfflineResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.LoriIsNotReplyingToMyCommands
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.LoriMandarCmdsResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowToSetupJoinLeaveMessages
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.JoinLeaveResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowLorittaExperienceSystemWorks
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.LoriXpResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowToEarnSonhos
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.ReceiveSonhosResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.WhatCanIDoWithSonhos
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.CanIExchangeSonhosForSomethingElseResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.DailyDoesNotWork
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.DailyCaptchaDoesNotWorkResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.LorittaPremiumFeatures
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.LorittaPremiumResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowMuteWorks
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.MuteResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.WhatAreReputations
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.ReputationsResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.ChangeShipValue
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.ValorShipResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowToReportAnotherUser
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.HowDoIReportResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.WhatIsSparklyPower
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.portuguese.SparklyPowerInfoResponse::class.simpleName!!
                                )

                                // ===[ SPECIAL CASE ]===
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.MyQuestionIsntHere
                                    ),
                                    HelperResponseSelectMenuExecutor.MY_QUESTION_ISNT_HERE_SPECIAL_KEY
                                ) {
                                    emoji = DiscordPartialEmoji(Snowflake(648695501398605825), "sad_cat18")
                                }
                            }
                            TicketUtils.LanguageName.ENGLISH -> {
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowToAddLorittaToMyServer
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.AddLoriResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.WhyLorittaIsOffline
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.LoriOfflineResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.LoriIsNotReplyingToMyCommands
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.LoriMandarCmdsResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowToSetupJoinLeaveMessages
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.JoinLeaveResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowLorittaExperienceSystemWorks
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.LoriXpResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowToEarnSonhos
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.ReceiveSonhosResponse::class.simpleName!!
                                )
                                /* option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.WhatCanIDoWithSonhos
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.CanIExchangeSonhosForSomethingElseResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.DailyDoesNotWork
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.DailyCaptchaDoesNotWorkResponse::class.simpleName!!
                                )
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.LorittaPremiumFeatures
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.LorittaPremiumResponse::class.simpleName!!
                                ) */
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowMuteWorks
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.MuteResponse::class.simpleName!!
                                )
                                /* option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.WhatAreReputations
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.ReputationsResponse::class.simpleName!!
                                ) */
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.ChangeShipValue
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.ValorShipResponse::class.simpleName!!
                                )
                                /* option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.HowToReportAnotherUser
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.HowDoIReportResponse::class.simpleName!!
                                ) */
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.WhatIsSparklyPower
                                    ),
                                    net.perfectdreams.loritta.helper.serverresponses.english.SparklyPowerInfoResponse::class.simpleName!!
                                )

                                // ===[ SPECIAL CASE ]===
                                option(
                                    i18nContext.get(
                                        I18nKeysData.Tickets.Menu.MyQuestionIsntHere
                                    ),
                                    HelperResponseSelectMenuExecutor.MY_QUESTION_ISNT_HERE_SPECIAL_KEY
                                ) {
                                    emoji = DiscordPartialEmoji(Snowflake(648695501398605825), "sad_cat18")
                                }
                            }
                        }
                    }
                }
            }
        } else if (systemInfo is TicketUtils.FirstFanArtTicketSystemInformation) {
            val rulesChannelId = systemInfo.fanartRulesChannelId

            helper.helperRest.channel.createMessage(channel.id) {
                embed {
                    title = i18nContext.get("${Emotes.LORI_HEART} Enviar Primeira Fan-Art")
                    color = Color(26, 160, 254)

                    description = """Quer enviar uma fan-art da Loritta e receber um cargo especial de Desenhista?
                                    |
                                    |Então você veio ao lugar certo! Aqui você poderá enviar todas as suas maravilhosas fan-arts, basta apenas clicar no botão abaixo para criar um ticket
                                    |
                                    |**Mas lembre-se!** Não iremos aprovar fan-arts mal feitas ou que não estejam de acordo com as regras em <#${fanartRulesChannelId}>!
                                """.trimMargin()
                }

                actionRow {
                    interactiveButton(
                        ButtonStyle.Primary,
                        CreateTicketButtonExecutor,
                        ComponentDataUtils.encode(
                            TicketSystemTypeData(systemInfo.systemType)
                        )
                    ) {
                        emoji = DiscordPartialEmoji(name = "➕")
                        label = i18nContext.get(I18nKeysData.Tickets.CreateTicket)
                    }
                }
            }
        }
    }
}