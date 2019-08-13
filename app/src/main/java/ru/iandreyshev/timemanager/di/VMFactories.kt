package ru.iandreyshev.timemanager.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ru.iandreyshev.timemanager.domain.DateProvider
import ru.iandreyshev.timemanager.ui.timer.TimerFragment
import ru.iandreyshev.timemanager.ui.timer.TimerViewModel

fun TimerFragment.getViewModel() =
        ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TimerViewModel(DateProvider()) as T
            }
        })[TimerViewModel::class.java]
