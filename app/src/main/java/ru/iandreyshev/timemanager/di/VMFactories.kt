package ru.iandreyshev.timemanager.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.TimeWalkerApp
import ru.iandreyshev.timemanager.domain.Event
import ru.iandreyshev.timemanager.ui.editor.EditorActivity
import ru.iandreyshev.timemanager.ui.editor.EditorViewModel
import ru.iandreyshev.timemanager.ui.timeline.TimelineFragment
import ru.iandreyshev.timemanager.ui.timeline.TimelineViewModel

fun TimelineFragment.getViewModel() =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TimelineViewModel(
                dateProvider = TimeWalkerApp.dateProvider,
                eventsRepo = TimeWalkerApp.eventsRepo
            ) as T
        }
    })[TimelineViewModel::class.java]

fun EditorActivity.getViewModel(date: ZonedDateTime, eventToEdit: Event?) =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EditorViewModel(
                date = date,
                eventToEdit = eventToEdit,
                eventsRepo = TimeWalkerApp.eventsRepo
            ) as T
        }
    })[EditorViewModel::class.java]
