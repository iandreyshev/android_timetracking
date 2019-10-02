package ru.iandreyshev.timemanager.ui.editor

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Observer
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.timemanager.domain.*
import ru.iandreyshev.timemanager.ui.extensions.asText
import ru.iandreyshev.timemanager.ui.extensions.sameDateWith
import ru.iandreyshev.timemanager.ui.extensions.withTime
import ru.iandreyshev.timemanager.ui.utils.LiveDataEvent
import ru.iandreyshev.timemanager.ui.utils.execute

class EditorViewModel(
    private val cardId: CardId,
    private val eventId: EventId,
    private val resources: Resources,
    private val repository: IRepository,
    private val dateProvider: IDateProvider,
    private val observer: Observer<EditorAction>
) : ViewModel() {

    val datePicker: LiveData<DatePickerViewState> by lazy { mDatePickerViewState }

    val startDatePreview: LiveData<StartDateViewState> by lazy { mStartDateViewState }
    val startTimePreview: LiveData<StartTimeViewState> by lazy { mStartTimeViewState }

    val endDatePreview: LiveData<EndDateViewState> by lazy { mEndDateViewState }
    val endTimePreview: LiveData<String> by lazy { mEndTimeViewState }

    val saveButtonViewState: LiveData<Boolean> by lazy { mSaveButtonViewState }

    val updateTitleEvent = MutableLiveData<LiveDataEvent<String>>()
    val exitEvent = MutableLiveData<LiveDataEvent<Unit>>()
    val showErrorEvent = MutableLiveData<LiveDataEvent<String>>()

    private var mTitle = ""

    private val mDatePickerViewState = MutableLiveData<DatePickerViewState>()

    private var mPickedStartDate: ZonedDateTime? = null
    private var mPickedStartTime: ZonedDateTime? = null

    private var mPickedEndDate = dateProvider.current()
    private var mPickedEndTime = dateProvider.current()

    private val mUpdateMode = eventId != EventId.default()
    private var mHasStartTime = false

    private val mLoadDataViewState = MutableLiveData(true)

    private val mStartDateViewState = MutableLiveData<StartDateViewState>(StartDateViewState.Hidden)
    private val mStartTimeViewState = MutableLiveData<StartTimeViewState>(StartTimeViewState.Hidden)

    private val mEndDateViewState = MutableLiveData<EndDateViewState>(EndDateViewState.Hidden)
    private val mEndTimeViewState = MutableLiveData("")

    private val mSaveButtonViewState = MutableLiveData(false)

    private val mDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val mTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private var mBackgroundJob: Job = Job()

    init {
        updateTimeViewState()
    }

    fun onLoadData() {
        mBackgroundJob.cancel()
        mBackgroundJob = viewModelScope.launch {
            mLoadDataViewState.value = true

            if (mUpdateMode) {
                val eventToEdit = repository.getEvent(eventId) ?: return@launch
                updateTitleEvent.execute(eventToEdit.description)
                mPickedEndTime = eventToEdit.endDateTime
                mHasStartTime = repository.getEventsCount(cardId) == 1
            } else {
                mHasStartTime = repository.getEventsCount(cardId) < 1
            }

            updateTimeViewState()

            mLoadDataViewState.value = false
        }
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            if (!mUpdateMode) {
                onInputValidationError { error ->
                    updateErrorViewState(error)
                    return@launch
                }

                val event = createEventToUpdate()
                when (val result = repository.saveEvent(cardId, event)) {
                    is RepoResult.Error ->
                        updateErrorViewState(result.error)
                    is RepoResult.Success -> {
                        exitEvent.execute()
                        observer.onNext(EditorAction.EditCompleted(cardId))
                    }
                }
            } else {
                onInputValidationError { error ->
                    updateErrorViewState(error)
                    return@launch
                }

                val event = createEventToSave()
                when (val result = repository.update(cardId, event)) {
                    is RepoResult.Error ->
                        updateErrorViewState(result.error)
                    is RepoResult.Success -> {
                        exitEvent.execute()
                        observer.onNext(EditorAction.EditCompleted(cardId))
                    }
                }
            }
        }
    }

    fun onStartDatePickerClick() {
        val default = mPickedStartDate
            ?.let(dateProvider::asEpochTime)
            ?: dateProvider.current2()

        mDatePickerViewState.value = DatePickerViewState.StartDate(default) { time ->
            time ?: return@StartDate
            mPickedStartDate = dateProvider.asZonedDateTime(time, time)
            updateTimeViewState()
        }
    }

    fun onStartTimePickerClick() {
        val default = mPickedStartTime
            ?.let(dateProvider::asEpochTime)
            ?: dateProvider.current2()

        mDatePickerViewState.value = DatePickerViewState.StartTime(default) { time ->
            time ?: return@StartTime
            mPickedStartTime = dateProvider.asZonedDateTime(time, time)
            updateTimeViewState()
        }
    }

    fun onEndDatePickerClick() {
        val default = mPickedEndDate.let(dateProvider::asEpochTime)
        mDatePickerViewState.value = DatePickerViewState.EndDate(default) { time ->
            time ?: return@EndDate
            mPickedEndDate = dateProvider.asZonedDateTime(time, time)
            updateTimeViewState()
        }
    }

    fun onEndTimePickerClick() {
        val default = mPickedEndTime.let(dateProvider::asEpochTime)
        mDatePickerViewState.value = DatePickerViewState.EndTime(default) { time ->
            time ?: return@EndTime
            mPickedEndTime = dateProvider.asZonedDateTime(time, time)
            updateTimeViewState()
        }
    }

    fun onTitleChanged(title: CharSequence?) {
        mSaveButtonViewState.value = title?.isNotBlank() ?: false
        mTitle = title.toString()
    }

    private fun updateTimeViewState() {
        val pickedStartDate = mPickedStartDate
        mStartDateViewState.value = when {
            !mHasStartTime -> StartDateViewState.Hidden
            pickedStartDate == null -> StartDateViewState.Today
            else -> {
                val formattedTime = pickedStartDate.format(mDateFormatter)
                StartDateViewState.ShowDate(formattedTime)
            }
        }

        val pickedStartTime = mPickedStartTime
        mStartTimeViewState.value = when {
            !mHasStartTime -> StartTimeViewState.Hidden
            pickedStartTime == null -> StartTimeViewState.Undefined
            else -> {
                val formattedTime = pickedStartTime.format(mTimeFormatter)
                StartTimeViewState.ShowTime(formattedTime)
            }
        }

        mEndDateViewState.value = when {
            dateProvider.current() sameDateWith mPickedEndTime ->
                EndDateViewState.Today
            else ->
                EndDateViewState.ShowDate(mPickedEndDate.format(mDateFormatter))
        }

        mEndTimeViewState.value = mPickedEndTime.format(mTimeFormatter)
    }

    private fun updateErrorViewState(error: InputValidationError) {
        val errorText = error.asText(resources)
        showErrorEvent.execute(errorText)
    }

    private fun updateErrorViewState(error: RepoError) {
        val errorText = error.asText(resources)
        showErrorEvent.execute(errorText)
    }

    private inline fun onInputValidationError(onError: (InputValidationError) -> Unit) {
        if (mTitle.isBlank()) {
            onError(InputValidationError.EmptyText)
        }

        if (mHasStartTime && mPickedStartTime == null) {
            onError(InputValidationError.ExpectedStartTime)
        }
    }

    private fun createEventToUpdate(): Event {
        val startDate = mPickedStartDate ?: dateProvider.current()
        val startDateTime = startDate withTime (mPickedStartTime ?: dateProvider.current())

        val endDate = mPickedEndDate
        val endDateTime = endDate withTime mPickedEndTime

        return Event(
            id = EventId.default(),
            description = mTitle,
            startDateTime = startDateTime,
            endDateTime = endDateTime
        )
    }

    private fun createEventToSave(): Event {
        val startDate = mPickedStartDate ?: dateProvider.current()
        val startDateTime = startDate withTime (mPickedStartTime ?: dateProvider.current())

        val endDate = mPickedEndDate
        val endDateTime = endDate withTime mPickedEndTime

        return Event(
            id = eventId,
            description = mTitle,
            startDateTime = startDateTime,
            endDateTime = endDateTime
        )
    }

}
