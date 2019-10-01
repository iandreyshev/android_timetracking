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
import ru.iandreyshev.timemanager.domain.Card
import ru.iandreyshev.timemanager.domain.IDateProvider
import ru.iandreyshev.timemanager.domain.IRepository
import ru.iandreyshev.timemanager.ui.editor.EditorAction
import ru.iandreyshev.timemanager.ui.extensions.asViewState
import ru.iandreyshev.timemanager.ui.extensions.getTitleViewState
import ru.iandreyshev.timemanager.ui.utils.updateIfChanged

class TimelineViewModel(
    private val dateProvider: IDateProvider,
    private val repository: IRepository,
    editorObservable: Observable<EditorAction>
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
    private var mHasPrevious = false
    private var mHasNext = false

    private var mDisposables = CompositeDisposable()

    init {
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

            mEventsAdapter.events = mCurrentCard?.let { repository.getEvents(it.id) }.asViewState()
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

            mEventsAdapter.events = mCurrentCard?.let { repository.getEvents(it.id) }.asViewState()
            updateEventsView()
        }
    }

    fun onPreviousCard() {
        viewModelScope.launch {
            repository.getPreviousCard(mCurrentCard ?: return@launch)?.let { card ->
                mHasPrevious = repository.getPreviousCard(card) != null
                mHasNext = repository.getNextCard(card) != null
                updateNavBarView()

                mEventsAdapter.events = repository.getEvents(card.id).asViewState()
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

                mEventsAdapter.events = repository.getEvents(card.id).asViewState()
                updateEventsView()

                mCurrentCard = card
                updateTimelineView()
            }
        }
    }

    fun onOpenDatePicker(): Boolean {
        return true
    }

    fun onResetToLast() {
        viewModelScope.launch {
            mCurrentCard = repository.getLastCard()
            updateTimelineView()

            mHasPrevious = mCurrentCard?.let { repository.getPreviousCard(it) != null } ?: false
            mHasNext = mCurrentCard?.let { repository.getNextCard(it) != null } ?: false
            updateNavBarView()

            mEventsAdapter.events = mCurrentCard?.let { repository.getEvents(it.id) }.asViewState()
            updateEventsView()
        }
    }

    fun onCreateEvent() {
        val cardId = mCurrentCard?.id ?: return
        TimeWalkerApp.navigator.openEditor(cardId)
    }

    private fun onEditorAction(action: EditorAction) {
        when (action) {
            is EditorAction.EditCompleted -> {
                if (action.cardId != mCurrentCard?.id) {
                    return
                }

                viewModelScope.launch {
                    mEventsAdapter.events = mCurrentCard?.let { repository.getEvents(it.id) }.asViewState()
                    updateEventsView()
                }
            }
        }
    }

    private fun onEventClick(position: Int) {
        val cardId = mCurrentCard?.id ?: return
        val eventId = mEventsAdapter.events[position].id
        TimeWalkerApp.navigator.openEditor(cardId, eventId)
    }

    private fun updateNavBarView() {
        val leftArrow = if (mHasPrevious) ArrowViewState.ARROW else ArrowViewState.HIDDEN
        val right = if (mHasNext) ArrowViewState.ARROW else ArrowViewState.NEXT_CARD
        mArrowsViewState.updateIfChanged(leftArrow to right)
    }

    private fun updateEventsView() {
        mHasEvents.value = mEventsAdapter.events.isNotEmpty()
    }

    private fun updateTimelineView() {
        mCardTitleViewState.value = mCurrentCard?.getTitleViewState()
        mTimelineViewState.value =
            if (mCurrentCard == null) TimelineViewState.EMPTY
            else TimelineViewState.HAS_CARD
    }

}
