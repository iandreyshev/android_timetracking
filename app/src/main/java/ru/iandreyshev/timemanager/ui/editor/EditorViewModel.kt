package ru.iandreyshev.timemanager.ui.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.timemanager.domain.*
import java.util.*

class EditorViewModel(
    private val cardId: CardId,
    private val eventToEdit: Event?,
    private val eventsRepo: IEventsRepo,
    private val dateProvider: IDateProvider
) : ViewModel() {

    val timeViewState: LiveData<String> by lazy { mTimeViewState }
    val saveButtonViewState: LiveData<Boolean> by lazy { mSaveButtonViewState }

    private val mTimeViewState = MutableLiveData<String>()
    private val mSaveButtonViewState = MutableLiveData(false)
    private var mTitle = ""
    private var mPickedTime = dateProvider.current()
    private val mTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    init {
        updateTimeViewState()
    }

    fun onSave() {
        viewModelScope.launch {
            if (eventToEdit == null) {
                eventsRepo.createEvent(
                    cardId, Event(
                        id = EventId.undefined(),
                        title = mTitle,
                        epochTime = 0,
                        zoneId = ""
                    )
                )
            } else {
                eventsRepo.update(
                    Event(
                        id = eventToEdit.id,
                        title = mTitle,
                        epochTime = 0,
                        zoneId = ""
                    )
                )
            }
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
