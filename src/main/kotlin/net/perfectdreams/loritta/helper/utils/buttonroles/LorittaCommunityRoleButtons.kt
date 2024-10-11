package net.perfectdreams.loritta.helper.utils.buttonroles

import net.dv8tion.jda.api.entities.emoji.CustomEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji

object LorittaCommunityRoleButtons {
    const val AUDIT_LOG_REASON = "Loritta Helper's Button Role Manager, yay!"

    // ===[ NOTIFICATIONS ]===
    val notifyNews = RoleButton(
        "Novidades da Loritta",
        334734175531696128L,
        Emoji.fromCustom("lori_yay_ping", 640141673531441153L, false),
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
        526720753991811072L,
        Emoji.fromCustom("lori_ameno", 673868465433477126L, false),
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
        889852001016487986L,
        Emoji.fromCustom("lori_sob", 556524143281963008L, false),
        "\"aaaah, Lori caiu!!!\", seja notificado em <#610094449737072660> quando a Loritta está offline para manutenção, atualizações, problemas técnicos... ou quando ela resolveu ir tomar uma água geladinha!",
        {
            content = "Pois cansa ficar adivinhando quando cai, não é mesmo? Agora você irá ser notificado sobre atualizações de status em <#610094449737072660>!"
        },
        {
            content = "Quer dizer que você acha que eu caio muito para você pedir para que remova o cargo? E eu pensava que nós eramos amigos..."
        }
    )

    val notifyLorittaSocialNetworks = RoleButton(
        "Seguidores do Culto do Pudim",
        979832728537157703L,
        Emoji.fromCustom("lori_zap", 956404868417990776L, false),
        "Receba notificações sobre atualizações nas redes sociais da Loritta!",
        {
            content = "Obrigada por me ajudar a chegar mais próximo do meu sonho de dominar o mundo! Você irá receber novas notificações em <#979838040518316073>. <:lori_demon:964699688429297664>"
        },
        {
            content = "Quer dizer que você não quer me ajudar a dominar o mundo? E eu pensava que nós eramos amigos..."
        }
    )

    val notifyMrPowerGamerBRSocialNetworks = RoleButton(
        "Seguidores da Gambiarra",
        979832965775364236L,
        Emoji.fromCustom("pet_the_power", 775152943607185409L, false),
        "Receba notificações sobre atualizações nas redes sociais do MrPowerGamerBR!",
        {
            content = "Agora você irá receber notificações em <#979838040518316073> quando o MrPowerGamerBR resolve lembrar a senha dos canais dele!"
        },
        {
            content = "Cansou das gambiarras? <:lori_troll:971610172529209364>"
        }
    )

    val notifications = listOf(
        notifyNews,
        notifyBetaNews,
        notifyStatus,
        notifyLorittaSocialNetworks,
        notifyMrPowerGamerBRSocialNetworks
    )

    // ===[ CUSTOM BADGES ]===
    val coinHeads = customBadge(
        889914343817371720L,
        Emoji.fromCustom("cara", 412586256409559041L, false)
    )

    val emojo = customBadge(
        889933875684454470L,
        Emoji.fromCustom("emojo", 351535675004420106L, false)
    )

    val loriYayPing = customBadge(
        889937613908222015L,
        Emoji.fromCustom("lori_yay_ping", 640141673531441153L, false)
    )

    val loriSob = customBadge(
        889937765465227315L,
        Emoji.fromCustom("lori_sob", 556524143281963008L, false)
    )

    val loriAmeno = customBadge(
        889937943907676220L,
        Emoji.fromCustom("lori_ameno", 673868465433477126, false)
    )

    val pantufaAmeno = customBadge(
        889938876477636618L,
        Emoji.fromCustom("pantufa_ameno", 854811058992447530L, false)
    )

    val gabrielaAmeno = customBadge(
        889938785268301895L,
        Emoji.fromCustom("gabriela_ameno", 854810604538953759L, false)
    )

    val floppa = customBadge(
        889939709898391604L,
        Emoji.fromCustom("floppa_static", 881250695238525020L, false)
    )

    val owo = customBadge(
        889942290951442533L,
        Emoji.fromCustom("owo", 889945299185975308L, false)
    )

    val wumpusBombado = customBadge(
        889946509771157514L,
        Emoji.fromCustom("wumpus_bombado", 889946719641534474L, false)
    )

    val amogus = customBadge(
        889946919995064361L,
        Emoji.fromCustom("amogus", 889949004840972318L, false)
    )

