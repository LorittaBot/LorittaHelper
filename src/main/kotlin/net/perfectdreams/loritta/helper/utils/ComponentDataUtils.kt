package net.perfectdreams.loritta.helper.utils

import io.github.netvl.ecoji.Ecoji
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf

/**
 * Used to encode or decode component data classes into an [ProtoBuf] object, encoded by [Ecoji]
 *
 * Useful if you want to store data directly in the component itself, instead of storing in a database.
 *
 * However, the stored data must be less than 100 characters due to Discord limitations! So this is useful
 * for pagination and other small features.
 *
 * The reason [Ecoji] is used instead of Base64 is because Discord does not check the length of the String,
 * it checks the amount of codepoints.
 *
 * (From Loritta Cinnamon)
 */
object ComponentDataUtils {
    inline fun <reified T> encode(data: T) = Ecoji.getEncoder()
        .readFrom(
            ProtoBuf.encodeToByteArray(data)
        ).writeToString()

    inline fun <reified T> decode(data: String) = Ecoji.getDecoder()
        .readFrom(data)
        .writeToBytes()
        .let {
            ProtoBuf.decodeFromByteArray<T>(it)
        }
}