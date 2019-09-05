package ru.iandreyshev.timemanager.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
class CardEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var title: String,
    var order: Long
)
