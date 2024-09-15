package net.perfectdreams.loritta.helper.utils

import net.perfectdreams.loritta.helper.utils.buttonroles.LorittaCommunityRoleButtons
import net.perfectdreams.loritta.helper.utils.buttonroles.RoleButton
import net.perfectdreams.loritta.helper.utils.buttonroles.SparklyPowerRoleButtons

enum class LorittaLandGuild(
    val colors: List<RoleButton>,
    val coolBadges: List<RoleButton>,
    val notifications: List<RoleButton>
) {
    LORITTA_COMMUNITY(
        LorittaCommunityRoleButtons.colors,
        LorittaCommunityRoleButtons.coolBadges,
        LorittaCommunityRoleButtons.notifications
    ),
    SPARKLYPOWER(
        SparklyPowerRoleButtons.colors,
        SparklyPowerRoleButtons.coolBadges,
        SparklyPowerRoleButtons.notifications
    );

    companion object {
        fun fromId(id: String): LorittaLandGuild {
            return entries.first { it.name == id }
        }
    }
}