package net.perfectdreams.loritta.helper.utils.buttonroles

import net.dv8tion.jda.api.entities.emoji.CustomEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji

object SparklyPowerRoleButtons {
    // ===[ NOTIFICATIONS ]===
    val notifyWarpResources = RoleButton(
        "Notificar quando a warp recursos reseta",
        961736936790302800L,
        Emoji.fromCustom(
            "pantufa_comfy",
            853048447254396978L,
            false
        ),
        "Seja notificado nas <#332866197701918731> e fique sabendo quando a `/warp recursos` é resetada",
        {
            content = "Agora você irá ser notificado sobre quando a warp recursos será resetada em <#332866197701918731>!"
        },
        {
            content = "Se você está afim de perder dinheiro sem minerar, o problema não é meu."
        }
    )

    val notifications = listOf<RoleButton>(
        notifyWarpResources,
        // notifyBetaNews,
        // notifyStatus
    )

    val leno = customBadge(
        892198360692449341,
        Emoji.fromCustom(
            "LENO_BREGA",
            846866643229212704L,
            false
        )
    )
    val pantufaAmeno = customBadge(
        892198613185351690,
        Emoji.fromCustom(
            "PANTUFA_AMENO",
            854811058992447530L,
            false
        )
    )
    val loriAmeno = customBadge(
        892198996532138004,
        Emoji.fromCustom(
            "LORI_AMENO",
            673868465433477126L,
            false
        )
    )
    val sadCatComfy = customBadge(
        892198729946390539,
        Emoji.fromCustom(
            "SAD_CAT_COMFY",
            862357854651154453L,
            false
        )
    )
    val sadCat = customBadge(
        892200046131560548,
        Emoji.fromCustom(
            "SAD_CAT",
            627906923743674379L,
            false
        )
    )
    val catClown = customBadge(
        892198505500774430,
        Emoji.fromCustom(
            "cat_clown",
            889949642895290369L,
            false
        )
    )

    val coolBadges = listOf(
        leno,
        pantufaAmeno,
        sadCatComfy,
        sadCat,
        catClown
    )

    private fun customBadge(roleId: Long, emoji: CustomEmoji) = RoleButton(
        null,
        roleId,
        emoji,
        null,
        {
            content = "Você definiu seu ícone para ${emoji.asMention}! Agora você finalmente se sente chique entre seu grupo de amigos"
        },
        {
            content = "Você removeu seu ícone ${emoji.asMention}! Bem, eu *gostava* desse ícone, mas fazer o que né, as vezes *nada* é melhor, certo?~"
        }
    )

    // ===[ CUSTOM COLORS ]===
    val colors = mutableListOf<RoleButton>()

    val black = customColor(
        934578678279594064L,
        Emoji.fromCustom(
            "lori_rich_black",
            889922793112752138L,
            false
        )
    )

    val neonGreen = customColor(
        934577731654524928L,
        Emoji.fromCustom(
            "lori_rich_green_neon",
            889922793246953553L,
            false
        )
    )

    val lightViolet = customColor(
        934578629088804935L,
        Emoji.fromCustom(
            "lori_rich_light_violet",
            889922793246953552L,
            false
        )
    )

    val lightBlue = customColor(
        934578583958077481L,
        Emoji.fromCustom(
            "lori_rich_light_blue",
            889922793112752139L,
            false
        )
    )

    val orange = customColor(
        934578471286497290L,
        Emoji.fromCustom(
            "lori_rich_orange",
            889922793200816169L,
            false
        )
    )

    val violet = customColor(
        934578421864996924L,
        Emoji.fromCustom(
            "lori_rich_violet",
            889922792802361365L,
            false
        )
    )

    val darkRed = customColor(
        934578392068657172L,
        Emoji.fromCustom(
            "lori_rich_dark_red",
            889922793217622057L,
            false
        )
    )

    val darkGreen = customColor(
        934578365074112562L,
        Emoji.fromCustom(
            "lori_rich_dark_green",
            889922793322463233L,
            false
        )
    )

    val hotPink = customColor(
        934578309910642829L,
        Emoji.fromCustom(
            "lori_rich_hot_pink",
            889922793167265852L,
            false
        )
    )

    val darkPink = customColor(
        934578514131296287L,
        Emoji.fromCustom(
            "lori_rich_dark_pink",
            889922793116926053L,
            false
        )
    )

    val darkBlue = customColor(
        934578276054216844L,
        Emoji.fromCustom(
            "lori_rich_dark_blue",
            889922793465065474L,
            false
        )
    )

    val lightPink = customColor(
        934578235507892234L,
        Emoji.fromCustom(
            "lori_rich_light_pink",
            889922793381187626L,
            false
        )
    )

    val red = customColor(
        934578166217981972L,
        Emoji.fromCustom(
            "lori_rich_red",
            889922792915611659L,
            false
        )
    )

    val yellow = customColor(
        934578153505030204L,
        Emoji.fromCustom(
            "lori_rich_yellow",
            889922792974336012L,
            false
        )
    )

    val gold = customColor(
        934578120021921863L,
        Emoji.fromCustom(
            "lori_rich_gold",
            889922793188257823L,
            false
        )
    )

    val green = customColor(
        934577855306821762L,
        Emoji.fromCustom(
            "lori_rich_green",
            889922793129521212L,
            false
        )
    )

    private fun customColor(roleId: Long, emoji: CustomEmoji) = RoleButton(
        null,
        roleId,
        emoji,
        null,
        {
            content = "Você definiu sua cor para <@&${it.roleId}>! Tá muito chique essa sua cor amigah~"
        },
        {
            content = "Você removeu a cor <@&${it.roleId}>! Tá certo amigah tem que mudar o style para não ficar brega~"
        }
    ).also { colors.add(it) }
}