package net.perfectdreams.loritta.helper.utils

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern

object Constants {
    /* Portuguese
        Here we'll store portuguese things
     */
    const val PORTUGUESE_SUPPORT_CHANNEL_ID = 761337439095881748L
    const val PORTUGUESE_LORITTA_SUPPORT_ROLE_ID = 421325387889377291L
    const val PORTUGUESE_STATUS_CHANNEL_ID = 752294116708319324L
    const val PORTUGUESE_NEWS_CHANNEL_ID = 761349260200509460L
    const val PORTUGUESE_FAQ_CHANNEL_ID = 761337893951635458L
    const val PORTUGUESE_STAFF_CHANNEL_ID = 358774895850815488L
    const val PORTUGUESE_REPORTS_WARNINGS_CHANNEL_ID = 1078554562664857680L
    const val PORTUGUESE_SADDEST_OF_THE_SADS_CHANNEL_ID = 643624690874712094L

    /* English
        Here we'll store english things
     */
    const val ENGLISH_SUPPORT_CHANNEL_ID = 420628148044955648L
    const val ENGLISH_LORITTA_SUPPORT_ROLE_ID = 761586798971322370L
    const val ENGLISH_STATUS_CHANNEL_ID = 761385919479414825L
    const val ENGLISH_NEWS_CHANNEL_ID = 420627916028641280L
    const val ENGLISH_FAQ_CHANNEL_ID = 761337709720633392L

    /* Other
        Here we'll store other things
     */
    const val COMMUNITY_SERVER_ID = 297732013006389252L
    const val SUPPORT_SERVER_ID = 420626099257475072L
    const val OTHER_BOTS_CHANNEL_ID = 761956906368892958L
    const val SPARKLY_POWER_INVITE_CODE = "https://discord.gg/JYN6g2s"

    val URL_PATTERN : Pattern = Pattern.compile("[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[A-z]{2,7}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)")

    val TIME_ZONE_ID = ZoneId.of("America/Sao_Paulo")
    val PRETTY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        .withLocale(Locale.US)
        .withZone(TIME_ZONE_ID)
}
