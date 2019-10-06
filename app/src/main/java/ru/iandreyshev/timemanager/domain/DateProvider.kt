package ru.iandreyshev.timemanager.domain

import org.threeten.bp.*
import org.threeten.bp.temporal.TemporalAccessor
import java.util.*

class DateProvider : IDateProvider {

    private var mDate = ZonedDateTime.now()

    override fun get(): ZonedDateTime = mDate

    override fun current(): ZonedDateTime = ZonedDateTime.now()

    override fun currentAsJavaDate(): Date = Date()

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
        return Date(zonedDateTime.toInstant().toEpochMilli())
    }

    override fun asZonedDateTime(date: Date, time: Date): ZonedDateTime {
        val zonedDate = ZonedDateTime.ofInstant(Instant.ofEpochMilli(date.time), ZoneId.systemDefault())
        val zonedTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time.time), ZoneId.systemDefault())

        return ZonedDateTime.of(zonedDate.toLocalDate(), zonedTime.toLocalTime(), ZoneId.systemDefault())
    }

}
