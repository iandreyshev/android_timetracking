package ru.iandreyshev.timemanager.ui.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Observer
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.timemanager.domain.*
import ru.iandreyshev.timemanager.ui.utils.LiveDataEvent
import ru.iandreyshev.timemanager.ui.utils.execute
import java.util.*

class EditorViewModel(
    private val cardId: CardId,
    private val eventId: EventId?,
    private val repository: IRepository,
    private val dateProvider: IDateProvider,
    private val observer: Observer<EditorAction>
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
            if (eventId != null) {
                mLoadDataViewState.value = true
                val eventToEdit = repository.getEvent(eventId) ?: return@launch
                updateTitleEvent.execute(eventToEdit.description)
                mPickedTime = eventToEdit.endTime
                updateTimeViewState()
            }
            mLoadDataViewState.value = false
        }
    }

    fun onSave() {
        viewModelScope.launch {
            if (eventId == null) {
                repository.saveEvent(
                    cardId,
                    Event(
                        id = EventId.default(),
                        description = mTitle,
                        endTime = mPickedTime
                    )
                )
            } else {
                repository.update(
                    cardId,
                    Event(
                        id = eventId,
                        description = mTitle,
                        endTime = mPickedTime
                    )
                )
            }
            exitEvent.execute()
            observer.onNext(EditorAction.EditCompleted)
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
