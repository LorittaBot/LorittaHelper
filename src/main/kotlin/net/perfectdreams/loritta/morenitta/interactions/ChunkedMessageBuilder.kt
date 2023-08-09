package net.perfectdreams.loritta.morenitta.interactions

class ChunkedMessageBuilder {
    var content = ""

    fun styled(content: String, prefix: String) {
        this.content += (createStyledContent(content, prefix) + "\n")
    }
}