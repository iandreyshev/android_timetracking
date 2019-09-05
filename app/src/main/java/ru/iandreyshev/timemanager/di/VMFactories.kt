package ru.iandreyshev.timemanager.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ru.iandreyshev.timemanager.AppActivity
import ru.iandreyshev.timemanager.TimeWalkerApp
import ru.iandreyshev.timemanager.domain.CardId
import ru.iandreyshev.timemanager.domain.EventId
import ru.iandreyshev.timemanager.ui.editor.EditorActivity
import ru.iandreyshev.timemanager.ui.editor.EditorViewModel
import ru.iandreyshev.timemanager.ui.timeline.TimelineViewModel

fun AppActivity.getViewModel() =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val vm = TimelineViewModel(
                dateProvider = TimeWalkerApp.dateProvider,
                eventsRepo = TimeWalkerApp.eventsRepo
            )
            vm.loadData()

            return vm as T
        }
    })[TimelineViewModel::class.java]

fun EditorActivity.getViewModel(cardId: CardId, eventId: EventId?) =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EditorViewModel(
                cardId = cardId,
                eventId = eventId ?: EventId.default(),
                eventsRepo = TimeWalkerApp.eventsRepo,
                dateProvider = TimeWalkerApp.dateProvider
            ) as T
        }
    })[EditorViewModel::class.java].apply {
        onLoadData()
    }
