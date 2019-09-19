package ru.iandreyshev.timemanager.ui.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Observer
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.timemanager.domain.*
import ru.iandreyshev.timemanager.ui.utils.LiveDataEvent
import ru.iandreyshev.timemanager.ui.utils.execute
import java.util.*

class EditorViewModel(
    private val cardId: CardId,
    private val eventId: EventId,
    private val repository: IRepository,
    private val dateProvider: IDateProvider,
    private val observer: Observer<EditorAction>
) : ViewModel() {

    val startTimeViewState: LiveData<StartTimeViewState> by lazy { mStartTimeViewState }
    val endTimeViewState: LiveData<String> by lazy { mEndTimeViewState }
    val saveButtonViewState: LiveData<Boolean> by lazy { mSaveButtonViewState }

    val updateTitleEvent = MutableLiveData<LiveDataEvent<String>>()
    val exitEvent = MutableLiveData<LiveDataEvent<Unit>>()

    private var mTitle = ""
    private var mPickedStartTime = dateProvider.current()
    private var mPickedEndTime = dateProvider.current()
    private val mUpdateMode = eventId != EventId.default()
    private var mHasStartTime = false

    private val mLoadDataViewState = MutableLiveData(true)
    private val mStartTimeViewState = MutableLiveData<StartTimeViewState>(StartTimeViewState.Hidden)
    private val mEndTimeViewState = MutableLiveData("")
    private val mSaveButtonViewState = MutableLiveData(false)

    private val mTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private var mBackgroundJob: Job = Job()

    init {
        updateTimeViewState()
    }

    fun onLoadData() {
        mBackgroundJob.cancel()
        mBackgroundJob = viewModelScope.launch {
            if (mUpdateMode) {
                mLoadDataViewState.value = true
                val eventToEdit = repository.getEvent(eventId) ?: return@launch
                updateTitleEvent.execute(eventToEdit.description)
                mPickedEndTime = eventToEdit.endTime
                updateTimeViewState()
            }

            mHasStartTime = repository.getEventsCount(cardId) <= 1

            mLoadDataViewState.value = false
        }
    }

    fun onSave() {
        viewModelScope.launch {
            if (!mUpdateMode) {
                repository.saveEvent(
                    cardId,
                    Event(
                        id = EventId.default(),
                        description = mTitle,
                        endTime = mPickedEndTime
                    )
                )
            } else {
                repository.update(
                    cardId,
                    Event(
                        id = eventId,
                        description = mTitle,
                        endTime = mPickedEndTime
                    )
                )
            }
            exitEvent.execute()
            observer.onNext(EditorAction.EditCompleted(cardId))
        }
    }

    fun onStartTimePicked(time: Date?) {
        time ?: return
        mPickedStartTime = dateProvider.asZonedDateTime(time, time)
        updateTimeViewState()
    }

    fun onEndTimePicked(time: Date?) {
        time ?: return
        mPickedEndTime = dateProvider.asZonedDateTime(time, time)
        updateTimeViewState()
    }

    fun onTitleChanged(title: CharSequence?) {
        mSaveButtonViewState.value = title?.isNotBlank() ?: false
        mTitle = title.toString()
    }

    private fun updateTimeViewState() {
        mStartTimeViewState.value = if (mHasStartTime) {
            val formattedTime = mPickedStartTime.format(mTimeFormatter)
            StartTimeViewState.ShowTime(formattedTime)
        } else {
            StartTimeViewState.Hidden
        }
        mEndTimeViewState.value = mPickedEndTime.format(mTimeFormatter)
    }

}
