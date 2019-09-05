package ru.iandreyshev.timemanager.domain

sealed class RepoUpdate {
    object CardsUpdated : RepoUpdate()
}
