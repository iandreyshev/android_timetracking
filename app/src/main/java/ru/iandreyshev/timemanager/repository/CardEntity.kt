package ru.iandreyshev.timemanager.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.ZonedDateTime

@Entity(tableName = "cards")
@TypeConverters(TimeTypeConverter::class)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var order: Long,
    var date: ZonedDateTime,
    var indexOfDate: Int
)
