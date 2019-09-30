package ru.iandreyshev.timemanager.ui.extensions

import android.content.res.Resources
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.domain.RepoError

fun RepoError.asText(resources: Resources) =
    when (this) {
        RepoError.Unknown -> resources.getString(R.string.repo_error_unknown)
        RepoError.EarlierThanFirst -> resources.getString(R.string.repo_error_earlier_than_first)
    }
