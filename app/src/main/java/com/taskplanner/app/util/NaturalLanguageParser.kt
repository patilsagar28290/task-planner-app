package com.taskplanner.app.util

import com.taskplanner.app.data.model.NotificationFrequency
import com.taskplanner.app.data.model.TaskPriority
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

data class ParsedTaskResult(
    val title: String,
    val scheduledHour: Int,
    val priority: TaskPriority,
    val endDateMillis: Long? = null,
    val notificationFrequency: NotificationFrequency = NotificationFrequency.ONCE
)

object NaturalLanguageParser {

    private val TIME_PATTERN = Pattern.compile(
        """(?i)\b(?:by|at|for)?\s*(\d{1,2})(?::(\d{2}))?\s*(am|pm)\b""",
        Pattern.CASE_INSENSITIVE
    )
    private val TWENTY_FOUR_HOUR_TIME_PATTERN = Pattern.compile(
        """\b([01]?\d|2[0-3]):([0-5]\d)\b"""
    )
    private val DATE_ISO_PATTERN = Pattern.compile(
        """\b(\d{4})-(\d{2})-(\d{2})\b"""
    )

    fun parseInput(rawInput: String, defaultHour: Int): List<ParsedTaskResult> {
        if (rawInput.isBlank()) return emptyList()

        val lines = rawInput.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val results = mutableListOf<ParsedTaskResult>()

        for (line in lines) {
            // Clean leading bullet points if present
            val cleanedLine = line.replace(Regex("""^[\s•\-\*\d\.\)]+"""), "").trim()
            if (cleanedLine.isBlank()) continue

            var workingText = cleanedLine

            // 1. Priority Detection
            val priority = parsePriority(workingText)
            workingText = stripPriorityKeywords(workingText)

            // 2. End Date / Due Date Detection
            val parsedEndDate = parseEndDate(workingText)
            workingText = stripDateKeywords(workingText)

            // 3. Time/Hour Detection
            val parsedHour = parseHour(workingText, defaultHour)
            workingText = stripTimeKeywords(workingText)

            // 4. Frequency Detection
            val freq = parseFrequency(workingText)
            workingText = stripFrequencyKeywords(workingText)

            val finalTitle = workingText.trim().replace(Regex("""\s+"""), " ")
            if (finalTitle.isNotBlank()) {
                results.add(
                    ParsedTaskResult(
                        title = finalTitle.capitalizeFirst(),
                        scheduledHour = parsedHour.coerceIn(0, 23),
                        priority = priority,
                        endDateMillis = parsedEndDate,
                        notificationFrequency = freq
                    )
                )
            }
        }

        return results
    }

    private fun parsePriority(text: String): TaskPriority {
        val lower = text.lowercase(Locale.getDefault())
        return when {
            lower.contains("high priority") || lower.contains("high") || lower.contains("urgent") || lower.contains("p1") -> TaskPriority.HIGH
            lower.contains("low priority") || lower.contains("low") || lower.contains("p3") -> TaskPriority.LOW
            lower.contains("medium priority") || lower.contains("med priority") || lower.contains("medium") || lower.contains("med") || lower.contains("p2") -> TaskPriority.MEDIUM
            else -> TaskPriority.MEDIUM
        }
    }

    private fun stripPriorityKeywords(text: String): String {
        return text
            .replace(Regex("""(?i)[\-\[\(\s]*(high|medium|med|low)\s*priority[\-\]\)\s]*"""), " ")
            .replace(Regex("""(?i)[\-\[\(\s]*urgent[\-\]\)\s]*"""), " ")
            .replace(Regex("""(?i)[\-\[\(\s]*p[123][\-\]\)\s]*"""), " ")
            .replace(Regex("""(?i)\s*[\-\|]\s*(high|medium|med|low)\b"""), " ")
    }

    private fun parseEndDate(text: String): Long? {
        val lower = text.lowercase(Locale.getDefault())
        val cal = Calendar.getInstance()

        // Check ISO format YYYY-MM-DD
        val isoMatcher = DATE_ISO_PATTERN.matcher(text)
        if (isoMatcher.find()) {
            val year = isoMatcher.group(1)?.toIntOrNull()
            val month = isoMatcher.group(2)?.toIntOrNull()
            val day = isoMatcher.group(3)?.toIntOrNull()
            if (year != null && month != null && day != null) {
                cal.set(year, month - 1, day, 23, 59, 59)
                return cal.timeInMillis
            }
        }

        return when {
            lower.contains("today") -> {
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.timeInMillis
            }
            lower.contains("tomorrow") -> {
                cal.add(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.timeInMillis
            }
            lower.contains("next week") -> {
                cal.add(Calendar.DAY_OF_YEAR, 7)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.timeInMillis
            }
            else -> null
        }
    }

    private fun stripDateKeywords(text: String): String {
        var result = DATE_ISO_PATTERN.matcher(text).replaceAll("")
        return result
            .replace(Regex("""(?i)\b(end date:?|due:?|by|on)?\s*(today|tomorrow|next week)\b"""), " ")
            .replace(Regex("""(?i)\bend date:?\s*"""), " ")
            .replace(Regex("""(?i)\bdue:?\s*"""), " ")
            .trim()
    }

    private fun parseHour(text: String, defaultHour: Int): Int {
        val matcher12 = TIME_PATTERN.matcher(text)
        if (matcher12.find()) {
            val hourStr = matcher12.group(1) ?: return defaultHour
            var hour = hourStr.toIntOrNull() ?: defaultHour
            val amPm = matcher12.group(3)?.lowercase(Locale.getDefault()) ?: ""
            if (amPm == "pm" && hour < 12) hour += 12
            if (amPm == "am" && hour == 12) hour = 0
            return hour
        }

        val matcher24 = TWENTY_FOUR_HOUR_TIME_PATTERN.matcher(text)
        if (matcher24.find()) {
            val hourStr = matcher24.group(1)
            return hourStr?.toIntOrNull() ?: defaultHour
        }

        return defaultHour
    }

    private fun stripTimeKeywords(text: String): String {
        var result = TIME_PATTERN.matcher(text).replaceAll("")
        result = TWENTY_FOUR_HOUR_TIME_PATTERN.matcher(result).replaceAll("")
        return result
            .replace(Regex("""(?i)\b(by|at|for)\s*$"""), "")
            .trim()
    }

    private fun parseFrequency(text: String): NotificationFrequency {
        val lower = text.lowercase(Locale.getDefault())
        return when {
            lower.contains("freq none") -> NotificationFrequency.NONE
            lower.contains("freq once") -> NotificationFrequency.ONCE
            lower.contains("freq hourly") -> NotificationFrequency.HOURLY
            lower.contains("freq daily") -> NotificationFrequency.DAILY
            else -> NotificationFrequency.ONCE
        }
    }

    private fun stripFrequencyKeywords(text: String): String {
        return text
            .replace(Regex("""(?i)[\-\s]*freq (none|once|hourly|daily)[\-\s]*"""), " ")
            .trim()
    }

    private fun String.capitalizeFirst(): String {
        if (isEmpty()) return this
        return substring(0, 1).uppercase(Locale.getDefault()) + substring(1)
    }
}

