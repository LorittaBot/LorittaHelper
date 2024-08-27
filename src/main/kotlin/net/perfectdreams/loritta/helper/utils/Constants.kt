package net.perfectdreams.loritta.helper.utils

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

object Constants {
    /* Other
        Here we'll store other things
     */
    const val SPARKLY_POWER_INVITE_CODE = "https://discord.gg/JYN6g2s"

    val URL_PATTERN : Pattern = Pattern.compile("[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[A-z]{2,7}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)")

    val TIME_ZONE_ID = ZoneId.of("America/Sao_Paulo")
    val PRETTY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        .withLocale(Locale.US)
        .withZone(TIME_ZONE_ID)
}
