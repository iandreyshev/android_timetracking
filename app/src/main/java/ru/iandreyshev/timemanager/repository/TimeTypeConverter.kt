package ru.iandreyshev.timemanager.repository

import androidx.room.TypeConverter
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

object TimeTypeConverter {

    private val mFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toZonedDateTime(value: String?): ZonedDateTime? {
        return value?.let {
            return mFormatter.parse(value, ZonedDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromZonedDateTime(date: ZonedDateTime?): String? {
        return date?.format(mFormatter)
    }

}