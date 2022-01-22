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

    val notifications = listOf<RoleButton>(
        // notifyNews,
        // notifyBetaNews,
        // notifyStatus
    )

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

    // ===[ CUSTOM COLORS ]===
    val black = customColor(
        Snowflake(934578678279594064L),
        DiscordPartialEmoji(Snowflake(889922793112752138L), "lori_rich_black")
    )

    val neonGreen = customColor(
        Snowflake(934577731654524928L),
        DiscordPartialEmoji(Snowflake(889922793246953553L), "lori_rich_green_neon")
    )

    val lightViolet = customColor(
        Snowflake(934578629088804935L),
        DiscordPartialEmoji(Snowflake(889922793246953552L), "lori_rich_light_violet")
    )

    val lightBlue = customColor(
        Snowflake(934578583958077481L),
        DiscordPartialEmoji(Snowflake(889922793112752139L), "lori_rich_light_blue")
    )

    val orange = customColor(
        Snowflake(934578471286497290L),
        DiscordPartialEmoji(Snowflake(889922793200816169L), "lori_rich_orange")
    )

    val violet = customColor(
        Snowflake(934578421864996924L),
        DiscordPartialEmoji(Snowflake(889922792802361365L), "lori_rich_violet")
    )

    val darkRed = customColor(
        Snowflake(934578392068657172L),
        DiscordPartialEmoji(Snowflake(889922793217622057L), "lori_rich_dark_red")
    )

    val darkGreen = customColor(
        Snowflake(934578365074112562L),
        DiscordPartialEmoji(Snowflake(889922793322463233L), "lori_rich_dark_green")
    )

    val hotPink = customColor(
        Snowflake(934578309910642829L),
        DiscordPartialEmoji(Snowflake(889922793167265852L), "lori_rich_hot_pink")
    )

    val darkPink = customColor(
        Snowflake(934578514131296287L),
        DiscordPartialEmoji(Snowflake(889922793116926053L), "lori_rich_dark_pink")
    )

    val darkBlue = customColor(
        Snowflake(934578276054216844L),
        DiscordPartialEmoji(Snowflake(889922793465065474L), "lori_rich_dark_blue")
    )

    val lightPink = customColor(
        Snowflake(934578235507892234L),
        DiscordPartialEmoji(Snowflake(889922793381187626L), "lori_rich_light_pink")
    )

    val red = customColor(
        Snowflake(934578166217981972L),
        DiscordPartialEmoji(Snowflake(889922792915611659L), "lori_rich_red")
    )

    val yellow = customColor(
        Snowflake(934578153505030204L),
        DiscordPartialEmoji(Snowflake(889922792974336012L), "lori_rich_yellow")
    )

    val gold = customColor(
        Snowflake(934578120021921863L),
        DiscordPartialEmoji(Snowflake(889922793188257823L), "lori_rich_gold")
    )

    val green = customColor(
        Snowflake(934577855306821762L),
        DiscordPartialEmoji(Snowflake(889922793129521212L), "lori_rich_green")
    )

    val colors = mutableListOf<RoleButton>()

    private fun customColor(roleId: Snowflake, emoji: DiscordPartialEmoji) = RoleButton(
        null,
        roleId,
        emoji,
        null,
        {
            content = "Você definiu sua cor para <@&${it.roleId.value}>! Tá muito chique essa sua cor amigah~"
        },
        {
            content = "Você removeu a cor <@&${it.roleId.value}>! Tá certo amigah tem que mudar o style para não ficar brega~"
        }
    ).also { colors.add(it) }
}
