package ru.iandreyshev.timemanager.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.domain.CardId
import ru.iandreyshev.timemanager.domain.Event
import ru.iandreyshev.timemanager.domain.EventId

@Entity(tableName = "events")
@TypeConverters(TimeTypeConverter::class)
class EventEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var cardId: Long,
    var description: String,
    var endTime: ZonedDateTime
) {

    companion object {
        fun create(cardIdValue: Long, event: Event) =
            create(CardId(cardIdValue), event)

        fun create(cardId: CardId, event: Event) = EventEntity(
            id = event.id.value,
            cardId = cardId.value,
            description = event.description,
            endTime = event.endTime
        )
    }

}
