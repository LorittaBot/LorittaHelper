package net.perfectdreams.logging

import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.rolling.helper.CompressionMode
import ch.qos.logback.core.rolling.helper.Compressor
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.Future


/**
 * A Mojang-like log rollover
 *
 * The log files are rolled over on application startup, and after each day
 *
 * Yes, I know, this should be implemented as a rolling policy... but I don't care lmao, Logback is very complex.
 *
 * @author MrPowerGamerBR
 */
class MojangStyleFileAppenderAndRollover<E> : FileAppender<E>() {
    private lateinit var nextRolloverDate: LocalDateTime
    private lateinit var compressor: Compressor
    private var compressionFuture: Future<*>? = null
    private var archivedLogsFilePrefix: String? = null

    fun setArchivedLogsFilePrefix(archivedLogsFilePrefix: String) {
        this.archivedLogsFilePrefix = archivedLogsFilePrefix
    }

    override fun start() {
        compressor = Compressor(CompressionMode.GZ)
        compressor.context = this.context

        // Trigger the rollover when the application first starts, we need to do it here on start instead of on isTriggeringEvent, because Logback WILL write the log line to the log file BEFORE it is triggered!
        // This must be ran BEFORE the super.start(), to avoid an empty file being created due to the openFile call
        val lastActiveLogFile = File(file)
        if (lastActiveLogFile.exists()) {
            // The lastActiveLogFile exists, let's rollover!
            rollover(
                Instant.ofEpochMilli(lastActiveLogFile.lastModified())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            )
        }

        nextRolloverDate = getNextRolloverDate(LocalDateTime.now())

        super.start()
    }

    override fun writeOut(event: E) {
        // Check if we need to rotate the log file
        val date = LocalDateTime.now()

        // Trigger rollover if the current date is >= the nextRolloverDate, the nextRolloverDate will be updated on rollover
        if (date >= nextRolloverDate) {
            rollover()
        }

        super.writeOut(event)
    }

    private fun rollover() {
        val date = LocalDateTime.now()

        // Calculate next rollover date
        nextRolloverDate = getNextRolloverDate(date)

        rollover(date)
    }

    private fun rollover(date: LocalDateTime) {
        try {
            // Check what index we should use
            var index = 1
            val compressedFile: File?
            while (true) {
                val fileName = "${date.year}-${date.monthValue.toString().padStart(2, '0')}-${date.dayOfMonth.toString().padStart(2, '0')}.$index"

                val newCompressedFile = File("$archivedLogsFilePrefix$fileName.log.gz")
                if (!newCompressedFile.exists()) {
                    compressedFile = newCompressedFile
                    break
                }
                index++
            }

            closeOutputStream() // Close the output stream to let the file be moved

            // Bye to the raw file!!! If it exists it is GONE
            val compressedFileRaw = File("$archivedLogsFilePrefix${UUID.randomUUID()}.log")
            compressedFileRaw.delete()

            File(file).renameTo(compressedFileRaw)

            // Reopen the latest.log file
            openFile(file)

            val future = compressor.asyncCompress(
                compressedFileRaw.toString(),
                compressedFile.toString(),
                "xd" // Not used for gz files
            )

            this.compressionFuture = future
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Tomorrow at midnight
    private fun getNextRolloverDate(date: LocalDateTime) = date.plusDays(1)
        .withHour(0)
        .withMinute(0)
        .withNano(0)
}