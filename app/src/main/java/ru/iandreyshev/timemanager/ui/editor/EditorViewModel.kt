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
import ru.iandreyshev.timemanager.ui.extensions.formatDate
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

    val startDateTimeAvailable: LiveData<Boolean> by lazy { mCanEditStartTime }
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
    private var mCanEditStartTime = MutableLiveData(false)

    private val mLoadDataViewState = MutableLiveData(true)

    private val mStartDateViewState = MutableLiveData<StartDateViewState>(StartDateViewState.Today)
    private val mStartTimeViewState =
        MutableLiveData<StartTimeViewState>(StartTimeViewState.Undefined)

    private val mEndDateViewState = MutableLiveData<EndDateViewState>(EndDateViewState.Hidden)
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
            mLoadDataViewState.value = true

            if (mUpdateMode) {
                with(repository.getEvent(eventId) ?: return@launch) {
                    updateTitleEvent.execute(description)
                    mPickedStartDate = startDateTime
                    mPickedStartTime = startDateTime
                    mPickedEndDate = endDateTime
                    mPickedEndTime = endDateTime
                    mCanEditStartTime.value = isFirstInCard
                }
            } else {
                mCanEditStartTime.value = repository.getEventsCount(cardId) < 1
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
            ?: dateProvider.currentAsJavaDate()

        mDatePickerViewState.value = DatePickerViewState.StartDate(default) { pickedDate ->
            pickedDate ?: return@StartDate
            mPickedStartDate = dateProvider.asZonedDateTime(pickedDate, pickedDate)
            updateTimeViewState()
        }
    }

    fun onStartTimePickerClick() {
        val default = mPickedStartTime
            ?.let(dateProvider::asEpochTime)
            ?: dateProvider.currentAsJavaDate()

        mDatePickerViewState.value = DatePickerViewState.StartTime(default) { pickedTime ->
            pickedTime ?: return@StartTime
            mPickedStartTime = dateProvider.asZonedDateTime(pickedTime, pickedTime)
            updateTimeViewState()
        }
    }

    fun onEndDatePickerClick() {
        val default = mPickedEndDate.let(dateProvider::asEpochTime)

        mDatePickerViewState.value = DatePickerViewState.EndDate(default) { pickedDate ->
            pickedDate ?: return@EndDate
            mPickedEndDate = dateProvider.asZonedDateTime(pickedDate, pickedDate)
            updateTimeViewState()
        }
    }

    fun onEndTimePickerClick() {
        val default = mPickedEndTime.let(dateProvider::asEpochTime)

        mDatePickerViewState.value = DatePickerViewState.EndTime(default) { pickedTime ->
            pickedTime ?: return@EndTime
            mPickedEndTime = dateProvider.asZonedDateTime(pickedTime, pickedTime)
            updateTimeViewState()
        }
    }

    fun onTitleChanged(title: CharSequence?) {
        mSaveButtonViewState.value = title?.isNotBlank() ?: false
        mTitle = title.toString()
    }

    fun onBackPressed() {
        if (mDatePickerViewState.value == null
            || mDatePickerViewState.value == DatePickerViewState.Hidden
        ) return exitEvent.execute()

        mDatePickerViewState.value = DatePickerViewState.Hidden
    }

    private fun updateTimeViewState() {
        val pickedStartDate = mPickedStartDate
        mStartDateViewState.value = when {
            pickedStartDate == null -> StartDateViewState.Today
            pickedStartDate sameDateWith dateProvider.current() ->
                StartDateViewState.Today
            else -> {
                val formattedTime = pickedStartDate.formatDate()
                StartDateViewState.ShowDate(formattedTime)
            }
        }

        val pickedStartTime = mPickedStartTime
        mStartTimeViewState.value = when (pickedStartTime) {
            null -> StartTimeViewState.Undefined
            else -> {
                val formattedTime = pickedStartTime.format(mTimeFormatter)
                StartTimeViewState.ShowTime(formattedTime)
            }
        }

        mEndDateViewState.value = when {
            mPickedEndDate sameDateWith dateProvider.current() ->
                EndDateViewState.Today
            else ->
                EndDateViewState.ShowDate(mPickedEndDate.formatDate())
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

        if (mCanEditStartTime.value == true && mPickedStartTime == null) {
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
            endDateTime = endDateTime,
            isFirstInCard = false
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
            endDateTime = endDateTime,
            isFirstInCard = false
        )
    }

}
