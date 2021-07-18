package net.perfectdreams.loritta.helper.listeners

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.serverresponses.EnglishResponses
import net.perfectdreams.loritta.helper.serverresponses.PortugueseResponses
import net.perfectdreams.loritta.helper.utils.Constants
import net.perfectdreams.loritta.helper.utils.checkillegalnitrosell.CheckIllegalNitroSell
import net.perfectdreams.loritta.helper.utils.checksonhosmendigagem.CheckSonhosMendigagem
import net.perfectdreams.loritta.helper.utils.dontmentionstaff.EnglishDontMentionStaff
import net.perfectdreams.loritta.helper.utils.dontmentionstaff.PortugueseDontMentionStaff
import net.perfectdreams.loritta.helper.utils.generatebanstatusreport.GenerateBanStatusReport
import net.perfectdreams.loritta.helper.utils.generateserverreport.GenerateServerReport
import net.perfectdreams.loritta.helper.utils.gotolangchannel.GoToCorrectLanguageChannel

class MessageListener(val m: LorittaHelper) : ListenerAdapter() {
    private val dontMentionStaffs = listOf(
            EnglishDontMentionStaff(),
            PortugueseDontMentionStaff()
    )

    val goToTheCorrectLanguageChannel = GoToCorrectLanguageChannel(m)
    val checkIllegalNitroSell = CheckIllegalNitroSell()
    val generateBanStatusReport = GenerateBanStatusReport(m)
    val generateServerReport = GenerateServerReport(m)
    val checkSonhosBraggers = CheckSonhosMendigagem(m)

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        super.onGuildMessageReceived(event)

        // If this check wasn't here, Loritta Helper will reply to a user... then she thinks that it is someone asking
        // something, and the loop goes on...
        if (event.author.isBot) {
            // Updates
            if (event.channel.idLong == 422103894462824468L && event.author.name == "Loritta Helper Update") { // support staff channel
                if (event.message.contentRaw.startsWith("Atualização OwO!")) {
                    m.launch {
                        m.update()
                    }
                }
            }

            m.launch {
                if (event.message.channel.idLong == 790292619769937940L && event.message.attachments.isNotEmpty()) {
                    if (event.message.contentRaw == "report") {
                        generateServerReport.onMessageReceived(event)
                    } else if (event.message.contentRaw == "appeal") {
                        // Don't do anything... yet
                    }
                }
            }
            return
        }

        m.launch {
            checkSonhosBraggers.onMessageReceived(event)
        }

        // We launch in a separate task because we want both responses (automatic responses + don't mention staff) to go off, if they
        // are triggered in the same message
        m.launch {
            dontMentionStaffs.forEach {
                it.onMessageReceived(event)
            }
        }

        m.launch {
            goToTheCorrectLanguageChannel.onMessageReceived(event)
        }

        m.launch {
            if (event.message.channel.idLong == 781878469427986452L)
                generateBanStatusReport.onMessageReceived(event)
        }

        m.launch {
            checkIllegalNitroSell.onMessageReceived(event)
        }

        m.launch {
            val channelResponses = when (event.message.channel.idLong) {
                Constants.PORTUGUESE_SUPPORT_CHANNEL_ID, 547119872568459284L /* open bar */ -> {
                    PortugueseResponses.responses
                }
                Constants.ENGLISH_SUPPORT_CHANNEL_ID, 422103894462824468L /* support server staff channel */ -> {
                    EnglishResponses.responses
                }
                else -> null
            }

            if (channelResponses != null) {
                // We remove any lines starting with > (quote) because this sometimes causes responses to something inside a citation, and that looks kinda bad
                val cleanMessage = event.message.contentRaw.lines()
                        .dropWhile { it.startsWith(">") }
                        .joinToString("\n")

                val responses = channelResponses
                        .firstOrNull { it.handleResponse(cleanMessage) }?.getResponse(event, cleanMessage) ?: return@launch

                if (responses.isNotEmpty())
                    event.channel.sendMessage(
                            MessageBuilder()
                                    // We mention roles in some of the messages, so we don't want the mention to actually go off!
                                    .setAllowedMentions(listOf(Message.MentionType.USER, Message.MentionType.CHANNEL, Message.MentionType.EMOTE))
                                    .setContent(responses.joinToString("\n") { it.build(event) })
                                    .build()
                    ).reference(event.message)
                            .queue()
                return@launch
            }
        }
    }
}