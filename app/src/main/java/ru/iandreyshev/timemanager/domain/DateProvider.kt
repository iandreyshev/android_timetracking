package ru.iandreyshev.timemanager.domain

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import java.util.*

class DateProvider : IDateProvider {

    private var mDate = ZonedDateTime.now()

    override fun get(): ZonedDateTime = mDate

    override fun current(): ZonedDateTime = ZonedDateTime.now()

    override fun current2(): Date = Date()

    override fun setNextDay(): ZonedDateTime {
        mDate = mDate.plusDays(1)
        return mDate
    }

    override fun setPreviousDay(): ZonedDateTime {
        mDate = mDate.minusDays(1)
        return mDate
    }

    override fun setCurrent(): ZonedDateTime {
        mDate = ZonedDateTime.now()
        return mDate
    }

    override fun asEpochTime(zonedDateTime: ZonedDateTime): Date {
        return Date()
    }

    override fun asZonedDateTime(date: Date, time: Date): ZonedDateTime =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(time.time), ZoneId.systemDefault())

}
