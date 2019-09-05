package ru.iandreyshev.timemanager.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val arrowsViewState: LiveData<Pair<ArrowViewState, ArrowViewState>> by lazy { mArrowsViewState }
    val cardTitleViewState: LiveData<String> by lazy { mCardTitleViewState }
    val nextCardButtonViewState: LiveData<Boolean> by lazy { mNextCardButtonViewState }

    private val mEventsAdapter = TimelineAdapter(::onEventClick)
    private val mTimelineViewState = MutableLiveData(TimelineViewState.LOADING)
    private val mHasEvents = MutableLiveData(false)
    private val mArrowsViewState = MutableLiveData(ArrowViewState.HIDDEN to ArrowViewState.HIDDEN)
    private val mNextCardButtonViewState = MutableLiveData(false)
    private val mCardTitleViewState = MutableLiveData<String>()

    private var mCurrentCard: Card? = null

    fun loadData() {
        viewModelScope.launch {
            if (mCurrentCard != null) {
                mTimelineViewState.value = TimelineViewState.LOADING
                mCurrentCard = eventsRepo.getActualCard(dateProvider.current())
                mCardTitleViewState.value = mCurrentCard?.getTitleViewState()

                updateArrows()
                updateCurrentEvents()

                mTimelineViewState.value = TimelineViewState.HAS_CARD
            } else {
                mTimelineViewState.value = TimelineViewState.EMPTY
            }
        }
    }

    fun onCreateFirstCard() {
        viewModelScope.launch {
            val currentDate = dateProvider.current()
            val newCard = Card(CardId(0L), currentDate.second.toString(), currentDate)

            mCurrentCard = eventsRepo.createCard(newCard)

            if (mCurrentCard != null) {
                mTimelineViewState.value = TimelineViewState.HAS_CARD
            }

            updateArrows()
            updateCurrentEvents()

            onCreateEvent()
        }
    }

    fun onCreateCard() {
        viewModelScope.launch {
            val currentDate = dateProvider.current()
            val newCard = Card(CardId(0L), currentDate.second.toString(), currentDate)

            mCurrentCard = eventsRepo.createCard(newCard)

            if (mCurrentCard != null) {
                mTimelineViewState.value = TimelineViewState.HAS_CARD
            }

            updateArrows()
            updateCurrentEvents()
        }
    }

    fun onPreviousDate() {
        viewModelScope.launch {
            val current = mCurrentCard ?: return@launch
            mCurrentCard = eventsRepo.getPreviousCard(current) ?: return@launch
            mCardTitleViewState.value = mCurrentCard?.getTitleViewState()
            updateArrows()
            updateCurrentEvents()
        }
    }

    fun onNextDate() {
        viewModelScope.launch {
            val current = mCurrentCard ?: return@launch
            mCurrentCard = eventsRepo.getNextCard(current) ?: return@launch
            mCardTitleViewState.value = mCurrentCard?.getTitleViewState()
            updateArrows()
            updateCurrentEvents()
        }
    }

    fun onOpenDatePicker(): Boolean {
        return true
    }

    fun onResetToCurrent() {
        viewModelScope.launch {
            mCurrentCard = eventsRepo.getActualCard(currentDate = dateProvider.current())
                ?: throw IllegalStateException("Actual card is null")
        }
    }

    fun onCreateEvent() {
        val cardId = mCurrentCard?.id ?: return
        TimeWalkerApp.navigator.openEditor(cardId, EventId.default())
    }

    private fun onEventClick(position: Int) {
        val cardId = mCurrentCard?.id ?: return
        val eventId = mEventsAdapter.events[position].id
        TimeWalkerApp.navigator.openEditor(cardId, eventId)
    }

    private suspend fun updateArrows() {
        val previous = eventsRepo.getPreviousCard(mCurrentCard ?: return)
        val leftArrow = when {
            previous != null -> ArrowViewState.ARROW
            else -> ArrowViewState.HIDDEN
        }

        val next = eventsRepo.getNextCard(mCurrentCard ?: return)
        val right = when {
            next != null -> ArrowViewState.ARROW
            else -> ArrowViewState.NEXT_CARD
        }

        mArrowsViewState.value = leftArrow to right
    }

    private suspend fun updateCurrentEvents() {
        withContext(Dispatchers.Main) {
            val events = eventsRepo.getEvents(mCurrentCard ?: return@withContext)
            mEventsAdapter.events = events.asViewState()
            mHasEvents.value = events.isNotEmpty()
        }
    }

}
