package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

data class Card(val id: CardId, val date: ZonedDateTime) : Comparable<Card> {

    override fun compareTo(other: Card): Int =
        when {
            date.isBefore(other.date) -> -1
            date.isAfter(other.date) -> 1
            else -> 0
        }

}
