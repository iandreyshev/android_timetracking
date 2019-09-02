package ru.iandreyshev.timemanager.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CardEntity(
    @PrimaryKey
    var id: Long
)
