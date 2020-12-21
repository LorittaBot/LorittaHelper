package net.perfectdreams.loritta.helper.utils.generateserverreport

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    // Yes, this SHOULD be random, but because we are encrypting something that doesn't even really matter at all, let's
    // just use a empty init vector.
    //
    // The worst thing that could happen is someone impersonating someone else in the report.
    fun generateInitVector() = ByteArray(16).apply {}

    fun encryptMessage(secretKey: String, content: String): String {
        val initVector = generateInitVector()

        val iv = IvParameterSpec(initVector)
        val skeySpec = SecretKeySpec(secretKey.toByteArray(charset("UTF-8")), "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
        val encrypted = cipher.doFinal(content.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decryptMessage(secretKey: String, encryptedContent: String): String {
        val iv = IvParameterSpec(generateInitVector())
        val skeySpec = SecretKeySpec(secretKey.toByteArray(charset("UTF-8")), "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
        val original = cipher.doFinal(Base64.getDecoder().decode(encryptedContent))

        return String(original)
    }
}