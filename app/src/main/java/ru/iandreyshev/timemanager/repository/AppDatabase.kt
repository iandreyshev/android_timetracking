package ru.iandreyshev.timemanager.repository

import androidx.room.RoomDatabase
import androidx.room.Database

@Database(entities = [CardEntity::class, EventEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun eventDao(): EventDao
}
