package ru.iandreyshev.timemanager.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface IEventDao {

    @Insert
    fun insert(entity: EventEntity): Long

    @Query("SELECT * FROM events WHERE :id == id LIMIT 1")
    fun get(id: Long): EventEntity?

    @Query("SELECT * FROM events WHERE :cardId == cardId ORDER BY endTime DESC")
    fun getAll(cardId: Long): List<EventEntity>

    @Update
    fun update(eventEntity: EventEntity)

}
