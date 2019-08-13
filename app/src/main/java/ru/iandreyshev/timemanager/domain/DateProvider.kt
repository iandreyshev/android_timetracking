package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

class DateProvider : IDateProvider {

    private val mCurrentDate
        get() = ZonedDateTime.now()

    override fun currentDate(): ZonedDateTime = mCurrentDate
    override fun nextDay(): ZonedDateTime = mCurrentDate.plusDays(1)
    override fun previousDate(): ZonedDateTime = mCurrentDate.minusDays(1)

}
