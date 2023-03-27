package net.perfectdreams.loritta.helper.utils.slash.declarations

import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import net.perfectdreams.loritta.helper.LorittaHelperKord
import net.perfectdreams.loritta.helper.utils.slash.DailyCheckByIpExecutor
import net.perfectdreams.loritta.helper.utils.slash.DailyCheckByLorittaClientIdExecutor
import net.perfectdreams.loritta.helper.utils.slash.DailyCheckExecutor

class DailyCheckCommand(val helper: LorittaHelperKord) : SlashCommandDeclarationWrapper {
    override fun declaration() = slashCommand(
        "dailycheck",
        "Pega todos os dailies de vários usuários"
    ) {
        subcommand("users", "Pega todos os dailies de vários usuários") {
            executor = DailyCheckExecutor(helper)
        }

        subcommand("ips", "Pega todos os dailies de vários IPs") {
            executor = DailyCheckByIpExecutor(helper)
        }

        subcommand("ips", "Pega todos os dailies de vários Loritta Client IDs") {
            executor = DailyCheckByLorittaClientIdExecutor(helper)
        }
    }
}
