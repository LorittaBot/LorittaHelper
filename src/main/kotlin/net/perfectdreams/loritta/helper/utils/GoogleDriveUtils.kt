package net.perfectdreams.loritta.helper.utils

import io.ktor.client.request.*
import io.ktor.client.statement.*
import net.perfectdreams.loritta.helper.LorittaHelper
import java.net.HttpURLConnection
import java.net.URL

object GoogleDriveUtils {
    suspend fun downloadGoogleDriveUrl(fileId: String): ByteArray? {
        return LorittaHelper.http.get(getDiscordEmbeddableGoogleDriveUrl(fileId) ?: return null).readBytes()
    }

    fun getDiscordEmbeddableGoogleDriveUrl(fileId: String): String? {
        val urlString = getBrowserViewableGoogleDriveUrl(fileId)
        val connection = URL(urlString)
            .openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = false
        connection.connect()
        return if (connection.responseCode == 302 || connection.responseCode == 303)
            connection.getHeaderField("Location")
        else
            null
    }

    fun getBrowserViewableGoogleDriveUrl(fileId: String) = "https://drive.google.com/uc?export=view&id=$fileId"

    data class DriveImage(
        val url: String,
        val mimeType: String
    )
}