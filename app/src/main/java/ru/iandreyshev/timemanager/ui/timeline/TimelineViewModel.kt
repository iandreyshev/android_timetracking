package ru.iandreyshev.timemanager.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.coroutines.launch
import ru.iandreyshev.timemanager.TimeWalkerApp
import ru.iandreyshev.timemanager.domain.*
import ru.iandreyshev.timemanager.ui.editor.EditorAction
import ru.iandreyshev.timemanager.ui.extensions.asViewState
import ru.iandreyshev.timemanager.ui.extensions.getTitleViewState
import ru.iandreyshev.timemanager.ui.timeline.state.TimelineState
import ru.iandreyshev.timemanager.ui.timeline.state.ITimelineStateContext
import ru.iandreyshev.timemanager.ui.utils.updateIfChanged
import ru.iandreyshev.timemanager.utils.exhaustive

class TimelineViewModel(
    private val dateProvider: IDateProvider,
    private val repository: IRepository,
    private var timelineState: TimelineState,
    editorObservable: Observable<EditorAction>
) : ViewModel() {

    val eventsAdapter: RecyclerView.Adapter<*> by lazy { mEventsAdapter }
    val timelineViewState: LiveData<TimelineViewState> by lazy { mTimelineViewState }
    val hasEventsList: LiveData<Boolean> by lazy { mHasEvents }
    val canAddEvents: LiveData<Boolean> by lazy { mCanAddEvent }
    val toolbarViewState: LiveData<ToolbarViewState> by lazy { mToolbarViewState }
    val arrowsViewState: LiveData<Pair<ArrowViewState, ArrowViewState>> by lazy { mArrowsViewState }
    val cardTitleViewState: LiveData<String> by lazy { mCardTitleViewState }
    val nextCardButtonViewState: LiveData<Boolean> by lazy { mNextCardButtonViewState }

    private val mTimelineContext: ITimelineStateContext = TimelineContext()

    private val mEventsAdapter = TimelineAdapter(
        onClickListener = { timelineState.onEventClick(it) },
        onLongClickListener = { timelineState.onStartTimerMode(it) }
    )
    private val mTimelineViewState = MutableLiveData(TimelineViewState.LOADING)
    private val mHasEvents = MutableLiveData(false)
    private val mCanAddEvent = MutableLiveData(false)
    private val mToolbarViewState = MutableLiveData<ToolbarViewState>(ToolbarViewState.CardTitle)
    private val mArrowsViewState = MutableLiveData(ArrowViewState.HIDDEN to ArrowViewState.HIDDEN)
    private val mNextCardButtonViewState = MutableLiveData(false)
    private val mCardTitleViewState = MutableLiveData<String>()

    private var mCurrentCard: Card? = null
    private var mCurrentEvents: List<Event> = listOf()
    private var mHasPrevious = false
    private var mHasNext = false

    private var mDisposables = CompositeDisposable()

    init {
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
            mEventsAdapter.events = mCurrentEvents.asViewState()
            timelineState.onEventsUpdated(mCurrentEvents)
            updateEventsView()
        }
    }

    fun onCreateFirstCard() {
        viewModelScope.launch {
            val currentDate = dateProvider.current()
            val card = Card(title = currentDate.second.toString(), date = currentDate)

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
            val cardToSave = Card(title = currentDate.second.toString(), date = currentDate)

            mCurrentCard = repository.saveCard(cardToSave)
            updateTimelineView()

            mHasPrevious = mCurrentCard?.let { repository.getPreviousCard(it) != null } ?: false
            mHasNext = mCurrentCard?.let { repository.getNextCard(it) != null } ?: false
            updateNavBarView()

            mCurrentEvents = mCurrentCard?.let { repository.getEvents(it.id) }.orEmpty()
            mEventsAdapter.events = mCurrentEvents.asViewState()
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
                mEventsAdapter.events = mCurrentEvents.asViewState()
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
                mEventsAdapter.events = mCurrentEvents.asViewState()
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
            mEventsAdapter.events = mCurrentEvents.asViewState()
            timelineState.onEventsUpdated(mCurrentEvents)
            updateEventsView()
        }
    }

    fun onCreateEvent() {
        val cardId = mCurrentCard?.id ?: return
        TimeWalkerApp.navigator.openEditor(cardId)
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
                        mEventsAdapter.events = mCurrentEvents.asViewState()
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
                    mEventsAdapter.events = mCurrentEvents.asViewState()
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
        val hasEvents = mEventsAdapter.events.isNotEmpty()
        mHasEvents.value = hasEvents
        mCanAddEvent.value = hasEvents && mCanAddEvent.value ?: false
    }

    private fun updateTimelineView() {
        mCardTitleViewState.value = mCurrentCard?.getTitleViewState()
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
            val eventId = mEventsAdapter.events[position].id
            TimeWalkerApp.navigator.openEditor(cardId, eventId)
        }

        override fun updateToolbar(viewState: ToolbarViewState) {
            mToolbarViewState.value = viewState
        }

        override fun updateEventSelection(position: Int, viewState: EventSelectionViewState) {
            mEventsAdapter.events[position].selection = viewState
            mEventsAdapter.notifyItemChanged(position)
        }

        override fun updateAllEventsSelection(viewState: EventSelectionViewState) {
            mEventsAdapter.events.forEach { it.selection = viewState }
            mEventsAdapter.notifyDataSetChanged()
        }

        override fun updateAddEventButton(isVisible: Boolean) {
            mCanAddEvent.value = isVisible
            updateEventsView()
        }
    }

}
