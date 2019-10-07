package ru.iandreyshev.timemanager.domain.cards

inline class CardId(val value: Long) {

    companion object {
        fun default() = CardId(0)
    }

}
