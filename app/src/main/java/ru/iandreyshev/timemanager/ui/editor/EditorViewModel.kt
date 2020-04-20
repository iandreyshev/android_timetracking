package ru.iandreyshev.timemanager.ui.editor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import ru.iandreyshev.timemanager.domain.cards.*
import ru.iandreyshev.timemanager.ui.extensions.*
import ru.iandreyshev.timemanager.ui.utils.LiveDataEvent
import ru.iandreyshev.timemanager.ui.utils.execute

class EditorViewModel(
    private val cardId: CardId,
    private val eventId: EventId,
    private val resources: Resources,
    private val repository: ICardsRepository,
    private val dateProvider: IDateProvider,
    private val observer: Observer<EditorAction>
) : ViewModel() {

    val datePicker: LiveData<DatePickerViewState> by lazy { mDatePickerViewState }

    val startDatePreview: LiveData<StartDateViewState> by lazy { mStartDateViewState }
    val startTimePreview: LiveData<StartTimeViewState> by lazy { mStartTimeViewState }
    val startDateTimeComment: LiveData<DatePickerCommentViewState> by lazy { mStartDateTimeComment }

    val endDatePreview: LiveData<EndDateViewState> by lazy { mEndDateViewState }
    val endTimePreview: LiveData<String> by lazy { mEndTimeViewState }
    val endDateTimeComment: LiveData<DatePickerCommentViewState> by lazy { mEndDateTimeComment }

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

    private var mPreviousEvent: Event? = null

    private val mUpdateMode = eventId != EventId.default()

    private val mLoadDataViewState = MutableLiveData(true)

    private val mStartDateViewState =
        MutableLiveData<StartDateViewState>(StartDateViewState.Today(dateProvider.current().formatDate2()))
    private val mStartTimeViewState =
        MutableLiveData<StartTimeViewState>(StartTimeViewState.Undefined)
    private val mStartDateTimeComment =
        MutableLiveData<DatePickerCommentViewState>(DatePickerCommentViewState.Hidden)

    private val mEndDateViewState =
        MutableLiveData<EndDateViewState>(EndDateViewState.Today(dateProvider.current().formatDate2()))
    private val mEndTimeViewState = MutableLiveData("")
    private val mEndDateTimeComment =
        MutableLiveData<DatePickerCommentViewState>(DatePickerCommentViewState.Hidden)

    private val mSaveButtonViewState = MutableLiveData(false)

    private val mTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    private var mBackgroundJob: Job = Job()

    fun onLoadData() {
        updateTimeViewState()

        mBackgroundJob.cancel()
        mBackgroundJob = viewModelScope.launch {
            mLoadDataViewState.value = true

            // FIXME: 4/20/2020 Вынести в App-модель
            when (mUpdateMode) {
                true -> {
                    val events = repository.getEvents(cardId)
                    val eventIndex = events.indexOfFirst { it.id == eventId }
                    val event = events[eventIndex]

                    mPreviousEvent = events.getOrNull(eventIndex + 1)

                    updateTitleEvent.execute(event.description)
                    mPickedStartDate = event.startDateTime
                    mPickedStartTime = event.startDateTime
                    mPickedEndDate = event.endDateTime
                    mPickedEndTime = event.endDateTime
                }
                else -> {
                    mPreviousEvent = repository.getEvents(cardId).firstOrNull()
                    mPickedStartDate = mPreviousEvent?.endDateTime
                    mPickedStartTime = mPreviousEvent?.endDateTime
                }
            }

            updateTimeViewState()

            mLoadDataViewState.value = false
        }
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            if (!mUpdateMode) {
                validateInput { error ->
                    showError(error)
                    return@launch
                }

                val event = createEventToUpdate()
                when (val result = repository.saveEvent(cardId, event)) {
                    is RepoResult.Error ->
                        showError(result.error)
                    is RepoResult.Success -> {
                        exitEvent.execute()
                        observer.onNext(EditorAction.EditCompleted(cardId))
                    }
                }
            } else {
                validateInput { error ->
                    showError(error)
                    return@launch
                }

                val event = createEventToSave()
                when (val result = repository.update(cardId, event)) {
                    is RepoResult.Error ->
                        showError(result.error)
                    is RepoResult.Success -> {
                        exitEvent.execute()
                        observer.onNext(EditorAction.EditCompleted(cardId))
                    }
                }
            }
        }
    }

    fun onStartDatePickerClick() {
        mDatePickerViewState.value = DatePickerViewState.StartDate(
            date = mPickedStartDate ?: dateProvider.current(),
            listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mPickedStartDate = dateProvider.asZonedDateTime(year, month, dayOfMonth)
                updateTimeViewState()
            }
        )
    }

    fun onStartTimePickerClick() {
        mDatePickerViewState.value = DatePickerViewState.StartTime(
            time = mPickedStartTime ?: dateProvider.current(),
            listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                mPickedStartTime = dateProvider.asZonedDateTime(hourOfDay, minute)
                updateTimeViewState()
            }
        )
    }

    fun onEndDatePickerClick() {
        mDatePickerViewState.value = DatePickerViewState.EndDate(
            date = mPickedEndDate,
            listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                mPickedEndDate = dateProvider.asZonedDateTime(year, month, dayOfMonth)
                updateTimeViewState()
            }
        )
    }

    fun onEndTimePickerClick() {
        mDatePickerViewState.value = DatePickerViewState.EndTime(
            time = mPickedEndTime,
            listener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                mPickedEndTime = dateProvider.asZonedDateTime(hourOfDay, minute)
                updateTimeViewState()
            }
        )
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
        updateStartTimeViewState()
        updateEndTimeViewState()
    }

    // FIXME: 4/20/2020 Перенести тексты в UI
    private fun updateStartTimeViewState() {
        mStartDateViewState.value = when (val pickedStartDate = mPickedStartDate) {
            null -> StartDateViewState.Today(dateProvider.current().formatDate2())
            else -> when {
                pickedStartDate sameDateWith dateProvider.current() ->
                    StartDateViewState.Today(pickedStartDate.formatDate2())
                else -> {
                    val formattedTime = pickedStartDate.formatDate()
                    StartDateViewState.ShowDate(formattedTime)
                }
            }
        }

        mStartTimeViewState.value = when (val pickedStartTime = mPickedStartTime) {
            null -> StartTimeViewState.Undefined
            else -> {
                val formattedTime = pickedStartTime.format(mTimeFormatter)
                StartTimeViewState.ShowTime(formattedTime)
            }
        }

        val previousEventEnd = mPreviousEvent?.endDateTime ?: dateProvider.current()
        val pickedStartDateTime = mPickedStartTime?.let { pickedStartTime ->
            mPickedStartDate?.withTime(pickedStartTime)
        }

        mStartDateTimeComment.value = when {
            mPickedStartTime?.sameTimeWith(previousEventEnd) == true
                    && mPickedStartDate?.sameDateWith(previousEventEnd) == true ->
                DatePickerCommentViewState.RightAfter(mPreviousEvent?.description.orEmpty())
            pickedStartDateTime?.isBefore(previousEventEnd) == true ->
                DatePickerCommentViewState.ErrorStartBeforePrevious(mPreviousEvent?.description.orEmpty())
            else ->
                DatePickerCommentViewState.Hidden
        }
    }

    // FIXME: 4/20/2020 Перенести тексты в UI
    private fun updateEndTimeViewState() {
        val currentDateTime = dateProvider.current()

        mEndDateViewState.value = when {
            mPickedEndDate sameDateWith currentDateTime ->
                EndDateViewState.Today(mPickedEndDate.formatDate2())
            else ->
                EndDateViewState.ShowDate(mPickedEndDate.formatDate())
        }

        mEndTimeViewState.value = mPickedEndTime.format(mTimeFormatter)

        val pickedDateTime = mPickedEndDate withTime mPickedEndTime
        mEndDateTimeComment.value = when {
            pickedDateTime sameTimeWith currentDateTime
                    && pickedDateTime sameDateWith currentDateTime ->
                DatePickerCommentViewState.JustNow
            pickedDateTime.isBefore(mPickedStartDate) ->
                DatePickerCommentViewState.ErrorEndBeforeStart
            else ->
                DatePickerCommentViewState.Hidden
        }
    }

    private fun showError(error: InputValidationError) {
        val errorText = error.asText(resources)
        showErrorEvent.execute(errorText)
    }

    private fun showError(error: RepoError) {
        val errorText = error.asText(resources)
        showErrorEvent.execute(errorText)
    }

    private inline fun validateInput(onError: (InputValidationError) -> Unit) {
        if (mTitle.isBlank()) {
            onError(InputValidationError.EmptyText)
        }

        if (mPickedStartTime == null) {
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

        val endDateTime = mPickedEndDate withTime mPickedEndTime

        return Event(
            id = eventId,
            description = mTitle,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            isFirstInCard = false
        )
    }

}
