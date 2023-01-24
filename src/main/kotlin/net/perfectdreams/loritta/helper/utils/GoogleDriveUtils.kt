package net.perfectdreams.loritta.helper.utils

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

object GoogleDriveUtils {
    suspend fun retrieveImageFromDrive(url: String, httpClient: HttpClient): DriveImage? {
        val request = httpClient.get(url)

        if (request.status == HttpStatusCode.OK) {
            val driveUrl = request.bodyAsText(Charsets.UTF_8)
                .substringAfter("<meta property=\"og:image\" content=\"")
                .substringBefore("\"")
                .substringBeforeLast("=w")

            return DriveImage(driveUrl, "png")
        }
        return null
    }

    data class DriveImage(
        val url: String,
        val mimeType: String
    )
}