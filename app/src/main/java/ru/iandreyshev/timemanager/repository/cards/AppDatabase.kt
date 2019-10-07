package ru.iandreyshev.timemanager.repository.cards

import androidx.room.RoomDatabase
import androidx.room.Database

@Database(entities = [CardEntity::class, EventEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): ICardDao
    abstract fun eventDao(): IEventDao
}
