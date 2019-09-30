package ru.iandreyshev.timemanager.domain

sealed class RepoError {
    object Unknown : RepoError()
    object EarlierThanFirst : RepoError()
}
