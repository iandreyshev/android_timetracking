package ru.iandreyshev.timemanager.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.TimeWalkerApp
import ru.iandreyshev.timemanager.domain.*
import ru.iandreyshev.timemanager.ui.extensions.asViewState
import ru.iandreyshev.timemanager.ui.extensions.getTitleViewState

class TimelineViewModel(
    private val dateProvider: IDateProvider,
    private val eventsRepo: IEventsRepo
) : ViewModel() {

    val eventsAdapter: RecyclerView.Adapter<*> by lazy { mEventsAdapter }
    val timelineViewState: LiveData<TimelineViewState> by lazy { mTimelineViewState }
    val hasEventsList: LiveData<Boolean> by lazy { mHasEvents }
    val arrowsViewState: LiveData<Pair<Boolean, Boolean>> by lazy { mArrowsViewState }
    val cardTitleViewState: LiveData<String> by lazy { mCardTitleViewState }
    val nextCardButtonViewState: LiveData<Boolean> by lazy { mNextCardButtonViewState }

    private val mEventsAdapter = TimelineAdapter(::onEventClick)
    private val mTimelineViewState = MutableLiveData(TimelineViewState.LOADING)
    private val mHasEvents = MutableLiveData(false)
    private val mArrowsViewState = MutableLiveData(false to false)
    private val mNextCardButtonViewState = MutableLiveData(false)
    private val mCardTitleViewState = MutableLiveData<String>()

    private var mCurrentCard: Card = newCardStub()

    init {
        eventsRepo.onEventUpdated { updateCurrentEvents() }
    }

    fun loadData() {
        viewModelScope.launch {
            if (eventsRepo.hasCards()) {
                mTimelineViewState.value = TimelineViewState.LOADING
                mCurrentCard = eventsRepo.getActualCard(dateProvider.current()) ?: Card.stub()
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

            updateArrows()
            updateCurrentEvents()

            onCreateEvent()
        }
    }

    fun onCreateCard() {
        viewModelScope.launch {
            val currentDate = dateProvider.current()
            val newCard = Card(CardId(0L), currentDate)
            mCurrentCard = eventsRepo.createCard(newCard)

            if (eventsRepo.hasCards()) {
                mTimelineViewState.value = TimelineViewState.TIMELINE
            }

            updateArrows()
            updateCurrentEvents()
        }
    }

    fun onPreviousDate() {
        viewModelScope.launch {
            mCurrentCard = eventsRepo.getPreviousCard(mCurrentCard) ?: return@launch
            mCardTitleViewState.value = mCurrentCard.getTitleViewState()
            updateArrows()
            updateCurrentEvents()
        }
    }

    fun onNextDate() {
        viewModelScope.launch {
            mCurrentCard = eventsRepo.getNextCard(mCurrentCard) ?: return@launch
            mCardTitleViewState.value = mCurrentCard.getTitleViewState()
            updateArrows()
            updateCurrentEvents()
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
        TimeWalkerApp.navigator.openEditor(mCurrentCard.id, EventId.undefined())
    }

    private fun onEventClick(position: Int) {
        val eventId = mEventsAdapter.events[position].id
        TimeWalkerApp.navigator.openEditor(mCurrentCard.id, eventId)
    }

    private fun updateArrows() {
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
