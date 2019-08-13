package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

interface IDateProvider {
    fun currentDate(): ZonedDateTime
    fun nextDay(): ZonedDateTime
    fun previousDate(): ZonedDateTime
}
