package ru.iandreyshev.timemanager.repository.cards

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.domain.cards.CardId
import ru.iandreyshev.timemanager.domain.cards.Event

@Entity(tableName = "events")
@TypeConverters(TimeTypeConverter::class)
class EventEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var cardId: Long,
    var description: String,
    var startTime: ZonedDateTime,
    var endTime: ZonedDateTime,
    var isFirstInCard: Boolean
) {

    companion object {
        fun create(cardIdValue: Long, event: Event) =
            create(
                CardId(
                    cardIdValue
                ), event
            )

        fun create(cardId: CardId, event: Event) =
            EventEntity(
                id = event.id.value,
                cardId = cardId.value,
                description = event.description,
                startTime = event.startDateTime,
                endTime = event.endDateTime,
                isFirstInCard = event.isFirstInCard
            )
    }

}
