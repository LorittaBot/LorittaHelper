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
