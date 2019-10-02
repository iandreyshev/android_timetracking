package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime
import java.util.*

interface IDateProvider {
    fun get(): ZonedDateTime
    fun current(): ZonedDateTime
    fun current2(): Date

    fun setNextDay(): ZonedDateTime
    fun setPreviousDay(): ZonedDateTime
    fun setCurrent(): ZonedDateTime

    fun asZonedDateTime(date: Date, time: Date): ZonedDateTime
    fun asEpochTime(zonedDateTime: ZonedDateTime): Date
}
