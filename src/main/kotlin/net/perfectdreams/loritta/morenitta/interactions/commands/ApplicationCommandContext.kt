package net.perfectdreams.loritta.morenitta.interactions.commands

import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent
import net.perfectdreams.loritta.helper.LorittaHelper
import net.perfectdreams.loritta.morenitta.interactions.InteractionContext

/**
 * Context of the executed command
 */
class ApplicationCommandContext(
    loritta: LorittaHelper,
    override val event: GenericCommandInteractionEvent
) : InteractionContext(loritta)