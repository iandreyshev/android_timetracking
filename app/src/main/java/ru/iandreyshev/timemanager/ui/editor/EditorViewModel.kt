package ru.iandreyshev.timemanager.ui.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.timemanager.domain.*
import ru.iandreyshev.timemanager.ui.utils.LiveDataEvent
import ru.iandreyshev.timemanager.ui.utils.execute
import java.util.*

class EditorViewModel(
    private val cardId: CardId,
    private val eventId: EventId,
    private val eventsRepo: IEventsRepo,
    private val dateProvider: IDateProvider
) : ViewModel() {

    val timeViewState: LiveData<String> by lazy { mTimeViewState }
    val saveButtonViewState: LiveData<Boolean> by lazy { mSaveButtonViewState }

    val updateTitleEvent = MutableLiveData<LiveDataEvent<String>>()
    val exitEvent = MutableLiveData<LiveDataEvent<Unit>>()

    private var mTitle = ""
    private var mPickedTime = dateProvider.current()

    private val mLoadDataViewState = MutableLiveData(true)
    private val mTimeViewState = MutableLiveData<String>()
    private val mSaveButtonViewState = MutableLiveData(false)

    private val mTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    init {
        updateTimeViewState()
    }

    fun onLoadData() {
        viewModelScope.launch {
            if (eventId != EventId.undefined()) {
                mLoadDataViewState.value = true
                val eventToEdit = eventsRepo.getEvent(eventId) ?: return@launch
                updateTitleEvent.execute(eventToEdit.title)
                mPickedTime = eventToEdit.endTime
                updateTimeViewState()
            }
            mLoadDataViewState.value = false
        }
    }

    fun onSave() {
        viewModelScope.launch {
            if (eventId == EventId.undefined()) {
                eventsRepo.createEvent(
                    cardId,
                    Event(
                        id = EventId.undefined(),
                        title = mTitle,
                        endTime = mPickedTime
                    )
                )
            } else {
                eventsRepo.update(
                    Event(
                        id = eventId,
                        title = mTitle,
                        endTime = mPickedTime
                    )
                )
            }
            exitEvent.execute()
        }
    }

    fun onDatePicked(date: Date?) {
        date ?: return
    }

    fun onTimePicked(time: Date?) {
        time ?: return
        mPickedTime = dateProvider.asZonedDateTime(time, time)
        updateTimeViewState()
    }

    fun onTitleChanged(title: CharSequence?) {
        mSaveButtonViewState.value = title?.isNotBlank() ?: false
        mTitle = title.toString()
    }

    private fun updateTimeViewState() {
        mTimeViewState.value = mPickedTime.format(mTimeFormatter)
    }

}
