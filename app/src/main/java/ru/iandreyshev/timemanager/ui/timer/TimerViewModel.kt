package ru.iandreyshev.timemanager.ui.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.domain.IDateProvider

class TimerViewModel(
        private val dateProvider: IDateProvider
) : ViewModel() {

    val dateViewState: LiveData<String>
        get() = mDateViewState

    private var mCurrentDate = ZonedDateTime.now()

    private val mDateViewState = MutableLiveData<String>()
        .apply { value = mCurrentDate.asUserReadableDate(ZonedDateTime.now()) }

    fun onPreviousDate() {
        mCurrentDate = mCurrentDate.minusDays(1)
        mDateViewState.value = mCurrentDate.asUserReadableDate(ZonedDateTime.now())
    }

    fun onNextDate() {
        mCurrentDate = mCurrentDate.plusDays(1)
        mDateViewState.value = mCurrentDate.asUserReadableDate(ZonedDateTime.now())
    }

    fun onOpenDatePicker(): Boolean {
        return true
    }

    fun onCurrentDatePicked() {
        mCurrentDate = ZonedDateTime.now()
        mDateViewState.value = mCurrentDate.asUserReadableDate(ZonedDateTime.now())
    }

}
