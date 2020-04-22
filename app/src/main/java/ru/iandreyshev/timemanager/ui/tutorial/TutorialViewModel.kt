package ru.iandreyshev.timemanager.ui.tutorial

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.TimeCardsApp
import ru.iandreyshev.timemanager.domain.cards.EventId
import ru.iandreyshev.timemanager.domain.system.IAppRepository
import ru.iandreyshev.timemanager.ui.extensions.END_DATE_FORMATTER
import ru.iandreyshev.timemanager.ui.extensions.START_DATE_FORMATTER
import ru.iandreyshev.timemanager.ui.extensions.asTimerTitleViewState
import ru.iandreyshev.timemanager.ui.timeline.EventSelectionViewState
import ru.iandreyshev.timemanager.ui.timeline.EventViewState
import ru.iandreyshev.timemanager.ui.utils.LiveDataEvent
import ru.iandreyshev.timemanager.ui.utils.execute
import ru.iandreyshev.timemanager.utils.betweenWithSecondsRounding
import ru.iandreyshev.timemanager.utils.exhaustive

class TutorialViewModel(
    private val resources: Resources,
    private val repository: IAppRepository
) : ViewModel() {

    val startTransitionEvent = MutableLiveData<LiveDataEvent<TutorialState>>()
    val event1ViewState = MutableLiveData<EventViewState>()
    val event2ViewState = MutableLiveData<EventViewState>()
    val event3ViewState = MutableLiveData<EventViewState>()
    val timerViewState = MutableLiveData<String>()

    private var mFirstSelectedEvent: TutorialEvent? = null
    private var mSecondSelectedEvent: TutorialEvent? = null

    init {
        event1ViewState.value = EventViewState(
            id = EventId(0),
            title = resources.getString(R.string.tutorial_event_1_title),
            startTime = EVENT_1_START.format(START_DATE_FORMATTER),
            endTime = EVENT_1_END.format(END_DATE_FORMATTER),
            isMiddleEndTime = false,
            durationInMinutes = betweenWithSecondsRounding(EVENT_1_START, EVENT_1_END),
            selection = EventSelectionViewState.Normal
        )
        event2ViewState.value = EventViewState(
            id = EventId(0),
            title = resources.getString(R.string.tutorial_event_2_title),
            startTime = EVENT_2_START.format(START_DATE_FORMATTER),
            endTime = EVENT_2_END.format(END_DATE_FORMATTER),
            isMiddleEndTime = true,
            durationInMinutes = betweenWithSecondsRounding(EVENT_1_END, EVENT_2_END),
            selection = EventSelectionViewState.Normal
        )
        event3ViewState.value = EventViewState(
            id = EventId(0),
            title = resources.getString(R.string.tutorial_event_3_title),
            startTime = EVENT_3_START.format(START_DATE_FORMATTER),
            endTime = EVENT_3_END.format(END_DATE_FORMATTER),
            isMiddleEndTime = false,
            durationInMinutes = betweenWithSecondsRounding(EVENT_2_END, EVENT_3_END),
            selection = EventSelectionViewState.Normal
        )
    }

    fun onTutorialCompleted() {
        repository.setFirstLaunch(true)
        TimeCardsApp.navigator.openCards()
    }

    fun onFirstEventSelected(event: TutorialEvent): Boolean {
        if (mFirstSelectedEvent != null) {
            return false
        }

        event1ViewState.updateTimerModeSelection(event == TutorialEvent.EVENT_1)
        event2ViewState.updateTimerModeSelection(event == TutorialEvent.EVENT_2)
        event3ViewState.updateTimerModeSelection(event == TutorialEvent.EVENT_3)

        mFirstSelectedEvent = event

        startTransitionEvent.execute(TutorialState.ONE_EVENT_SELECTED)

        timerViewState.value = calculateTimerState()

        return true
    }

    fun onSecondEventSelected(event: TutorialEvent) {
        if (mFirstSelectedEvent == null
            || mFirstSelectedEvent == event
            || mSecondSelectedEvent != null
        ) {
            return
        }

        mSecondSelectedEvent = event
        startTransitionEvent.execute(TutorialState.TWO_EVENTS_SELECTED)

        when (event) {
            TutorialEvent.EVENT_1 -> event1ViewState.updateTimerModeSelection(true)
            TutorialEvent.EVENT_2 -> event2ViewState.updateTimerModeSelection(true)
            TutorialEvent.EVENT_3 -> event3ViewState.updateTimerModeSelection(true)
        }.exhaustive

        timerViewState.value = calculateTimerState()
    }

    private fun calculateTimerState(): String {
        fun getEventDurationInMinutes(event: TutorialEvent?): Int = when (event) {
            TutorialEvent.EVENT_1 -> betweenWithSecondsRounding(EVENT_1_START, EVENT_1_END)
            TutorialEvent.EVENT_2 -> betweenWithSecondsRounding(EVENT_1_END, EVENT_2_END)
            TutorialEvent.EVENT_3 -> betweenWithSecondsRounding(EVENT_2_END, EVENT_3_END)
            null -> 0
        }

        val selectedEventsDuration = getEventDurationInMinutes(mFirstSelectedEvent) +
                getEventDurationInMinutes(mSecondSelectedEvent)

        return selectedEventsDuration.asTimerTitleViewState(resources)
    }

    private fun MutableLiveData<EventViewState>.updateTimerModeSelection(isSelected: Boolean) {
        value = value?.apply {
            selection = EventSelectionViewState.TimerMode(isSelected)
        }
    }

    companion object {
        private val EVENT_1_START =
            ZonedDateTime.of(2015, 12, 10, 8, 0, 0, 0, ZoneId.systemDefault())
        private val EVENT_1_END =
            ZonedDateTime.of(2015, 12, 10, 8, 30, 0, 0, ZoneId.systemDefault())
        private val EVENT_2_START =
            ZonedDateTime.of(2015, 12, 10, 8, 30, 0, 0, ZoneId.systemDefault())
        private val EVENT_2_END =
            ZonedDateTime.of(2015, 12, 10, 9, 15, 0, 0, ZoneId.systemDefault())
        private val EVENT_3_START =
            ZonedDateTime.of(2015, 12, 10, 9, 15, 0, 0, ZoneId.systemDefault())
        private val EVENT_3_END =
            ZonedDateTime.of(2015, 12, 10, 10, 0, 0, 0, ZoneId.systemDefault())
    }

}
