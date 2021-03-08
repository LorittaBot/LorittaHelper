package net.perfectdreams.loritta.helper.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object GuildProfiles : LongIdTable() {
	val guildId = long("guild").index()
	val userId = long("user").index()
	val xp = long("xp").index()
}
