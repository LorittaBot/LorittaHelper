package net.perfectdreams.loritta.helper.tables

object Profiles : SnowflakeTable() {
	val money = long("money").index()
}