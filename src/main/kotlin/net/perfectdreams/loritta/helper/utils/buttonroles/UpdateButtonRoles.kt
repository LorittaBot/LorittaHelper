package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.message.create.actionRow
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import net.perfectdreams.discordinteraktions.common.components.interactiveButton
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.helper.utils.ComponentDataUtils

class UpdateButtonRoles(val m: LorittaHelper) : Runnable {
    companion object {
        private val logger = KotlinLogging.logger {}
        private val channel = Snowflake(889586110114713681L)
    }

    override fun run() {
        try {
            runBlocking {
                val messages = m.helperRest.channel.getMessages(
                    Snowflake(889586110114713681L),
                    limit = 100
                )

                messages.forEach {
                    m.helperRest.channel.deleteMessage(channel, it.id)
                }

                m.helperRest.channel.createMessage(channel) {
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
        } catch (e: Exception) {
            logger.error(e) { "Something went wrong while trying to update button roles!" }
        }
    }
}