package net.perfectdreams.loritta.helper.utils.dailycatcher

enum class SuspiciousLevel(val text: String, val emote: String, val level: Int) {
    TOTALLY_THE_SAME_USER(
        "Meu deus, é o mesmo usuário!",
        "<a:peixoto_ban:637691002185842698>",
    1000
    ),
    MEGA_VERY_SUS(
            "Eu tenho quase certeza que é! Mas é melhor você analisar...",
            "<a:among_us_vent:759519990150856794>",
            900
    ),
    SUPER_VERY_SUS(
            "Muuuuuuuito sus...",
            "<a:crewmate_red_pat:803723887027552276>",
            800
    ),
    VERY_SUS(
        "Muito sus",
        "<a:crewmate_yellow_pat:803723941960089612>",
        500
    ),
    SUS(
        "sus",
        "<a:crewmate_cyan_dance:803745242007207966>",
        250
    ),
    NOT_REALLY_SUS(
        "Não acho que realmente seja sus, mas tá aí",
        "<a:crewmate_black_dance:803745269866823740>",
        0
    );

    fun increase() = SuspiciousLevel.values()[Math.max(this.ordinal - 1, 0)]
    fun decrease() = SuspiciousLevel.values()[Math.max(this.ordinal + 1, 0)]
}