package ru.iandreyshev.timemanager.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.timemanager.TimeWalkerApp
import ru.iandreyshev.timemanager.domain.IDateProvider
import ru.iandreyshev.timemanager.domain.IEventsRepo
import ru.iandreyshev.timemanager.ui.extensions.asUserReadableDate
import ru.iandreyshev.timemanager.ui.extensions.asViewState

class TimelineViewModel(
    private val dateProvider: IDateProvider,
    private val eventsRepo: IEventsRepo
) : ViewModel() {

    val eventsAdapter: RecyclerView.Adapter<*>
        get() = mEventsAdapter
    val dateViewState: LiveData<String>
        get() = mDateViewState

    private val mEventsAdapter = TimelineAdapter {  }
    private val mDateViewState = MutableLiveData<String>()
        .apply { value = dateProvider.get().asUserReadableDate(from = dateProvider.current()) }

    fun onPreviousDate() {
        val newDate = dateProvider.setPreviousDay()
        mDateViewState.value = newDate.asUserReadableDate(from = dateProvider.current())
        mEventsAdapter.events = eventsRepo.list(newDate).asViewState()
    }

    fun onNextDate() {
        mDateViewState.value = dateProvider.setNextDay()
            .asUserReadableDate(from = dateProvider.current())
    }

    fun onOpenDatePicker(): Boolean {
        return true
    }

    fun onResetToCurrent() {
        mDateViewState.value = dateProvider.setCurrent()
            .asUserReadableDate(from = dateProvider.current())
    }

    fun onCreateEvent() {
        TimeWalkerApp.navigator.openEditor(dateProvider.current(), null)
    }

}
