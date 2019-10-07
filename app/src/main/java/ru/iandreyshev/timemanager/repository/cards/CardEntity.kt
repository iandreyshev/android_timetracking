package ru.iandreyshev.timemanager.repository.cards

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.repository.cards.TimeTypeConverter

@Entity(tableName = "cards")
@TypeConverters(TimeTypeConverter::class)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var order: Long,
    var date: ZonedDateTime,
    var indexOfDate: Int
)
