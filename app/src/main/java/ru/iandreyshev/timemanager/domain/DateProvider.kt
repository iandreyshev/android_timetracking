package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

class DateProvider : IDateProvider {

    private var mDate = ZonedDateTime.now()

    override fun get(): ZonedDateTime = mDate

    override fun current(): ZonedDateTime = ZonedDateTime.now()

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

}
