package ru.iandreyshev.timemanager.repository

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class EventEntity(
    @PrimaryKey
    var id: Long
)
