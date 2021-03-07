package net.perfectdreams.loritta.helper.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object GuildProfiles : LongIdTable() {
	val guildId = long("guild").index()
	val userId = long("user").index()
	val xp = long("xp").index()
	val quickPunishment = bool("quick_punishment")
	val money = decimal("money", 12, 2).index()
	val isInGuild = bool("is_in_guild").default(true).index()
}
