package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class RoleButtonData(
    val roleId: Snowflake
)