package net.perfectdreams.loritta.morenitta.interactions

import dev.minn.jda.ktx.messages.InlineMessage
import net.perfectdreams.loritta.helper.utils.Emotes

/**
 * Appends a Loritta-styled formatted message to the builder's message content.
 *
 * By default, Loritta-styled formatting looks like this: `[prefix] **|** [content]`.
 *
 * If there's already content present in the builder, a new line will be inserted before the styled replied!
 *
 * @param content the content of the styled message
 * @param prefix  the emote prefix of the styled message
 */
fun InlineMessage<*>.styled(content: String, prefix: String = Emotes.DefaultStyledPrefix) {
    val styled = createStyledContent(content, prefix)

    if (this.content != null) {
        this.content += "\n"
        this.content += styled
    } else {
        this.content = styled
    }
}

/**
 * Creates a Loritta-styled formatted content.
 *
 * By default, Loritta-styled formatting looks like this: `[prefix] **|** [content]`.
 *
 * @param content the content of the styled message
 * @param prefix  the emote prefix of the styled message
 */
fun createStyledContent(content: String, prefix: String = Emotes.DefaultStyledPrefix) = "$prefix **|** $content"