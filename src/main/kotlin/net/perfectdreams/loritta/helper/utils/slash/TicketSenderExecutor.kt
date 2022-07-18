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
import net.perfectdreams.loritta.helper.utils.tickets.CreateTicketButtonExecutor
import net.perfectdreams.loritta.helper.utils.tickets.HelperResponseSelectMenuExecutor
import net.perfectdreams.loritta.helper.utils.tickets.TicketSystemTypeData
import net.perfectdreams.loritta.helper.utils.tickets.TicketUtils
import net.perfectdreams.loritta.helper.utils.tickets.systems.FirstFanArtTicketSystem
import net.perfectdreams.loritta.helper.utils.tickets.systems.HelpDeskTicketSystem

class TicketSenderExecutor(helper: LorittaHelperKord) : HelperSlashExecutor(helper, PermissionLevel.ADMIN) {
    companion object : SlashCommandExecutorDeclaration(TicketSenderExecutor::class) {
        object Options : ApplicationCommandOptions() {
            val channel = channel("channel", "Canal aonde a mensagem será enviada")
                .register()

            val type = string("type", "O tipo da mensagem")
                .choice(TicketUtils.TicketSystemType.HELP_DESK_ENGLISH.name, "Suporte (Inglês)")
                .choice(TicketUtils.TicketSystemType.HELP_DESK_PORTUGUESE.name, "Suporte (Português)")
                .choice(TicketUtils.TicketSystemType.FIRST_FAN_ARTS_PORTUGUESE.name, "Primeira Fan Art (Português)")
                .choice(TicketUtils.TicketSystemType.SPARKLYPOWER_HELP_DESK_PORTUGUESE.name, "SparklyPower Suporte (Português)")
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
        val systemInfo = helper.ticketUtils.getSystemBySystemType(ticketSystemType)
        val i18nContext = systemInfo.getI18nContext(helper.languageManager)

        if (systemInfo is HelpDeskTicketSystem) {
            if (systemInfo.systemType == TicketUtils.TicketSystemType.SPARKLYPOWER_HELP_DESK_PORTUGUESE) {
                helper.helperRest.channel.createMessage(channel.id) {
                    embed {
                        title = "<:pantufa_reading:853048447169986590> Central de Ajuda"
                        color = Color(26, 160, 254)

                        description = """Seja bem-vind@ a Central de Ajuda do SparklyPower! Um lugar onde você pode encontrar as respostas para as suas perguntas, desde que elas sejam relacionadas ao SparklyPower, é claro!

Antes de perguntar, verifique se a resposta dela não está no <#${systemInfo.faqChannelId}>! Se você irá perguntar se ao SparklyPower caiu, veja as <#332866197701918731> primeiro!
                                """.trimMargin()

                        image =
                            "https://cdn.discordapp.com/attachments/691041345275691021/996989791054876692/Support_System_v3.png"
                    }
                }

                helper.helperRest.channel.createMessage(channel.id) {
                    content = LorittaReply(
                        i18nContext.get(I18nKeysData.Tickets.SelectYourQuestion),
                        "<:pantufa_reading:853048447169986590>"
                    ).build()

                    actionRow {
                        selectMenu(HelperResponseSelectMenuExecutor) {
                            placeholder = i18nContext.get(
                                I18nKeysData.Tickets.ClickToFindYourQuestion
                            )

                            option(
                                "IP, porta do servidor e versão",
                                ServerInformationResponse::class.simpleName!!
                            )
                            option(
                                "Como se registrar?",
                                HowToRegisterResponse::class.simpleName!!
                            )
                            option(
                                "Como conseguir sonhos/sonecas?",
                                HowToEarnSonecasResponse::class.simpleName!!
                            )
                            option(
                                "Como transferir os sonhos?",
                                HowToTransferSonhosResponse::class.simpleName!!
                            )
                            option(
                                "Como conseguir pesadelos?",
                                HowToEarnPesadelosResponse::class.simpleName!!
                            )
                            option(
                                "Como redefinir a senha no servidor?",
                                HowToResetPasswordResponse::class.simpleName!!
                            )
                            option(
                                "Como comprar pesadelos?",
                                HowToBuyPesadelosResponse::class.simpleName!!
                            )

                            // ===[ SPECIAL CASE ]===
                            option(
                                i18nContext.get(
                                    I18nKeysData.Tickets.Menu.CreateSupportTicket
                                ),
                                HelperResponseSelectMenuExecutor.MY_QUESTION_ISNT_HERE_SPECIAL_KEY
                            ) {
                                emoji = DiscordPartialEmoji(Snowflake(648695501398605825), "sad_cat18")
                            }
                        }
                    }
                }
            } else {
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
                                        net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.AddLoriResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.WhyLorittaIsOffline
                                        ),
                                        LoriOfflineResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.LoriIsNotReplyingToMyCommands
                                        ),
                                        net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.LoriMandarCmdsResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.HowToSetupJoinLeaveMessages
                                        ),
                                        net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.JoinLeaveResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.HowLorittaExperienceSystemWorks
                                        ),
                                        net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.LoriXpResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.HowToEarnSonhos
                                        ),
                                        ReceiveSonhosResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.WhatCanIDoWithSonhos
                                        ),
                                        CanIExchangeSonhosForSomethingElseResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.DailyDoesNotWork
                                        ),
                                        DailyCaptchaDoesNotWorkResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.LorittaPremiumFeatures
                                        ),
                                        LorittaPremiumResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.HowMuteWorks
                                        ),
                                        net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.MuteResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.WhatAreReputations
                                        ),
                                        ReputationsResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.ChangeShipValue
                                        ),
                                        ValorShipResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.HowToReportAnotherUser
                                        ),
                                        HowDoIReportResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.WhatIsSparklyPower
                                        ),
                                        net.perfectdreams.loritta.helper.serverresponses.loritta.portuguese.SparklyPowerInfoResponse::class.simpleName!!
                                    )

                                    // ===[ SPECIAL CASE ]===
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.CreateSupportTicket
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
                                        AddLoriResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.WhyLorittaIsOffline
                                        ),
                                        net.perfectdreams.loritta.helper.serverresponses.loritta.english.LoriOfflineResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.LoriIsNotReplyingToMyCommands
                                        ),
                                        LoriMandarCmdsResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.HowToSetupJoinLeaveMessages
                                        ),
                                        JoinLeaveResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.HowLorittaExperienceSystemWorks
                                        ),
                                        LoriXpResponse::class.simpleName!!
                                    )
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.HowToEarnSonhos
                                        ),
                                        net.perfectdreams.loritta.helper.serverresponses.loritta.english.ReceiveSonhosResponse::class.simpleName!!
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
                                        MuteResponse::class.simpleName!!
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
                                        net.perfectdreams.loritta.helper.serverresponses.loritta.english.ValorShipResponse::class.simpleName!!
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
                                        SparklyPowerInfoResponse::class.simpleName!!
                                    )

                                    // ===[ SPECIAL CASE ]===
                                    option(
                                        i18nContext.get(
                                            I18nKeysData.Tickets.Menu.CreateSupportTicket
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
            }
        } else if (systemInfo is FirstFanArtTicketSystem) {
            val rulesChannelId = systemInfo.fanArtRulesChannelId

            helper.helperRest.channel.createMessage(channel.id) {
                embed {
                    title = "${Emotes.LORI_HEART} Enviar Primeira Fan Art"
                    color = Color(26, 160, 254)

                    description = """Quer enviar uma fan art da Loritta e receber um cargo especial de Desenhista?
                                    |
                                    |Então você veio ao lugar certo! Aqui você poderá enviar todas as suas maravilhosas fan-arts, basta apenas clicar no botão abaixo para criar um ticket
                                    |
                                    |**Mas lembre-se!** Não iremos aprovar fan-arts mal feitas ou que não estejam de acordo com as regras em <#${rulesChannelId}>!
                                """.trimMargin()

                    image = "https://loritta.website/v3/assets/img/faq/fanarts/banner.png"
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