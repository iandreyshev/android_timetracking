package ru.iandreyshev.timemanager.domain.cards

sealed class RepoError {
    object Unknown : RepoError()
    object EarlierThanFirst : RepoError()
}
