package ru.iandreyshev.timemanager.ui.tutorial

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.iandreyshev.timemanager.TimeCardsApp
import ru.iandreyshev.timemanager.domain.system.IAppRepository

class TutorialViewModel(
    private val repository: IAppRepository
) : ViewModel() {

    val firstCardButtonVisible = MutableLiveData(false)
    val firstEventButtonVisible = MutableLiveData(false)
    val createEventButtonVisible = MutableLiveData(false)
    val cardVisible = MutableLiveData(false)
    val event1Visible = MutableLiveData(false)
    val event2Visible = MutableLiveData(false)
    val event3Visible = MutableLiveData(false)
    val timerVisible = MutableLiveData(false)
    val doneButtonVisible = MutableLiveData(false)

    fun onCancelTutorial() {
        repository.setFirstLaunch(true)
        TimeCardsApp.navigator.openCards()
    }

}
