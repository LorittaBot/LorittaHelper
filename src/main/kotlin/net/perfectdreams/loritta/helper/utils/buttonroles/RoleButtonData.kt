package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import net.perfectdreams.loritta.helper.utils.LorittaLandGuild

@Serializable
data class RoleButtonData(
    val guild: LorittaLandGuild,
    val roleId: Snowflake
)