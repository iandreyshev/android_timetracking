package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

interface IDateProvider {
    fun get(): ZonedDateTime
    fun current(): ZonedDateTime

    fun setNextDay(): ZonedDateTime
    fun setPreviousDay(): ZonedDateTime
    fun setCurrent(): ZonedDateTime
}