    val catClown = customBadge(
        889946982905430016L,
        Emoji.fromCustom("cat_clown", 889949642895290369L, false)
    )

    val smolGessy = customBadge(
        889949073921171477L,
        Emoji.fromCustom("smol_gessy", 593907632784408644L, false)
    )

    val loriStonks = customBadge(
        889951454750707714L,
        Emoji.fromCustom("lori_stonks", 788434890927505448L, false)
    )

    val sadCatEmocionado = customBadge(
        889951712960454657L,
        Emoji.fromCustom("sad_cat4", 585667678828494877L, false)
    )

    val sadCatDrama = customBadge(
        889952029223575632L,
        Emoji.fromCustom("sad_cat18", 648695501398605825L, false)
    )

    val smolDokyo = customBadge(
        889952077218971678L,
        Emoji.fromCustom("smol_dokyo", 649023525998297088L, false)
    )

    val vegetaPerdemo = customBadge(
        889952173834768394L,
        Emoji.fromCustom("vegeta_perdemo", 791641452575850516L, false)
    )

    val sadCat = customBadge(
        889952294135791636L,
        Emoji.fromCustom("sad_cat2", 585536245891858470L, false)
    )

    val sadCatSuborno = customBadge(
        889952387425505290L,
        Emoji.fromCustom("sad_cat_suborno", 649678113185202197L, false)
    )

    val porFavor = customBadge(
        889955810166308865L,
        Emoji.fromCustom("porfavor", 784929195112923176L, false)
    )

    val dokyoHm = customBadge(
        889956409582694450L,
        Emoji.fromCustom("dokyo_hm", 591441682810142730L, false)
    )

    val loriPat = customBadge(
        889956804191199242L,
        Emoji.fromCustom("lori_pat", 706263175892566097L, false)
    )

    val ehmole = customBadge(
        889958693494480936L,
        Emoji.fromCustom("ehmole", 589518158952398879L, false)
    )

    val deltarune = customBadge(
        889957530795638815L,
        Emoji.fromCustom("deltarune", 889958066362155008L, false)
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
    val black = customColor(
        751256879534964796L,
        Emoji.fromCustom("lori_rich_black", 889922793112752138L, false)
    )

    val neonGreen = customColor(
        760681173608431647L,
        Emoji.fromCustom("lori_rich_green_neon", 889922793246953553L, false)
    )

    val lightViolet = customColor(
        750738232735432817L,
        Emoji.fromCustom("lori_rich_light_violet", 889922793246953552L, false)
    )

    val lightBlue = customColor(
        373539846620315648L,
        Emoji.fromCustom("lori_rich_light_blue", 889922793112752139L, false)
    )

    val orange = customColor(
        738914237598007376L,
        Emoji.fromCustom("lori_rich_orange", 889922793200816169L, false)
    )

    val violet = customColor(
        738880144403464322L,
        Emoji.fromCustom("lori_rich_violet", 889922792802361365L, false)
    )

    val darkRed = customColor(
        373540076095012874L,
        Emoji.fromCustom("lori_rich_dark_red", 889922793217622057L, false)
    )

    val darkGreen = customColor(
        374613624536170500L,
        Emoji.fromCustom("lori_rich_dark_green", 889922793322463233L, false)
    )

    val hotPink = customColor(
        411235044842012674L,
        Emoji.fromCustom("lori_rich_hot_pink", 889922793167265852L, false)
    )

    val darkPink = customColor(
        374614002707333120L,
        Emoji.fromCustom("lori_rich_dark_pink", 889922793116926053L, false)
    )

    val darkBlue = customColor(
        373539894259351553L,
        Emoji.fromCustom("lori_rich_dark_blue", 889922793465065474L, false)
    )

    val lightPink = customColor(
        374613958608551936L,
        Emoji.fromCustom("lori_rich_light_pink", 889922793381187626L, false)
    )

    val red = customColor(
        373540030053875713L,
        Emoji.fromCustom("lori_rich_red", 889922792915611659L, false)
    )

    val yellow = customColor(
        373539918863007745L,
        Emoji.fromCustom("lori_rich_yellow", 889922792974336012L, false)
    )

    val gold = customColor(
        373539973984550912L,
        Emoji.fromCustom("lori_rich_gold", 889922793188257823L, false)
    )

    val green = customColor(
        374613592185634816L,
        Emoji.fromCustom("lori_rich_green", 889922793129521212L, false)
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
    )
}