package net.perfectdreams.loritta.helper.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object TimeUtils {
    private val TIME_PATTERN = "(([01]?\\d|2[0-3]):([0-5]\\d?)(:([0-5]\\d))?) ?(am|pm)?".toPattern()
    private val DATE_PATTERN = "(0[1-9]|[12][0-9]|3[01])[-/.](0[1-9]|1[012])[-/.]([0-9]+)".toPattern()
    private val YEAR_PATTERN = "([0-9]+) ?(y|a)".toPattern()
    private val MONTH_PATTERN = "([0-9]+) ?(month(s)?|m(e|ê)s(es)?)".toPattern()
    private val WEEK_PATTERN = "([0-9]+) ?(w)".toPattern()
    private val DAY_PATTERN = "([0-9]+) ?(d)".toPattern()
    private val HOUR_PATTERN = "([0-9]+) ?(h)".toPattern()
    private val SHORT_MINUTE_PATTERN = "([0-9]+) ?(m)".toPattern()
    private val MINUTE_PATTERN = "([0-9]+) ?(min)".toPattern()
    private val SECONDS_PATTERN = "([0-9]+) ?(s)".toPattern()
    // TODO: Would be better to not hardcode it
    val TIME_ZONE = ZoneId.of("America/Sao_Paulo")

    fun convertToMillisRelativeToNow(input: String) = convertToLocalDateTimeRelativeToNow(input)
        .toInstant()
        .toEpochMilli()

    fun convertToLocalDateTimeRelativeToNow(input: String) = convertToLocalDateTimeRelativeToTime(input, ZonedDateTime.now(TIME_ZONE))

    fun convertToLocalDateTimeRelativeToTime(input: String, relativeTo: ZonedDateTime): ZonedDateTime {
        val content = input.toLowerCase()
        var localDateTime = relativeTo
            .withNano(0)
        var foundViaTime = false

        // This is here instead of on the convertToMillisDurationRelative because durations don't have concept of "plus one month" (is it 31 or 30 days?)
        val yearsMatcher = YEAR_PATTERN.matcher(content)
        if (yearsMatcher.find()) {
            val addYears = yearsMatcher.group(1).toLongOrNull() ?: 0
            localDateTime = localDateTime.plus(addYears, ChronoUnit.YEARS)
        }
        val monthMatcher = MONTH_PATTERN.matcher(content)
        if (monthMatcher.find()) {
            val addMonths = monthMatcher.group(1).toLongOrNull() ?: 0
            localDateTime = localDateTime.plus(addMonths, ChronoUnit.MONTHS)
        }
        val weekMatcher = WEEK_PATTERN.matcher(content)
        if (weekMatcher.find()) {
            val addWeeks = weekMatcher.group(1).toLongOrNull() ?: 0
            localDateTime = localDateTime.plusDays(addWeeks)
        }

        if (content.contains(":")) { // horário
            val matcher = TIME_PATTERN.matcher(content)

            if (matcher.find()) { // Se encontrar...
                val hour = matcher.group(2).toIntOrNull() ?: 0
                val minute = matcher.group(3).toIntOrNull() ?: 0
                val seconds = try {
                    matcher.group(5)?.toIntOrNull() ?: 0
                } catch (e: IllegalStateException) {
                    0
                }

                var meridiem = try {
                    matcher.group(6)
                } catch (e: IllegalStateException) {
                    null
                }

                // Horários que usam o meridiem
                if (meridiem != null) {
                    meridiem = meridiem.replace(".", "").replace(" ", "")
                    if (meridiem.equals("pm", true)) { // Se for PM, aumente +12
                        localDateTime = localDateTime.withHour((hour % 12) + 12)
                    } else { // Se for AM, mantenha do jeito atual
                        localDateTime = localDateTime.withHour(hour % 12)
                    }
                } else {
                    localDateTime = localDateTime.withHour(hour)
                }
                localDateTime = localDateTime
                    .withMinute(minute)
                    .withSecond(seconds)

                foundViaTime = true
            }
        }

        if (content.contains("/")) { // data
            val matcher = DATE_PATTERN.matcher(content)

            if (matcher.find()) { // Se encontrar...
                val day = matcher.group(1).toIntOrNull() ?: 1
                val month = matcher.group(2).toIntOrNull() ?: 1
                val year = matcher.group(3).toIntOrNull() ?: 1999

                // This is a hack
                // This fixes bugs when you use "31/12/2024" on a month that does NOT have 31 days "Invalid date 'JUNE 31'"
                localDateTime = localDateTime.withYear(1999)
                    .withMonth(1)
                    .withDayOfMonth(1)
                    .withYear(year)
                    .withMonth(month)
                    .withDayOfMonth(day)
            }
        } else if (foundViaTime && localDateTime.isBefore(LocalDateTime.now().atZone(TIME_ZONE))) {
            // If it was found via time but there isn't any day set, we are going to check if it is in the past and, if true, we are going to add one day
            localDateTime = localDateTime
                .plusDays(1)
        }

        val duration = convertToMillisDurationRelative(content)
        localDateTime = localDateTime.plus(duration)

        return localDateTime
    }

    /**
     * Converts a [content] into a [Duration]
     */
    fun convertToMillisDurationRelative(content: String): Duration {
        var duration = Duration.ZERO

        val dayMatcher = DAY_PATTERN.matcher(content)
        if (dayMatcher.find()) {
            val addDays = dayMatcher.group(1).toLongOrNull() ?: 0
            duration = duration.plus(addDays, ChronoUnit.DAYS)
        }
        val hourMatcher = HOUR_PATTERN.matcher(content)
        if (hourMatcher.find()) {
            val addHours = hourMatcher.group(1).toLongOrNull() ?: 0
            duration = duration.plus(addHours, ChronoUnit.HOURS)
        }

        val minuteMatcher = MINUTE_PATTERN.matcher(content)
        if (minuteMatcher.find()) {
            val addMinutes = minuteMatcher.group(1).toLongOrNull() ?: 0
            duration = duration.plus(addMinutes, ChronoUnit.MINUTES)
        }

        val secondsMatcher = SECONDS_PATTERN.matcher(content)
        if (secondsMatcher.find()) {
            val addSeconds = secondsMatcher.group(1).toLongOrNull() ?: 0
            duration = duration.plus(addSeconds, ChronoUnit.SECONDS)
        }

        return duration
    }
}