package net.perfectdreams.loritta.helper.utils

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

object GoogleDriveUtils {
    fun getEmbeddableDirectGoogleDriveUrl(fileId: String) = "https://drive.google.com/uc?export=view&id=$fileId"

    suspend fun retrieveImageFromDrive(url: String, httpClient: HttpClient): DriveImage? {
        val request = httpClient.get<HttpResponse>(url)

        if (request.status == HttpStatusCode.OK) {
            val array = Json.parseToJsonElement(
                request.readText()
                    .substringAfter("window.viewerData = ")
                    .substringBefore("};")
                    .substringAfter("itemJson: ")
            ).jsonArray
            return DriveImage(array[10].jsonPrimitive.content, array[11].jsonPrimitive.content)
        }
        return null
    }

    data class DriveImage(
        val url: String,
        val mimeType: String
    )
}