package ru.iandreyshev.timemanager.domain.cards

import org.threeten.bp.ZonedDateTime
import java.util.*

interface IDateProvider {
    fun get(): ZonedDateTime
    fun current(): ZonedDateTime
    fun currentAsJavaDate(): Date

    fun setNextDay(): ZonedDateTime
    fun setPreviousDay(): ZonedDateTime
    fun setCurrent(): ZonedDateTime

    fun asZonedDateTime(hourOfDay: Int, minute: Int): ZonedDateTime
    fun asZonedDateTime(year: Int, month: Int, dayOfMonth: Int): ZonedDateTime
    fun asZonedDateTime(date: Date, time: Date): ZonedDateTime
    fun asEpochTime(zonedDateTime: ZonedDateTime): Date
}
