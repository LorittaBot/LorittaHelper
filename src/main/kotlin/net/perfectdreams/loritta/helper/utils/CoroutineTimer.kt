package net.perfectdreams.loritta.helper.utils

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.toKotlinDuration

/**
 * Schedules [action] to be executed on [scope] every [period] with a [initialDelay]
 */
fun scheduleCoroutineAtFixedRate(taskName: String, scope: CoroutineScope, period: Duration, initialDelay: Duration = Duration.ZERO, action: RunnableCoroutine) {
    val logger = KotlinLogging.logger(taskName)

    scope.launch(CoroutineName("$taskName Scheduler")) {
        delay(initialDelay)

        val mutex = Mutex()

        while (true) {
            launch(CoroutineName("$taskName Task")) {
                logger.info { "Preparing to run task - Is mutex locked? ${mutex.isLocked}" }
                mutex.withLock {
                    logger.info { "Running task..." }
                    try {
                        action.run()
                    } catch (e: Throwable) {
                        logger.warn(e) { "Uncaught error when running the task!" }
                    }
                    logger.info { "Task has finished running!" }
                }
            }
            logger.info { "Waiting $period to execute next task..." }
            delay(period)
        }
    }
}

/**
 * Schedules [action] to be executed on [scope] every [period] with a [initialDelay]
 */
fun scheduleCoroutineEveryDayAtSpecificTime(
    taskName: String,
    scope: CoroutineScope,
    targetTime: LocalTime,
    action: RunnableCoroutine
) {
    val now = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))

    // Calculate the next execution time (today or tomorrow at 05:00 AM)
    val nextRun = if (now.toLocalTime().isBefore(targetTime)) {
        now.with(targetTime)
    } else {
        now.plusDays(1).with(targetTime)
    }

    // Calculate the delay until the next execution time
    val delayDuration = java.time.Duration.between(now, nextRun)

    scheduleCoroutineAtFixedRate(
        taskName,
        scope,
        24.hours,
        delayDuration.toKotlinDuration(),
        action
    )
}

fun interface RunnableCoroutine {
    suspend fun run()
}