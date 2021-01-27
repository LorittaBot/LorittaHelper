package net.perfectdreams.loritta.helper.utils.dailycatcher

import mu.KotlinLogging

class DailyCatcherTask(val dailyCatcherManager: DailyCatcherManager) : Runnable {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun run() {
        try {
            dailyCatcherManager.doReports()
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong while generating reports!" }
        }
    }
}