package ru.iandreyshev.timemanager.repository.cards

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.iandreyshev.timemanager.domain.cards.EventId

@Dao
interface IEventDao {

    @Insert
    fun insert(entity: EventEntity): Long

    @Query("SELECT * FROM events WHERE :id == id LIMIT 1")
    fun get(id: Long): EventEntity?

    @Query("SELECT * FROM events WHERE :cardId == cardId ORDER BY endTime DESC")
    fun getAll(cardId: Long): List<EventEntity>

    @Query("SELECT * FROM events WHERE (:cardId == cardId) AND (startTime == (SELECT MIN(startTime) FROM events))")
    fun getFirst(cardId: Long): EventEntity

    @Update
    fun update(eventEntity: EventEntity)

    @Query("DELETE FROM events WHERE :eventId = `id`")
    fun delete(eventId: Long)

    @Query("DELETE FROM events WHERE :cardId = `cardId`")
    fun deleteAll(cardId: Long)

}
