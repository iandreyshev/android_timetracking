package ru.iandreyshev.timemanager.domain

inline class CardId(val value: Long) {

    companion object {
        fun default() = CardId(0)
    }

}
