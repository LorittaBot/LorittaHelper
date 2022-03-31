package net.perfectdreams.loritta.helper.utils.tickets

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.DiscordPartialEmoji
import net.perfectdreams.discordinteraktions.common.builder.message.actionRow
import net.perfectdreams.discordinteraktions.common.components.ComponentContext
import net.perfectdreams.discordinteraktions.common.components.GuildComponentContext
import net.perfectdreams.discordinteraktions.common.components.SelectMenuExecutor
import net.perfectdreams.discordinteraktions.common.components.SelectMenuExecutorDeclaration
import net.perfectdreams.discordinteraktions.common.components.interactiveButton
import net.perfectdreams.discordinteraktions.common.entities.User
import net.perfectdreams.loritta.api.messages.LorittaReply
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.i18n.I18nKeysData
import net.perfectdreams.loritta.helper.tables.SelectedResponsesLog
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

class HelperResponseSelectMenuExecutor(val m: LorittaHelperKord) : SelectMenuExecutor {
    companion object : SelectMenuExecutorDeclaration(HelperResponseSelectMenuExecutor::class, "helper_response") {
        const val MY_QUESTION_ISNT_HERE_SPECIAL_KEY = "MyQuestionIsntHere!"
    }

    override suspend fun onSelect(user: User, context: ComponentContext, values: List<String>) {
        if (context is GuildComponentContext) {
            val systemInfo = TicketUtils.informations[context.channelId]!!
            if (systemInfo !is TicketUtils.HelpDeskTicketSystemInformation)
                return
            val channelResponses = systemInfo.channelResponses
            val i18nContext = systemInfo.getI18nContext(m.languageManager)

            val firstValue = values.first()
            if (firstValue == MY_QUESTION_ISNT_HERE_SPECIAL_KEY) {
                context.sendEphemeralMessage {
                    content = listOf(
                        LorittaReply(
                            i18nContext.get(
                                I18nKeysData.Tickets.LookUpInTheFAQIfQuestionWasntFound(
                                    "<#${systemInfo.faqChannelId.value}>",
                                    "<https://loritta.website/extras>",
                                )
                            ),
                            "<:lori_reading:853052040430878750>"
                        ),
                        LorittaReply(
                            i18nContext.get(
                                I18nKeysData.Tickets.CreateATicketIfQuestionWasntFound(
                                    "<@&${systemInfo.supportRoleId.value}>",
                                )
                            ),
                            "<:lori_comfy:726873685021163601>"
                        )
                    ).joinToString("\n") { it.build() }

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
            } else {
                val response = channelResponses.first { it::class.simpleName == firstValue }

                // Workaround because the LorittaResponse requires a content (it is only used for the "HelpMeResponse")
                // So let's just use "button" as the content because it doesn't matter
                val replies = response.getResponse("button")

                context.sendEphemeralMessage {
                    content = replies.joinToString("\n") { it.build(user) }
                }

                transaction(m.databases.helperDatabase) {
                    SelectedResponsesLog.insert {
                        it[SelectedResponsesLog.timestamp] = Instant.now()
                        it[SelectedResponsesLog.ticketSystemType] = systemInfo.systemType
                        it[SelectedResponsesLog.userId] = user.id.value.toLong()
                        it[SelectedResponsesLog.selectedResponse] = firstValue
                    }
                }
            }
        }
    }
}