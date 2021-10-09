package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake

object SparklyPowerRoleButtons {
    fun partialEmojiAsMention(emoji: DiscordPartialEmoji): String {
        val builder = StringBuilder()
        builder.append('<')
        if (emoji.animated.discordBoolean)
            builder.append('a')
        builder.append(':')
        builder.append(emoji.name)
        builder.append(':')
        builder.append(emoji.id?.value)
        builder.append('>')
        return builder.toString()
    }

    const val AUDIT_LOG_REASON = "Loritta Helper's Button Role Manager, yay!"

    val notDemon = customBadge(
        Snowflake(892197646599606302),
        DiscordPartialEmoji(
            Snowflake(835210767540289597),
            "NOTDEMON"
        )
    )
    val leno = customBadge(
        Snowflake(892198360692449341),
        DiscordPartialEmoji(
            Snowflake(846866643229212704),
            "LENO_BREGA"
        )
    )
    val pantufaAmeno = customBadge(
        Snowflake(892198613185351690),
        DiscordPartialEmoji(
            Snowflake(854811058992447530),
            "PANTUFA_AMENO"
        )
    )
    val loriAmeno = customBadge(
        Snowflake(892198996532138004),
        DiscordPartialEmoji(
            Snowflake(673868465433477126),
            "LORI_AMENO"
        )
    )
    val sadCatComfy = customBadge(
        Snowflake(892198729946390539),
        DiscordPartialEmoji(
            Snowflake(862357854651154453),
            "SAD_CAT_COMFY"
        )
    )
    val sadCat = customBadge(
        Snowflake(892200046131560548),
        DiscordPartialEmoji(
            Snowflake(627906923743674379),
            "SAD_CAT"
        )
    )
    val catClown = customBadge(
        Snowflake(892198505500774430),
        DiscordPartialEmoji(
            Snowflake(860889894108004372),
            "CAT_CLOWN"
        )
    )

    val coolBadges = listOf(
        notDemon,
        leno,
        pantufaAmeno,
        sadCatComfy,
        sadCat,
        catClown
    )

    private fun customBadge(roleId: Snowflake, emoji: DiscordPartialEmoji) = RoleButton(
        null,
        roleId,
        emoji,
        null,
        {
            content = "Você definiu seu ícone para ${partialEmojiAsMention(emoji)}! Agora você finalmente se sente chique entre seu grupo de amigos"
        },
        {
            content = "Você removeu seu ícone ${partialEmojiAsMention(emoji)}! Bem, eu *gostava* desse ícone, mas fazer o que né, as vezes *nada* é melhor, certo?~"
        }
    )
}
