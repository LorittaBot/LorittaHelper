package net.perfectdreams.loritta.helper.utils.dailycatcher

enum class SuspiciousLevel(val text: String, val emote: String, val level: Int) {
    TOTALLY_THE_SAME_USER(
        "Meu deus, é o mesmo usuário!",
        "<a:crewmate_red_pat:803723887027552276>",
    1000
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
        "Não acho que realmente sejam sus, mas tá aí",
        "<a:crewmate_black_dance:803745269866823740>",
        0
    )
}