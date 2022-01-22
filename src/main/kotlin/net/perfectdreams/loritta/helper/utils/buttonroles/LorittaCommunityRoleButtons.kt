package net.perfectdreams.loritta.helper.utils.buttonroles

import dev.kord.common.entity.DiscordPartialEmoji
import dev.kord.common.entity.Snowflake

object LorittaCommunityRoleButtons {
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

    // ===[ NOTIFICATIONS ]===
    val notifyNews = RoleButton(
        "Novidades da Loritta",
        Snowflake(334734175531696128L),
        DiscordPartialEmoji(
            Snowflake(640141673531441153L),
            "lori_yay_ping"
        ),
        "Seja notificado em <#302976807135739916> e fique por dentro de novas funcionalidades, sorteios, atualizações importantes e muito mais!",
        {
            content = "Agora você irá ser notificado sobre as minhas novidades em <#302976807135739916>! Espero que você goste delas!!"
        },
        {
            content = "Sério mesmo que você não quer mais receber minhas incríveis novidades? E eu pensava que nós eramos amigos..."
        }
    )

    val notifyBetaNews = RoleButton(
        "WIPs da Loritta",
        Snowflake(526720753991811072L),
        DiscordPartialEmoji(
            Snowflake(673868465433477126L),
            "lori_ameno"
        ),
        "O cargo de <@&334734175531696128> é supimpa, mas... e se você é um super fã da Loritta e quer *mais* novidades dela? Seja notificado em <#526721901196738571> e fique por dentro de novas funcionalidades que a equipe está fazendo e que estarão na Loritta no futuro! (ou seja, trabalho em progresso/work in progress/WIP)",
        {
            content = "Então você gosta de ficar sabendo das novidades antes de todo mundo, não é mesmo? Agora você irá ser notificado sobre as novas funcionalidades WIP em <#526721901196738571>!"
        },
        {
            content = "Mas você queria receber as novidades em progresso... Eu mesmo falo que só é para você pegar o cargo se você é um super fã! E eu pensava que nós eramos amigos..."
        }
    )

    val notifyStatus = RoleButton(
        "Status da Loritta",
        Snowflake(889852001016487986L),
        DiscordPartialEmoji(
            Snowflake(556524143281963008L),
            "lori_sob"
        ),
        "\"aaaah, Lori caiu!!!\", seja notificado em <#610094449737072660> quando a Loritta está offline para manutenção, atualizações, problemas técnicos... ou quando ela resolveu ir tomar uma água geladinha!",
        {
            content = "Pois cansa ficar adivinhando quando cai, não é mesmo? Agora você irá ser notificado sobre atualizações de status em <#610094449737072660>!"
        },
        {
            content = "Quer dizer que você acha que eu caio muito para você pedir para que remova o cargo? E eu pensava que nós eramos amigos..."
        }
    )

    val notifications = listOf(
        notifyNews,
        notifyBetaNews,
        notifyStatus
    )

    // ===[ CUSTOM BADGES ]===
    val coinHeads = customBadge(
        Snowflake(889914343817371720L),
        DiscordPartialEmoji(
            Snowflake(412586256409559041L),
            "cara"
        )
    )

    val emojo = customBadge(
        Snowflake(889933875684454470L),
        DiscordPartialEmoji(
            Snowflake(351535675004420106L),
            "emojo"
        )
    )

    val loriYayPing = customBadge(
        Snowflake(889937613908222015L),
        DiscordPartialEmoji(
            Snowflake(640141673531441153L),
            "lori_yay_ping"
        )
    )

    val loriSob = customBadge(
        Snowflake(889937765465227315L),
        DiscordPartialEmoji(
            Snowflake(556524143281963008L),
            "lori_sob"
        )
    )

    val loriAmeno = customBadge(
        Snowflake(889937943907676220L),
        DiscordPartialEmoji(
            Snowflake(673868465433477126),
            "lori_ameno"
        )
    )

    val pantufaAmeno = customBadge(
        Snowflake(889938876477636618L),
        DiscordPartialEmoji(
            Snowflake(854811058992447530L),
            "pantufa_ameno"
        )
    )

    val gabrielaAmeno = customBadge(
        Snowflake(889938785268301895L),
        DiscordPartialEmoji(
            Snowflake(854810604538953759L),
            "gabriela_ameno"
        )
    )

    val floppa = customBadge(
        Snowflake(889939709898391604L),
        DiscordPartialEmoji(
            Snowflake(881250695238525020L),
            "floppa_static"
        )
    )

    val owo = customBadge(
        Snowflake(889942290951442533L),
        DiscordPartialEmoji(
            Snowflake(889945299185975308L),
            "owo"
        )
    )

    val wumpusBombado = customBadge(
        Snowflake(889946509771157514L),
        DiscordPartialEmoji(
            Snowflake(889946719641534474L),
            "wumpus_bombado"
        )
    )

    val amogus = customBadge(
        Snowflake(889946919995064361L),
        DiscordPartialEmoji(
            Snowflake(889949004840972318L),
            "amogus"
        )
    )

    val catClown = customBadge(
        Snowflake(889946982905430016L),
        DiscordPartialEmoji(
            Snowflake(889949642895290369L),
            "cat_clown"
        )
    )

    val smolGessy = customBadge(
        Snowflake(889949073921171477L),
        DiscordPartialEmoji(
            Snowflake(593907632784408644L),
            "smol_gessy"
        )
    )

    val loriStonks = customBadge(
        Snowflake(889951454750707714L),
        DiscordPartialEmoji(
            Snowflake(788434890927505448L),
            "lori_stonks"
        )
    )

