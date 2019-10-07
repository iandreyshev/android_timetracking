package ru.iandreyshev.timemanager.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ru.iandreyshev.timemanager.AppActivity
import ru.iandreyshev.timemanager.TimeCardsApp
import ru.iandreyshev.timemanager.domain.cards.CardId
import ru.iandreyshev.timemanager.domain.cards.EventId
import ru.iandreyshev.timemanager.ui.editor.EditorActivity
import ru.iandreyshev.timemanager.ui.editor.EditorViewModel
import ru.iandreyshev.timemanager.domain.system.AppLauncher
import ru.iandreyshev.timemanager.ui.timeline.TimelineActivity
import ru.iandreyshev.timemanager.ui.timeline.TimelineAdapter
import ru.iandreyshev.timemanager.ui.timeline.TimelineViewModel
import ru.iandreyshev.timemanager.ui.timeline.state.NormalState
import ru.iandreyshev.timemanager.ui.tutorial.TutorialActivity
import ru.iandreyshev.timemanager.ui.tutorial.TutorialViewModel

fun TutorialActivity.getViewModel() = getViewModel {
    TutorialViewModel(
        repository = TimeCardsApp.appRepository
    )
}

fun TimelineActivity.getViewModel() =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val vm = TimelineViewModel(
                dateProvider = TimeCardsApp.dateProvider,
                timelineState = NormalState(),
                eventsAdapter = TimelineAdapter(),
                repository = TimeCardsApp.cardsRepository,
                editorObservable = TimeCardsApp.editorObservable
            )
            vm.loadData()

            return vm as T
        }
    })[TimelineViewModel::class.java]

fun EditorActivity.getViewModel(
    cardId: CardId,
    eventId: EventId?
) =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EditorViewModel(
                cardId = cardId,
                eventId = eventId ?: EventId.default(),
                resources = resources,
                repository = TimeCardsApp.cardsRepository,
                dateProvider = TimeCardsApp.dateProvider,
                observer = TimeCardsApp.editorObserver
            ) as T
        }
    })[EditorViewModel::class.java].apply {
        onLoadData()
    }

private inline fun <reified TVM : ViewModel> AppCompatActivity.getViewModel(crossinline buildAction: () -> TVM): TVM =
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = buildAction() as T
    })[TVM::class.java]
