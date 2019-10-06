package ru.iandreyshev.timemanager.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.iandreyshev.timemanager.domain.Card

@Dao
interface ICardDao {

    @Insert
    fun insert(card: CardEntity): Long

    @Query("SELECT * FROM cards WHERE :id == id")
    fun get(id: Long): CardEntity?

    @Query("SELECT * FROM cards")
    fun getAll(): List<CardEntity>

    @Query("SELECT * FROM cards WHERE `order` = (SELECT MAX (`order`) FROM cards) LIMIT 1")
    fun getLast(): CardEntity?

    @Query("SELECT * FROM cards WHERE :order < `order` LIMIT 1")
    fun getNext(order: Long): CardEntity?

    @Query("SELECT * FROM cards WHERE :order > `order` ORDER BY `order` DESC LIMIT 1")
    fun getPrevious(order: Long): CardEntity?

    @Query("SELECT MAX(`order`) FROM cards")
    fun lastOrder(): Long?

    @Query("SELECT count(*) FROM cards")
    fun count(): Int

    @Query("DELETE FROM cards WHERE :cardId = `id`")
    fun delete(cardId: Long)

}
