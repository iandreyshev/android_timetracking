package ru.iandreyshev.timemanager.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.launch
import ru.iandreyshev.timemanager.TimeCardsApp
import ru.iandreyshev.timemanager.domain.cards.*
import ru.iandreyshev.timemanager.ui.editor.EditorAction
import ru.iandreyshev.timemanager.ui.extensions.asViewState
import ru.iandreyshev.timemanager.ui.timeline.state.TimelineState
import ru.iandreyshev.timemanager.ui.timeline.state.ITimelineStateContext
import ru.iandreyshev.timemanager.ui.utils.updateIfChanged
import ru.iandreyshev.timemanager.utils.exhaustive

class TimelineViewModel(
    private val dateProvider: IDateProvider,
    private val repository: ICardsRepository,
    private var timelineState: TimelineState,
    val eventsAdapter: TimelineAdapter,
    editorObservable: Observable<EditorAction>
) : ViewModel() {

    val timelineViewState: LiveData<TimelineViewState> by lazy { mTimelineViewState }
    val hasEventsList: LiveData<Boolean> by lazy { mHasEvents }
    val canAddEvent: LiveData<Boolean> by lazy { mCanAddEventViewState }
    val toolbarViewState: LiveData<ToolbarViewState> by lazy { mToolbarViewState }
    val arrowsViewState: LiveData<Pair<ArrowViewState, ArrowViewState>> by lazy { mArrowsViewState }
    val cardTitleViewState: LiveData<CardTitleViewState> by lazy { mCardTitleViewState }
    val nextCardButtonViewState: LiveData<Boolean> by lazy { mNextCardButtonViewState }

    private val mTimelineContext: ITimelineStateContext = TimelineContext()

    private val mTimelineViewState = MutableLiveData(TimelineViewState.LOADING)
    private val mHasEvents = MutableLiveData(false)
    private val mCanAddEventViewState = MutableLiveData(false)
    private val mToolbarViewState = MutableLiveData<ToolbarViewState>(ToolbarViewState.CardTitle)
    private val mArrowsViewState = MutableLiveData(ArrowViewState.HIDDEN to ArrowViewState.HIDDEN)
    private val mNextCardButtonViewState = MutableLiveData(false)
    private val mCardTitleViewState = MutableLiveData<CardTitleViewState>()

    private var mCurrentCard: Card? = null
    private var mCurrentEvents: List<Event> = listOf()
    private var mHasPrevious = false
    private var mHasNext = false
    private var mIsAddEventButtonVisible = false

    private var mDisposables = CompositeDisposable()

    init {
        eventsAdapter.onClickListener = { timelineState.onEventClick(it) }
        eventsAdapter.onLongClickListener = { timelineState.onStartTimerMode(it) }
        mTimelineContext.setState(timelineState)
        mDisposables += editorObservable.subscribe(::onEditorAction)
    }

    override fun onCleared() {
        mDisposables.dispose()
    }

    fun loadData() {
        viewModelScope.launch {
            mTimelineViewState.value = TimelineViewState.LOADING

            mCurrentCard = repository.getLastCard()
            updateTimelineView()

            mHasPrevious = mCurrentCard?.let { repository.getPreviousCard(it) != null } ?: false
            mHasNext = mCurrentCard?.let { repository.getNextCard(it) != null } ?: false
            updateNavBarView()

            mCurrentEvents = mCurrentCard?.let { repository.getEvents(it.id) }.orEmpty()
            eventsAdapter.events = mCurrentEvents.asViewState()
            timelineState.onEventsUpdated(mCurrentEvents)
            updateEventsView()
        }
    }

    fun onCreateFirstCard() {
        viewModelScope.launch {
            val currentDate = dateProvider.current()
            val card =
                Card(date = currentDate, indexOfDate = 0)

            mCurrentCard = repository.saveCard(card)

            updateNavBarView()
            updateEventsView()
            updateTimelineView()

            onCreateEvent()
        }
    }

    fun onCreateCard() {
        viewModelScope.launch {
            val currentDate = dateProvider.current()
            val cardToSave =
                Card(date = currentDate, indexOfDate = 0)

            mCurrentCard = repository.saveCard(cardToSave)
            updateTimelineView()

            mHasPrevious = mCurrentCard?.let { repository.getPreviousCard(it) != null } ?: false
            mHasNext = mCurrentCard?.let { repository.getNextCard(it) != null } ?: false
            updateNavBarView()

            mCurrentEvents = mCurrentCard?.let { repository.getEvents(it.id) }.orEmpty()
            eventsAdapter.events = mCurrentEvents.asViewState()
            timelineState.onEventsUpdated(mCurrentEvents)
            updateEventsView()
        }
    }

    fun onPreviousCard() {
        viewModelScope.launch {
            repository.getPreviousCard(mCurrentCard ?: return@launch)?.let { card ->
                mHasPrevious = repository.getPreviousCard(card) != null
                mHasNext = repository.getNextCard(card) != null
                updateNavBarView()

                mCurrentEvents = repository.getEvents(card.id)
                eventsAdapter.events = mCurrentEvents.asViewState()
                timelineState.onEventsUpdated(mCurrentEvents)
                updateEventsView()

                mCurrentCard = card
                updateTimelineView()
            }
        }
    }

    fun onNextCard() {
        viewModelScope.launch {
            repository.getNextCard(mCurrentCard ?: return@launch)?.let { card ->
                mHasPrevious = repository.getPreviousCard(card) != null
                mHasNext = repository.getNextCard(card) != null
                updateNavBarView()

                mCurrentEvents = repository.getEvents(card.id)
                eventsAdapter.events = mCurrentEvents.asViewState()
                timelineState.onEventsUpdated(mCurrentEvents)
                updateEventsView()

                mCurrentCard = card
                updateTimelineView()
            }
        }
    }

    fun onResetToLast() {
        viewModelScope.launch {
            mCurrentCard = repository.getLastCard()
            updateTimelineView()

            mHasPrevious = mCurrentCard?.let { repository.getPreviousCard(it) != null } ?: false
            mHasNext = mCurrentCard?.let { repository.getNextCard(it) != null } ?: false
            updateNavBarView()

            mCurrentEvents = mCurrentCard?.let { repository.getEvents(it.id) }.orEmpty()
            eventsAdapter.events = mCurrentEvents.asViewState()
            timelineState.onEventsUpdated(mCurrentEvents)
            updateEventsView()
        }
    }

    fun onCreateEvent() {
        val cardId = mCurrentCard?.id ?: return
        TimeCardsApp.navigator.openEditor(cardId)
    }

    fun onExitFromTimer() {
        timelineState.onEndTimerMode()
    }

    fun onDeleteCard() {
        viewModelScope.launch {
            val cardId = mCurrentCard?.id ?: return@launch
            val newCurrentCard = repository.getNextCard(mCurrentCard ?: return@launch)
                ?: repository.getPreviousCard(mCurrentCard ?: return@launch)

            when (repository.deleteCard(cardId)) {
                is RepoResult.Success -> {
                    newCurrentCard.let { card ->
                        mHasPrevious = card?.let { repository.getPreviousCard(it) != null } ?: false
                        mHasNext = card?.let { repository.getNextCard(it) != null } ?: false
                        updateNavBarView()

                        mCurrentEvents = card?.let { repository.getEvents(card.id) }.orEmpty()
                        eventsAdapter.events = mCurrentEvents.asViewState()
                        timelineState.onEventsUpdated(mCurrentEvents)
                        updateEventsView()

                        mCurrentCard = card
                        updateTimelineView()
                    }
                }
                is RepoResult.Error -> return@launch
            }.exhaustive
        }
    }

    fun onDeleteEventAt(position: Int) {
        viewModelScope.launch {
            mCurrentEvents = mCurrentEvents[position].let { event ->
                when (val result = repository.deleteEvent(event.id)) {
                    is RepoResult.Success -> result.data
                    is RepoResult.Error -> return@launch
                }.exhaustive
            }
            eventsAdapter.events = mCurrentEvents.asViewState()
            timelineState.onEventsUpdated(mCurrentEvents)
            updateEventsView()
        }
    }

    fun onBackPressed(): Boolean {
        return timelineState.onBackPressed()
    }

    private fun onEditorAction(action: EditorAction) {
        when (action) {
            is EditorAction.EditCompleted -> {
                if (action.cardId != mCurrentCard?.id) {
                    return
                }

                viewModelScope.launch {
                    mCurrentEvents = mCurrentCard?.let { repository.getEvents(it.id) }.orEmpty()
                    eventsAdapter.events = mCurrentEvents.asViewState()
                    timelineState.onEventsUpdated(mCurrentEvents)
                    updateEventsView()
                }
            }
        }
    }

    private fun updateNavBarView() {
        val leftArrow = if (mHasPrevious) ArrowViewState.ARROW else ArrowViewState.HIDDEN
        val right = if (mHasNext) ArrowViewState.ARROW else ArrowViewState.NEXT_CARD
        mArrowsViewState.updateIfChanged(leftArrow to right)
    }

    private fun updateEventsView() {
        val hasEvents = eventsAdapter.events.isNotEmpty()
        mHasEvents.value = hasEvents
        mCanAddEventViewState.value = hasEvents && mIsAddEventButtonVisible
    }

    private fun updateTimelineView() {
        mCardTitleViewState.value =
            CardTitleViewState(mCurrentCard?.date, mCurrentCard?.indexOfDate)
        mTimelineViewState.value =
            if (mCurrentCard == null) TimelineViewState.EMPTY
            else TimelineViewState.HAS_CARD
    }

    private inner class TimelineContext : ITimelineStateContext {
        override fun setState(state: TimelineState) {
            timelineState = state
            timelineState.onEventsUpdated(mCurrentEvents)
            timelineState.setContext(mTimelineContext)
        }

        override fun openEvent(position: Int) {
            val cardId = mCurrentCard?.id ?: return
            val eventId = eventsAdapter.events[position].id
            TimeCardsApp.navigator.openEditor(cardId, eventId)
        }

        override fun updateToolbar(viewState: ToolbarViewState) {
            mToolbarViewState.value = viewState
        }

        override fun updateEventSelection(position: Int, viewState: EventSelectionViewState) {
            eventsAdapter.events[position].selection = viewState
            eventsAdapter.notifyItemChanged(position)
        }

        override fun updateAllEventsSelection(viewState: EventSelectionViewState) {
            eventsAdapter.events.forEach { it.selection = viewState }
            eventsAdapter.notifyDataSetChanged()
        }

        override fun updateAddEventButton(isVisible: Boolean) {
            mIsAddEventButtonVisible = isVisible
            updateEventsView()
        }
    }

}
