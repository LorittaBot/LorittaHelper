package net.perfectdreams.loritta.helper.utils

import java.util.regex.Pattern

object Constants {
    const val SUPPORT_SERVER_ID = 420626099257475072L
    const val PORTUGUESE_SUPPORT_CHANNEL_ID = 761337439095881748L
    const val ENGLISH_SUPPORT_CHANNEL_ID = 420628148044955648L

    const val PORTUGUESE_LORITTA_SUPPORT_ROLE_ID = 421325387889377291L
    const val ENGLISH_LORITTA_SUPPORT_ROLE_ID = 761586798971322370L

    val URL_PATTERN = Pattern.compile("[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[A-z]{2,7}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)")
}