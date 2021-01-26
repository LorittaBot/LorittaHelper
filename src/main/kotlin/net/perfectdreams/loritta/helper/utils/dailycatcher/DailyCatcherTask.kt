package net.perfectdreams.loritta.helper.utils.dailycatcher

import mu.KotlinLogging

class DailyCatcherTask(val dailyCatcher: DailyCatcher) : Runnable {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun run() {
        try {
            dailyCatcher.doReports()
        } catch (e: Exception) {
            logger.warn(e) { "Something went wrong while generating reports!" }
        }
    }
}