package ru.iandreyshev.timemanager.utils

import org.threeten.bp.Duration
import org.threeten.bp.temporal.Temporal

fun betweenWithSecondsRounding(startInclusive: Temporal, endExclusive: Temporal): Int {
    val duration = Duration.between(startInclusive, endExclusive)
    val durationInMinutes = duration.toMinutes().toInt()
    val secondsLeft = duration.seconds % (durationInMinutes * 60)

    return if (secondsLeft > 0) durationInMinutes + 1 else durationInMinutes
}