    val sadCatEmocionado = customBadge(
        Snowflake(889951712960454657L),
        DiscordPartialEmoji(
            Snowflake(585667678828494877L),
            "sad_cat4"
        )
    )

    val sadCatDrama = customBadge(
        Snowflake(889952029223575632L),
        DiscordPartialEmoji(
            Snowflake(648695501398605825L),
            "sad_cat18"
        )
    )

    val smolDokyo = customBadge(
        Snowflake(889952077218971678L),
        DiscordPartialEmoji(
            Snowflake(649023525998297088L),
            "smol_dokyo"
        )
    )

    val vegetaPerdemo = customBadge(
        Snowflake(889952173834768394L),
        DiscordPartialEmoji(
            Snowflake(791641452575850516L),
            "vegeta_perdemo"
        )
    )

    val sadCat = customBadge(
        Snowflake(889952294135791636L),
        DiscordPartialEmoji(
            Snowflake(585536245891858470L),
            "sad_cat2"
        )
    )

    val sadCatSuborno = customBadge(
        Snowflake(889952387425505290L),
        DiscordPartialEmoji(
            Snowflake(649678113185202197L),
            "sad_cat_suborno"
        )
    )

    val porFavor = customBadge(
        Snowflake(889955810166308865L),
        DiscordPartialEmoji(
            Snowflake(784929195112923176L),
            "porfavor"
        )
    )

    val dokyoHm = customBadge(
        Snowflake(889956409582694450L),
        DiscordPartialEmoji(
            Snowflake(591441682810142730L),
            "dokyo_hm"
        )
    )

    val loriPat = customBadge(
        Snowflake(889956804191199242L),
        DiscordPartialEmoji(
            Snowflake(706263175892566097L),
            "lori_pat"
        )
    )

    val ehmole = customBadge(
        Snowflake(889958693494480936L),
        DiscordPartialEmoji(
            Snowflake(589518158952398879L),
            "ehmole"
        )
    )

    val deltarune = customBadge(
        Snowflake(889957530795638815L),
        DiscordPartialEmoji(
            Snowflake(889958066362155008L),
            "deltarune"
        )
    )

    val coolBadges = listOf(
        coinHeads,
        emojo,
        loriYayPing,
        loriSob,
        floppa,
        loriAmeno,
        pantufaAmeno,
        gabrielaAmeno,
        owo,
        wumpusBombado,
        amogus,
        catClown,
        smolDokyo,
        smolGessy,
        loriStonks,
        sadCatEmocionado,
        sadCatDrama,
        sadCat,
        sadCatSuborno,
        vegetaPerdemo,
        porFavor,
        dokyoHm,
        loriPat,
        ehmole,
        deltarune
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
        Snowflake(751256879534964796L),
        DiscordPartialEmoji(Snowflake(889922793112752138L), "lori_rich_black")
    )

    val neonGreen = customColor(
        Snowflake(760681173608431647L),
        DiscordPartialEmoji(Snowflake(889922793246953553L), "lori_rich_green_neon")
    )

    val lightViolet = customColor(
        Snowflake(750738232735432817L),
        DiscordPartialEmoji(Snowflake(889922793246953552L), "lori_rich_light_violet")
    )

    val lightBlue = customColor(
        Snowflake(373539846620315648L),
        DiscordPartialEmoji(Snowflake(889922793112752139L), "lori_rich_light_blue")
    )

    val orange = customColor(
        Snowflake(738914237598007376L),
        DiscordPartialEmoji(Snowflake(889922793200816169L), "lori_rich_orange")
    )

    val violet = customColor(
        Snowflake(738880144403464322L),
        DiscordPartialEmoji(Snowflake(889922792802361365L), "lori_rich_violet")
    )

    val darkRed = customColor(
        Snowflake(373540076095012874L),
        DiscordPartialEmoji(Snowflake(889922793217622057L), "lori_rich_dark_red")
    )

    val darkGreen = customColor(
        Snowflake(374613624536170500L),
        DiscordPartialEmoji(Snowflake(889922793322463233L), "lori_rich_dark_green")
    )

    val hotPink = customColor(
        Snowflake(411235044842012674L),
        DiscordPartialEmoji(Snowflake(889922793167265852L), "lori_rich_hot_pink")
    )

    val darkPink = customColor(
        Snowflake(374614002707333120L),
        DiscordPartialEmoji(Snowflake(889922793116926053L), "lori_rich_dark_pink")
    )

    val darkBlue = customColor(
        Snowflake(373539894259351553L),
        DiscordPartialEmoji(Snowflake(889922793465065474L), "lori_rich_dark_blue")
    )

    val lightPink = customColor(
        Snowflake(374613958608551936L),
        DiscordPartialEmoji(Snowflake(889922793381187626L), "lori_rich_light_pink")
    )

    val red = customColor(
        Snowflake(373540030053875713L),
        DiscordPartialEmoji(Snowflake(889922792915611659L), "lori_rich_red")
    )

    val yellow = customColor(
        Snowflake(373539918863007745L),
        DiscordPartialEmoji(Snowflake(889922792974336012L), "lori_rich_yellow")
    )

    val gold = customColor(
        Snowflake(373539973984550912L),
        DiscordPartialEmoji(Snowflake(889922793188257823L), "lori_rich_gold")
    )

    val green = customColor(
        Snowflake(374613592185634816L),
        DiscordPartialEmoji(Snowflake(889922793129521212L), "lori_rich_green")
    )

    val colors = listOf(
        black,
        darkBlue,
        lightBlue,
        lightViolet,
        violet,
        darkRed,
        red,
        lightPink,
        hotPink,
        darkPink,
        gold,
        orange,
        yellow,
        neonGreen,
        green,
        darkGreen
    )

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
    )
}