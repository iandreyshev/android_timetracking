package ru.iandreyshev.timemanager.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.TimeWalkerApp
import ru.iandreyshev.timemanager.domain.Card
import ru.iandreyshev.timemanager.domain.CardId
import ru.iandreyshev.timemanager.domain.IDateProvider
import ru.iandreyshev.timemanager.domain.IEventsRepo
import ru.iandreyshev.timemanager.ui.extensions.getTitleViewState
import ru.iandreyshev.timemanager.ui.extensions.asViewState
import java.lang.IllegalStateException

class TimelineViewModel(
        private val dateProvider: IDateProvider,
        private val eventsRepo: IEventsRepo
) : ViewModel() {

    val eventsAdapter: RecyclerView.Adapter<*> by lazy { mEventsAdapter }
    val timelineViewState: LiveData<TimelineViewState> by lazy { mTimelineViewState }
    val hasEventsList: LiveData<Boolean> by lazy { mHasEvents }
    val arrowsViewState: LiveData<Pair<Boolean, Boolean>> by lazy { mArrowsViewState }
    val cardTitleViewState: LiveData<String> by lazy { mCardTitleViewState }

    private val mEventsAdapter = TimelineAdapter(::onEventClick)
    private val mTimelineViewState = MutableLiveData(TimelineViewState.PRELOADER)
    private val mHasEvents = MutableLiveData(false)
    private val mArrowsViewState = MutableLiveData(false to false)
    private val mCardTitleViewState = MutableLiveData<String>()

    private var mCurrentCard: Card = newCardStub()

    init {
        eventsRepo.onEventUpdated { updateCurrentEvents() }
    }

    fun loadData() {
        viewModelScope.launch {
            if (eventsRepo.hasCards()) {
                mTimelineViewState.value = TimelineViewState.PRELOADER
                mCurrentCard = eventsRepo.getActualCard(currentDate = dateProvider.current())
                        ?: throw IllegalStateException("Actual card is null")
                mCardTitleViewState.value = mCurrentCard.getTitleViewState()

                updateArrows()
                updateCurrentEvents()
                mTimelineViewState.value = TimelineViewState.TIMELINE
            } else {
                mTimelineViewState.value = TimelineViewState.EMPTY
            }
        }
    }

    fun onCreateFirstCard() {
        viewModelScope.launch {
            val currentDate = dateProvider.current()
            val newCard = Card(CardId(0L), currentDate)
            mCurrentCard = eventsRepo.createCard(newCard)

            if (eventsRepo.hasCards()) {
                mTimelineViewState.value = TimelineViewState.TIMELINE
            }

            updateCurrentEvents()
            updateArrows()
            updateCurrentEvents()
        }
    }

    fun onPreviousDate() {
        viewModelScope.launch {
            val previous = eventsRepo.getPreviousCard(mCurrentCard) ?: return@launch
            mCardTitleViewState.value = previous.getTitleViewState()
            mEventsAdapter.events = eventsRepo.getEvents(previous).asViewState()
            mCurrentCard = previous
        }
    }

    fun onNextDate() {
        viewModelScope.launch {
            val next = eventsRepo.getNextCard(mCurrentCard) ?: return@launch
            mCardTitleViewState.value = next.getTitleViewState()
            mEventsAdapter.events = eventsRepo.getEvents(next).asViewState()
            mCurrentCard = next
        }
    }

    fun onOpenDatePicker(): Boolean {
        return true
    }

    fun onResetToCurrent() {
        mCurrentCard = eventsRepo.getActualCard(currentDate = dateProvider.current())
                ?: throw IllegalStateException("Actual card is null")
    }

    fun onCreateEvent() {
        TimeWalkerApp.navigator.openEditor(mCurrentCard, null)
    }

    private fun onEventClick(position: Int) {
        val eventToEdit = mEventsAdapter.events[position].id
        TimeWalkerApp.navigator.openEditor(mCurrentCard, eventToEdit)
    }

    private suspend fun updateArrows() {
        val hasPrevious = eventsRepo.getPreviousCard(mCurrentCard) != null
        val hasNext = eventsRepo.getNextCard(mCurrentCard) != null
        mArrowsViewState.value = hasPrevious to hasNext
    }

    private suspend fun updateCurrentEvents() {
        val events = eventsRepo.getEvents(mCurrentCard)
        mEventsAdapter.events = events.asViewState()
        mHasEvents.value = events.isNotEmpty()
    }

    private fun newCardStub() = Card(CardId(0L), ZonedDateTime.now())

}
